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

/**
 * Implementa las reglas de consistencia estructural y semántica para definiciones de DFA y NFA.
 */
public class AutomatonValidationService {

    /**
     * Valida la definición del autómata, sus estados, su alfabeto y transiciones completas.
     */
    public ValidationResult validate(AutomatonDefinition definition) {
        List<String> errors = new ArrayList<>();
        if (definition == null) {
            errors.add("La definicion del automata es obligatoria.");
            return new ValidationResult(false, List.copyOf(errors));
        }

        Set<String> states = normalizeSet(definition.getStates());
        Set<String> alphabet = normalizeSet(definition.getAlphabet());
        String initialState = normalizeValue(definition.getInitialState());
        Set<String> acceptingStates = normalizeSet(definition.getAcceptingStates());
        List<TransitionRule> transitions = normalizeTransitions(definition.getTransitions());

        if (definition.getType() == null) {
            errors.add("El tipo de automata es obligatorio.");
        }
        if (states.isEmpty()) {
            errors.add("Se requiere al menos un estado.");
        }
        if (alphabet.isEmpty()) {
            errors.add("Se requiere al menos un simbolo en el alfabeto.");
        }
        if (initialState == null) {
            errors.add("El estado inicial es obligatorio.");
        } else if (!states.contains(initialState)) {
            errors.add("El estado inicial debe pertenecer al conjunto de estados.");
        }
        if (acceptingStates.isEmpty()) {
            errors.add("Se requiere al menos un estado de aceptacion.");
        }

        for (String acceptingState : acceptingStates) {
            if (!states.contains(acceptingState)) {
                errors.add("El estado de aceptacion '" + acceptingState + "' no pertenece a los estados.");
            }
        }

        for (TransitionRule transition : transitions) {
            if (!states.contains(transition.getFromState())) {
                errors.add("El estado origen de la transicion '" + transition.getFromState() + "' no pertenece a los estados.");
            }
            if (!states.contains(transition.getToState())) {
                errors.add("El estado destino de la transicion '" + transition.getToState() + "' no pertenece a los estados.");
            }
            if (!alphabet.contains(transition.getSymbol())) {
                errors.add("El simbolo de transicion '" + transition.getSymbol() + "' no pertenece al alfabeto.");
            }
        }

        if (definition.getType() == AutomatonType.DFA) {
            validateDfaConstraints(states, alphabet, transitions, errors);
        }

        return new ValidationResult(errors.isEmpty(), List.copyOf(errors));
    }

    /**
     * Comprueba restricciones suplementarias para los Autómatas Deterministas (DFA).
     */
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
                errors.add("Un DFA no puede definir mas de una transicion para el estado '" + parts[0]
                    + "' y el simbolo '" + parts[1] + "'.");
            }
        }

        for (String state : states) {
            for (String symbol : alphabet) {
                String key = state + "|" + symbol;
                if (!transitionCountByStateAndSymbol.containsKey(key)) {
                    errors.add("Un DFA requiere exactamente una transicion para el estado '" + state
                        + "' y el simbolo '" + symbol + "'.");
                }
            }
        }
    }

    /**
     * Limpia los valores en blanco dentro de un conjunto y lo retorna nuevo.
     */
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

    /**
     * Revisa y limpia las transiciones defectuosas antes de insertarlas al resultado final.
     */
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

    /**
     * Toma una cadena simple y la reduce, devolviendo null si está vacía.
     */
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
