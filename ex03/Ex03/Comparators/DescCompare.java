package  Ex03.Comparators;

import Ex03.ProcessPackage.ProcessPCB;
import java.util.Comparator;

public class DescCompare<Procees> implements  Comparator<ProcessPCB>{//will be used for LCFS

    @Override
    public int compare(ProcessPCB o1, ProcessPCB o2) {
        return (new AscCompare()).compare(o1, o2)*-1;//DESC is just the opposite of ASC
    }
    
}
