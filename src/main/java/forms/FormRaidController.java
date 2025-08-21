package forms;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

public class FormRaidController implements Initializable {

    // --- FX Controls
    @FXML
    private ComboBox<Streamer> cbStreamer;
    @FXML
    private ComboBox<Canal> cbCanal;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private Button btnGuardar;
    @FXML
    private ComboBox<Raid> cbRaid;
    @FXML
    private TextField txtHora;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnLimpiar;

    // --- Datos (en memoria; sustituye por tu servicio/repositorio)
    private final ObservableList<Raid> raids = FXCollections.observableArrayList();
    private final ObservableList<Streamer> streamers = FXCollections.observableArrayList();
    private final ObservableList<Canal> canales = FXCollections.observableArrayList();

    private final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm[:ss]");
    @FXML
    private Label lblTitulo;
    @FXML
    private Button btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBoxes();
        cargarDatosIniciales(); // TODO: reemplaza por servicio real
        limpiarFormulario();
    }

    // ------------------ Configuración UI ------------------
    private void configurarComboBoxes() {
        // Formateo de nombres (reemplaza "_" por espacio)
        StringConverter<Streamer> streamerConv = new StringConverter<>() {
            @Override
            public String toString(Streamer s) {
                return s == null ? "" : formatNombre(s.nombre);
            }

            @Override
            public Streamer fromString(String s) {
                return null;
            }
        };
        StringConverter<Canal> canalConv = new StringConverter<>() {
            @Override
            public String toString(Canal c) {
                return c == null ? "" : formatNombre(c.nombre);
            }

            @Override
            public Canal fromString(String s) {
                return null;
            }
        };

        cbStreamer.setConverter(streamerConv);
        cbCanal.setConverter(canalConv);

        cbStreamer.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Streamer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatNombre(item.nombre));
            }
        });
        cbCanal.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Canal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatNombre(item.nombre));
            }
        });

        // --- Raid: SOLO "Streamer -> Canal" (sin #id ni fecha/hora)
        cbRaid.setConverter(new StringConverter<>() {
            @Override
            public String toString(Raid r) {
                return r == null ? "" : formatRaid(r);
            }

            @Override
            public Raid fromString(String s) {
                return null;
            }
        });
        cbRaid.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Raid item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatRaid(item));
            }
        });

        // Listas
        cbStreamer.setItems(streamers);
        cbCanal.setItems(canales);
        cbRaid.setItems(raids);

        // Cambio de selección de Raid
        cbRaid.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                cargarRaidEnFormulario(sel);
            }
        });
    }

    private String formatNombre(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.replace('_', ' ').replaceAll("\\s+", " ").trim();
        return s;
    }

    private String formatRaid(Raid r) {
        String streamer = r.streamer != null ? formatNombre(r.streamer.nombre) : "-";
        String canal = r.canal != null ? formatNombre(r.canal.nombre) : "-";
        return streamer + " -> " + canal;
    }

    private void cargarDatosIniciales() {
        // --- Ejemplos en memoria. Sustituye por tu capa de datos ---
        if (streamers.isEmpty()) {
            streamers.addAll(new Streamer(1, "Ibai"),
                    new Streamer(2, "Auronplay"),
                    new Streamer(3, "IlloJuan"));
        }
        if (canales.isEmpty()) {
            canales.addAll(new Canal(10, "canal_ibai"),
                    new Canal(20, "canal_auron"),
                    new Canal(30, "canal_illojuan"));
        }
        if (raids.isEmpty()) {
            raids.addAll(
                    new Raid(100L, streamers.get(0), canales.get(1), LocalDateTime.now().minusDays(1)),
                    new Raid(101L, streamers.get(1), canales.get(2), LocalDateTime.now().minusHours(5))
            );
        }
    }

    // ------------------ Acciones FXML ------------------
    @FXML
    private void accionCbRaid(ActionEvent event) {/* manejado por listener */
    }

    @FXML
    private void accionNuevo(ActionEvent event) {
        limpiarFormulario();
        cbRaid.getSelectionModel().clearSelection();
    }

    @FXML
    private void accionActualizar(ActionEvent event) {
        Raid seleccionado = cbRaid.getValue();
        if (seleccionado == null) {
            alertWarning("Selecciona una raid para actualizar.");
            return;
        }
        Optional<Raid> actualizado = construirRaidDesdeFormulario(seleccionado.id);
        if (actualizado.isEmpty()) {
            return;
        }

        Raid r = actualizado.get();
        int idx = raids.indexOf(seleccionado);
        raids.set(idx, r);
        cbRaid.getSelectionModel().select(r);
        alertInfo("Raid actualizada correctamente.");
    }

    @FXML
    private void accionBorrar(ActionEvent event) {
        Raid seleccionado = cbRaid.getValue();
        if (seleccionado == null) {
            alertWarning("Selecciona una raid para borrar.");
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar la raid seleccionada?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText("Confirmar borrado");
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                // TODO: borrar en base de datos
                raids.remove(seleccionado);
                limpiarFormulario();
                cbRaid.getSelectionModel().clearSelection();
                alertInfo("Raid borrada.");
            }
        });
    }

    @FXML
    private void accionGuardar(ActionEvent event) {
        Optional<Raid> nueva = construirRaidDesdeFormulario(null);
        if (nueva.isEmpty()) {
            return;
        }

        Raid r = nueva.get();
        // Si usas DB con AUTO_INCREMENT, deja r.id = null
        r.id = generarId(); // en memoria
        raids.add(r);
        cbRaid.getSelectionModel().select(r);
        alertInfo("Raid guardada correctamente.");
    }

    @FXML
    private void accionLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    // ------------------ Lógica de formulario ------------------
    private void cargarRaidEnFormulario(Raid r) {
        cbStreamer.getSelectionModel().select(r.streamer);
        cbCanal.getSelectionModel().select(r.canal);
        if (r.fechaHora != null) {
            dpFecha.setValue(r.fechaHora.toLocalDate());
            LocalTime t = r.fechaHora.toLocalTime();
            txtHora.setText(String.format("%02d:%02d:%02d", t.getHour(), t.getMinute(), t.getSecond()));
        } else {
            dpFecha.setValue(null);
            txtHora.clear();
        }
    }

    private void limpiarFormulario() {
        cbStreamer.getSelectionModel().clearSelection();
        cbCanal.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
        txtHora.setText("00:00:00");
    }

    private Optional<Raid> construirRaidDesdeFormulario(Long id) {
        Streamer s = cbStreamer.getValue();
        Canal c = cbCanal.getValue();
        LocalDate d = dpFecha.getValue();
        String horaTxt = txtHora.getText() == null ? "" : txtHora.getText().trim();

        if (s == null) {
            alertWarning("Selecciona un streamer.");
            return Optional.empty();
        }
        if (c == null) {
            alertWarning("Selecciona un canal.");
            return Optional.empty();
        }
        if (d == null) {
            alertWarning("Selecciona una fecha.");
            return Optional.empty();
        }

        LocalTime t;
        try {
            t = LocalTime.parse(horaTxt.isEmpty() ? "00:00:00" : horaTxt, TIME_FMT);
        } catch (DateTimeParseException ex) {
            alertError("Hora inválida. Usa formato HH:mm o HH:mm:ss.");
            return Optional.empty();
        }

        LocalDateTime dt = LocalDateTime.of(d, t);
        return Optional.of(new Raid(id, s, c, dt));
    }

    private long generarId() {
        // Numeración simple consecutiva en memoria; si usas DB, delega al AUTO_INCREMENT
        long max = raids.stream().mapToLong(r -> r.id == null ? 0L : r.id).max().orElse(0L);
        return max + 1;
    }

    // ------------------ Helpers UI ------------------
    private void alertInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    @FXML
    private void accionCancelar(ActionEvent event) {
    }

    // ------------------ DTOs de ejemplo ------------------
    public static class Streamer {

        public final long id;
        public final String nombre;

        public Streamer(long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    public static class Canal {

        public final long id;
        public final String nombre;

        public Canal(long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    public static class Raid {

        public Long id;
        public Streamer streamer;
        public Canal canal;
        public LocalDateTime fechaHora;

        public Raid(Long id, Streamer streamer, Canal canal, LocalDateTime fechaHora) {
            this.id = id;
            this.streamer = streamer;
            this.canal = canal;
            this.fechaHora = fechaHora;
        }

        @Override
        public String toString() {
            return "Raid{id=" + id + "}";
        }
    }
}
