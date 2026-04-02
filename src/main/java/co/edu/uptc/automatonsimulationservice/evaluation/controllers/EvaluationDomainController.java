package co.edu.uptc.automatonsimulationservice.evaluation.controllers;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.evaluation.models.EvaluationResult;
import co.edu.uptc.automatonsimulationservice.evaluation.services.AutomatonEvaluationService;

import java.util.List;

public class EvaluationDomainController {
    private final AutomatonEvaluationService automatonEvaluationService;

    public EvaluationDomainController(AutomatonEvaluationService automatonEvaluationService) {
        this.automatonEvaluationService = automatonEvaluationService;
    }

    public List<EvaluationResult> evaluateBatch(AutomatonDefinition definition, List<String> inputs) {
        return automatonEvaluationService.evaluateBatch(definition, inputs);
    }
}

