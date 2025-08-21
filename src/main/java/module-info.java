module app.raids_and_clips {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // requires com.mysql.cj; // <- opcional: solo si el driver está en el module-path y quieres declararlo explícito
    requires java.base;

    // Paquetes abiertos para FXML/reflexión
    opens config to javafx.fxml;
    opens controllers to javafx.fxml;
    opens dao to javafx.fxml;
    opens forms to javafx.fxml;
    opens models to javafx.fxml;

    // Paquetes que expones públicamente
    exports config;
    exports controllers;
    exports dao;
    exports models;
}
