package models;

import java.time.LocalDateTime;

/**
 * Modelo para la tabla "raids".
 * Representa que un Streamer hizo un raid a un Canal en una fecha concreta.
 *
 * @author Milanes
 */
public class Raid {
    private int id;
    private int streamerId;
    private int canalId;
    private LocalDateTime fecha;

    // Para mostrar nombres directamente (rellenados desde JOIN en el DAO)
    private String streamerNombre;
    private String canalNombre;

    /* ==== Constructores ==== */
    public Raid() {
    }

    public Raid(int id, int streamerId, int canalId, LocalDateTime fecha) {
        this.id = id;
        this.streamerId = streamerId;
        this.canalId = canalId;
        this.fecha = fecha;
    }

    public Raid(int id, int streamerId, int canalId, LocalDateTime fecha,
                String streamerNombre, String canalNombre) {
        this(id, streamerId, canalId, fecha);
        this.streamerNombre = streamerNombre;
        this.canalNombre = canalNombre;
    }

    /* ==== Getters & Setters ==== */
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

    public int getCanalId() {
        return canalId;
    }

    public void setCanalId(int canalId) {
        this.canalId = canalId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getStreamerNombre() {
        return streamerNombre;
    }

    public void setStreamerNombre(String streamerNombre) {
        this.streamerNombre = streamerNombre;
    }

    public String getCanalNombre() {
        return canalNombre;
    }

    public void setCanalNombre(String canalNombre) {
        this.canalNombre = canalNombre;
    }

    /* ==== toString para mostrar en ListView/ComboBox ==== */
    @Override
    public String toString() {
        String nombreStreamer = (streamerNombre != null) ? streamerNombre : "Streamer " + streamerId;
        String nombreCanal = (canalNombre != null) ? canalNombre : "Canal " + canalId;
        // 🚨 Solo mostramos: "X hizo raid al canal Y"
        return nombreStreamer + " hizo raid al canal " + nombreCanal;
    }
}
