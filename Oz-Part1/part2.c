#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>   // wait, waitpid + status macros
#include <unistd.h>     // fork, execvp
#include <string.h>

#define BUFFERSIZE 1242

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
    int fd[2];
    char programFolder[BUFFERSIZE];
    char inputFile[BUFFERSIZE];
    char outputFile[BUFFERSIZE];

    pid_t pid;

    fgets(programFolder,sizeof(programFolder),configFile);
    fgets(inputFile,sizeof(inputFile),configFile);
    fgets(outputFile,sizeof(outputFile),configFile);
    fclose(configFile);


    
    printf("%s",programFolder);
    printf("%s",inputFile);
    printf("%s",outputFile);

    if(pipe(fd)<0)
        perror("pipe error");

    if((pid=fork())<0)
        perror("fork error");
    else
        if(pid==0){
            //child
            close(fd[0]);
            programFolder[strcspn(programFolder, "\r\n")] = '\0';
            char* args[]={"ls",programFolder,"> a2a.text",NULL};
            execvp("ls",args);
        }
    waitpid(pid,NULL,0);


    //per every folder run fork runProcess
    //find output files
    //compare the output file to the correct output file
    //give grades
    exit(0);
}