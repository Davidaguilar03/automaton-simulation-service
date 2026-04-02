package co.edu.uptc.automatonsimulationservice.ui.models;

public record TransitionEdgeView(
    String fromState,
    String toState,
    String label,
    boolean selfLoop,
    boolean curved,
    int curveDirection
) {
}

