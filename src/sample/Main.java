package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;
    private AnchorPane Layout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Battleships");
        primaryStage.getIcons().add(new Image("https://cdn1.iconfinder.com/data/icons/aircraft-and-ships-2/48/96-128.png"));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        initLayout();
    }

    public void initLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Layout.fxml"));
            Layout = (AnchorPane) loader.load();
            Scene scene = new Scene(Layout);
            primaryStage.setScene(scene);
            Controller controller = (Controller) loader.getController();
            controller.setTextArea("Witamy w grze w statki!\nWybierz odpowiednią opcję klient / server i zacznij grę.");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}