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
            Scene scene = new Scene(loader.load(), 1450, 780);
            stage.setTitle("Simulador y Analizador Universal de Automatas Finitos");
            stage.setScene(scene);
            stage.show();
        } catch (Exception exception) {
            throw new RuntimeException("No se pudo inicializar la aplicacion", exception);
        }
    }
}

