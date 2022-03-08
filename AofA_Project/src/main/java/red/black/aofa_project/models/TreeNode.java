package red.black.aofa_project.models;


import red.black.aofa_project.Author;

import java.util.UUID;

@Author(name = "Shemar Edwards, Ankit Sharma", date = "12 Oct 2018",URL = "https://github.com/beingmartinbmc/LearnTrees/blob/master/src/trees/gui/redBlack")
public class TreeNode<E extends Comparable<E>> {
    public E element;
    public TreeNode<E> left;
    public TreeNode<E> right;
    public int height = 0;
    private boolean red = true;
    private String PID;
    private int RunTime=0;


    public TreeNode(E e){
        element = e;
        PID= UUID.randomUUID().toString();
    }

    public boolean isRed() {
        return red;
    }

    public boolean isBlack() {
        return !red;
    }

    public void setBlack() {
        red = false;
    }

    public void setRed() {
        red = true;
    }

    public int getRunTime() {
        return RunTime;
    }

    public void setRunTime(int runTime) {
        RunTime = runTime;
    }

    public String getPID() {
        return PID;
    }
}