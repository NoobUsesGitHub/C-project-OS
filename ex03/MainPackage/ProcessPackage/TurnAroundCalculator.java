package MainPackage.ProcessPackage;

import MainPackage.Comparators.AscCompare;
import MainPackage.Runners.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class TurnAroundCalculator {

    /**
     * Creates a "fresh" list of processes for each scheduling run. We use the
     * ProcessPCB copy constructor so each runner starts from a clean state
     * (timeUsed reset, terminated reset, etc. as defined in
     * ProcessPCB(ProcessPCB)).
     */
    private static void deepCopy(Collection<ProcessPCB> org, Collection<ProcessPCB> dest) {
        for (ProcessPCB a : org) {
            dest.add(new ProcessPCB(a));
        }
    }

    public static void main(String[] args) {
        //arg[0] = input file path (C:\Users\Ozbm1\projects\OS\C-project-OS\ex03\example_files\input1.txt)
        if (args == null || args.length == 0) {
            System.out.println("error in TurnAroundCalculator <input_file_path>");
            return;
        }
        File f = new File(args[0]);

        // --- Basic file validation ---
        if (!f.exists() || !f.isFile()) {
            System.out.println("File not found or not a regular file: " + f.getPath());
            return;
        }

        int numberOfProcs = -1;
        ArrayList<ProcessPCB> processes = new ArrayList<>();

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
            if (!s.hasNextInt()) {
                System.out.println("Invalid file format: first token must be an integer (number of processes).");
                return;
            }
            numberOfProcs = s.nextInt();
            s.nextLine(); // consume the rest of the first line after the integer

            if (numberOfProcs <= 0) {
                System.out.println("File empty or number of processess = 0");
                return;
            }

            String line;
            int i = 0;
            while (s.hasNextLine() && processes.size() < numberOfProcs) {
                line = s.nextLine().trim();

                // Skip blank lines safely (does not count as a process)
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    // index/id is assigned by insertion order (0..n-1)
                    ProcessPCB p = new ProcessPCB(line, processes.size());
                    processes.add(p);
                } catch (MatchException e) {
                    System.out.println("Bad process line: '" + line + "'");
                    e.printStackTrace();
                    return;
                }
            }

            // If the file promised N processes but provided fewer usable lines, warn.
            if (processes.size() < numberOfProcs) {
                System.out.println("Warning: expected " + numberOfProcs + " processes, but read only " + processes.size());
            }

        } catch (FileNotFoundException e) {
            // Should be rare since we already checked exists(), but still good practice
            System.out.println("File not found: " + f.getPath());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("Error occurred while reading input.");
            e.printStackTrace();
            return;
        }

        processes.sort(new AscCompare()); 
        // List of scheduling algorithms to run
        ProcessRunner[] runners = {
            new FCFS(),
            new LCFSNP(),
            new LCFSP(),
            new RoundRobin(),
            new SJF()
        };
        
         // Run all algorithms on identical fresh copies of the same input
        for (ProcessRunner runner : runners) {
            ArrayList<ProcessPCB> inputToRunner = new ArrayList<>(processes.size());
            deepCopy(processes, inputToRunner);
            runner.runProcess(inputToRunner);
        }
    }
}
