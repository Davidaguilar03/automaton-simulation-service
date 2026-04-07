package co.edu.uptc.automatonsimulationservice.persistence.models;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modela la estructura persistida del archivo JSON con el autómata y sus posiciones visuales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomatonFile {
    private AutomatonDefinition automaton;
    private Map<String, StatePosition> statePositions = new LinkedHashMap<>();
}

