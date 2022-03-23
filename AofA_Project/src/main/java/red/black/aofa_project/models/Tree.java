package red.black.aofa_project.models;


import red.black.aofa_project.Author;

@Author(name = "Ankit Sharma", date = "12 Oct 2018",URL = "https://github.com/beingmartinbmc/LearnTrees/blob/master/src/trees/gui/redBlack")
public interface Tree<E> extends Iterable<E> {
    public boolean insert(E e,int RunTime);
    public TreeNode delete(E e);
    public TreeNode search(E e);
    public void inorder();
    public void postorder();
    public void preorder();
    public int getSize();
    public boolean isEmpty();
}
