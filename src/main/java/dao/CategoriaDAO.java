package dao;

import config.ConexionDB;
import models.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    /* ---------- Mapeo común ---------- */
    private Categoria map(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        Timestamp ts = rs.getTimestamp("creado_en");
        c.setCreadoEn(ts != null ? ts.toLocalDateTime() : null);
        return c;
    }

    private Connection conn() throws SQLException {
        // ¡OJO! No cerrar esta conexión en los métodos.
        return ConexionDB.getInstance().getConnection();
    }

    /* ---------- Consultas ---------- */
    public List<Categoria> findAll() {
        String sql = "SELECT id, nombre, descripcion, creado_en FROM categorias ORDER BY nombre";
        List<Categoria> lista = new ArrayList<>();
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

    public Categoria findById(int id) {
        String sql = "SELECT id, nombre, descripcion, creado_en FROM categorias WHERE id = ?";
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
    public int insert(Categoria c) {
        boolean withFecha = c.getCreadoEn() != null;
        String sql = withFecha
                ? "INSERT INTO categorias (nombre, descripcion, creado_en) VALUES (?, ?, ?)"
                : "INSERT INTO categorias (nombre, descripcion) VALUES (?, ?)";
        try {
            Connection conn = conn();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getNombre());
                ps.setString(2, c.getDescripcion());
                if (withFecha) {
                    ps.setTimestamp(3, Timestamp.valueOf(c.getCreadoEn()));
                }

                int n = ps.executeUpdate();
                if (n > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            int id = keys.getInt(1);
                            c.setId(id);
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
    public boolean update(Categoria c) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, creado_en = ? WHERE id = ?";
        try {
            Connection conn = conn();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getNombre());
                ps.setString(2, c.getDescripcion());
                if (c.getCreadoEn() != null) {
                    ps.setTimestamp(3, Timestamp.valueOf(c.getCreadoEn()));
                } else {
                    ps.setNull(3, Types.TIMESTAMP);
                }
                ps.setInt(4, c.getId());
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
        String sql = "DELETE FROM categorias WHERE id = ?";
        try {
            Connection conn = conn();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("delete() -> " + e.getMessage());
            return false;
        }
    }

    /* ---------- Extra opcional ---------- */
    public List<Categoria> searchByNombre(String term) {
        String sql = "SELECT id, nombre, descripcion, creado_en FROM categorias "
                + "WHERE nombre LIKE ? ORDER BY nombre";
        List<Categoria> lista = new ArrayList<>();
        try {
            Connection conn = conn();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
