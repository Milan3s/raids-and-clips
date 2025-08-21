package dao;

import config.ConexionDB;
import models.Clip;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla clips
 *
 * @author Milanes
 */
public class ClipDAO {

    /* ---------- Mapeo común ---------- */
    private Clip map(ResultSet rs) throws SQLException {
        Clip c = new Clip();
        c.setId(rs.getInt("id"));
        c.setStreamerId(rs.getInt("streamer_id"));
        c.setTitulo(rs.getString("titulo"));
        c.setUrl(rs.getString("url"));
        Timestamp ts = rs.getTimestamp("fecha");
        c.setFecha(ts != null ? ts.toLocalDateTime() : null);
        return c;
    }

    private Connection conn() throws SQLException {
        // No cerrar aquí, se maneja en ConexionDB (singleton).
        return ConexionDB.getInstance().getConnection();
    }

    /* ---------- Consultas ---------- */
    public List<Clip> findAll() {
        String sql = "SELECT id, streamer_id, titulo, url, fecha FROM clips ORDER BY fecha DESC";
        List<Clip> lista = new ArrayList<>();
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ClipDAO.findAll() -> " + e.getMessage());
        }
        return lista;
    }

    public Clip findById(int id) {
        String sql = "SELECT id, streamer_id, titulo, url, fecha FROM clips WHERE id = ?";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return map(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ClipDAO.findById() -> " + e.getMessage());
        }
        return null;
    }

    /* ---------- Mutaciones ---------- */
    /**
     * Inserta y devuelve el id generado (o -1 si falla).
     */
    public int insert(Clip clip) {
        boolean withFecha = clip.getFecha() != null;
        String sql = withFecha
                ? "INSERT INTO clips (streamer_id, titulo, url, fecha) VALUES (?, ?, ?, ?)"
                : "INSERT INTO clips (streamer_id, titulo, url) VALUES (?, ?, ?)";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, clip.getStreamerId());
                ps.setString(2, clip.getTitulo());
                ps.setString(3, clip.getUrl());
                if (withFecha) {
                    ps.setTimestamp(4, Timestamp.valueOf(clip.getFecha()));
                }

                int n = ps.executeUpdate();
                if (n > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            int id = keys.getInt(1);
                            clip.setId(id);
                            return id;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ClipDAO.insert() -> " + e.getMessage());
        }
        return -1;
    }

    /**
     * Actualiza por id; true si modificó alguna fila.
     */
    public boolean update(Clip clip) {
        String sql = "UPDATE clips SET streamer_id = ?, titulo = ?, url = ?, fecha = ? WHERE id = ?";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, clip.getStreamerId());
                ps.setString(2, clip.getTitulo());
                ps.setString(3, clip.getUrl());
                if (clip.getFecha() != null) {
                    ps.setTimestamp(4, Timestamp.valueOf(clip.getFecha()));
                } else {
                    ps.setNull(4, Types.TIMESTAMP);
                }
                ps.setInt(5, clip.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("ClipDAO.update() -> " + e.getMessage());
            return false;
        }
    }

    /**
     * Borra por id; true si borró.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM clips WHERE id = ?";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("ClipDAO.delete() -> " + e.getMessage());
            return false;
        }
    }

    /* ---------- Extra opcional ---------- */
    public List<Clip> searchByTitulo(String term) {
        String sql = "SELECT id, streamer_id, titulo, url, fecha FROM clips WHERE titulo LIKE ? ORDER BY fecha DESC";
        List<Clip> lista = new ArrayList<>();
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "%" + term + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(map(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ClipDAO.searchByTitulo() -> " + e.getMessage());
        }
        return lista;
    }
}
