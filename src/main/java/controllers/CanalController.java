package controllers;

import dao.CanalDAO;
import models.Canal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de Canales. Gestiona CRUD con la tabla canales.
 *
 * @author Milanes
 */
public class CanalController implements Initializable {

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblDescripcion;  // Aquí mostramos el "creador"
    @FXML
    private Label lblFecha;        // ahora correctamente enlazado
    @FXML
    private TextField txtBuscar;
    @FXML
    private ListView<Canal> listCanales;
    @FXML
    private Button btnAccion;
    @FXML
    private AnchorPane canalPane;  // enlazado al AnchorPane raíz

    private final CanalDAO dao = new CanalDAO();
    private final ObservableList<Canal> canales = FXCollections.observableArrayList();
    private Canal canalActual;

    // 👉 Formato fecha + hora con espacio en medio
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCanales();

        listCanales.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                canalActual = newSel;
                mostrarCanal(newSel);
            }
        });
    }

    /* ---------- Helpers ---------- */
    private void cargarCanales() {
        canales.clear();
        List<Canal> lista = dao.findAll();
        canales.addAll(lista);
        listCanales.setItems(canales);
    }

    private void mostrarCanal(Canal c) {
        lblNombre.setText(c.getNombre());
        lblDescripcion.setText(c.getCreador() != null ? c.getCreador() : "");
        lblFecha.setText(c.getFecha() != null ? c.getFecha().format(dtf) : "");
    }

    /* ---------- Acciones ---------- */
    @FXML
    private void accionAcciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cruds/form_canal.fxml") // verifica la ruta
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestión de Canal");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(canalPane.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // refrescar al cerrar
            cargarCanales();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir el formulario de canal.",
                    ButtonType.OK
            ).showAndWait();
        }
    }

    @FXML
    private void accionBuscar(ActionEvent event) {
        String term = txtBuscar.getText().trim();
        canales.clear();
        if (term.isEmpty()) {
            canales.addAll(dao.findAll());
        } else {
            canales.addAll(dao.searchByNombre(term));
        }
    }
}
