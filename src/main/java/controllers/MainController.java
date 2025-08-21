package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

public class MainController implements Initializable {

    @FXML
    private StackPane contentArea;
    @FXML
    private MenuItem btnSalir;
    @FXML
    private MenuItem btnAcercaDe;
    @FXML
    private MenuItem btnVerLicencia;
    @FXML
    private MenuItem btnRepositorio;

    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarVistaInicial();
    }

    private void cargarVistaInicial() {
        URL fxml = getClass().getResource("/views/streamer.fxml");
        contentArea.getChildren().clear();

        if (fxml == null) {
            contentArea.getChildren().add(new Label("⚠️ No se encontró /views/streamer.fxml"));
            return;
        }

        try {
            Parent view = FXMLLoader.load(fxml);
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().add(new Label("⚠️ Error al cargar la vista inicial Streamer"));
        }
    }

    // ====== MENÚ SUPERIOR ======
    @FXML
    private void accionSalir(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void accionAcercaDe(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Twitch Raids & Clips");
        alert.setContentText(
                "Aplicación para gestionar streamers, canales, raids y clips.\n\n"
                + "Versión 1.0\n"
                + "© Desarrollado por Soujirito"
        );
        alert.showAndWait();
    }

    @FXML
    private void accionVerLicencia(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Licencia");
        alert.setHeaderText("Información de la licencia");
        alert.setContentText(
                "LICENCIA DE USO DEL SOFTWARE\n\n"
                + "© Desarrollado por Soujirito\n\n"
                + "Este software es de código abierto y se distribuye de forma gratuita.\n\n"
                + "Se concede permiso a cualquier persona que obtenga una copia de este software y de los archivos de documentación asociados "
                + "para utilizarlo, copiarlo, modificarlo y distribuirlo con fines educativos, personales o comunitarios.\n\n"
                + "RESTRICCIONES:\n"
                + "- Prohibida la venta, alquiler o cualquier forma de comercialización.\n"
                + "- Redistribuciones deben incluir este aviso de licencia y la atribución al autor original.\n\n"
                + "EL SOFTWARE SE ENTREGA 'TAL CUAL', SIN GARANTÍAS DE NINGÚN TIPO."
        );
        alert.showAndWait();
    }

    @FXML
    private void accionRepositorio(ActionEvent event) {
        if (hostServices != null) {
            hostServices.showDocument("https://github.com/Milan3s/raids-and-clips");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo acceder a los servicios del sistema.");
            alert.showAndWait();
        }
    }
}
