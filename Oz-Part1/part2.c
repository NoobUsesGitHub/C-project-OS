#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>   // wait, waitpid + status macros
#include <unistd.h>     // fork, execvp

#define BUFFERSIZE 100

int runProcess(){
    //find program file
    //compile program
    //run program, have it output it's data to output
}



int main(int argc,char *argv[])
{
    //input check
    if(argc!=2)
    {
        perror("wrong amount of arguments");
        exit(-1);
    }    
    //open config file
    FILE *configFile=fopen(argv[1],"r");
    char programFolder[BUFFERSIZE];
    char inputFile[BUFFERSIZE];
    char outputFile[BUFFERSIZE];

    fgets(programFolder,sizeof(programFolder),configFile);
    fgets(inputFile,sizeof(inputFile),configFile);
    fgets(outputFile,sizeof(outputFile),configFile);
 
    printf("%s",programFolder);
    printf("%s",inputFile);
    printf("%s",outputFile);
    fclose(configFile);

    
    //per every folder run fork runProcess
    //find output files
    //compare the output file to the correct output file
    //give grades
    exit(0);
}