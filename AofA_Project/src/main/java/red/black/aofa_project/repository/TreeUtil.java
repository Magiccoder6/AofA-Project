package red.black.aofa_project.repository;


import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import red.black.aofa_project.Author;
import red.black.aofa_project.models.TreeNode;


@Author(name = "Ankit Sharma", date = "20 Nov 2018",URL = "https://github.com/beingmartinbmc/LearnTrees/blob/master/src/trees/gui/redBlack")

public class TreeUtil extends Pane {
    private RedBlackTree<Integer> tree;
    private double radius = 15;
    private double vGap = 50;

    public TreeUtil(RedBlackTree<Integer> tree){
        this.tree = tree;
        setStatus("Tree is empty");
        setBackground(new Background(new BackgroundFill(Color.web("#" + "FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void setStatus(String msg){
        getChildren().add(new Text(20, 20, msg));
    }

    public void displayTree(){
        this.getChildren().clear();
        if(tree.getRoot() != null){
            displayTree(tree.getRoot(), getWidth() / 2, vGap, getWidth() / 4);
        }

    }

    public void clearTree(){
        this.getChildren().clear();
    }

    private void displayTree(TreeNode<Integer> root, double x, double y, double hGap){
        System.out.println("Circle Location: "+x);
        System.out.println("Container: "+getWidth());



        if(root.left != null){
            getChildren().add(new Line(x - hGap, y + vGap, x, y));
            displayTree(root.left, x - hGap, y + vGap, hGap / 2);
        }

        if (root.right != null){
            getChildren().add(new Line(x + hGap, y + vGap, x, y));
            displayTree(root.right, x + hGap, y + vGap, hGap / 2);
        }

        tree.getRed(root);

        Circle circle = new Circle(x, y, radius);
        circle.setStroke(Color.BLACK);
        if(tree.getRed(root))
            circle.setFill(Color.INDIANRED);
        else circle.setFill(Color.GRAY);
        getChildren().addAll(circle, new Text(x - 4, y + 4, root.element + ""));

    }
}