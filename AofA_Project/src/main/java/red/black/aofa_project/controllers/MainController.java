package red.black.aofa_project.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import red.black.aofa_project.models.TreeNode;
import red.black.aofa_project.repository.*;
import java.net.URL;
import java.util.PriorityQueue;
import java.util.Queue;
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

    @FXML
    private Label vruntime;

    @FXML
    private Circle indicator;

    private BorderPane treePane;
    private CompletlyFairScheduler scheduler;
    private RedBlackTree<Integer> tree;
    private TreeUtil view;
    private TextField processInput;
    private Button insertProcessButton;
    private Button deleteTree;

    private TranslateTransition circleTransition;
    private TranslateTransition textTransition;

    private Queue<TreeNode> WaitQueue = new PriorityQueue<>();//store processes that has the same burst time of a process in the tree

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTreeViewer();
        addSchedulerControllers();
    }

    public void setupTreeViewer(){
        tree = new RedBlackTree<>();
        treePane = new BorderPane();
        treePane.setPrefWidth(TreeContainer.getPrefWidth());
        treePane.setPrefHeight(TreeContainer.getPrefHeight());
        view = new TreeUtil(tree);
        setPane(treePane, view, tree);

        TreeContainer.setContent(treePane);
    }

    public void setPane(BorderPane pane, TreeUtil view, RedBlackTree<Integer> tree){
        pane.setCenter(view);
        processInput = new TextField();
        processInput.setPrefColumnCount(3);
        processInput.setAlignment(Pos.BASELINE_RIGHT);
        insertProcessButton = new Button("Insert");
        deleteTree = new Button("Delete Tree");
        addFunctionalities(tree, view);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(new Label("Enter process burst time: "), processInput, insertProcessButton, deleteTree);
        hBox.setAlignment(Pos.TOP_CENTER);
        pane.setTop(hBox);
    }

    //animation to move node to processor
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
        circleTransition = new TranslateTransition(time,circle);
        circleTransition.setToX(755);
        circleTransition.play();

        //text transition
        textTransition = new TranslateTransition(time,text);
        textTransition.setToX(755);
        textTransition.play();




    }

    //
    public void addFunctionalities(RedBlackTree<Integer> tree, TreeUtil view){
        insertProcessButton.setOnAction(e->{
            if(processInput.getText().length() == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You haven't entered anything!", ButtonType.OK);
                alert.getDialogPane().setMinHeight(80);
                alert.show();
            }
            else {
                int key = Integer.parseInt(processInput.getText());
                insertProcess(key);
            }
        });

        //clear tree nodes
        deleteTree.setOnAction(e->{
            tree.root=null;
            tree.size=0;
            view.clearTree();
            processInput.clear();
            vruntime.setText("");//reset vruntime
        });
    }

    //insert a process into the tree
    public void insertProcess(int key){
        if (tree.search(key)!=null) {
            view.displayTree();
            view.setStatus(key + " is already present!");
        } else {
            tree.insert(key,key);
            view.displayTree();
            view.setStatus(key + " is inserted!");

        }

        processInput.clear();

    }
    //function to remove node from tree
    public void fetchProcess(Integer key){
        TreeNode node = tree.search(key);

        if(node==null){
            view.displayTree();
            view.setStatus(key +" is not present!");

        }
        else {

            view.displayTree();
            view.setStatus(key + " is replaced by its predecessor and is deleted!");


        }
    }



    public void addSchedulerControllers(){
        pause.setDisable(true);
        stop.setDisable(true);

        play.setOnMouseClicked(e->{
            if(tree.size>0){
                scheduler = new CompletlyFairScheduler();
                scheduler.start();
                play.setDisable(true);
                pause.setDisable(false);
                stop.setDisable(false);
            }

        });

        pause.setOnMouseClicked(e->{
            scheduler.setPause(true);
            scheduler.setStop(true);
            pause.setDisable(true);
            stop.setDisable(true);
            play.setDisable(false);

        });

        stop.setOnMouseClicked(e->{
            scheduler.setStop(true);
            scheduler.setPause(true);
            stop.setDisable(true);
            pause.setDisable(true);
            play.setDisable(false);
        });


    }

    public class CompletlyFairScheduler extends Thread{
        public int StartingTreeSize;
        public int CurrentTreeSize;
        private int VRUNTIME;
        private boolean pause;
        private boolean stop;
        private TreeNode process;

        public CompletlyFairScheduler(){
            CurrentTreeSize =tree.size;
            StartingTreeSize =tree.size;
            VRUNTIME= 1;
            pause=false;
            stop=false;
        }

        @Override
        public void run() {
            super.run();

            while(!pause || !stop){
                try {

                if(WaitQueue.size()>0){ //check if the recent processes with conflicted burst times are still waiting
                    process=WaitQueue.poll();

                    if(tree.search((Integer)process.element)==null){//check if burst time is in the tree
                        tree.insert((Integer)process.element,process.RunTime);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                view.displayTree();
                            }
                        });
                        Thread.sleep(1000);
                    }else{
                        WaitQueue.add(process);//add process back to queue
                        TreeNode min = tree.findMin(tree.getRoot());//find the process with the smallest burst time
                        process = tree.delete((Integer)min.element);//fetch node from tree
                    }

                }
                else{
                    TreeNode min = tree.findMin(tree.getRoot());//find the process with the smallest burst time
                    process = tree.delete((Integer)min.element);//fetch node from tree
                }

                    if(process != null){

                        VRUNTIME=StartingTreeSize/tree.size;//set runtime for each process

                        //move process from tree to cpu
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                vruntime.setText(Integer.toString(VRUNTIME)+" MS");//set virtual runtime in gui
                                view.displayTree();
                                moveProcessToProcessor(process);

                            }
                        });
                        Thread.sleep(2000);

                        //add back the process to tree
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                process.element= ((Integer) process.element)+VRUNTIME;

                                //if process burst time is not in the tree then add it
                                if (tree.search((Integer)process.element)==null){
                                   tree.insert((Integer)process.element,process.RunTime);

                                }else{
                                    WaitQueue.add(process);
                                }

                                System.out.println("RunTime "+process.RunTime);
                                System.out.println("Element "+process.element);

                            }
                        });
                        Thread.sleep(4000);
                        //add process to tree end


                        circleTransition.getNode().setVisible(false);
                        textTransition.getNode().setVisible(false);

                        ///display updated tree
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                view.displayTree();
                            }
                        });
                        Thread.sleep(1000);
                        //display updated tree end

                    }else{
                        pause=true;
                        stop=true;


                        //reset gui controller buttons
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                MainController.this.stop.setDisable(true);
                                MainController.this.pause.setDisable(true);
                                MainController.this.play.setDisable(false);

                                //reset tree container section
                                tree.root=null;
                                tree.size=0;
                                processInput.clear();
                                vruntime.setText("");//reset vruntime
                            }
                        });
                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public void setPause(boolean pause) {
            this.pause = pause;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }
    }
}
