package ua.kpi.comsys.streams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStreamAnalyzerTest {
    private final TransactionStreamAnalyzer analyzer = new TransactionStreamAnalyzer();

    private static Transaction t(int id, String amount) {
        return new Transaction("T" + id, "goods", new BigDecimal(amount),
                LocalDateTime.of(2026, 10, 14, 12, 0));
    }

    @Test void findsFirstGreaterThanThreshold() {
        assertEquals(t(2, "50"), analyzer.findFirstHighValueTransaction(
                Stream.of(t(1, "10"), t(2, "50"), t(3, "100")), new BigDecimal("40")).orElseThrow());
    }

    @Test void thresholdIsStrict() {
        assertTrue(analyzer.findFirstHighValueTransaction(
                Stream.of(t(1, "40")), new BigDecimal("40")).isEmpty());
    }

    @Test void noMatchReturnsEmpty() {
        assertTrue(analyzer.findFirstHighValueTransaction(Stream.empty(), BigDecimal.TEN).isEmpty());
    }

    @Test @Timeout(2) void findsValueInInfiniteStream() {
        assertEquals("T6", analyzer.findFirstHighValueTransaction(
                Stream.iterate(1, n -> n + 1).map(n -> t(n, Integer.toString(n))),
                new BigDecimal("5")).orElseThrow().transactionId());
    }

    @Test void budgetIncludesExactLimit() {
        assertEquals(List.of(t(1, "20"), t(2, "30")), analyzer.takeTransactionsUntilBudgetExceeded(
                Stream.of(t(1, "20"), t(2, "30"), t(3, "1")), new BigDecimal("50")));
    }

    @Test void budgetRejectsFirstExceeded() {
        assertEquals(List.of(t(1, "9")), analyzer.takeTransactionsUntilBudgetExceeded(
                Stream.of(t(1, "9"), t(2, "2"), t(3, "1")), BigDecimal.TEN));
    }

    @Test @Timeout(2) void budgetStopsInfiniteStream() {
        assertEquals(3, analyzer.takeTransactionsUntilBudgetExceeded(
                Stream.generate(() -> t(1, "3")), BigDecimal.TEN).size());
    }

    @Test void emptyBudgetStream() {
        assertTrue(analyzer.takeTransactionsUntilBudgetExceeded(Stream.empty(), BigDecimal.TEN).isEmpty());
    }

    @Test void groupsFullBatches() {
        assertEquals(List.of(List.of(t(1, "1"), t(2, "2")), List.of(t(3, "3"), t(4, "4"))),
                analyzer.processBatches(Stream.of(t(1, "1"), t(2, "2"), t(3, "3"), t(4, "4")), 2, 2));
    }

    @Test void dropsIncompleteTrailingBatch() {
        assertEquals(List.of(List.of(t(1, "1"), t(2, "2"))),
                analyzer.processBatches(Stream.of(t(1, "1"), t(2, "2"), t(3, "3")), 2, 3));
    }

    @Test @Timeout(2) void batchesStopInfiniteStream() {
        List<List<Transaction>> batches = analyzer.processBatches(
                Stream.iterate(1, n -> n + 1).map(n -> t(n, "1")), 3, 2);
        assertEquals(2, batches.size());
        assertEquals(List.of("T1", "T2", "T3"),
                batches.getFirst().stream().map(Transaction::transactionId).toList());
        assertEquals(List.of("T4", "T5", "T6"),
                batches.get(1).stream().map(Transaction::transactionId).toList());
    }

    @Test void rejectsNullStream() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.findFirstHighValueTransaction(null, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> analyzer.takeTransactionsUntilBudgetExceeded(null, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> analyzer.processBatches(null, 1, 1));
    }

    @Test void rejectsNullRequiredArgument() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.findFirstHighValueTransaction(Stream.empty(), null));
        assertThrows(IllegalArgumentException.class, () -> analyzer.takeTransactionsUntilBudgetExceeded(Stream.empty(), null));
    }

    @Test void rejectsNonPositiveBatchParameters() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.processBatches(Stream.empty(), 0, 1));
        assertThrows(IllegalArgumentException.class, () -> analyzer.processBatches(Stream.empty(), 1, 0));
        assertThrows(IllegalArgumentException.class, () -> analyzer.processBatches(Stream.empty(), -1, 1));
        assertThrows(IllegalArgumentException.class, () -> analyzer.processBatches(Stream.empty(), 1, -1));
    }

    @Test void emptyStreamProducesNoBatches() {
        assertTrue(analyzer.processBatches(Stream.empty(), 2, 1).isEmpty());
    }
}
