package red.black.aofa_project.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import red.black.aofa_project.models.Terminated;
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
    private Label WaitQueueLabel;

    @FXML
    private ImageView pause;

    @FXML
    private ImageView play;

    @FXML
    private ImageView stop;

    @FXML
    private Label vruntime;

    @FXML
    private ImageView processor;

    @FXML
    private Circle indicator;

    @FXML
    private ImageView bus1;

    @FXML
    private ImageView bus2;

    @FXML
    private ImageView bus3;

    @FXML
    private ImageView bus4;

    @FXML
    private Pane ResultPane;


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
    private ObservableList<Terminated> finished = FXCollections.observableArrayList();
    private TableView<Terminated> table = new TableView<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTreeViewer();
        addSchedulerControllers();
        setupTable();
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
        double y = ((treePane.getHeight()/4)+100);

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
                try{
                    int key = Integer.parseInt(processInput.getText());
                    if(key>0)
                        insertProcess(key);
                    else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please Enter An Integer Value greater than 0.", ButtonType.OK);
                        alert.show();
                    }
                }catch (NumberFormatException exception){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please Enter An Integer Value", ButtonType.OK);
                    alert.show();
                }


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

    //function to setup data table
    public void setupTable(){

        TableColumn<Terminated,String>  PID = new TableColumn<>("PID");
        PID.setCellValueFactory(new PropertyValueFactory<>("PID"));

        TableColumn<Terminated,Integer> Start = new TableColumn<>("Start");
        Start.setCellValueFactory(new PropertyValueFactory<>("Start"));

        TableColumn<Terminated,Integer> End = new TableColumn<>("End");
        End.setCellValueFactory(new PropertyValueFactory<>("End"));

        table.setItems(finished);
        table.getColumns().addAll(PID,Start,End);

        ResultPane.getChildren().addAll(table);
    }

    public void notification(String title,String body){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Notifications notify = Notifications.create().title(title)
                        .text(body)
                        .hideAfter(javafx.util.Duration.seconds(3))
                        .position(Pos.TOP_RIGHT);
                notify.darkStyle();
                notify.showInformation();
            }
        });


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
                finished.clear();
            }

        });

        pause.setOnMouseClicked(e->{
            scheduler.setPause(true);
            scheduler.setStop(true);
            scheduler.animation.start=false;

            pause.setDisable(true);
            stop.setDisable(true);
            play.setDisable(false);

        });

        stop.setOnMouseClicked(e->{
            scheduler.setStop(true);
            scheduler.setPause(true);
            scheduler.animation.start=false;

            stop.setDisable(true);
            pause.setDisable(true);
            play.setDisable(false);
        });


    }


    public class CompletlyFairScheduler extends Thread{
        public int StartingTreeSize;
        private int VRUNTIME;
        private boolean pause;
        private boolean stop;
        private TreeNode process;
        private Animation animation;

        public CompletlyFairScheduler(){
            StartingTreeSize =tree.size;
            VRUNTIME= 1;
            pause=false;
            stop=false;
            animation=new Animation();
        }

        @Override
        public void run() {
            super.run();

            animation.start();//run animation of items
            while(!pause || !stop){
                try {

                    if(WaitQueue.size()>0){ //check if the recent processes with conflicted burst times are still waiting
                        process=WaitQueue.poll();

                        if(tree.search((Integer)process.element)==null){//check if burst time is in the tree
                            tree.insert((Integer)process.element,process.TargetRunTime);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    view.displayTree();
                                    WaitQueueLabel.setText(Integer.toString(WaitQueue.size()));
                                }

                            });
                            Thread.sleep(1000);

                            setVRuntime();
                            TreeNode min = tree.findMin(tree.getRoot());//find the process with the smallest burst time
                            process = tree.delete((Integer)min.element);//fetch node from tree
                        }else{
                            setVRuntime();
                            WaitQueue.add(process);//add process back to queue
                            TreeNode min = tree.findMin(tree.getRoot());//find the process with the smallest burst time
                            process = tree.delete((Integer)min.element);//fetch node from tree
                        }


                    }
                    else{
                        setVRuntime();
                        TreeNode min = tree.findMin(tree.getRoot());//find the process with the smallest burst time
                        if(min != null)
                            process = tree.delete((Integer)min.element);//fetch node from tree
                        else
                            process=null;

                    }


                    if(process != null){


                        //move process from tree to cpu
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                vruntime.setText(Integer.toString(VRUNTIME)+" MS");//set virtual runtime in gui
                                view.displayTree();
                                moveProcessToProcessor(process);

                            }
                        });
                        Thread.sleep(4000);

                        //add back the process to tree
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                process.element= ((Integer) process.element)+VRUNTIME;

                                //if process burst time is not in the tree then add it
                                if (tree.search((Integer)process.element)==null){
                                    if(((Integer)process.element / process.TargetRunTime)<2)//check if process time is finished
                                        tree.insert((Integer)process.element,process.TargetRunTime);
                                    else{//end of a process
                                        process.element=process.TargetRunTime*2;
                                        finished.add(new Terminated(process.getPID(),process.getTargetRunTime(),(Integer)process.getElement()));

                                        notification("Process Termination","Process "+process.getPID()+" is finished");
                                    }

                                }else{
                                    if(((Integer)process.element / process.TargetRunTime)<2){//check if the process is finished
                                        WaitQueue.add(process);
                                        WaitQueueLabel.setText(Integer.toString(WaitQueue.size()));
                                        notification("Conflict","Process is added to a wait queue");
                                    } else{//end of a process
                                        finished.add(new Terminated(process.getPID(),process.getTargetRunTime(),(Integer)process.getElement()));
                                        notification("Process Termination","Process "+process.getPID()+" is finished");
                                    }

                                }

                            }
                        });

                        //remove process visibility
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

                        System.out.println(finished.size());

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
                                animation.start=false;

                            }
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public void setVRuntime(){
            if(tree.size>0)
                VRUNTIME=StartingTreeSize/tree.size;//set runtime for each process
        }
        public void setPause(boolean pause) {
            this.pause = pause;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }
    }

    //responsible for animating objects
    public class Animation extends Thread{
        public boolean start=true;

        public Animation(){

        }

        @Override
        public void run(){
            try{
                while(start){

                    indicator.setVisible(false);
                    bus1.setBlendMode(BlendMode.EXCLUSION);
                    bus2.setBlendMode(BlendMode.EXCLUSION);
                    bus3.setBlendMode(BlendMode.EXCLUSION);
                    bus4.setBlendMode(BlendMode.EXCLUSION);
                    Thread.sleep(1000);
                    indicator.setVisible(true);
                    bus1.setBlendMode(null);
                    bus2.setBlendMode(null);
                    bus3.setBlendMode(null);
                    bus4.setBlendMode(null);
                    Thread.sleep(1000);

                }

            }catch (Exception e){

            }
        }
    }

}
