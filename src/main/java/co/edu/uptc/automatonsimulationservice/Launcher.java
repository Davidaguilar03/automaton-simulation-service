package co.edu.uptc.automatonsimulationservice;

import javafx.application.Application;

/**
 * Clase de arranque del sistema que delega la inicializacion al ciclo de vida de JavaFX.
 */
public class Launcher {
    /**
     * Punto de entrada secundario que delega la inicialización a JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(AutomatonSimulatorApplication.class, args);
    }
}
