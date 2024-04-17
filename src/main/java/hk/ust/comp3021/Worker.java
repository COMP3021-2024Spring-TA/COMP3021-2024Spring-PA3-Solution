package hk.ust.comp3021;
public class Worker implements Runnable {
    public String queryID;
    public String astID;
    public String queryName;
    public Object[] args;
    public int mode;
    public Worker(String queryID, String astID, String queryName, Object[] args, int mode) {
        this.queryID = queryID;
        this.astID = astID;
        this.queryName = queryName;
        this.args = args;
        this.mode = mode;
    }

    public void run() {
    }

}
