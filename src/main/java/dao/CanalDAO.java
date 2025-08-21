package dao;

import config.ConexionDB;
import models.Canal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla canales
 *
 * @author Milanes
 */
public class CanalDAO {

    /* ---------- Mapeo común ---------- */
    private Canal map(ResultSet rs) throws SQLException {
        Canal c = new Canal();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setCreador(rs.getString("creador"));
        Timestamp ts = rs.getTimestamp("fecha");
        c.setFecha(ts != null ? ts.toLocalDateTime() : null);
        return c;
    }

    private Connection conn() throws SQLException {
        // ¡OJO! No cerrar aquí la conexión, se maneja en ConexionDB (singleton).
        return ConexionDB.getInstance().getConnection();
    }

    /* ---------- Consultas ---------- */
    public List<Canal> findAll() {
        String sql = "SELECT id, nombre, creador, fecha FROM canales ORDER BY nombre";
        List<Canal> lista = new ArrayList<>();
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("CanalDAO.findAll() -> " + e.getMessage());
        }
        return lista;
    }

    public Canal findById(int id) {
        String sql = "SELECT id, nombre, creador, fecha FROM canales WHERE id = ?";
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
            System.err.println("CanalDAO.findById() -> " + e.getMessage());
        }
        return null;
    }

    /* ---------- Mutaciones ---------- */
    /**
     * Inserta y devuelve true si se guardó correctamente.
     * Además, asigna el id generado al objeto Canal.
     */
    public boolean insert(Canal canal) {
        boolean withFecha = canal.getFecha() != null;
        String sql = withFecha
                ? "INSERT INTO canales (nombre, creador, fecha) VALUES (?, ?, ?)"
                : "INSERT INTO canales (nombre, creador) VALUES (?, ?)";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, canal.getNombre());
                ps.setString(2, canal.getCreador());
                if (withFecha) {
                    ps.setTimestamp(3, Timestamp.valueOf(canal.getFecha()));
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            canal.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("CanalDAO.insert() -> " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza por id; true si modificó alguna fila.
     */
    public boolean update(Canal canal) {
        String sql = "UPDATE canales SET nombre = ?, creador = ?, fecha = ? WHERE id = ?";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, canal.getNombre());
                ps.setString(2, canal.getCreador());
                if (canal.getFecha() != null) {
                    ps.setTimestamp(3, Timestamp.valueOf(canal.getFecha()));
                } else {
                    ps.setNull(3, Types.TIMESTAMP);
                }
                ps.setInt(4, canal.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("CanalDAO.update() -> " + e.getMessage());
            return false;
        }
    }

    /**
     * Borra por id; true si borró.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM canales WHERE id = ?";
        try {
            Connection c = conn();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("CanalDAO.delete() -> " + e.getMessage());
            return false;
        }
    }

    /* ---------- Extra opcional ---------- */
    public List<Canal> searchByNombre(String term) {
        String sql = "SELECT id, nombre, creador, fecha FROM canales WHERE LOWER(nombre) LIKE LOWER(?) ORDER BY nombre";
        List<Canal> lista = new ArrayList<>();
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
            System.err.println("CanalDAO.searchByNombre() -> " + e.getMessage());
        }
        return lista;
    }
}
