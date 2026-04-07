package co.edu.uptc.automatonsimulationservice.automaton.models;

import java.util.List;

/**
 * Encapsula el resultado de una validacion con su estado booleano y listado de errores detectados.
 */
public record ValidationResult(boolean valid, List<String> errors) {
}

