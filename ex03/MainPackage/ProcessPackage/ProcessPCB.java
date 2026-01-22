// Names: עוז בן משה 208639906 דוד נורמן 206395592
// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
package MainPackage.ProcessPackage;

/**
 * ProcessPCB represents a single process control block for scheduling
 * simulations.
 *
 * Fields meaning (typical for scheduling exercises): - startTime: arrival time
 * of the process (when it becomes available) - endTime: completion time (when
 * it finishes) - timeNeeded: total CPU time the process requires - timeUsed:
 * how much CPU time it has already consumed - terminated: whether the process
 * has completed execution - index: unique ID used for equality / tie-breaking
 */
public class ProcessPCB {

    private final double startTime;
    private double endTime;
    private boolean terminated;
    private double timeNeeded;
    private double timeUsed;
    private final int index;

    /**
     * Full constructor.
     *
     * @param sT arrival time
     * @param eT end/completion time (often 0 at initialization)
     * @param tN total CPU time needed
     * @param i unique index/id
     */
    public ProcessPCB(double sT, double eT, double tN, int i) {
        if (tN < 0) {
            throw new IllegalArgumentException("timeNeeded cannot be negative");
        }
        this.startTime = sT;
        this.endTime = eT;
        this.timeNeeded = tN;
        this.timeUsed = 0;
        this.terminated = false;
        this.index = i;
    }

    /**
     * Copy constructor.
     *
     * @param o ProcessPCB to copy from
     */
    public ProcessPCB(ProcessPCB o) {
        this.startTime = o.startTime;
        this.endTime = o.endTime;
        this.timeNeeded = o.timeNeeded;
        this.timeUsed = 0;
        this.terminated = false;
        this.index = o.index;
    }

    /**
     * Constructor from a formatted string.
     *
     * @param line formatted as "startTime,timeNeeded"
     * @param i unique index/id
     * @throws MatchException if the input string is not properly formatted
     */
    public ProcessPCB(String line, int i) throws MatchException {
        String[] input = line.split(",");
        if (input.length != 2) {
            throw (new MatchException("line " + line + " not built as expected", null));
        }
        try {
            this.startTime = Double.parseDouble(input[0]);
            this.timeNeeded = Double.parseDouble(input[1]);
        } catch (NumberFormatException e) {
            throw new MatchException("line '" + line + "' contains invalid numbers", e);
        }

        this.endTime = 0;
        this.timeUsed = 0;
        this.terminated = false;
        this.index = i;
    }

    /**
     * Two PCBs are considered equal if they have the same index. This assumes
     * index is unique for each process.
     */
    @Override
    public boolean equals(Object p) {
        if (this == p) {
            return true;
        }
        if (!(p instanceof ProcessPCB)) {
            return false;
        }
        ProcessPCB o = (ProcessPCB) p;
        return this.index == o.index;
    }

    /**
     * Must match equals(): if equals uses index, hashCode must use index too.
     */
    @Override
    public int hashCode() {
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
        if (timeNeeded < 0) {
            timeNeeded = 0;
        }
        this.timeNeeded = timeNeeded;
    }

    public int getIndex() {
        return index;
    }

    public boolean isTerminated() {
        return terminated;
    }

    /*
     * Sets the terminated flag to true.
     */
    public void setTerminated() {
        this.terminated = true;
    }

    public double getTimeUsed() {
        if (timeUsed < 0) {
            throw new IllegalArgumentException("timeUsed cannot be negative");
        }
        return timeUsed;
    }

    public void setTimeUsed(double timeUsed) {
        this.timeUsed = timeUsed;
    }

    /**
     * Turnaround time = completion time - arrival time.
     */
    public double getTurnaroundTime() {
        return this.endTime - this.startTime;
    }

    /**
     * Remaining CPU time until completion.
     */
    public double getTimeLeft() {
        return this.timeNeeded - this.timeUsed;
    }

}
