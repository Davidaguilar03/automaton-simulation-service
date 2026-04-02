package co.edu.uptc.automatonsimulationservice.ui.models;

public record StateNodeView(
    String name,
    double x,
    double y,
    boolean initial,
    boolean accepting
) {
}

