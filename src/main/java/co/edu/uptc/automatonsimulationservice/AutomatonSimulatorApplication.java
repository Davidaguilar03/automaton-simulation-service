package co.edu.uptc.automatonsimulationservice;

import co.edu.uptc.automatonsimulationservice.ui.services.ApplicationBootstrapService;
import javafx.application.Application;
import javafx.stage.Stage;

public class AutomatonSimulatorApplication extends Application {
    private final ApplicationBootstrapService applicationBootstrapService = new ApplicationBootstrapService();

    @Override
    public void start(Stage stage) {
        applicationBootstrapService.start(stage);
    }
}

