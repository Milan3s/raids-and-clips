package models;

import java.time.LocalDateTime;

/**
 * Modelo Canal Representa un canal de Twitch.
 *
 * @author Milanes
 */
public class Canal {

    private int id;
    private String nombre;
    private String creador;
    private LocalDateTime fecha;

    // Constructores
    public Canal() {
    }

    public Canal(int id, String nombre, String creador, LocalDateTime fecha) {
        this.id = id;
        this.nombre = nombre;
        this.creador = creador;
        this.fecha = fecha;
    }

    public Canal(String nombre, String creador, LocalDateTime fecha) {
        this.nombre = nombre;
        this.creador = creador;
        this.fecha = fecha;
    }

    // Getters y setters
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

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // Para mostrar en ListView / ComboBox
    @Override
    public String toString() {
        return nombre + " (" + creador + ")";
    }
}
