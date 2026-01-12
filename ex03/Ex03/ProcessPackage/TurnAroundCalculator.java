package Ex03.ProcessPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TurnAroundCalculator {

//sjf Looks at total, not what's left
//time enter->index (priority)
//idle times are a thing too
//times might be zero

//run circles for RR: for(i=0 to infinite): i=(i+1)%number of processes up til now


//Round robin needs to give back range??
//if process ended early, other process needs to run before it (used 1 second? other process is 1 second early)
//create global clock that is updated in a loop maybe?


public static void main(String[] args) {
    File f=new File(args[0]);
    int numberOfProcs=-1;
    ArrayList<ProcessPCB> cp=new ArrayList<ProcessPCB>();
    try(Scanner s=new Scanner(f)){
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
       if(s.hasNext())//not empty file then
       {
            numberOfProcs=s.nextInt();
       }
       if(numberOfProcs<=0)
        {
            System.out.println("File empty or number of processess = 0");
            return;
        }
        int i=0;
        String line;
       while(s.hasNextLine()&&i<numberOfProcs){
        line=s.nextLine();
        try{
            ProcessPCB p=new ProcessPCB(line, i);
            cp.add(p);
        }
        catch(MatchException e){
            s.close();
            e.printStackTrace();
            return;    
        }
        s.close();
       }
    }catch(Exception e){
        System.out.println("Error Occured");
        e.printStackTrace();
        return;
    }
    
    cp.sort(new Ex03.Comparators.AscCompare());
    
    turnAroundPrinter()
    
    
    //create the lists
    //run ProcessRunners
    //output them
}
}
