package controllers;

import dao.StreamerDAO;
import models.Streamer;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class StreamersController implements Initializable {

    /* --------- UI del listado/buscador --------- */
    @FXML
    private ListView<Streamer> listStreamers;   // <--- Asegúrate del fx:id en FXML
    @FXML
    private TextField txtBuscar;                // <--- Asegúrate del fx:id en FXML

    /* --------- Botones --------- */
    @FXML
    private Button btnFormStreamer;

    /* --------- Panel de detalle --------- */
    @FXML
    private Label lblNombre;
    private Label lblNombre1;  // si no lo usas, puedes quitarlo del FXML y del controlador
    private Label lblNombre2;  // ídem
    @FXML
    private Label lblCreador;  // aquí mostramos “Sí/No” (activo)
    @FXML
    private Label lblFecha;

    /* --------- Estado interno --------- */
    private final StreamerDAO dao = new StreamerDAO();
    private final ObservableList<Streamer> data = FXCollections.observableArrayList();
    private FilteredList<Streamer> filtered;
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @FXML
    private Label sms_conexiondb;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListView();
        configurarBuscador();
        cargarStreamersAsync();
    }

    /* ================= Configuración UI ================= */
    private void configurarListView() {
        // Mostrar solo el nombre en la lista
        listStreamers.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Streamer s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getNombre());
            }
        });

        // Al seleccionar, mostramos detalle
        listStreamers.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                mostrarDetalle(sel);
            }
        });
    }

    private void configurarBuscador() {
        filtered = new FilteredList<>(data, s -> true);
        listStreamers.setItems(filtered);

        // Filtro en tiempo real
        txtBuscar.textProperty().addListener((obs, old, text) -> {
            final String t = text == null ? "" : text.trim().toLowerCase();
            filtered.setPredicate(s -> t.isEmpty() || s.getNombre().toLowerCase().contains(t));
        });
    }

    /* ================= Carga de datos ================= */
    private void cargarStreamersAsync() {
        listStreamers.setPlaceholder(new Label("Cargando..."));
        Task<List<Streamer>> task = new Task<>() {
            @Override
            protected List<Streamer> call() {
                return dao.findAll();
            }
        };
        task.setOnSucceeded(e -> {
            data.setAll(task.getValue());
            if (data.isEmpty()) {
                listStreamers.setPlaceholder(new Label("No hay streamers"));
                limpiarDetalle();
            } else {
                listStreamers.getSelectionModel().selectFirst();
            }
        });
        task.setOnFailed(e -> {
            listStreamers.setPlaceholder(new Label("Error al cargar streamers"));
            limpiarDetalle();
        });
        new Thread(task, "load-streamers").start();
    }

    /* ================= Detalle ================= */
    private void mostrarDetalle(Streamer s) {
        if (s == null) {
            limpiarDetalle();
            return;
        }
        lblNombre.setText(s.getNombre() == null ? "" : s.getNombre());
        // lblNombre1 / lblNombre2 si los quieres usar para info adicional
        lblCreador.setText(s.isEstaActivo() ? "Sí" : "No");
        lblFecha.setText(
                s.getFechaCreacion() == null ? "" : s.getFechaCreacion().format(FECHA_FMT)
        );
    }

    private void limpiarDetalle() {
        lblNombre.setText("");
        if (lblNombre1 != null) {
            lblNombre1.setText("");
        }
        if (lblNombre2 != null) {
            lblNombre2.setText("");
        }
        lblCreador.setText("");
        lblFecha.setText("");
    }

    /* ================= Abrir formulario ================= */
    @FXML
    private void accionFormStreamer(ActionEvent event) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();
        abrirFormStreamer("Formulario de Streamer", owner);
    }

    private void accionNuevo(ActionEvent event) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();
        abrirFormStreamer("Nuevo streamer", owner);
    }

    private void abrirFormStreamer(String titulo, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cruds/form_streamers.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.setTitle(titulo);
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.showAndWait();

            // Al cerrar el formulario, recarga la lista aplicando el filtro actual
            String filtroActual = txtBuscar.getText();
            cargarStreamersAsync();
            txtBuscar.setText(filtroActual); // re-aplica el filtro

        } catch (IOException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error al abrir el formulario");
            a.setHeaderText("No se pudo cargar el formulario de streamer");
            a.setContentText(ex.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void accionBuscar(KeyEvent event) {
        final String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        filtered.setPredicate(s -> texto.isEmpty() || s.getNombre().toLowerCase().contains(texto));
    }

}
