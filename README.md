# Simulador y Analizador Universal de Automatas Finitos

Aplicacion de escritorio en Java 21 para definir, validar, simular y persistir automatas finitos (DFA y NFA) usando JavaFX, FXML, JSON y Maven.

## Funcionalidades

- Definicion del automata usando la quintupla M = (Q, Sigma, delta, q0, F)
- Seleccion de DFA o NFA
- Validacion matematica de transiciones y de determinismo/completitud en DFA
- Importacion y exportacion en JSON
- Evaluacion por lotes de hasta 10 cadenas
- Trazabilidad paso a paso por cada cadena evaluada

## Formato de entrada

- Estados: valores separados por coma, por ejemplo `q0,q1,q2`
- Alfabeto: simbolos de un caracter separados por coma, por ejemplo `a,b`
- Transiciones: una por linea con formato `from,symbol,to`
- Evaluacion por lotes: una cadena por linea, maximo 10 cadenas

Ejemplo de transiciones:

```text
q0,a,q1
q0,b,q0
q1,a,q1
q1,b,q0
```

## Arquitectura

Estructura package-by-feature:

- `automaton`
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

Las reglas de negocio viven en servicios de dominio y son consumidas por los controladores.

## Archivos JSON de ejemplo

- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/dfa-even-a.json`
- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/nfa-ends-with-ab.json`

Ambos archivos siguen el esquema de persistencia con la propiedad raiz `automaton`.

## Ejecucion

Verifica Java primero:

```powershell
java -version
echo $env:JAVA_HOME
```

Si `JAVA_HOME` esta vacio, configuralo en la sesion actual:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Luego ejecuta pruebas e inicia la aplicacion:

```powershell
./mvnw.cmd clean test
./mvnw.cmd javafx:run
```

