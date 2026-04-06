package co.edu.uptc.automatonsimulationservice.ui.services;

import javafx.scene.control.Alert;

public class DialogService {

    /**
     * Levanta un mensaje emergente de tipo error (X roja).
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Levanta un mensaje emergente de información (i azul).
     */
    public void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

