package co.edu.uptc.automatonsimulationservice.ui.services;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Centraliza la creación y configuración visual de diálogos emergentes de la interfaz.
 */
public class DialogService {
    private static final String LOGO_PATH = "/co/edu/uptc/automatonsimulationservice/logo.png";
    private static final String STYLE_PATH = "/co/edu/uptc/automatonsimulationservice/ui/views/style.css";


    /**
     * Muestra un diálogo de error asociado a una ventana propietaria.
     */
    public void showError(Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        configureDialog(alert, owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo informativo asociado a una ventana propietaria.
     */
    public void showInformation(Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        configureDialog(alert, owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Aplica configuración visual y de ownership a un diálogo de captura de texto.
     */
    public void configureTextInputDialog(TextInputDialog dialog, Window owner) {
        configureDialog(dialog, owner);
    }

    /**
     * Define los elementos comunes de presentación para cualquier tipo de diálogo.
     */
    private void configureDialog(Dialog<?> dialog, Window owner) {
        if (owner != null) {
            dialog.initOwner(owner);
        }
        applyDialogStyles(dialog.getDialogPane());
        applyWindowIcon(dialog);
        dialog.setOnShown(event -> applyWindowIcon(dialog));
    }

    /**
     * Vincula la hoja de estilos institucional al panel del diálogo.
     */
    private void applyDialogStyles(DialogPane dialogPane) {
        String stylesheet = resolveStylesheet();
        if (stylesheet == null) {
            return;
        }
        if (!dialogPane.getStylesheets().contains(stylesheet)) {
            dialogPane.getStylesheets().add(stylesheet);
        }
    }

    /**
     * Resuelve la ruta absoluta de la hoja CSS usada por los diálogos.
     */
    private String resolveStylesheet() {
        java.net.URL resource = DialogService.class.getResource(STYLE_PATH);
        return resource == null ? null : resource.toExternalForm();
    }

    /**
     * Asigna el icono institucional a la ventana del diálogo cuando aún no tiene uno.
     */
    private void applyWindowIcon(Dialog<?> dialog) {
        Scene scene = dialog.getDialogPane().getScene();
        if (scene == null || !(scene.getWindow() instanceof Stage stage)) {
            return;
        }
        if (!stage.getIcons().isEmpty()) {
            return;
        }
        java.io.InputStream logoStream = DialogService.class.getResourceAsStream(LOGO_PATH);
        if (logoStream != null) {
            stage.getIcons().add(new Image(logoStream));
        }
    }
}

