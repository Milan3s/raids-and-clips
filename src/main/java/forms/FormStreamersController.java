package forms;

import dao.StreamerDAO;
import models.Streamer;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FormStreamersController implements Initializable {

    /* ==== FXML ==== */
    @FXML
    private TextField txtNombre;
    @FXML
    private DatePicker dpFechaCreacion;
    @FXML
    private TextField txtHoraCreacion;

    @FXML
    private RadioButton rbActivoSi;
    @FXML
    private RadioButton rbActivoNo;

    @FXML
    private ComboBox<Streamer> cbStreamer;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    /* ==== Estado/control ==== */
    private final StreamerDAO dao = new StreamerDAO();
    private final ObservableList<Streamer> data = FXCollections.observableArrayList();
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    @FXML
    private Button btnLimpiar;
    @FXML
    private Label lblTitulo;
    @FXML
    private ToggleGroup grupoActivo;

    /* ================= Ciclo de vida ================= */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombo();
        cargarStreamers();

        // Valores por defecto del formulario
        rbActivoSi.setSelected(true);
        if (dpFechaCreacion.getValue() == null) {
            dpFechaCreacion.setValue(LocalDate.now());
        }
        if (txtHoraCreacion.getText() == null || txtHoraCreacion.getText().isBlank()) {
            txtHoraCreacion.setText(LocalTime.now().format(HORA));
        }

        actualizarEstadoBotones();
    }

    /* ================= Wiring UI ================= */
    private void configurarCombo() {
        // Cómo mostrar Streamer en ComboBox
        cbStreamer.setConverter(new StringConverter<>() {
            @Override
            public String toString(Streamer s) {
                return s == null ? "" : s.getNombre();
            }

            @Override
            public Streamer fromString(String string) {
                return null;
            }
        });
        cbStreamer.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Streamer s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getNombre());
            }
        });

        // Listener de selección
        cbStreamer.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                setForm(sel);
            }
            actualizarEstadoBotones();
        });
    }

    private void cargarStreamers() {
        data.clear();
        List<Streamer> lista = dao.findAll();
        data.addAll(lista);
        cbStreamer.setItems(data);
        cbStreamer.getSelectionModel().clearSelection();
        limpiarForm(); // Modo "nuevo" por defecto
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = cbStreamer.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(!haySeleccion);
        btnBorrar.setDisable(!haySeleccion);
        // Guardar siempre disponible para crear uno nuevo
        btnGuardar.setDisable(false);
    }

    /* =================== Handlers FXML =================== */
    @FXML
    private void accionCbStreamer(ActionEvent event) {
        // Por si lo invocas desde FXML además del listener
        Streamer s = cbStreamer.getSelectionModel().getSelectedItem();
        if (s != null) {
            setForm(s);
        }
        actualizarEstadoBotones();
    }

    @FXML
    private void accionNuevo(ActionEvent event) {
        cbStreamer.getSelectionModel().clearSelection();
        limpiarForm();
        txtNombre.requestFocus();
        actualizarEstadoBotones();
    }

    @FXML
    private void accionActualizar(ActionEvent event) {
        Streamer sel = cbStreamer.getSelectionModel().getSelectedItem();
        if (sel == null) {
            warn("Selecciona un streamer para actualizar.");
            return;
        }

        Streamer s = leerFormSinId();
        s.setId(sel.getId());

        if (!validar(s)) {
            return;
        }

        boolean ok = dao.update(s);
        if (ok) {
            info("Actualizado", "Los datos se han actualizado correctamente.");
            cargarStreamers();
            seleccionarPorId(s.getId());
        } else {
            error("No se pudo actualizar el streamer.");
        }
    }

    @FXML
    private void accionBorrar(ActionEvent event) {
        Streamer sel = cbStreamer.getSelectionModel().getSelectedItem();
        if (sel == null) {
            warn("Selecciona un streamer para borrar.");
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar borrado");
        conf.setHeaderText("¿Borrar streamer?");
        conf.setContentText("Se eliminará: " + sel.getNombre());
        conf.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = dao.delete(sel.getId());
                if (ok) {
                    info("Eliminado", "Streamer borrado correctamente.");
                    cargarStreamers();
                    accionNuevo(null);
                } else {
                    error("No se pudo borrar el streamer.");
                }
            }
        });
    }

    @FXML
    private void accionGuardar(ActionEvent event) {
        // Crea un nuevo streamer
        Streamer s = leerFormSinId();
        if (!validar(s)) {
            return;
        }

        int id = dao.insert(s);
        if (id > 0) {
            info("Streamer creado", "Se creó el streamer con id: " + id);
            cargarStreamers();      // recarga lista
            accionNuevo(null);      // deja el formulario listo para otro alta
        } else {
            error("No se pudo crear el streamer.");
        }
    }

    @FXML
    private void accionCancelar(ActionEvent event) {
        // Obtiene el Stage actual desde cualquier nodo del formulario
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }


    /* =================== Helpers de formulario =================== */
    private void setForm(Streamer s) {
        txtNombre.setText(s.getNombre());
        if (s.isEstaActivo()) {
            rbActivoSi.setSelected(true);
        } else {
            rbActivoNo.setSelected(true);
        }

        if (s.getFechaCreacion() != null) {
            dpFechaCreacion.setValue(s.getFechaCreacion().toLocalDate());
            txtHoraCreacion.setText(s.getFechaCreacion().toLocalTime().format(HORA));
        } else {
            dpFechaCreacion.setValue(null);
            txtHoraCreacion.clear();
        }
    }

    private void limpiarForm() {
        txtNombre.clear();
        rbActivoSi.setSelected(true);
        dpFechaCreacion.setValue(LocalDate.now());
        txtHoraCreacion.setText(LocalTime.now().format(HORA));
    }

    private Streamer leerFormSinId() {
        Streamer s = new Streamer();
        s.setNombre(txtNombre.getText() != null ? txtNombre.getText().trim() : "");
        s.setEstaActivo(rbActivoSi.isSelected());
        s.setFechaCreacion(parseFechaHora(dpFechaCreacion.getValue(), txtHoraCreacion.getText()));
        return s;
    }

    private boolean validar(Streamer s) {
        if (s.getNombre() == null || s.getNombre().isBlank()) {
            warn("El nombre es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }
        // Si fecha/hora es obligatoria en BD, valida aquí. Ahora es opcional.
        return true;
    }

    private LocalDateTime parseFechaHora(LocalDate fecha, String horaStr) {
        if (fecha == null) {
            return null; // deja que la BD ponga CURRENT_TIMESTAMP si procede
        }
        LocalTime hora;
        try {
            hora = (horaStr == null || horaStr.isBlank())
                    ? LocalTime.MIDNIGHT
                    : LocalTime.parse(horaStr.trim(), HORA);
        } catch (Exception e) {
            hora = LocalTime.MIDNIGHT;
        }
        return LocalDateTime.of(fecha, hora);
    }

    private void seleccionarPorId(int id) {
        for (Streamer s : data) {
            if (s.getId() == id) {
                cbStreamer.getSelectionModel().select(s);
                break;
            }
        }
    }

    /* =================== Alerts util =================== */
    private void info(String header, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información");
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }

    private void warn(String content) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Aviso");
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private void error(String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    @FXML
    private void accionLimpiar(ActionEvent event) {
        // Limpia todos los campos del formulario
        txtNombre.clear();
        rbActivoSi.setSelected(true);   // por defecto Activo = Sí
        rbActivoNo.setSelected(false);

        dpFechaCreacion.setValue(LocalDate.now());
        txtHoraCreacion.setText(LocalTime.now().format(HORA));

        // Quitar selección del ComboBox
        cbStreamer.getSelectionModel().clearSelection();

        // Estado botones → modo nuevo
        actualizarEstadoBotones();
    }

}
