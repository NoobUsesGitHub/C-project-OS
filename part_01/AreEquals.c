#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

//Exit codes
//Exit code for identical files
#define FILES_IDENTICAL 2
//Exit code for different files
#define FILES_NOT_IDENTICAL 1
//Exit code for errors
#define ERROR 0

enum compare_exit_codes {
    COMPARE_SUCCESS = 0,
    COMPARE_FAILURE = 1,
    COMPARE_ERROR   = 2
};

/**
 * This function compare two files by getting their paths, and returns either they are equal or not.
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
        char *call_args[] = { (char *)"cmp", "--silent", (char *)file_path1, (char *)file_path2, NULL};
        execvp("cmp", call_args);
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
        int compare_value = WEXITSTATUS(status);
        //according to the result (may we'd like to return different values in the future)
        switch (compare_value) {
            // if equals
            case COMPARE_SUCCESS:
                return FILES_IDENTICAL;
            case COMPARE_FAILURE:
                return FILES_NOT_IDENTICAL;
            case COMPARE_ERROR:
            default:
                printf("error: compare failed\n");
                return ERROR;
        }
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
            printf("An error did occur and the comparison was not chekced 0_0\n");
    }
    if (exit_code == ERROR) {
        return FILES_NOT_IDENTICAL;
    }
    return exit_code;
}