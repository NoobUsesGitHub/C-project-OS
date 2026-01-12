package  Ex03.Comparators;

import Ex03.ProcessPackage.ProcessPCB;
import java.util.Comparator;

public class AscCompare implements  Comparator<ProcessPCB>{//will be used for FCFS

    @Override
    public int compare(ProcessPCB o1, ProcessPCB o2) {
        if(o1.getStartTime()>o2.getStartTime()){
            return 1;
        }
        if(o1.getStartTime()<o2.getStartTime()){
            return -1;
        }
        if(o1.getStartTime()==o2.getStartTime())
            if(o1.getIndex()>o2.getIndex()){
                return 1;
            }else if(o1.getIndex()<o2.getIndex()){
                return -1;
            }
        return 0;
        
    }
    
}
