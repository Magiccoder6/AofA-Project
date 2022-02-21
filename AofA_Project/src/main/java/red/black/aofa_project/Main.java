package red.black.aofa_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Red Black Tree");
        //primaryStage.getIcons().add(new Image("file:data/tree.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

    }




}
