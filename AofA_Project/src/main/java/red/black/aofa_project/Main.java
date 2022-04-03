package red.black.aofa_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws IOException {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),screenBounds.getWidth(), screenBounds.getHeight()-(screenBounds.getHeight()/5));
        primaryStage.setTitle("Completely Fair Scheduler");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/cpu.png")));
        primaryStage.setScene(scene);
        primaryStage.show();

    }




}
