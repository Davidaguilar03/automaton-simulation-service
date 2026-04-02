# Universal Finite Automata Simulator and Analyzer

Java 21 desktop application for defining, validating, simulating, and persisting finite automata (DFA and NFA) with JavaFX, FXML, JSON, and Maven.

## Features

- Automaton definition using quintuple M = (Q, Sigma, delta, q0, F)
- DFA and NFA selection
- Mathematical validation for transitions and DFA determinism/completeness
- JSON import and export
- Batch evaluation for up to 10 input strings
- Step-by-step trace per evaluated string

## Input format

- States: comma-separated values, for example `q0,q1,q2`
- Alphabet: comma-separated one-character symbols, for example `a,b`
- Transitions: one transition per line in `from,symbol,to` format
- Batch evaluation: one string per line, maximum 10 strings

Example transitions:

```text
q0,a,q1
q0,b,q0
q1,a,q1
q1,b,q0
```

## Architecture

Package-by-feature structure:

- `automata`
  - `models`
  - `services`
  - `controllers`
- `evaluation`
  - `models`
  - `services`
  - `controllers`
- `persistence`
  - `models`
  - `services`
  - `controllers`
- `ui`
  - `models`
  - `services`
  - `controllers`
  - `views` (FXML)

Business rules live in domain services and are consumed by controllers.

## Example JSON files

- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/dfa-even-a.json`
- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/nfa-ends-with-ab.json`

Both files follow the persistence schema with root property `automaton`.

## Run

Verify Java first:

```powershell
java -version
echo $env:JAVA_HOME
```

If `JAVA_HOME` is empty, set it in the current shell:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Then run tests and start the application:

```powershell
./mvnw.cmd clean test
./mvnw.cmd javafx:run
```

