#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>   // wait, waitpid + status macros
#include <unistd.h>     // fork, execvp
#include <fcntl.h>      //OWRTONLY
#include <string.h>

#define BUFFERSIZE 80
#define FAILEDGRADE 0
#define ACEDGRADE 100
#define nameSize 51 //assuming this is the max size for a normal name




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
//output: 1 if compiled and created main.exe file, 0 if didnt compile
int compiler(char *program){
    pid_t pid;
    int status,returnCode;
    if((pid=fork())<0)
                perror("fork error");
            else
                if(pid==0){
                    //child - compiler
                    char cmd[4096];
                    snprintf(cmd, sizeof(cmd), "gcc '%s'/*.c -o main.exe", program);
                    char *args[] = {"sh", "-c", cmd, NULL};
                    execvp("sh", args);    
                }
            pid_t waitTmp=waitpid(pid,&status,0);//checks if child finished successfully
            if(waitTmp==-1)
                perror("waitpid Failed");
            else if(WIFEXITED(status))
                    returnCode=WEXITSTATUS(status);
                else
                    returnCode=0;
            if(returnCode!=0)
                return 0;

    return 1;
}


int runProcessAndCompareOutput(char *program, char *input, char *output){
    pid_t pid;
    int fd,fd2;
    int status,returnCode;
    //run program, create programOutput.txt
    if((pid=fork())<0)
        perror("fork error");
    else{
        if(pid==0)
        {
            close(1);
            fd=open("programOutput.text",(O_WRONLY|O_CREAT|O_TRUNC),0666);
            dup(fd);
            close(0);
            fd2=open(input,O_RDONLY,0);
            dup(fd2);
            // stringBufferSize=snprintf(NULL,0,"%s < %s %s",program,input,"> ");
            // stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
            // sprintf(stringBuffer,"%s < %s %s",program,input,"> programOutput.txt");
            char* args[]={program,input,NULL};
            close(fd);
            close(fd2);
            execvp(program,args);
            // free(stringBuffer);
            exit(-1);//failed
        }
        waitpid(pid,NULL,0);//awaits program complete
    }
    //compare the output file to the correct output file
    if((pid=fork())<0)
        perror("fork error");
    else{
        if(pid==0)
        {
            //child - compare
            char* args[]={"./comp.out",output,"programOutput.txt",NULL};

        printf("\n!!!!testing file %s!!!!\n",output);//test print
            execvp("./comp.out",args);
        }
    pid_t waitTmp=waitpid(pid,&status,0);//checks if files are equal
    if(waitTmp==-1)
        perror("waitpid failed");
    else if(WIFEXITED(status))
        {
        returnCode=WEXITSTATUS(status);
        printf("\n!!!!testing file %d!!!!\n",returnCode);//test print
        }else
        {
            returnCode=0;
        }
    }
    if(returnCode!=2)
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
    inputFile[strcspn(inputFile, "\r\n")] = '\0';
    outputFile[strcspn(outputFile, "\r\n")] = '\0';
    // printf("%s",programFolder);
    // printf("%s",inputFile);
    // printf("%s",outputFile);
    if((pid=fork())<0)
        perror("fork error");
    else
        if(pid==0){
            //child- will run ls with dup to close the stdout
            int fd=open("folderNames.text",(O_WRONLY|O_CREAT|O_TRUNC),0666);//create new file
            close(STDOUT_FILENO);//closes ouput
            dup(fd);
            // stringBufferSize=snprintf(NULL,0,"ls %s %s",programFolder,"> folderNames.text");
            // stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
            // sprintf(stringBuffer,"ls %s > folderNames.text",programFolder);
            char* args[]={"ls",programFolder,NULL};
            close(fd);
            execvp("ls",args);
            // free(stringBuffer);
            exit(-1);//failed
        }
    waitpid(pid,NULL,0);
    //look through the list of folders, start for looping through them
    
    //FILE *folderFile=fileOpenerFromPath(programFolder,"/folderNames.text","r");
    FILE *folderFile=fileOpenerFromPath("","./folderNames.text","r");
    //FILE *gradeOutputFile=fileOpenerFromPath(programFolder,"/results.csv","w");
    FILE *gradeOutputFile=fileOpenerFromPath("","./results.csv","w");
    char programName[BUFFERSIZE*2];
    
    while(fgets(currentFile,sizeof(currentFile),folderFile)!=NULL){
        currentFile[strcspn(currentFile, "\r\n")] = '\0';
        grade=-1;
        //pull the program
        stringBufferSize=snprintf(NULL,0,"%s/%s",programFolder,currentFile);
        stringBuffer=malloc(sizeof(char)*stringBufferSize+1);
        sprintf(stringBuffer,"%s/%s",programFolder,currentFile);
        if(compiler(stringBuffer)==0)//compiling at this folder
        {
            grade=FAILEDGRADE;//program didnt compile, autoFail
        }
        free(stringBuffer);
        strcpy(programName,programFolder);//reset program name
        strcat(programName,"/main.exe");
        if(grade!=FAILEDGRADE)//no need to check an uncompiled Program
        {
            if(runProcessAndCompareOutput(programName,inputFile,outputFile)==0)
                grade=FAILEDGRADE;
            else
                grade=ACEDGRADE;
        }
        programName[0]='\0';
        stringBufferSize=snprintf(NULL,0,"%s,%d\n",currentFile,0);
        studentOutput=malloc(sizeof(char)*stringBufferSize+1);
        sprintf(studentOutput,"%s,%d\n",currentFile,grade); 
        if(fwrite(studentOutput,sizeof(char),strlen(studentOutput),gradeOutputFile)<=strlen(studentOutput))
            perror("writing Error");
        free(studentOutput);
    }
    
    fclose(folderFile);
    fclose(gradeOutputFile);
    exit(0);
}