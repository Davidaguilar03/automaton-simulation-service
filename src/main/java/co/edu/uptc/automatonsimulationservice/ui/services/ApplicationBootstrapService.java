package co.edu.uptc.automatonsimulationservice.ui.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ApplicationBootstrapService {

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                ApplicationBootstrapService.class.getResource("/co/edu/uptc/automatonsimulationservice/ui/views/main-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 1450, 780);
            stage.setTitle("SIMULADOR DE AUTÓMATAS FINITOS");
            stage.getIcons().add(new Image(ApplicationBootstrapService.class.getResourceAsStream("/co/edu/uptc/automatonsimulationservice/logo.png")));
            stage.setScene(scene);
            stage.show();
        } catch (Exception exception) {
            throw new RuntimeException("No se pudo inicializar la aplicacion", exception);
        }
    }
}
