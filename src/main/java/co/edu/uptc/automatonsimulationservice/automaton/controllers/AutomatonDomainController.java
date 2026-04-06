package co.edu.uptc.automatonsimulationservice.automaton.controllers;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.ValidationResult;
import co.edu.uptc.automatonsimulationservice.automaton.services.AutomatonValidationService;

public class AutomatonDomainController {

    private final AutomatonValidationService automatonValidationService;

    /**
     * Constructor del controlador de dominio de la lógica del autómata.
     */
    public AutomatonDomainController(AutomatonValidationService automatonValidationService) {
        this.automatonValidationService = automatonValidationService;
    }

    /**
     * Llama al servicio de validación para determinar si el autómata es estable y matemáticamente correcto.
     */
    public ValidationResult validate(AutomatonDefinition definition) {
        return automatonValidationService.validate(definition);
    }
}
