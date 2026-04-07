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

El proyecto sigue un enfoque **package-by-feature** con separación por responsabilidad:

- `co.edu.uptc.automatonsimulationservice.automaton`
  - `models`: entidades del dominio formal (`AutomatonDefinition`, `TransitionRule`, `AutomatonType`).
  - `services`: validación estructural y semántica de DFA/NFA (`AutomatonValidationService`).
  - `controllers`: fachada de dominio para consumo desde la capa UI.
- `co.edu.uptc.automatonsimulationservice.evaluation`
  - `models`: resultados y trazabilidad de ejecución (`EvaluationResult`, `TraceStep`).
  - `services`: motor de evaluación de cadenas para DFA y NFA.
  - `controllers`: orquestación de evaluación unitaria y por lotes.
- `co.edu.uptc.automatonsimulationservice.persistence`
  - `models`: DTOs para serialización JSON y coordenadas (`AutomatonFile`, `StatePosition`).
  - `services`: lectura/escritura JSON y validación de estructura de archivo.
  - `controllers`: interfaz de persistencia usada por la UI.
- `co.edu.uptc.automatonsimulationservice.ui`
  - `controllers`: lógica de interacción JavaFX para editor principal y panel de pruebas.
  - `services`: layout y renderizado del diagrama, bootstrap de aplicación y diálogos.
  - `models`: modelos de apoyo para representación visual.
  - `views`: archivos FXML y estilos CSS.

Las reglas de negocio se concentran en `services` de dominio y la UI se limita a coordinar flujos de interacción.

## Archivos JSON de ejemplo

- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/dfa-Pares0impares1.json`
- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/dfa-terminan-diferente-ayb.json`
- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/nfa-terminan-diferente-ayb.json`
- `src/main/resources/co/edu/uptc/automatonsimulationservice/examples/nfa-terminan-en-10.json`

Todos los archivos siguen el esquema de persistencia con la propiedad raiz `automaton`.

## Ejecucion

Compila el JAR ejecutable una vez:

```powershell
./mvnw.cmd clean package
```

Luego ejecuta la aplicacion corriendo solo el JAR:

```powershell
java -jar .\target\automateo.jar
```

# Manual de Usuario

## Carga de archivos

Una vez iniciada la aplicación, se podrá seleccionar la opción `Importar JSON` la cual permitirá cargar desde los archivos locales del sistema un autómata en dicho formato.

Es necesario realizar dos observaciones importantes previas:

```text
1. El archivo JSON contiene la información completa del autómata
```

Dicho archivo almacena la siguiente información:

- `Tipo de autómata`
- `Conjunto de estados`
- `Alfabeto`
- `Estado inicial`
- `Estados de aceptación`
- `Conjunto de transiciones`
  - `Estado del que parte la transición`
  - `Símbolo de la transición`
  - `Estado al que llega la transición`

Pero además de ello, cuenta con la información de las coordenadas exactas en las cuales se encuentra cada estado dentro del área de dibujo, lo cual permite que el autómata se dibuje exactamente igual a como se encontraba al momento de ser exportado.

- `Posiciones de estados`
  - `Estado`
    - `Coordenada X`
    - `Coordenada Y`

En caso tal de que la información de las coordenadas no se encuentre presente, el autómata se dibujará con una distribución circular automática de los estados dentro del área de dibujo, lo cual puede resultar en una disposición diferente a la que se tenía al momento de diseñar el autómata previamente.

```text
2. El sistema valida que la información del archivo JSON sea congruente
```
Al momento de cargar un archivo JSON, el sistema realiza una validación de la información contenida en el mismo para asegurar que sea congruente y pueda ser procesada correctamente. Esta validación incluye:
- Que el tipo de autómata sea válido (DFA o NFA)
- Que todos los elementos de la quíntupla se encuentren presentes
- Que los datos de la quíntupla sean consistentes con respecto al resto de elementos

Es decir, si se importa un archivo JSON que contenga un autómata con información inconsistente o incompleta, el sistema mostrará un mensaje de error.

Y por otra parte, si se importa un archivo JSON que no tenga nada que ver con la estructura definida del autómata, el sistema lo rechazará y alertará de igual forma errores en la carga del archivo.

## Descarga de archivos

Una vez iniciada la aplicación, se podrá seleccionar la opción `Exportar JSON` la cual permitirá descargar hacia los archivos locales del sistema un autómata previamente diseñado.

Dicho autómata almacenará toda la información mencionada en el punto de `Importar JSON`, incluyendo las coordenadas de los estados para así poder importarlos nuevamente conservando todos sus detalles y características específicas.
