package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class SidebarController {

    @FXML
    private VBox sidebar;          // VBox del sidebar (está en la escena principal)
    @FXML
    private Button btnCanal;
    @FXML
    private Button btnRaids;
    @FXML
    private Button btnClips;
    @FXML
    private Button btnStreamers1;
    @FXML
    private Button btnCategorias;
    @FXML
    private Button btnSalir;

    /**
     * Carga un FXML dentro del StackPane con id="contentArea"
     */
    private void loadView(String fileName) {
        if (sidebar == null || sidebar.getScene() == null) {
            return;
        }

        StackPane contentArea = (StackPane) sidebar.getScene().lookup("#contentArea");
        if (contentArea == null) {
            return;
        }

        String path = "/views/" + fileName;            // <-- siempre desde /views/
        URL url = getClass().getResource(path);

        contentArea.getChildren().clear();

        if (url == null) {
            contentArea.getChildren().add(new Label("⚠️ No se encontró la vista: " + path));
            return;
        }

        try {
            Parent view = FXMLLoader.load(url);
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().add(new Label("⚠️ Error al cargar la vista: " + path));
        }
    }

    // ----- Acciones del sidebar (usa los nombres reales en /views) -----
    @FXML
    private void showChannels() {
        loadView("canal.fxml");
    }

    @FXML
    private void showRaids() {
        loadView("raids.fxml");
    }        // crea views/raids.fxml cuando lo tengas

    @FXML
    private void showClips() {
        loadView("clips.fxml");
    }         // crea views/clip.fxml cuando lo tengas

    @FXML
    private void showStreamers() {
        loadView("streamer.fxml");
    }    // ya existe en tu screenshot

    @FXML
    private void showCategorias() {
        loadView("categorias.fxml");
    } // crea views/categorias.fxml cuando lo tengas

    @FXML
    private void showSalir(ActionEvent event) {
        // Cierra toda la aplicación
        Platform.exit();
    }
}
