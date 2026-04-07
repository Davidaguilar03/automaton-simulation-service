package co.edu.uptc.automatonsimulationservice.evaluation.models;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representa el resultado integral de evaluar una cadena, incluyendo aceptacion, estados finales y trazabilidad.
 */
@Getter
@AllArgsConstructor
public class EvaluationResult {
    private final String input;
    private final boolean accepted;
    private final Set<String> finalStates;
    private final List<TraceStep> trace;
}

