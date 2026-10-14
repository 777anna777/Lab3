# Лабораторна робота №3 — Java Stream API та Gatherers

**Варіант:** `V = 3 mod 3 = 0` 

## Опис

Аналітика транзакцій електронної комерції з використанням Java Stream API, Optional і кастомного `Gatherer`.

- `Transaction` — незмінна модель транзакції (`record`).
- `findFirstHighValueTransaction` — перша транзакція із сумою, строго більшою за поріг (`Optional`).
- `takeTransactionsUntilBudgetExceeded` — послідовне накопичення до бюджету через `takeWhile`. Транзакція, яка перевищить бюджет, не додається.
- `processBatches` — власний послідовний `Gatherer`, який формує лише повні пакети розміру `batchSize` та припиняє обробку після `maxBatches` пакетів. Неповний пакет наприкінці скінченного потоку не повертається.

Пакет: `ua.kpi.comsys.streams`.

## Запуск

В IntelliJ IDEA: `File → Open` (папка проєкту), в налаштуваннях Gradle оберіть JVM 27, дочекайтеся синхронізації, відкрийте `TransactionStreamAnalyzerTest.java` і запустіть тести.

Тести JUnit 5 включають `Stream.iterate`, `Stream.generate` та `@Timeout(2)` для нескінченних джерел.

