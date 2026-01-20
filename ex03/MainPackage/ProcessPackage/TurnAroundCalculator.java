package MainPackage.ProcessPackage;

import MainPackage.Comparators.AscCompare;
import MainPackage.Runners.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class TurnAroundCalculator {

//idle times are a thing too
//times might be zero
    private static void deepCopy(Collection<ProcessPCB> org, Collection<ProcessPCB> dest) {
        for (ProcessPCB a : org) {
            dest.add(new ProcessPCB(a));
        }
    }

    public static void main(String[] args) {
        args = new String[]{"C:\\Users\\Ozbm1\\projects\\OS\\C-project-OS\\ex03\\example_files\\input1.txt"};
        File f = new File(args[0]);
        int numberOfProcs = -1;
        ArrayList<ProcessPCB> cp = new ArrayList<ProcessPCB>();

        try (Scanner s = new Scanner(f)) {
            /*
                 we assume the file is organized as such
                 number of procs=n
                 proc1StartTime,proc1NeededTime
                 proc2StartTime,proc2NeededTime
                 proc3StartTime,proc3NeededTime
                 ..etc
                 procnStartTime,procnNeededTime
                 IMPORTANT NOTE oreder of arrival!= order of running (unless start time is equals!)
             */
            if (s.hasNext())//not empty file then
            {
                numberOfProcs = s.nextInt();
            }
            if (numberOfProcs <= 0) {
                System.out.println("File empty or number of processess = 0");
                return;
            }

            String line;
            int i = 0;
            line = s.nextLine();//consume the rest of the first line
            while (s.hasNextLine() && i < numberOfProcs) {
                line = s.nextLine();
                System.out.println(line);
                try {
                    ProcessPCB p = new ProcessPCB(line, i);
                    cp.add(p);
                    i++;
                } catch (MatchException e) {
                    s.close();
                    e.printStackTrace();
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Error Occured");
            e.printStackTrace();
            return;
        }
        cp.sort(new AscCompare());
        ProcessRunner[] pr = {new FCFS(), new LCFSNP(), new LCFSP(), new RoundRobin(), new SJF()};
        ArrayList<ProcessPCB> inputToRunners = new ArrayList<ProcessPCB>();
        for (ProcessRunner r : pr) {
            deepCopy(cp, inputToRunners);
            r.runProcess(inputToRunners);
            inputToRunners = new ArrayList<>();
        }
    }
}
