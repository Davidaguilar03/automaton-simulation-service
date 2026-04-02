package co.edu.uptc.automatonsimulationservice.automaton.models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AutomatonDefinition {
    private AutomatonType type = AutomatonType.DFA;
    private Set<String> states = new LinkedHashSet<>();
    private Set<String> alphabet = new LinkedHashSet<>();
    private String initialState;
    private Set<String> acceptingStates = new LinkedHashSet<>();
    private List<TransitionRule> transitions = new ArrayList<>();

    public AutomatonDefinition() {
    }

    public AutomatonType getType() {
        return type;
    }

    public void setType(AutomatonType type) {
        this.type = type;
    }

    public Set<String> getStates() {
        return states;
    }

    public void setStates(Set<String> states) {
        this.states = states;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Set<String> alphabet) {
        this.alphabet = alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public Set<String> getAcceptingStates() {
        return acceptingStates;
    }

    public void setAcceptingStates(Set<String> acceptingStates) {
        this.acceptingStates = acceptingStates;
    }

    public List<TransitionRule> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionRule> transitions) {
        this.transitions = transitions;
    }
}

