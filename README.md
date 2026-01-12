# secret-santa

This repository contains a compact, pragmatic Secret Santa tool written in Java. The program reads a CSV with participants and optional exclusions, computes Secret Santa assignments while respecting constraints, and writes the results as a timestamped CSV.

How the project is organized
- Source code: `src/main/java` (packages under `com.digitalxc.secretsanta`).
- Input file: `resources/input.csv` (see example below).
- Output files: `resources/output_{yyyyMMdd_HHmmss}.csv`.

Example input CSV (header optional). Columns: `name,email,excludedEmails`
- `excludedEmails` is optional and, when present, lists emails separated by a pipe (`|`).

Example:

```
name,email,excludedEmails
Alice,alice@example.com,
Bob,bob@example.com,alice@example.com
Carol,carol@example.com,
Dave,dave@example.com,bob@example.com|carol@example.com
```

Build and run (Windows PowerShell)

1. Build the project with Maven. This produces a JAR in `target/`:

```powershell
cd E:\repos\secret-santa
mvn -DskipTests package
```

2. Run the program. The application expects `resources/input.csv` in the repository root. It writes a timestamped CSV to `resources/`:

```powershell
java -cp target/secret-santa-1.0-SNAPSHOT.jar com.digitalxc.secretsanta.Main
```

Notes and troubleshooting
- If you get `IllegalArgumentException: email must be provided` it means one or more rows in the CSV are missing an email. Fix the CSV and re-run.
- The writer always produces a new file named like `output_20260112_154501.csv` — the timestamp uses local time and `yyyyMMdd_HHmmss` format.
- The CSV parser is resilient to quoted fields and doubled quotes, but it does not attempt full RFC4180 validation. Keep fields simple to avoid edge cases.

# secret-santa
secret-santa code base for digitalxc 
