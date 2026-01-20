package MainPackage.ProcessPackage;

import MainPackage.Comparators.AscCompare;
import MainPackage.Runners.*;
import java.util.ArrayList;
import java.util.Collection;

public class TurnAroundCalculator {

//idle times are a thing too
//times might be zero

    private static void deepCopy(Collection<ProcessPCB> org,Collection<ProcessPCB> dest){
        for(ProcessPCB a: org){
            dest.add(new ProcessPCB(a));
        }
    }


    public static void main(String[] args) {
        //File f=new File(args[0]);
        int numberOfProcs=-1;
        ArrayList<ProcessPCB> cp=new ArrayList<ProcessPCB>();
        

        cp.add(new ProcessPCB("1,3", 1));
        cp.add(new ProcessPCB("5,1", 2));
        cp.add(new ProcessPCB("2,4", 3));
        cp.add(new ProcessPCB("100,3", 4));
        cp.sort(new AscCompare());
        ArrayList<ProcessPCB> inputToRunners=new ArrayList<ProcessPCB>();
        deepCopy(cp,inputToRunners);
        new FCFS().runProcess(inputToRunners);
        inputToRunners=new ArrayList<ProcessPCB>();
        deepCopy(cp,inputToRunners);
        new LCFSNP().runProcess(inputToRunners);
        inputToRunners=new ArrayList<ProcessPCB>();
        deepCopy(cp,inputToRunners);
        new LCFSP().runProcess(inputToRunners);
       inputToRunners=new ArrayList<ProcessPCB>();
        deepCopy(cp,inputToRunners);
        new RoundRobin().runProcess(inputToRunners);
        inputToRunners=new ArrayList<ProcessPCB>();
        deepCopy(cp,inputToRunners);
        new SJF().runProcess(inputToRunners);
        
    }
    //     try(Scanner s=new Scanner(f)){
    //         /*
    //             we assume the file is organized as such
    //             number of procs=n
    //             proc1StartTime,proc1NeededTime
    //             proc2StartTime,proc2NeededTime
    //             proc3StartTime,proc3NeededTime
    //             ..etc
    //             procnStartTime,procnNeededTime
    //             IMPORTANT NOTE oreder of arrival!= order of running (unless start time is equals!)
    //         */
    //        if(s.hasNext())//not empty file then
    //        {
    //             numberOfProcs=s.nextInt();
    //        }
    //        if(numberOfProcs<=0)
    //         {
    //             System.out.println("File empty or number of processess = 0");
    //             return;
    //         }
    //         int i=0;
    //         String line;
    //        while(s.hasNextLine()&&i<numberOfProcs){
    //         line=s.nextLine();
    //         try{
    //             ProcessPCB p=new ProcessPCB(line, i);
    //             cp.add(p);
    //         }
    //         catch(MatchException e){
    //             s.close();
    //             e.printStackTrace();
    //             return;    
    //         }
    //         s.close();
    //        }
    //     }catch(Exception e){
    //         System.out.println("Error Occured");
    //         e.printStackTrace();
    //         return;
    //     }
        
    //     cp.sort(new Ex03.Comparators.AscCompare());
    //     //copy array into it    
    //     turnAroundPrinter()
        
        
    //     //create the lists
    //     //run ProcessRunners
    //     //output them
    // }
}
