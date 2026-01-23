// Names: עוז בן משה 208639906 דוד נורמן 206395592
// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
package  MainPackage.Comparators;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Comparator;

public class AscCompare implements  Comparator<ProcessPCB>{//will be used for sorting by start time in ascending order then by index

    @Override
    public int compare(ProcessPCB o1, ProcessPCB o2) {
        int byStart = Double.compare(o1.getStartTime(), o2.getStartTime());
        if (byStart != 0) {
            return byStart;
        }

        // Tie-breaker: smaller index first
        return Integer.compare(o1.getIndex(), o2.getIndex());
        
    }
    
}
