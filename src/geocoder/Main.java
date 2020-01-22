package geocoder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setMinHeight(785);
        primaryStage.setMinWidth(1024);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("geocoder.fxml"));
        Parent root = loader.load();

        Controller controller = (Controller) loader.getController();
        controller.setMainStage(primaryStage);
        primaryStage.setTitle("GeoKodowanie");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("resources/theme.css").toString());
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
