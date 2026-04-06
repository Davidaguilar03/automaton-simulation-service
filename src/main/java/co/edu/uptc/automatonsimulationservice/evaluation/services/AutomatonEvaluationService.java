package co.edu.uptc.automatonsimulationservice.evaluation.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonType;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.evaluation.models.EvaluationResult;
import co.edu.uptc.automatonsimulationservice.evaluation.models.TraceStep;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AutomatonEvaluationService {

    /**
     * Evalúa una lista de palabras con un máximo de diez a la vez.
     */
    public List<EvaluationResult> evaluateBatch(AutomatonDefinition definition, List<String> inputs) {
        if (inputs == null) {
            return List.of();
        }
        if (inputs.size() > 10) {
            throw new IllegalArgumentException("La evaluacion por lotes permite hasta 10 cadenas.");
        }
        List<EvaluationResult> results = new ArrayList<>();
        for (String input : inputs) {
            results.add(evaluate(definition, input == null ? "" : input));
        }
        return results;
    }

    /**
     * Define dinámicamente si evaluar la palabra bajo un enfoque Determinista (DFA) o No determinista (NFA).
     */
    public EvaluationResult evaluate(AutomatonDefinition definition, String input) {
        if (definition.getType() == AutomatonType.NFA) {
            return evaluateNfa(definition, input);
        }
        return evaluateDfa(definition, input);
    }

    /**
     * Procesa letras mediante estados simples y únicos registrando errores en caso de bloqueo.
     */
    private EvaluationResult evaluateDfa(AutomatonDefinition definition, String input) {
        String currentState = definition.getInitialState();
        List<TraceStep> trace = new ArrayList<>();
        for (char character : input.toCharArray()) {
            String symbol = String.valueOf(character);
            String nextState = findSingleTransition(definition.getTransitions(), currentState, symbol);
            if (nextState == null) {
                trace.add(new TraceStep("(" + currentState + ", " + symbol + ") -> undefined"));
                return new EvaluationResult(input, false, Set.of(), List.copyOf(trace));
            }
            trace.add(new TraceStep("(" + currentState + ", " + symbol + ") -> " + nextState));
            currentState = nextState;
        }
        boolean accepted = definition.getAcceptingStates().contains(currentState);
        return new EvaluationResult(input, accepted, Set.of(currentState), List.copyOf(trace));
    }

    /**
     * Procesa el autómata permitiendo exploración paralela sobre múltiples estados al mismo tiempo.
     */
    private EvaluationResult evaluateNfa(AutomatonDefinition definition, String input) {
        Set<String> currentStates = new LinkedHashSet<>();
        currentStates.add(definition.getInitialState());
        List<TraceStep> trace = new ArrayList<>();

        for (char character : input.toCharArray()) {
            String symbol = String.valueOf(character);
            Set<String> nextStates = new LinkedHashSet<>();
            for (String currentState : currentStates) {
                for (TransitionRule transition : definition.getTransitions()) {
                    if (transition.getFromState().equals(currentState) && transition.getSymbol().equals(symbol)) {
                        nextStates.add(transition.getToState());
                    }
                }
            }
            trace.add(new TraceStep(currentStates + " --" + symbol + "--> " + nextStates));
            currentStates = nextStates;
            if (currentStates.isEmpty()) {
                break;
            }
        }

        boolean accepted = currentStates.stream().anyMatch(definition.getAcceptingStates()::contains);
        return new EvaluationResult(input, accepted, Set.copyOf(currentStates), List.copyOf(trace));
    }

    /**
     * Busca qué estado es el siguiente en una lista basándose en el origen y un símbolo dado.
     */
    private String findSingleTransition(List<TransitionRule> transitions, String fromState, String symbol) {
        for (TransitionRule transition : transitions) {
            if (transition.getFromState().equals(fromState) && transition.getSymbol().equals(symbol)) {
                return transition.getToState();
            }
        }
        return null;
    }
}
