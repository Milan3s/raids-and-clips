package forms;

import dao.ClipDAO;
import dao.StreamerDAO;   // 🔹 nuevo DAO para streamers
import models.Clip;
import models.Streamer;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

/**
 * Controlador para formulario Clips
 *
 * @author Milanes
 */
public class FormClipsController implements Initializable {

    @FXML
    private ComboBox<Clip> cbClip;
    @FXML
    private ComboBox<Streamer> cbStreamer;  // ✅ ahora es modelo real
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtUrl;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtHora;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnLimpiar;

    private final ClipDAO clipDAO = new ClipDAO();
    private final StreamerDAO streamerDAO = new StreamerDAO();

    private final DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Clip clipActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarClips();
        cargarStreamers();

        /* --- Mostrar solo el título en el ComboBox --- */
        cbClip.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Clip item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitulo());
            }
        });
        cbClip.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Clip item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitulo());
            }
        });
    }

    /* ---------------- CRUD ---------------- */
    @FXML
    private void accionNuevo(ActionEvent event) {
        limpiarFormulario();
        clipActual = null;
    }

    @FXML
    private void accionGuardar(ActionEvent event) {
        try {
            Streamer streamer = cbStreamer.getSelectionModel().getSelectedItem();
            int streamerId = (streamer != null) ? streamer.getId() : 0;

            String titulo = txtTitulo.getText();
            String url = txtUrl.getText();
            LocalDate fecha = dpFecha.getValue();
            LocalTime hora = LocalTime.parse(txtHora.getText(), formatterHora);
            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

            if (clipActual == null) {
                // Insertar
                Clip nuevo = new Clip(0, streamerId, titulo, url, fechaHora);
                int id = clipDAO.insert(nuevo);
                if (id > 0) {
                    mostrarInfo("Clip guardado correctamente.");
                    cargarClips();
                    cbClip.getSelectionModel().select(nuevo);
                } else {
                    mostrarError("Error al guardar el clip.");
                }
            } else {
                // Actualizar
                clipActual.setStreamerId(streamerId);
                clipActual.setTitulo(titulo);
                clipActual.setUrl(url);
                clipActual.setFecha(fechaHora);
                if (clipDAO.update(clipActual)) {
                    mostrarInfo("Clip actualizado correctamente.");
                    cargarClips();
                    cbClip.getSelectionModel().select(clipActual);
                } else {
                    mostrarError("Error al actualizar el clip.");
                }
            }
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void accionActualizar(ActionEvent event) {
        if (clipActual != null) {
            cargarFormulario(clipActual);
        } else {
            mostrarError("Selecciona un clip primero.");
        }
    }

    @FXML
    private void accionBorrar(ActionEvent event) {
        if (clipActual != null) {
            if (clipDAO.delete(clipActual.getId())) {
                mostrarInfo("Clip eliminado.");
                cargarClips();
                limpiarFormulario();
            } else {
                mostrarError("No se pudo eliminar el clip.");
            }
        } else {
            mostrarError("Selecciona un clip primero.");
        }
    }

    @FXML
    private void accionCbClip(ActionEvent event) {
        clipActual = cbClip.getSelectionModel().getSelectedItem();
        if (clipActual != null) {
            cargarFormulario(clipActual);
        }
    }

    @FXML
    private void accionLimpiar(ActionEvent event) {
        limpiarFormulario();
        clipActual = null;
        cbClip.getSelectionModel().clearSelection();
    }

    /* ---------------- Utilidades ---------------- */
    private void cargarClips() {
        ObservableList<Clip> lista = FXCollections.observableArrayList(clipDAO.findAll());
        cbClip.setItems(lista);
    }

    private void cargarStreamers() {
        ObservableList<Streamer> lista = FXCollections.observableArrayList(streamerDAO.findAll());
        cbStreamer.setItems(lista);
    }

    private void limpiarFormulario() {
        cbStreamer.getSelectionModel().clearSelection();
        txtTitulo.clear();
        txtUrl.clear();
        dpFecha.setValue(null);
        txtHora.clear();
    }

    private void cargarFormulario(Clip clip) {
        // 🔹 buscar el streamer en el combo
        cbStreamer.getItems().stream()
                .filter(s -> s.getId() == clip.getStreamerId())
                .findFirst()
                .ifPresent(s -> cbStreamer.getSelectionModel().select(s));

        txtTitulo.setText(clip.getTitulo());
        txtUrl.setText(clip.getUrl());
        if (clip.getFecha() != null) {
            dpFecha.setValue(clip.getFecha().toLocalDate());
            txtHora.setText(clip.getFecha().toLocalTime().format(formatterHora));
        }
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
