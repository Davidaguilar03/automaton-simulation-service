package co.edu.uptc.automatonsimulationservice.automaton.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modela una transición elemental del autómata en la forma (estadoOrigen, símbolo, estadoDestino).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransitionRule {
    private String fromState;
    private String symbol;
    private String toState;
}

