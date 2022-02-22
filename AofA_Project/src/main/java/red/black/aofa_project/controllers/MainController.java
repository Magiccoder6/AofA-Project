package red.black.aofa_project.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import red.black.aofa_project.models.TreeNode;
import red.black.aofa_project.repository.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class MainController implements Initializable {
    @FXML
    private ScrollPane TreeContainer;
    @FXML
    private Pane controlsPane;
    @FXML
    private Pane processorPane;

    @FXML
    private ImageView pause;

    @FXML
    private ImageView play;

    @FXML
    private ImageView stop;

    private BorderPane treePane;
    private static ArrayList<Integer> nodes = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupTreeViewer();

        addSchedulerControllers();
    }

    public void setupTreeViewer(){
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        treePane = new BorderPane();
        treePane.setPrefWidth(TreeContainer.getPrefWidth());
        treePane.setPrefHeight(TreeContainer.getPrefHeight());
        TreeUtil view = new TreeUtil(tree);
        setPane(treePane, view, tree);

        TreeContainer.setContent(treePane);
    }

    public void setPane(BorderPane pane, TreeUtil view, RedBlackTree<Integer> tree){
        pane.setCenter(view);
        TextField textField = new TextField();
        textField.setPrefColumnCount(3);
        textField.setAlignment(Pos.BASELINE_RIGHT);
        Button insert = new Button("Insert");
        Button delete = new Button("Delete");
        addFunctionalities(textField, insert, delete, tree, view);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(new Label("Enter process burst time: "), textField, insert, delete);
        hBox.setAlignment(Pos.TOP_CENTER);
        pane.setTop(hBox);
    }

    public void addSchedulerControllers(){
        play.setOnMouseClicked(e->{




        });
    }

    public void moveProcessToProcessor(TreeNode process){
        double x = ((processorPane.getWidth()-70)*-1);
        double y = ((treePane.getHeight()/4)+20);

        Circle circle = new Circle(x, y, 15);
        if(process.isRed())
            circle.setFill(Color.INDIANRED);
        else circle.setFill(Color.GRAY);
        Text text = new Text(x - 4, y + 4, process.element + "");


        processorPane.getChildren().addAll(circle,text);
        Duration time = Duration.millis(1000);

        //circle transition
        TranslateTransition circleTransition = new TranslateTransition(time,circle);
        circleTransition.setToX(755);
        circleTransition.play();

        //text transition
        TranslateTransition textTransition = new TranslateTransition(time,text);
        textTransition.setToX(755);
        textTransition.play();


        //listener for animation when finished
        textTransition.setOnFinished(e->{
            System.out.println("Finished");

            int size = processorPane.getChildren().size();
            processorPane.getChildren().remove(size-1);
            processorPane.getChildren().remove(size-2);

        });
    }

    //function to remove node from tree
    public void fetchProcess(){

    }

    public void addFunctionalities(TextField textField, Button insert, Button delete, RedBlackTree<Integer> tree, TreeUtil view){
        insert.setOnAction(e->{
            if(textField.getText().length() == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You haven't entered anything!", ButtonType.OK);
                alert.getDialogPane().setMinHeight(80);
                alert.show();
            }
            else {
                int key = Integer.parseInt(textField.getText());

                if (tree.search(key)!=null) {
                    view.displayTree();
                    view.setStatus(key + " is already present!");
                } else {
                    tree.insert(key);
                    view.displayTree();
                    view.setStatus(key + " is inserted!");
                    nodes.add(key);

                }
                textField.clear();

                System.out.println(nodes.toString());
            }
        });

        delete.setOnAction(e->{
            
            int key = Integer.parseInt(textField.getText());

            TreeNode node = tree.search(key);

            if(node==null){
                view.displayTree();
                view.setStatus(key +" is not present!");
            }
            else{
                tree.delete(key);
                view.displayTree();
                view.setStatus(key+" is replaced by its predecessor and is deleted!");

                nodes.remove(Integer.valueOf(key));

                moveProcessToProcessor(node);


            }
            textField.clear();
        });
    }
}
