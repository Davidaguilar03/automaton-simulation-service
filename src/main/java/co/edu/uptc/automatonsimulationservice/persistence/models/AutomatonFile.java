package co.edu.uptc.automatonsimulationservice.persistence.models;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutomatonFile {
    private AutomatonDefinition automaton;
    private Map<String, StatePosition> statePositions = new LinkedHashMap<>();

    public AutomatonFile() {
    }

    public AutomatonFile(AutomatonDefinition automaton) {
        this.automaton = automaton;
    }

    public AutomatonFile(AutomatonDefinition automaton, Map<String, StatePosition> statePositions) {
        this.automaton = automaton;
        this.statePositions = statePositions;
    }

    public AutomatonDefinition getAutomaton() {
        return automaton;
    }

    public void setAutomaton(AutomatonDefinition automaton) {
        this.automaton = automaton;
    }

    public Map<String, StatePosition> getStatePositions() {
        return statePositions;
    }

    public void setStatePositions(Map<String, StatePosition> statePositions) {
        this.statePositions = statePositions;
    }
}

