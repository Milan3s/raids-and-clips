package dao;

import config.ConexionDB;
import models.Raid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de solo lectura para la tabla raids
 * (los inserts/updates/deletes se gestionan en otros DAOs específicos)
 *
 * @author Milanes
 */
public class RaidDAO {

    /* ---------- Mapeo común ---------- */
    private Raid map(ResultSet rs) throws SQLException {
        Raid r = new Raid();
        r.setId(rs.getInt("id"));
        r.setStreamerId(rs.getInt("streamer_id"));
        r.setCanalId(rs.getInt("canal_id"));
        Timestamp ts = rs.getTimestamp("fecha");
        r.setFecha(ts != null ? ts.toLocalDateTime() : null);

        // Nombres del JOIN
        r.setStreamerNombre(rs.getString("streamer_nombre"));
        r.setCanalNombre(rs.getString("canal_nombre"));
        return r;
    }

    private Connection conn() throws SQLException {
        return ConexionDB.getInstance().getConnection();
    }

    /* ---------- Consultas ---------- */
    public List<Raid> findAll() {
        String sql = "SELECT r.id, r.streamer_id, r.canal_id, r.fecha, "
                + "s.nombre AS streamer_nombre, c.nombre AS canal_nombre "
                + "FROM raids r "
                + "JOIN streamer s ON r.streamer_id = s.id "
                + "JOIN canales c ON r.canal_id = c.id "
                + "ORDER BY r.fecha DESC";
        List<Raid> lista = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("RaidDAO.findAll() -> " + e.getMessage());
        }
        return lista;
    }

    public Raid findById(int id) {
        String sql = "SELECT r.id, r.streamer_id, r.canal_id, r.fecha, "
                + "s.nombre AS streamer_nombre, c.nombre AS canal_nombre "
                + "FROM raids r "
                + "JOIN streamer s ON r.streamer_id = s.id "
                + "JOIN canales c ON r.canal_id = c.id "
                + "WHERE r.id = ?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("RaidDAO.findById() -> " + e.getMessage());
        }
        return null;
    }

    public List<Raid> findByStreamer(int streamerId) {
        String sql = "SELECT r.id, r.streamer_id, r.canal_id, r.fecha, "
                + "s.nombre AS streamer_nombre, c.nombre AS canal_nombre "
                + "FROM raids r "
                + "JOIN streamer s ON r.streamer_id = s.id "
                + "JOIN canales c ON r.canal_id = c.id "
                + "WHERE r.streamer_id = ? "
                + "ORDER BY r.fecha DESC";
        List<Raid> lista = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, streamerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("RaidDAO.findByStreamer() -> " + e.getMessage());
        }
        return lista;
    }
}
