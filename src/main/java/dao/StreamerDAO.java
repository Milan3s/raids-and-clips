package dao;

import config.ConexionDB;
import models.Streamer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StreamerDAO {

    /* ---------- Mapeo común ---------- */
    private Streamer map(ResultSet rs) throws SQLException {
        Streamer s = new Streamer();
        s.setId(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setEstaActivo(rs.getBoolean("esta_activo"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        s.setFechaCreacion(ts != null ? ts.toLocalDateTime() : null);
        return s;
    }

    private Connection conn() throws SQLException {
        // ¡OJO! No cerrar esta conexión en los métodos.
        return ConexionDB.getInstance().getConnection();
    }

    /* ---------- Consultas ---------- */
    public List<Streamer> findAll() {
        String sql = "SELECT id, nombre, esta_activo, fecha_creacion FROM streamer ORDER BY nombre";
        List<Streamer> lista = new ArrayList<>();
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("findAll() -> " + e.getMessage());
        }
        return lista;
    }

    public Streamer findById(int id) {
        String sql = "SELECT id, nombre, esta_activo, fecha_creacion FROM streamer WHERE id = ?";
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
            System.err.println("findById() -> " + e.getMessage());
        }
        return null;
    }

    /* ---------- Mutaciones ---------- */
    /**
     * Inserta y devuelve el id generado (o -1 si falla).
     */
    public int insert(Streamer s) {
        boolean withFecha = s.getFechaCreacion() != null;
        String sql = withFecha
                ? "INSERT INTO streamer (nombre, esta_activo, fecha_creacion) VALUES (?, ?, ?)"
                : "INSERT INTO streamer (nombre, esta_activo) VALUES (?, ?)";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getNombre());
                ps.setBoolean(2, s.isEstaActivo());
                if (withFecha) {
                    ps.setTimestamp(3, Timestamp.valueOf(s.getFechaCreacion()));
                }

                int n = ps.executeUpdate();
                if (n > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            int id = keys.getInt(1);
                            s.setId(id);
                            return id;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("insert() -> " + e.getMessage());
        }
        return -1;
    }

    /**
     * Actualiza por id; true si modificó alguna fila.
     */
    public boolean update(Streamer s) {
        String sql = "UPDATE streamer SET nombre = ?, esta_activo = ?, fecha_creacion = ? WHERE id = ?";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, s.getNombre());
                ps.setBoolean(2, s.isEstaActivo());
                if (s.getFechaCreacion() != null) {
                    ps.setTimestamp(3, Timestamp.valueOf(s.getFechaCreacion()));
                } else {
                    ps.setNull(3, Types.TIMESTAMP);
                }
                ps.setInt(4, s.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("update() -> " + e.getMessage());
            return false;
        }
    }

    /**
     * Borra por id; true si borró.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM streamer WHERE id = ?";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("delete() -> " + e.getMessage());
            return false;
        }
    }

    /* ---------- Extra opcional ---------- */
    public List<Streamer> searchByNombre(String term) {
        String sql = "SELECT id, nombre, esta_activo, fecha_creacion FROM streamer "
                + "WHERE nombre LIKE ? ORDER BY nombre";
        List<Streamer> lista = new ArrayList<>();
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
            System.err.println("searchByNombre() -> " + e.getMessage());
        }
        return lista;
    }
}
