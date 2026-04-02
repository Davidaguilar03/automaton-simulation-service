package co.edu.uptc.automatonsimulationservice.automaton.models;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {
}

