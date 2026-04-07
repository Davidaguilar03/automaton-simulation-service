package co.edu.uptc.automatonsimulationservice.ui.models;

/**
 * Representa un estado ya proyectado a coordenadas de interfaz con sus banderas visuales.
 */
public record StateNodeView(
    String name,
    double x,
    double y,
    boolean initial,
    boolean accepting
) {
}

