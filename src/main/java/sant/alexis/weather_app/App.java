package sant.alexis.weather_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("WeatherApp.fxml"));
        Scene scene = new Scene(root, 1900, 1200);
        stage.setTitle("WeatherApp");
        stage.setScene(scene);
        stage.getIcons().add(new Image("https://cdn-icons-png.flaticon.com/128/831/831268.png"));
        stage.show();
    }

    public static void main(String[] args)  {
        Application.launch();
    }
}