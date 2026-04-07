package co.edu.uptc.automatonsimulationservice;

import co.edu.uptc.automatonsimulationservice.ui.services.ApplicationBootstrapService;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada JavaFX que delega la configuracion inicial de la interfaz al servicio de arranque.
 */
public class AutomatonSimulatorApplication extends Application {
    private final ApplicationBootstrapService applicationBootstrapService = new ApplicationBootstrapService();

    /**
     * Inicia la aplicación cargando la configuración base.
     */
    @Override
    public void start(Stage stage) {
        applicationBootstrapService.start(stage);
    }
}

