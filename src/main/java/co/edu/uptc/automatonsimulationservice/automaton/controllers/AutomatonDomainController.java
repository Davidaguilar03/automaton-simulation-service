package co.edu.uptc.automatonsimulationservice.automaton.controllers;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.ValidationResult;
import co.edu.uptc.automatonsimulationservice.automaton.services.AutomatonValidationService;

public class AutomatonDomainController {
    private final AutomatonValidationService automatonValidationService;

    public AutomatonDomainController(AutomatonValidationService automatonValidationService) {
        this.automatonValidationService = automatonValidationService;
    }

    public ValidationResult validate(AutomatonDefinition definition) {
        return automatonValidationService.validate(definition);
    }
}

