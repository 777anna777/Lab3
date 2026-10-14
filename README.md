# Лабораторна робота №3 — Java Stream API та Gatherers

**Варіант:** `V = N mod 3 = 3 mod 3 = 0` (за номером 3; перевірте правильність свого N).

## Опис

Аналітика транзакцій електронної комерції з використанням Java Stream API, Optional і кастомного `Gatherer`.

- `Transaction` — незмінна модель транзакції (`record`).
- `findFirstHighValueTransaction` — перша транзакція із сумою, строго більшою за поріг (`Optional`).
- `takeTransactionsUntilBudgetExceeded` — послідовне накопичення до бюджету через `takeWhile`. Транзакція, яка перевищить бюджет, не додається.
- `processBatches` — власний послідовний `Gatherer`, який формує лише повні пакети розміру `batchSize` та припиняє обробку після `maxBatches` пакетів. Неповний пакет наприкінці скінченного потоку не повертається.

Пакет: `ua.kpi.comsys.streams`.

## Запуск

Потрібен **JDK 27** та доступ до Інтернету під час першої синхронізації залежностей.
Gradle **9.8.1** підтримує JDK 27.

В IntelliJ IDEA: `File → Open` (папка проєкту), в налаштуваннях Gradle оберіть JVM 27, дочекайтеся синхронізації, відкрийте `TransactionStreamAnalyzerTest.java` і запустіть тести.

На macOS/Linux у терміналі з кореня проєкту:

```bash
./gradlew test
./gradlew build
```

`gradlew` тут — спрощений bootstrap-скрипт: при першому запуску завантажує офіційний Gradle 9.8.1. Стандартний `gradle-wrapper.jar` не додається; для прямого імпорту через Gradle в IntelliJ за потреби оберіть *Specified location* установленого Gradle або виконайте команди в терміналі. Для стандартного Gradle Wrapper після завантаження Gradle можна виконати `./gradlew wrapper --gradle-version 9.8.1`, щоб згенерувати `gradle-wrapper.jar`.

Тести JUnit 5 включають `Stream.iterate`, `Stream.generate` та `@Timeout(2)` для нескінченних джерел.

## GitHub

Завантажте вихідні файли, `gradle/`, `build.gradle.kts`, `settings.gradle.kts`, `gradlew`, `gradlew.bat`, `README.md`, `.gitignore`; не завантажуйте `.idea`, `.gradle`, `build`.
