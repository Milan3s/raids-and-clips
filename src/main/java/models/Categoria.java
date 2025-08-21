package models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * Modelo JavaFX para la tabla categorias: - id INT PK - nombre VARCHAR(100) NOT
 * NULL UNIQUE - descripcion VARCHAR(255) NULL - creado_en TIMESTAMP NOT NULL
 * (DEFAULT CURRENT_TIMESTAMP)
 */
public class Categoria {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty nombre = new SimpleStringProperty(this, "nombre");
    private final StringProperty descripcion = new SimpleStringProperty(this, "descripcion");
    private final ObjectProperty<LocalDateTime> creadoEn
            = new SimpleObjectProperty<>(this, "creadoEn");

    // --- Constructores ---
    public Categoria() {
    }

    public Categoria(int id, String nombre) {
        setId(id);
        setNombre(nombre);
    }

    public Categoria(int id, String nombre, String descripcion, LocalDateTime creadoEn) {
        setId(id);
        setNombre(nombre);
        setDescripcion(descripcion);
        setCreadoEn(creadoEn);
    }

    // --- id ---
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // --- nombre ---
    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String value) {
        nombre.set(value);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    // --- descripcion ---
    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String value) {
        descripcion.set(value);
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    // --- creadoEn ---
    public LocalDateTime getCreadoEn() {
        return creadoEn.get();
    }

    public void setCreadoEn(LocalDateTime value) {
        creadoEn.set(value);
    }

    public ObjectProperty<LocalDateTime> creadoEnProperty() {
        return creadoEn;
    }

    // --- Para ListView/ComboBox ---
    @Override
    public String toString() {
        return getNombre();
    }
}
