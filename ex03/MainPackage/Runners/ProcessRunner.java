package MainPackage.Runners;

import MainPackage.ProcessPackage.ProcessPCB;
import java.util.Collection;

public interface ProcessRunner{
    void runProcess(Collection<ProcessPCB> ar);
}
