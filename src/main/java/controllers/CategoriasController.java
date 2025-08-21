package controllers;

import dao.CategoriaDAO;
import models.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriasController implements Initializable {

    // Formato: yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblDescripcion;
    @FXML
    private Label lblCreadoEn;

    @FXML
    private TextField txtBuscar;
    @FXML
    private ListView<Categoria> listCategorias;
    @FXML
    private AnchorPane categoriasPane;
    @FXML
    private Button btnAcciones;

    private final CategoriaDAO dao = new CategoriaDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private Categoria categoriaActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCategorias();

        listCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                categoriaActual = newSel;
                mostrarCategoria(newSel);
            } else {
                limpiarDetalle();
            }
        });

        txtBuscar.setOnAction(this::accionBuscar);
    }

    /* ---------- Helpers ---------- */
    private void cargarCategorias() {
        categorias.setAll(dao.findAll());
        listCategorias.setItems(categorias);
    }

    private void mostrarCategoria(Categoria c) {
        lblNombre.setText(nullSafe(c.getNombre()));
        lblDescripcion.setText(nullSafe(c.getDescripcion()));
        lblCreadoEn.setText(formatFechaHora(c.getCreadoEn()));
    }

    private void limpiarDetalle() {
        lblNombre.setText("");
        lblDescripcion.setText("");
        lblCreadoEn.setText("");
        categoriaActual = null;
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String formatFechaHora(LocalDateTime dt) {
        return dt == null ? "" : DT_FMT.format(dt);
    }

    /* ---------- Acciones ---------- */
    @FXML
    private void accionAcciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cruds/form_categorias.fxml"));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Formulario Categorías");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(categoriasPane.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // refrescamos lista después de cerrar el formulario
            cargarCategorias();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al abrir el formulario de categorías").showAndWait();
        }
    }

    @FXML
    private void accionBuscar(ActionEvent event) {
        String term = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
        if (term.isEmpty()) {
            categorias.setAll(dao.findAll());
        } else {
            categorias.setAll(dao.searchByNombre(term));
        }
    }
}
