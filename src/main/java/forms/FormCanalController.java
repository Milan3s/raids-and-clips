package forms;

import dao.CanalDAO;
import models.Canal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.stage.Stage;

/**
 * Controlador del formulario de Canal Gestiona CRUD sobre la tabla canales
 *
 * @author Milanes
 */
public class FormCanalController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCreador;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtHora;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private ComboBox<Canal> cbCanal;

    private final CanalDAO dao = new CanalDAO();
    private final ObservableList<Canal> canales = FXCollections.observableArrayList();
    private Canal canalActual;

    // 🔹 Formatos de fecha/hora
    private final DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    @FXML
    private Label lblTitulo;
    @FXML
    private Button btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCanales();

        /* --- Mostrar solo el nombre en el ComboBox --- */
        cbCanal.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Canal item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getNombre());
            }
        });
        cbCanal.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Canal item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getNombre());
            }
        });
    }

    /* ----------------- Helpers ----------------- */
    private void cargarCanales() {
        canales.setAll(dao.findAll());
        cbCanal.setItems(canales);
    }

    private void mostrarCanal(Canal c) {
        txtNombre.setText(c.getNombre());
        txtCreador.setText(c.getCreador());
        if (c.getFecha() != null) {
            dpFecha.setValue(c.getFecha().toLocalDate());
            txtHora.setText(c.getFecha().toLocalTime().format(formatterHora));
        } else {
            dpFecha.setValue(null);
            txtHora.clear();
        }
    }

    private Canal recogerFormulario() {
        String nombre = txtNombre.getText().trim();
        String creador = txtCreador.getText().trim();
        LocalDate fecha = dpFecha.getValue();
        LocalTime hora = txtHora.getText().isEmpty()
                ? LocalTime.MIDNIGHT
                : LocalTime.parse(txtHora.getText(), formatterHora);
        LocalDateTime fechaHora = (fecha != null) ? LocalDateTime.of(fecha, hora) : null;

        Canal c = new Canal();
        if (canalActual != null) {
            c.setId(canalActual.getId()); // conservar ID si es edición
        }
        c.setNombre(nombre);
        c.setCreador(creador);
        c.setFecha(fechaHora);
        return c;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtCreador.clear();
        dpFecha.setValue(null);
        txtHora.clear();
        cbCanal.getSelectionModel().clearSelection();
        canalActual = null;
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    /* ----------------- Acciones ----------------- */
    @FXML
    private void accionNuevo(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    private void accionActualizar(ActionEvent event) {
        if (canalActual != null) {
            Canal c = recogerFormulario();
            boolean ok = dao.update(c);
            if (ok) {
                cargarCanales();
                cbCanal.getSelectionModel().select(c);
                mostrarInfo("Canal actualizado.");
            } else {
                mostrarError("Error al actualizar canal.");
            }
        } else {
            mostrarError("Selecciona un canal primero.");
        }
    }

    @FXML
    private void accionBorrar(ActionEvent event) {
        if (canalActual != null) {
            boolean ok = dao.delete(canalActual.getId());
            if (ok) {
                cargarCanales();
                limpiarFormulario();
                mostrarInfo("Canal eliminado.");
            } else {
                mostrarError("Error al eliminar canal.");
            }
        } else {
            mostrarError("Selecciona un canal primero.");
        }
    }

    @FXML
    private void accionGuardar(ActionEvent event) {
        Canal c = recogerFormulario();
        boolean ok = dao.insert(c);
        if (ok) {
            cargarCanales();
            cbCanal.getSelectionModel().select(c);
            mostrarInfo("Canal guardado.");
        } else {
            mostrarError("Error al guardar canal.");
        }
    }

    @FXML
    private void accionCbCanal(ActionEvent event) {
        canalActual = cbCanal.getSelectionModel().getSelectedItem();
        if (canalActual != null) {
            mostrarCanal(canalActual);
        }
    }

    @FXML
    private void accionLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    private void accionCancelar(ActionEvent event) {
        // Cierra la ventana actual
        Button btn = (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

}
