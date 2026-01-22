## SpiceJet UI Testing (Java + Selenium + Maven + Cucumber + TestNG)

### Project structure
- **Features**: `src/test/resources/features`
- **Step Definitions**: `src/test/java/com/spicejet/steps`
- **Hooks**: `src/test/java/com/spicejet/hooks`
- **Pages (POM)**: `src/test/java/com/spicejet/pages`
- **Driver / Config**: `src/test/java/com/spicejet/driver`, `src/test/java/com/spicejet/config`
- **Runner**: `src/test/java/com/spicejet/runners/RunCucumberTest.java`

### Prerequisites
- Java 17+
- Maven 3.8+
- Google Chrome installed

### Run tests
```bash
mvn -q test
```

### Run headless
```bash
mvn -q test -Dheadless=true
```

### Reports
- HTML: `target/cucumber-report.html`
- JSON: `target/cucumber-report.json`

