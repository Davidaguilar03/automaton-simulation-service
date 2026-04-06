package co.edu.uptc.automatonsimulationservice.evaluation.controllers;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.evaluation.models.EvaluationResult;
import co.edu.uptc.automatonsimulationservice.evaluation.services.AutomatonEvaluationService;

import java.util.List;

/**
 * Controlador que expone servicios para la evaluación de autómatas.
 */
public class EvaluationDomainController {

    private final AutomatonEvaluationService automatonEvaluationService;

    /**
     * Constructor del controlador que expone validaciones sobre la evaluación matemática.
     */
    public EvaluationDomainController(AutomatonEvaluationService automatonEvaluationService) {
        this.automatonEvaluationService = automatonEvaluationService;
    }

    /**
     * Evalúa una sola palabra en el autómata proporcionado.
     */
    public EvaluationResult evaluate(AutomatonDefinition definition, String input) {
        return automatonEvaluationService.evaluate(definition, input);
    }

    /**
     * Evalúa múltiples cadenas (lote) pasándolas al proveedor de servicios matemáticos.
     */
    public List<EvaluationResult> evaluateBatch(AutomatonDefinition definition, List<String> inputs) {
        return automatonEvaluationService.evaluateBatch(definition, inputs);
    }
}
