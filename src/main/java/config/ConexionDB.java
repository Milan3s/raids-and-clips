package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {

    private static final String URL =
        "jdbc:mysql://localhost:3306/twitch_raids_and_clips"
      + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static volatile ConexionDB INSTANCE;
    private volatile Connection conn;

    private ConexionDB() {}

    public static ConexionDB getInstance() {
        if (INSTANCE == null) {
            synchronized (ConexionDB.class) {
                if (INSTANCE == null) INSTANCE = new ConexionDB();
            }
        }
        return INSTANCE;
    }

    /** Devuelve SIEMPRE la misma conexión, reabriendo si hace falta. */
    public synchronized Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed() || !isValid(conn)) {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión (singleton) abierta/reabierta.");
        }
        return conn;
    }

    private boolean isValid(Connection c) {
        try { return c != null && c.isValid(2); }
        catch (SQLException e) { return false; }
    }

    /** Llamar al salir de la app. No cierres desde los DAO. */
    public synchronized void close() {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
            conn = null;
            System.out.println("🔌 Conexión (singleton) cerrada.");
        }
    }
}
