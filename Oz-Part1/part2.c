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




FILE* fileOpenerFromPath(char *path,char *catinated,char *mode){
    char *stringBuffer;
    int stringBufferSize=0;
    stringBufferSize=snprintf(NULL,0,"%s%s",path,catinated);
    stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
    sprintf(stringBuffer,"%s%s",path,catinated);
    
    FILE* f=fopen(stringBuffer,mode);
    free(stringBuffer);
    return f;
}

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
    pid_t pid;
    char *stringBuffer;
    int stringBufferSize=0;
    int status,returnCode;
    //run program, create programOutput.txt
    if((pid=fork())<0)
        perror("fork error");
    else
        if(pid==0)
        {
            stringBufferSize=snprintf(NULL,0,".\a.out %s < %s %s",program,input,"> programOutput.txt");
            stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
            sprintf(stringBuffer,".\a.out %s < %s %s",program,input,"> programOutput.txt");
            char* args[]={"sh","-c",stringBuffer,NULL};
            execvp("sh",args);
            free(stringBuffer);
        }
        waitpid(pid,NULL,0);//awaits program complete

    //compare the output file to the correct output file
    if((pid=fork())<0)
        perror("fork error");
    else
        if(pid==0)
        {
            //child - compare
            char* args[]={"./comp.out",output,"programOutput.txt",NULL};
            execvp("./comp.out",args);        
        }
    waitpid(pid,&status,0);//checks if files are equal
    returnCode=WEXITSTATUS(status);
    printf("%d",returnCode);
    if(returnCode!=2);
        return 0;
    return 1;
    
}

int main(int argc,char *argv[])
{

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

    //input check
    if(argc!=2)
    {
        perror("wrong amount of arguments");
        exit(-1);
    }    
    //open config file
    FILE *configFile=fopen(argv[1],"r");

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
    
    FILE *folderFile=fileOpenerFromPath(programFolder,"/folderNames.text","r");
    FILE *outputFile=fileOpenerFromPath(programFolder,"/results.csv","w");
    

    char programName[BUFFERSIZE*2];
    fgets(currentFile,sizeof(currentFile),folderFile);
    while(currentFile!=NULL){//RETURN EOF?
        grade=-1;
        //pull the program
        stringBufferSize=snprintf(NULL,0,"%s/%s",programFolder,currentFile);
        stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
        sprintf(stringBuffer,"%s/%s",programFolder,currentFile);
        if(compiler(stringBuffer)==0)
        {
            grade=FAILEDGRADE;//program didnt compile, autoFail
        }
        strcat(programName,"/a.out");
        if(grade!=FAILEDGRADE&&runProcessAndCompareOutput(programName,inputFile,outputFile)==0)//no need to check an uncompiled Program
        {
            grade=FAILEDGRADE;
        }else{
            grade=ACEDGRADE;
        }
        programName[0]='\0';
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