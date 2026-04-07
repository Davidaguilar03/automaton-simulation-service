package co.edu.uptc.automatonsimulationservice.ui.models;

/**
 * Representa una transición ya preparada para su renderizado, con metadatos de curvatura y etiqueta.
 */
public record TransitionEdgeView(
    String fromState,
    String toState,
    String label,
    boolean selfLoop,
    boolean curved,
    int curveDirection
) {
}

