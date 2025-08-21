package controllers;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Cargar FXML desde src/main/resources/views/home.fxml
        URL fxml = App.class.getResource("/views/home.fxml");
        if (fxml == null) {
            throw new IllegalStateException("No se encontró /views/home.fxml en el classpath");
        }

        // Usamos FXMLLoader para poder obtener el controlador
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        // Obtener el controlador y pasarle HostServices
        HomeController controller = loader.getController();
        controller.setHostServices(getHostServices());

        Scene scene = new Scene(root);

        stage.setTitle("Twitch Raids & Clips");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
