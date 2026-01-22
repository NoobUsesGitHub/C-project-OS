// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>   // wait, waitpid + status macros
#include <unistd.h>     // fork, execvp
#include <fcntl.h>      // OWRTONLY
#include <string.h>
#include "../config/config.h"

#define BUFFERSIZE 80
#define TEMPOUTPUTFILE "programOutput.txt"


// input: path and read/write
// output: opens a file object from path
FILE* fileOpenerFromPath(char *path,char *catinated,char *mode){
    char *stringBuffer;
    int stringBufferSize = 0;
    stringBufferSize = snprintf(NULL, 0, "%s%s", path, catinated); // checking the needed size
    stringBuffer = malloc(sizeof(char)* stringBufferSize+1);
    sprintf(stringBuffer, "%s%s", path, catinated);
    FILE* f = fopen(stringBuffer, mode);
    free(stringBuffer);
    return f;
}

// input: a path to folder
// output: nothing (creates a file with the ls output for the path)
FILE* createFolderList(char* programFolder)
{
    pid_t pid;
    if((pid = fork()) < 0)
        perror("fork error");
    else
        if(pid == 0){
            // child- will run ls with dup to close the stdout
            int fd = open("folderNames.text", (O_WRONLY | O_CREAT | O_TRUNC), READ_WRITE); // create new file
            dup2(fd, STDOUT_FILENO);
            char* args[] = {"ls", programFolder, NULL};
            close(fd);
            execvp("ls", args);
            exit(-1); // failed
        }
    
    waitpid(pid, NULL, 0);
    return fileOpenerFromPath("", "./folderNames.text", "r");
}
// input: a file path to a c file
// output: 1 if compiled and created main.exe file, 0 if didnt compile
int compiler(char *program){
    pid_t pid;
    int status, returnCode;
    if((pid = fork()) < 0)
        perror("fork error");
    if(pid == 0){
        // child - compiler
        // ignoring the error codes from compiler
        int dn_in = open("/dev/null", O_WRONLY);
        dup2(dn_in, STDOUT_FILENO);
        dup2(dn_in, STDERR_FILENO);
        close(dn_in);
        char cmd[PAGE_SIZE];
        snprintf(cmd, sizeof(cmd), "gcc '%s'/*.c -o main.exe", program); // checking the needed size
        char *args[] = {"sh", "-c", cmd, NULL};
        execvp("sh", args);
        // if failed
        exit(-1);  
    }
    pid_t waitTmp = waitpid(pid, &status, 0); // checks if child finished successfully
    if (waitTmp == -1)
        perror("waitpid Failed");
    else if (WIFEXITED(status)) {
            returnCode = WEXITSTATUS(status);
        } else
            returnCode = 0;
    if (returnCode != 0)
        return 0;
    return 1;
}

// input: program name, input file and output file to compare
// output: 1 if the output of program is the same of the expected output when inserted input
int runProcessAndCompareOutput(char *program, char *input, char *output){
    pid_t pid;
    int fd, fd2;
    int status, returnCode;
    // un program, create programOutput.txt
    if((pid = fork()) < 0)
        perror("fork error");
    else {
        if (pid == 0) {
            // O redirect
            fd = open(TEMPOUTPUTFILE, (O_WRONLY | O_CREAT | O_TRUNC), READ_WRITE);
            dup2(fd, STDOUT_FILENO);
            fd2 = open(input, O_RDONLY, 0);
            dup2(fd2, STDIN_FILENO);
            char* args[] = {program, NULL};
            close(fd);
            close(fd2);
            execvp(program, args);
            exit(-1); // failed
        }
    }
    waitpid(pid, NULL, 0); // awaits program complete
    // compare the output file to the correct output file
    if ((pid = fork()) < 0)
        perror("fork error");
    else {
        if(pid == 0)
        {
            // child - compare
            char* args[]={"./comp.out", output, TEMPOUTPUTFILE,NULL};
            execvp(args[0], args);
            // if failes
            exit(-1);
        }
    pid_t waitTmp = waitpid(pid, &status, 0); // checks if files are equal
    if (waitTmp == -1)
        perror("waitpid failed");
    else if(WIFEXITED(status)) {
            returnCode = WEXITSTATUS(status);
        } else {
            returnCode = 0;
        }
    }
    if (returnCode != 2)
        return 0;
    return 1;
}

int main(int argc,char *argv[]) {
    char programFolder[BUFFERSIZE];
    char inputFile[BUFFERSIZE];
    char outputFile[BUFFERSIZE];
    char currentStudentFile[BUFFERSIZE];
    char *studentGradeOutput;
    int grade;
    char *stringBuffer;
    int stringBufferSize=0;
    // input check
    if(argc != 2) {
        perror("wrong amount of arguments");
        exit(-1);
    }    
    // open config file
    FILE *configFile = fopen(argv[1], "r");

    fgets(programFolder, sizeof(programFolder), configFile);
    fgets(inputFile, sizeof(inputFile), configFile);
    fgets(outputFile, sizeof(outputFile), configFile);
    fclose(configFile);

    programFolder[strcspn(programFolder, "\r\n")] = '\0';
    inputFile[strcspn(inputFile, "\r\n")] = '\0';
    outputFile[strcspn(outputFile, "\r\n")] = '\0';
    
    FILE *folderFile=createFolderList(programFolder);
    // look through the list of folders, to start looping through them
    FILE *gradeOutputFile=fileOpenerFromPath("","./results.csv","w");
 
    
    while(fgets(currentStudentFile, sizeof(currentStudentFile), folderFile) != NULL){
        currentStudentFile[strcspn(currentStudentFile, "\r\n")] = '\0';
        unlink("./main.exe");
        grade = -1;
        // pull the program
        stringBufferSize=snprintf(NULL,0,"%s/%s",programFolder,currentStudentFile); // checking the needed size
        stringBuffer = malloc(sizeof(char)* stringBufferSize+1);
        sprintf(stringBuffer, "%s/%s", programFolder, currentStudentFile);
        if (compiler(stringBuffer) == 0) // compiling at this folder
        {
            grade = FAILED_GRADE; // program didnt compile, autoFail
        }
        free(stringBuffer);
        
        if (grade != FAILED_GRADE) // no need to check an uncompiled Program
        {
               if(runProcessAndCompareOutput("./main.exe", inputFile,outputFile) == 0)
                grade = FAILED_GRADE;
            else
                grade = ACED_GRADE;
        }
        stringBufferSize=snprintf(NULL, 0, "%s,%d\n", currentStudentFile, grade); // checking the needed size
        studentGradeOutput=malloc(sizeof(char)* stringBufferSize+1);
        sprintf(studentGradeOutput, "%s,%d\n", currentStudentFile, grade); 
 
        if(fwrite(studentGradeOutput, sizeof(char), strlen(studentGradeOutput), gradeOutputFile)<strlen(studentGradeOutput))
            perror("writing Error");
        free(studentGradeOutput);
        currentStudentFile[0] = '\0';
    }
    
    fclose(folderFile);
    unlink("./folderNames.text");
    unlink("./main.exe");
    unlink("./programOutput.txt");

    fclose(gradeOutputFile);
    exit(0);
}