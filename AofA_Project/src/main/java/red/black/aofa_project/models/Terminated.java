package red.black.aofa_project.models;

public class Terminated {
    private String PID;
    private int Start;
    private int End;

    public Terminated(String PID,int Start,int End){
        this.PID=PID;
        this.Start=Start;
        this.End=End;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public int getStart() {
        return Start;
    }

    public void setStart(int start) {
        Start = start;
    }

    public int getEnd() {
        return End;
    }

    public void setEnd(int end) {
        End = end;
    }
}
