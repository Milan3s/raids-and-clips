package forms;

import dao.CategoriaDAO;
import models.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para el formulario de Categorías (CRUD).
 *
 * @author Milanes
 */
public class FormCategoriasController implements Initializable {

    @FXML
    private ComboBox<Categoria> cbCategoria;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private DatePicker dpFechaCreacion;
    @FXML
    private TextField txtHoraCreacion;
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

    private final CategoriaDAO dao = new CategoriaDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private Categoria actual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Poblar ComboBox con categorías
        categorias.setAll(dao.findAll());
        cbCategoria.setItems(categorias);

        // Mostrar solo el nombre en el ComboBox
        cbCategoria.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cbCategoria.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        limpiar();
        setModoEdicion(false);
    }

    /* ---------- Helpers ---------- */
    private void mostrar(Categoria c) {
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion() != null ? c.getDescripcion() : "");
        if (c.getCreadoEn() != null) {
            dpFechaCreacion.setValue(c.getCreadoEn().toLocalDate());
            txtHoraCreacion.setText(c.getCreadoEn().toLocalTime().toString());
        } else {
            dpFechaCreacion.setValue(null);
            txtHoraCreacion.clear();
        }
    }

    private Categoria recogerFormulario() {
        Categoria c = actual != null ? actual : new Categoria();
        c.setNombre(txtNombre.getText().trim());
        c.setDescripcion(txtDescripcion.getText().trim());

        if (dpFechaCreacion.getValue() != null) {
            LocalTime hora = LocalTime.MIDNIGHT;
            if (!txtHoraCreacion.getText().isBlank()) {
                try {
                    hora = LocalTime.parse(txtHoraCreacion.getText().trim());
                } catch (Exception ignored) {
                }
            }
            c.setCreadoEn(LocalDateTime.of(dpFechaCreacion.getValue(), hora));
        } else {
            c.setCreadoEn(null);
        }
        return c;
    }

    private void limpiar() {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFechaCreacion.setValue(null);
        txtHoraCreacion.clear();
        actual = null;
        cbCategoria.getSelectionModel().clearSelection();
        setModoEdicion(false);
    }

    private void setModoEdicion(boolean editando) {
        btnActualizar.setDisable(!editando);
        btnBorrar.setDisable(!editando);
        btnGuardar.setDisable(editando);
    }

    /* ---------- Acciones ---------- */
    @FXML
    private void accionCbCategoria(ActionEvent event) {
        Categoria seleccionada = cbCategoria.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            actual = seleccionada;
            mostrar(seleccionada);
            setModoEdicion(true);
        } else {
            limpiar();
        }
    }

    @FXML
    private void accionNuevo(ActionEvent event) {
        limpiar();
    }

    @FXML
    private void accionActualizar(ActionEvent event) {
        if (actual != null) {
            Categoria c = recogerFormulario();
            if (dao.update(c)) {
                int idx = categorias.indexOf(actual);
                if (idx >= 0) {
                    categorias.set(idx, c);
                }
                cbCategoria.getSelectionModel().select(c);
                new Alert(Alert.AlertType.INFORMATION, "Categoría actualizada.", ButtonType.OK).showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Error al actualizar.", ButtonType.OK).showAndWait();
            }
        }
    }

    @FXML
    private void accionBorrar(ActionEvent event) {
        if (actual != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas borrar la categoría?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                if (dao.delete(actual.getId())) {
                    categorias.remove(actual);
                    limpiar();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error al borrar.", ButtonType.OK).showAndWait();
                }
            }
        }
    }

    @FXML
    private void accionGuardar(ActionEvent event) {
        Categoria c = recogerFormulario();
        int id = dao.insert(c);
        if (id > 0) {
            c.setId(id);
            categorias.add(c);
            cbCategoria.getSelectionModel().select(c);
            new Alert(Alert.AlertType.INFORMATION, "Categoría guardada.", ButtonType.OK).showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error al guardar categoría.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void accionLimpiar(ActionEvent event) {
        limpiar();
    }
}
