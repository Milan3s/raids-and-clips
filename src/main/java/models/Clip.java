package models;

import java.time.LocalDateTime;

/**
 * Modelo para la tabla clips
 *
 * @author Milanes
 */
public class Clip {

    private int id;
    private int streamerId;
    private String titulo;
    private String url;
    private LocalDateTime fecha;

    public Clip() {
    }

    public Clip(int id, int streamerId, String titulo, String url, LocalDateTime fecha) {
        this.id = id;
        this.streamerId = streamerId;
        this.titulo = titulo;
        this.url = url;
        this.fecha = fecha;
    }

    /* -------- Getters y Setters -------- */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(int streamerId) {
        this.streamerId = streamerId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /* -------- Utilidad -------- */
    @Override
    public String toString() {
        return titulo + " (" + fecha + ")";
    }
}
