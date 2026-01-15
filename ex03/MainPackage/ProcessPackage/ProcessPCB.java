package MainPackage.ProcessPackage;

public class ProcessPCB {
    private final double startTime;
    private double endTime;
    private boolean terminated;
    private double timeNeeded;
    private double timeUsed;
    private final int index;
    

    public ProcessPCB(double sT, double eT, double tN, int i){
        this.startTime=sT;
        this.endTime=eT;
        this.timeNeeded=tN;
        this.timeUsed=0;
        this.terminated=false;
        this.index=i;
    }

    public ProcessPCB(ProcessPCB o){
        this.startTime=o.startTime;
        this.endTime=o.endTime;
        this.timeNeeded=o.timeNeeded;
        this.timeUsed=0;
        this.terminated=false;
        this.index=o.index;
    }

    public ProcessPCB(String line, int i) throws MatchException{
        String[] input=line.split(",");
        if(input.length>2){
            throw(new MatchException("line "+line+" not built as expected", null));
        }
        this.startTime=Integer.parseInt(input[0]);
        this.endTime=0;
        this.timeNeeded=Integer.parseInt(input[1]);
        this.timeUsed=0;
        this.terminated=false;
        this.index=i;
    }
    @Override
    public boolean equals(Object p){
        if(p instanceof  ProcessPCB){
            ProcessPCB o=((ProcessPCB)p);
            return (this.index==o.index);
        }
        return false;
    }

    public int hashCode(){
        return Integer.hashCode(this.index);
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getTimeNeeded() {
        return timeNeeded;
    }

    public void setTimeNeeded(double timeNeeded) {
        this.timeNeeded = timeNeeded;
    }

    public int getIndex() {
        return index;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated() {
        this.terminated = true;
    }

    public double getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(double timeUsed) {
        this.timeUsed = timeUsed;
    }

    

}
