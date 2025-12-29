#include "../config/config.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

/**
 * This function compares two files by getting their paths, and returns either they are equal or not.
 * Input: 
 *      - const char *file_path1: file path, as a string
 *      - const char *file_path2: file path, as a string
 * Output:
 *      - FILES_NOT_IDENTICAL value if the files are different
 *      - FILES_IDENTICAL value if the files are the same
*/
int compare_files(const char *file_path1, const char *file_path2) {
    pid_t pid;
    int status;

    //create a child
    pid = fork();
    //check for an error
    if (pid < 0) {
        printf("error: fork failed\n");
        exit(ERROR);
    } else
    // for the child
    if (pid == 0) {
        // build a syscall
        char *call_args[] = { "./AreEquals", (char *)file_path1, (char *)file_path2, NULL};
        execvp(call_args[0], call_args);
        // if execvp fails
        printf("error: execvp failed\n");
        exit(ERROR);
    }
    //for the parent
    else {
        //wait for child to finish
        if (waitpid(pid, &status, 0) == ERROR) {
            printf("error: waiting for child failed\n");
            return ERROR;
        }
        //if child failed
        //WIFEXITED = "was it flagged exited"
        if (!WIFEXITED(status)) {
            printf("error: child failed\n");
            return ERROR;
        }
        //get the compare result from the child
        //WEXITSTATUS = "wait exit status"
        return WEXITSTATUS(status);
    }
    //if did not reach the end
    return ERROR;
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        //print error to errors stream
        fprintf(stderr, "error: expected to get two file paths, instead got %d arguments\n", (argc-1));
        exit(ERROR);
    }
    int exit_code = compare_files(argv[1], argv[2]);
    switch (exit_code) {
        case FILES_IDENTICAL: {
            printf("The two files are identical ^_^\n");
            break;
        }
        case FILES_NOT_IDENTICAL: {
            printf("The two files are not identical x_x\n");
            break;
        }
        default:
            printf("An error did occur and the comparison was not checked 0_0\n");
    }
    if (exit_code == ERROR) {
        return FILES_NOT_IDENTICAL;
    }
    return exit_code;
}

/*
To compile:
gcc -I. part_01/AreEquals.c -o part_01/AreEquals && gcc -I. part_01/CompareFiles.c -o part_01/comp.out
*/ 