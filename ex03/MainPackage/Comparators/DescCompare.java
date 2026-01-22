package  MainPackage.Comparators;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Comparator;

public class DescCompare<Procees> implements  Comparator<ProcessPCB>{//will be used for sorting by start time in descending order then by index

    // Reuse a single comparator instance (no repeated allocations)
    private static final Comparator<ProcessPCB> DESC =
            new AscCompare().reversed();

    @Override
    public int compare(ProcessPCB o1, ProcessPCB o2) {
        return DESC.compare(o1, o2);
    }
    
}
