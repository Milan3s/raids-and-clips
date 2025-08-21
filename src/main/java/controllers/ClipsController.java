package controllers;

import dao.ClipDAO;
import models.Clip;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipsController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private Hyperlink lblUrl;    // clickable (sin Desktop)
    @FXML
    private Label lblFecha;
    @FXML
    private Button btnAccion;
    @FXML
    private ListView<Clip> listClips;
    @FXML
    private TextField txtBuscar;

    private final ClipDAO dao = new ClipDAO();
    private final ObservableList<Clip> data = FXCollections.observableArrayList();
    private Clip clipActual;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar datos
        cargarClips();

        // Cómo se ve cada fila del ListView
        listClips.setCellFactory(lv -> new ListCell<Clip>() {
            @Override
            protected void updateItem(Clip item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Solo título, limpio y compacto
                    setText(item.getTitulo());
                }
            }
        });

        // Al seleccionar, mostramos detalle
        listClips.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                clipActual = sel;
                mostrarClip(sel);
            }
        });

        // Hacer el hyperlink "clicable" sin abrir navegador (evita Desktop)
        lblUrl.setOnAction(e -> {
            String urlTxt = lblUrl.getText();
            if (urlTxt != null && !urlTxt.isBlank()) {
                // Ejemplo: copiar al portapapeles y avisar
                final ClipboardContent content = new ClipboardContent();
                content.putString(urlTxt);
                Clipboard.getSystemClipboard().setContent(content);
                new Alert(Alert.AlertType.INFORMATION, "URL copiada al portapapeles:\n" + urlTxt, ButtonType.OK).showAndWait();
            }
        });

        // Buscar al pulsar ENTER en el buscador
        txtBuscar.setOnAction(e -> accionBuscar());
    }

    /* ---------- Helpers ---------- */
    private void cargarClips() {
        data.clear();
        List<Clip> lista = dao.findAll();
        data.addAll(lista);
        listClips.setItems(data);

        // Si hay datos, seleccionar el primero
        if (!data.isEmpty()) {
            listClips.getSelectionModel().selectFirst();
        } else {
            limpiarDetalle();
        }
    }

    private void mostrarClip(Clip clip) {
        lblTitulo.setText(clip.getTitulo() != null ? clip.getTitulo() : "");
        lblUrl.setText(clip.getUrl() != null ? clip.getUrl() : "");
        lblUrl.setVisited(false); // para que se vea como "no visitado" cada vez
        if (clip.getFecha() != null) {
            lblFecha.setText(clip.getFecha().format(formatter));
        } else {
            lblFecha.setText("");
        }
    }

    private void limpiarDetalle() {
        clipActual = null;
        lblTitulo.setText("");
        lblUrl.setText("");
        lblFecha.setText("");
    }

    private void accionBuscar() {
        String term = txtBuscar.getText() != null ? txtBuscar.getText().trim() : "";
        data.clear();
        if (term.isEmpty()) {
            data.addAll(dao.findAll());
        } else {
            data.addAll(dao.searchByTitulo(term));
        }
        if (!data.isEmpty()) {
            listClips.getSelectionModel().selectFirst();
        } else {
            limpiarDetalle();
        }
    }

    /* ---------- Acciones ---------- */
    @FXML
    private void accionAcciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cruds/form_clips.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Gestión de Clips");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Al volver, refrescar listado (por si hubo cambios)
            cargarClips();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir form_clips.fxml:\n" + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }
}
