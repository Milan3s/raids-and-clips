package controllers;

import config.ConexionDB;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    // === BOTONES / UI ===
    @FXML
    private Button btnManualUsuario;
    @FXML
    private Button btnInstalarXampp;
    @FXML
    private Button btnIniciar;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnLicencia;
    @FXML
    private Label sms_conexiondb;

    // === Servicios auxiliares ===
    private HostServices hostServices;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (sms_conexiondb != null && !sms_conexiondb.getStyleClass().contains("status-label")) {
            sms_conexiondb.getStyleClass().add("status-label");
        }
        comprobarConexionAsync();
    }

    // === BOTONES DEL PANEL ===
    @FXML
    private void accionManualUsuario(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Manual de Usuario");
        alert.setHeaderText("Guía básica para usar la aplicación");
        alert.setContentText(
                "1. Descarga XAMPP desde https://www.apachefriends.org\n\n"
                + "2. Instálalo en C:\\xampp.\n\n"
                + "3. Abre 'xampp-control.exe'.\n\n"
                + "4. Pulsa 'Start' en Apache y MySQL.\n\n"
                + "5. Mantén XAMPP abierto mientras usas la aplicación."
        );
        alert.showAndWait();
    }

    @FXML
    private void accionInstalarXampp(ActionEvent event) {
        try {
            new ProcessBuilder("C:\\xampp\\xampp-control.exe").start();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo abrir XAMPP.\nAsegúrate de que está instalado en C:\\xampp.");
            e.printStackTrace();
        }
    }

    @FXML
    private void accionIniciarApp(ActionEvent event) {
        try {
            URL fxml = getClass().getResource("/views/main.fxml");
            if (fxml == null) {
                throw new IllegalStateException("No se encontró /views/main.fxml en el classpath");
            }
            Parent root = FXMLLoader.load(fxml);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Twitch Raids & Clips - Main");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al iniciar",
                    "No se pudo iniciar la vista principal.\n"
                    + "Revisa que 'views/main.fxml' exista en resources y tenga fx:controller correcto.");
            e.printStackTrace();
        }
    }

    @FXML
    private void accionLicencia(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Licencia");
        alert.setHeaderText("Información de la licencia");
        alert.setContentText(
                "LICENCIA DE USO DEL SOFTWARE\n\n"
                + "© Desarrollado por Soujirito\n\n"
                + "Este software es de código abierto y gratuito.\n\n"
                + "Se permite usar, copiar, modificar y distribuir para fines educativos o personales.\n\n"
                + "RESTRICCIONES:\n"
                + "- Prohibida la venta o alquiler.\n"
                + "- Redistribución debe mantener este aviso.\n\n"
                + "EL SOFTWARE SE ENTREGA 'TAL CUAL', SIN GARANTÍAS."
        );
        alert.showAndWait();
    }

    @FXML
    private void accionCerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // === Helpers ===
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Ping rápido al puerto MySQL para saber si el servicio está arriba, aunque
     * la BD/credenciales no sean correctas.
     */
    private boolean isMysqlPortUp(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private void comprobarConexionAsync() {
        if (sms_conexiondb != null) {
            sms_conexiondb.setText("⏳ Probando conexión...");
            sms_conexiondb.getStyleClass().removeAll("db-ok", "db-ko");
            if (!sms_conexiondb.getStyleClass().contains("status-label")) {
                sms_conexiondb.getStyleClass().add("status-label");
            }
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                // 1) Intento real con JDBC
                try {
                    Connection conn = ConexionDB.getInstance().getConnection();
                    if (conn != null && !conn.isClosed() && conn.isValid(2)) {
                        return true;
                    }
                } catch (SQLException | RuntimeException ignore) {
                    // ignoramos para probar el puerto
                }

                // 2) Si no pudimos validar con JDBC, comprobamos si MySQL está arriba
                boolean mysqlUp = isMysqlPortUp("127.0.0.1", 3306, 600);
                if (!mysqlUp) {
                    updateMessage("❌ Debes iniciar XAMPP para que funcione la aplicación");
                }
                return mysqlUp;
            }
        };

        task.setOnSucceeded(ev -> {
            boolean ok = Boolean.TRUE.equals(task.getValue());
            if (sms_conexiondb != null) {
                sms_conexiondb.getStyleClass().removeAll("db-ok", "db-ko");
                if (ok) {
                    sms_conexiondb.setText("✅ Todo correcto, puedes usar la aplicación");
                    sms_conexiondb.getStyleClass().add("db-ok");

                    // 🔒 Deshabilitar abrir XAMPP porque MySQL está activo
                    if (btnInstalarXampp != null) {
                        btnInstalarXampp.setDisable(true);
                    }
                } else {
                    String msg = task.getMessage();
                    sms_conexiondb.setText(
                            (msg != null && !msg.isBlank())
                            ? msg
                            : "❌ Debes iniciar XAMPP para que funcione la aplicación"
                    );
                    sms_conexiondb.getStyleClass().add("db-ko");

                    // 🔓 Habilitar abrir XAMPP porque MySQL NO está activo
                    if (btnInstalarXampp != null) {
                        btnInstalarXampp.setDisable(false);
                    }
                }
            }
        });

        task.setOnFailed(ev -> {
            if (sms_conexiondb != null) {
                sms_conexiondb.getStyleClass().removeAll("db-ok");
                sms_conexiondb.setText("❌ Debes iniciar XAMPP para que funcione la aplicación");
                sms_conexiondb.getStyleClass().add("db-ko");
                if (btnInstalarXampp != null) {
                    btnInstalarXampp.setDisable(false);
                }
            }
        });

        Thread th = new Thread(task, "db-check");
        th.setDaemon(true);
        th.start();
    }

    // Setter para inyectar HostServices desde App.java
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
