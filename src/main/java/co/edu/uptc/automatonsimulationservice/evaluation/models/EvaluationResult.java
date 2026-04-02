package co.edu.uptc.automatonsimulationservice.evaluation.models;

import java.util.List;
import java.util.Set;

public class EvaluationResult {
    private final String input;
    private final boolean accepted;
    private final Set<String> finalStates;
    private final List<TraceStep> trace;

    public EvaluationResult(String input, boolean accepted, Set<String> finalStates, List<TraceStep> trace) {
        this.input = input;
        this.accepted = accepted;
        this.finalStates = finalStates;
        this.trace = trace;
    }

    public String getInput() {
        return input;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    public List<TraceStep> getTrace() {
        return trace;
    }
}

