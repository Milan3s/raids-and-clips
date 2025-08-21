package controllers;

import dao.RaidDAO;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import models.Raid;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RaidsController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private ListView<Raid> listRaids;
    @FXML
    private Label lblStreamer;
    @FXML
    private Label lblCanal;
    @FXML
    private Label lblFecha;

    @FXML
    private Button btnRaid;
    @FXML
    private Label lblStreamerCreacion;
    @FXML
    private Label lblCanalCreador;
    @FXML
    private Label lblCanalCreacion;

    private final RaidDAO dao = new RaidDAO();
    private final ObservableList<Raid> data = FXCollections.observableArrayList();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarRaids();

        // listener para actualizar el detalle
        listRaids.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                mostrarDetalle(sel);
            } else {
                limpiarDetalle();
            }
        });

        // buscador simple
        txtBuscar.textProperty().addListener((obs, old, term) -> filtrar(term));
    }

    /* ---------- Carga de raids ---------- */
    private void cargarRaids() {
        List<Raid> lista = dao.findAll();
        data.setAll(lista);
        listRaids.setItems(data);
    }

    /* ---------- Detalle ---------- */
    private void mostrarDetalle(Raid r) {
        // Nombres amigables con fallback
        lblStreamer.setText((r.getStreamerNombre() != null) ? r.getStreamerNombre() : "Streamer " + r.getStreamerId());
        lblCanal.setText((r.getCanalNombre() != null) ? r.getCanalNombre() : "Canal " + r.getCanalId());
        lblFecha.setText(r.getFecha() != null ? r.getFecha().format(FMT) : "-");

        // Extra detalle si queremos mostrar otros campos
        lblStreamerCreacion.setText("Creado por: " + (r.getStreamerNombre() != null ? r.getStreamerNombre() : "-"));
        lblCanalCreador.setText("Canal destino: " + (r.getCanalNombre() != null ? r.getCanalNombre() : "-"));
        lblCanalCreacion.setText("Fecha raid: " + (r.getFecha() != null ? r.getFecha().format(FMT) : "-"));
    }

    private void limpiarDetalle() {
        lblStreamer.setText("");
        lblCanal.setText("");
        lblFecha.setText("");

        lblStreamerCreacion.setText("");
        lblCanalCreador.setText("");
        lblCanalCreacion.setText("");
    }

    /* ---------- Filtro ---------- */
    private void filtrar(String term) {
        if (term == null || term.isBlank()) {
            listRaids.setItems(data);
            return;
        }
        String lower = term.toLowerCase();
        ObservableList<Raid> filtrada = FXCollections.observableArrayList();
        for (Raid r : data) {
            if (r.toString().toLowerCase().contains(lower)) {
                filtrada.add(r);
            }
        }
        listRaids.setItems(filtrada);
    }

    /* ---------- Abrir formulario ---------- */
    @FXML
    private void accionRaid(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cruds/form_raid.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Raid");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);

            // Ventana padre
            Stage parentStage = (Stage) btnRaid.getScene().getWindow();
            stage.initOwner(parentStage);

            stage.showAndWait();

            // Recargar lista tras cerrar el form
            cargarRaids();

        } catch (IOException e) {
            System.err.println("Error al abrir el formulario de Raid: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
