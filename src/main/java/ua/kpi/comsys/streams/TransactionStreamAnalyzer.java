package ua.kpi.comsys.streams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public class TransactionStreamAnalyzer {

    public Optional<Transaction> findFirstHighValueTransaction(
            Stream<Transaction> stream, BigDecimal threshold) {
        if (stream == null || threshold == null) {
            throw new IllegalArgumentException("stream and threshold cannot be null");
        }
        return stream.filter(transaction -> transaction.amount().compareTo(threshold) > 0)
                .findFirst();
    }

    public List<Transaction> takeTransactionsUntilBudgetExceeded(
            Stream<Transaction> stream, BigDecimal maxBudget) {
        if (stream == null || maxBudget == null) {
            throw new IllegalArgumentException("stream and maxBudget cannot be null");
        }
        AtomicReference<BigDecimal> spent = new AtomicReference<>(BigDecimal.ZERO);
        // takeWhile stops before including the transaction that exceeds the budget.
        return stream.sequential().takeWhile(transaction -> {
            BigDecimal next = spent.get().add(transaction.amount());
            if (next.compareTo(maxBudget) > 0) {
                return false;
            }
            spent.set(next);
            return true;
        }).toList();
    }

    public List<List<Transaction>> processBatches(
            Stream<Transaction> stream, int batchSize, int maxBatches) {
        if (stream == null) {
            throw new IllegalArgumentException("stream cannot be null");
        }
        if (batchSize <= 0 || maxBatches <= 0) {
            throw new IllegalArgumentException("batchSize and maxBatches must be greater than 0");
        }

        class BatchState {
            final List<Transaction> current = new ArrayList<>(batchSize);
            int emitted;
        }

        Gatherer<Transaction, BatchState, List<Transaction>> batchGatherer = Gatherer.ofSequential(
                BatchState::new,
                (state, item, downstream) -> {
                    state.current.add(item);
                    if (state.current.size() == batchSize) {
                        List<Transaction> completeBatch = List.copyOf(state.current);
                        state.current.clear();
                        state.emitted++;
                        if (!downstream.push(completeBatch)) {
                            return false;
                        }
                        return state.emitted < maxBatches;
                    }
                    return true;
                }
        );
        // Only full batches are returned. No finisher means a partial trailing batch is discarded.
        return stream.sequential().gather(batchGatherer).toList();
    }
}
