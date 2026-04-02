package co.edu.uptc.automatonsimulationservice.ui.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationBootstrapService {

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                ApplicationBootstrapService.class.getResource("/co/edu/uptc/automatonsimulationservice/ui/views/main-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 1200, 780);
            stage.setTitle("Universal Finite Automata Simulator and Analyzer");
            stage.setScene(scene);
            stage.show();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to initialize application", exception);
        }
    }
}

