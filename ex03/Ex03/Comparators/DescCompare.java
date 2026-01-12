package  Ex03.Comparators;

import Ex03.Process;
import java.util.Comparator;

public class DescCompare<Procees> implements  Comparator<Process>{//will be used for LCFS

    @Override
    public int compare(Process o1, Process o2) {
        Comparator<Process> c=new AscCompare<Process>();
        return c.compare(o1, o2)*-1;
    }
    
}
