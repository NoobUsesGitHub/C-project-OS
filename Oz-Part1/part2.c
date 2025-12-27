#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>   // wait, waitpid + status macros
#include <unistd.h>     // fork, execvp
#include <string.h>

#define BUFFERSIZE 1242
#define FAILEDGRADE 0
#define ACEDGRADE 100
#define nameSize 450 //assuming this is the max size for a normal name

//input: a file path to a c file
//output: 1 if compiled and created a.out file, 0 if didnt compile
int compiler(const *program){
    pid_t pid;
    int status,returnCode;
    if((pid=fork())<0)
                perror("fork error");
            else
                if(pid==0){
                    //child - compiler
                    char* args[]={"gcc","main.c","-o","a.out",NULL};
                    execvp("gcc",args);        
                }
            waitpid(pid,&status,0);//checks if child finished successfully
            returnCode=WEXITSTATUS(status);
            printf("%d",returnCode);
            if(returnCode!=0);
                return 0;

    return 1;
}


int runProcessAndCompareOutput(const *program, const *input, const *output){
    //per every folder run fork runProcess
    //find output files
    //compare the output file to the correct output file
    //find program file
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
    char currentFile[BUFFERSIZE];
    char *studentOutput;
    int grade;
    int folderAmount=0;
    pid_t pid;
    char *stringBuffer;
    int stringBufferSize=0;
    fgets(programFolder,sizeof(programFolder),configFile);
    fgets(inputFile,sizeof(inputFile),configFile);
    fgets(outputFile,sizeof(outputFile),configFile);
    fclose(configFile);
    programFolder[strcspn(programFolder, "\r\n")] = '\0';
    programFolder[strcspn(inputFile, "\r\n")] = '\0';
    programFolder[strcspn(outputFile, "\r\n")] = '\0';
    
    // printf("%s",programFolder);
    // printf("%s",inputFile);
    // printf("%s",outputFile);
    if((pid=fork())<0)
        perror("fork error");
    else
        if(pid==0){
            //child- will run ls as a shell command on execvp
            stringBufferSize=snprintf(NULL,0,"ls %s %s",programFolder,"> folderNames.text");
            stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
            sprintf(stringBuffer,"ls %s %s",programFolder,"> folderNames.text");
            char* args[]={"sh","-c",stringBuffer,NULL};
            execvp("sh",args);
            free(stringBuffer);
        }
    waitpid(pid,NULL,0);
    //look through the list of folders, start for looping through them
    
    stringBufferSize=snprintf(NULL,0,"%s%s",programFolder,"/folderNames.text");
    stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
    sprintf(stringBuffer,"%s%s",programFolder,"/folderNames.text");
    FILE *folderFile=fopen(stringBuffer,"r");
    free(stringBuffer);
    
    stringBufferSize=snprintf(NULL,0,"%s%s",programFolder,"/results.csv");
    stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
    sprintf(stringBuffer,"%s%s",programFolder,"/results.csv");
    FILE *outputFile=fopen(stringBuffer,"w");
    free(stringBuffer);


    fgets(currentFile,sizeof(currentFile),folderFile);
    while(currentFile!=NULL){//RETURN EOF?
        grade=-1;
        //pull the program
        stringBufferSize=snprintf(NULL,0,"%s,%d",currentFile,0);
        stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
        sprintf(stringBuffer,"%s/%s",programFolder,currentFile);
        if(compiler(stringBuffer)==0)
        {
            grade=FAILEDGRADE;//program didnt compile, autoFail
        }

        if(grade!=FAILEDGRADE&&runProcessAndCompareOutput()==0)//no need to check an uncompiled Program
        {
            grade=FAILEDGRADE;
        }else{
            grade=ACEDGRADE;
        }

        stringBufferSize=snprintf(NULL,0,"%s,%d",currentFile,0);
        studentOutput=malloc(sizeof(char)*stringBufferSize+1);
        sprintf(studentOutput,"%s,%d",currentFile,grade); 
        if(fwrite(studentOutput,sizeof(char),strlen(studentOutput),outputFile)<=strlen(studentOutput))
            peror("writing Error");
        fgets(currentFile,sizeof(currentFile),folderFile);
    }
    
    fclose(folderFile);
    fclose(outputFile);
    exit(0);
}