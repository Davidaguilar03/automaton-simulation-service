package co.edu.uptc.automatonsimulationservice.automaton.models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la definicion matemática de un autómata finito.
 *
 * <p>La entidad agrupa los cinco componentes de la quíntupla (Q, Sigma, q0, F, delta)
 * y conserva orden de inserción en colecciones para mantener consistencia visual y de
 * serialización en la interfaz y los archivos JSON.</p>
 */
@Data
@NoArgsConstructor
public class AutomatonDefinition {
    private AutomatonType type = AutomatonType.DFA;
    private Set<String> states = new LinkedHashSet<>();
    private Set<String> alphabet = new LinkedHashSet<>();
    private String initialState;
    private Set<String> acceptingStates = new LinkedHashSet<>();
    private List<TransitionRule> transitions = new ArrayList<>();
}

