package co.edu.uptc.automatonsimulationservice.automaton.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonType;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.automaton.models.ValidationResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AutomatonValidationService {

    public ValidationResult validate(AutomatonDefinition definition) {
        List<String> errors = new ArrayList<>();
        if (definition == null) {
            errors.add("Automaton definition is required.");
            return new ValidationResult(false, List.copyOf(errors));
        }

        Set<String> states = normalizeSet(definition.getStates());
        Set<String> alphabet = normalizeSet(definition.getAlphabet());
        String initialState = normalizeValue(definition.getInitialState());
        Set<String> acceptingStates = normalizeSet(definition.getAcceptingStates());
        List<TransitionRule> transitions = normalizeTransitions(definition.getTransitions());

        if (definition.getType() == null) {
            errors.add("Automaton type is required.");
        }
        if (states.isEmpty()) {
            errors.add("At least one state is required.");
        }
        if (alphabet.isEmpty()) {
            errors.add("At least one symbol in the alphabet is required.");
        }
        if (initialState == null) {
            errors.add("Initial state is required.");
        } else if (!states.contains(initialState)) {
            errors.add("Initial state must belong to the states set.");
        }
        if (acceptingStates.isEmpty()) {
            errors.add("At least one accepting state is required.");
        }

        for (String acceptingState : acceptingStates) {
            if (!states.contains(acceptingState)) {
                errors.add("Accepting state '" + acceptingState + "' does not belong to states.");
            }
        }

        for (TransitionRule transition : transitions) {
            if (!states.contains(transition.getFromState())) {
                errors.add("Transition from-state '" + transition.getFromState() + "' does not belong to states.");
            }
            if (!states.contains(transition.getToState())) {
                errors.add("Transition to-state '" + transition.getToState() + "' does not belong to states.");
            }
            if (!alphabet.contains(transition.getSymbol())) {
                errors.add("Transition symbol '" + transition.getSymbol() + "' does not belong to alphabet.");
            }
        }

        if (definition.getType() == AutomatonType.DFA) {
            validateDfaConstraints(states, alphabet, transitions, errors);
        }

        return new ValidationResult(errors.isEmpty(), List.copyOf(errors));
    }

    private void validateDfaConstraints(Set<String> states,
                                        Set<String> alphabet,
                                        List<TransitionRule> transitions,
                                        List<String> errors) {
        Map<String, Integer> transitionCountByStateAndSymbol = new LinkedHashMap<>();
        for (TransitionRule transition : transitions) {
            String key = transition.getFromState() + "|" + transition.getSymbol();
            transitionCountByStateAndSymbol.merge(key, 1, Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : transitionCountByStateAndSymbol.entrySet()) {
            if (entry.getValue() > 1) {
                String[] parts = entry.getKey().split("\\|", 2);
                errors.add("DFA cannot define more than one transition for state '" + parts[0]
                    + "' and symbol '" + parts[1] + "'.");
            }
        }

        for (String state : states) {
            for (String symbol : alphabet) {
                String key = state + "|" + symbol;
                if (!transitionCountByStateAndSymbol.containsKey(key)) {
                    errors.add("DFA requires exactly one transition for state '" + state
                        + "' and symbol '" + symbol + "'.");
                }
            }
        }
    }

    private Set<String> normalizeSet(Set<String> source) {
        Set<String> normalized = new LinkedHashSet<>();
        if (source == null) {
            return normalized;
        }
        for (String value : source) {
            String normalizedValue = normalizeValue(value);
            if (normalizedValue != null) {
                normalized.add(normalizedValue);
            }
        }
        return normalized;
    }

    private List<TransitionRule> normalizeTransitions(List<TransitionRule> source) {
        List<TransitionRule> normalized = new ArrayList<>();
        if (source == null) {
            return normalized;
        }
        for (TransitionRule transition : source) {
            if (transition == null) {
                continue;
            }
            String fromState = normalizeValue(transition.getFromState());
            String symbol = normalizeValue(transition.getSymbol());
            String toState = normalizeValue(transition.getToState());
            if (Objects.nonNull(fromState) && Objects.nonNull(symbol) && Objects.nonNull(toState)) {
                normalized.add(new TransitionRule(fromState, symbol, toState));
            }
        }
        return normalized;
    }

    private String normalizeValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized;
    }
}

