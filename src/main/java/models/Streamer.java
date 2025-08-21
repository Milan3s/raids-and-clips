package models;

import java.time.LocalDateTime;

/**
 * Modelo para los Streamers Representa la tabla streamer (id, nombre,
 * esta_activo, fecha_creacion)
 *
 * Campos BD: - id INT PK - nombre VARCHAR(100) - esta_activo TINYINT(1)
 * (1=activo, 0=inactivo) - fecha_creacion TIMESTAMP (DEFAULT CURRENT_TIMESTAMP)
 *
 * @author Milanes
 */
public class Streamer {

    private int id;
    private String nombre;
    private boolean estaActivo;
    private LocalDateTime fechaCreacion;

    // --- Constructores ---
    public Streamer() {
    }

    public Streamer(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Streamer(int id, String nombre, boolean estaActivo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.estaActivo = estaActivo;
        this.fechaCreacion = fechaCreacion;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // --- Métodos auxiliares ---
    @Override
    public String toString() {
        return nombre; // mantenemos solo el nombre para ListView/ComboBox
    }
}
