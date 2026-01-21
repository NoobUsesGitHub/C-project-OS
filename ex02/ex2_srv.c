// Names: Oz Ben Moshe, David Norman, IDs: 208639906, 206395592
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <string.h>

// calculator operator values
#define OPR_ADD 1 // addition
#define OPR_SUB 2 // subscription
#define OPR_MULT 3 // multiplication
#define OPR_DIV 4 // division

// buffer sizes
#define SRV_BFR 128 // to read
#define CLIENT_BFR 64 // to write, we need much less

// permissions
#define CLIENT_FILE_PERMISSION 0666

// time
#define TIMEOUT 60

// handle client's requests
void handle_client(int sig) {
    pid_t pid = fork();
    // if the child fails
    if (pid < 0) {
        printf("ERROR_FROM_EX2\n");
        return;
    }
    // the child section
    if (pid == 0) {
        printf("end of stage 5f\n");
        // open the to_serv file and read it
        int fd_server = open("to_srv", O_RDONLY);
        if (fd_server < 0) {
            printf("ERROR_FROM_EX2\n");
            exit(1);
        }
        int client_pid, frst_num, scnd_num, opr;
        char server_buffer[SRV_BFR];
        // read file, and also count the amount of bytes read and verify that nothing is invalid
        int bytes_read = read(fd_server, server_buffer, sizeof(server_buffer) - 1);
        if (bytes_read > 0)
            server_buffer[bytes_read] = '\0'; // terminate the string since there's a problem
        close(fd_server);
        unlink("to_srv");
        printf("end of stage 5g\n");

        // read input and get values
        sscanf(server_buffer, "%d %d %d %d", &client_pid, &frst_num, &opr, &scnd_num);

        // create file
        char client_file[CLIENT_BFR];
        sprintf(client_file, "to_client_%d", client_pid);
        printf("end of stage 5h\n");

        //apply calculation
        int result = 0;
        switch (opr) {
            case 1: {
                result = frst_num + scnd_num;
                break;
            }
            case 2: {
                result = frst_num - scnd_num;
                break;
            }
            case 3: {
                result = frst_num * scnd_num;
                break;
            }
            case 4: {
                // check for division by zero. If it happens - return failure
                if (scnd_num != 0) result = frst_num / scnd_num;
                else {
                    printf("ERROR_FROM_EX2\n");
                    // also alert the client - so it won't wait for nothing
                    int fd_err = open(client_file, O_WRONLY | O_CREAT | O_TRUNC, CLIENT_FILE_PERMISSION);
                    if (fd_err >= 0) {
                        write(fd_err, "ERROR_FROM_EX2", strlen("ERROR_FROM_EX2"));
                        close(fd_err);
                        kill(client_pid, SIGUSR1);
                    }
                    exit(1);
                }
                break;
            }
            // if the operator value is something else
            default: {
                printf("ERROR_FROM_EX2\n");
                exit(1);
            }
        }

        // write the result to the file
        int fd_client = open(client_file, O_WRONLY | O_CREAT | O_TRUNC, CLIENT_FILE_PERMISSION);
        if (fd_client < 0) {
            printf("ERROR_FROM_EX2\n");
            exit(1);
        }
        
        // write to file
        sprintf(server_buffer, "%d", result);
        write(fd_client, server_buffer, strlen(server_buffer));
        close(fd_client);
        
        kill(client_pid, SIGUSR1); // signal user
        printf("end of stage 5i\n");
        exit(0);
    }
}

// stop running in case of timeout
void timeout_handler(int sig) {
    printf("The server was closed because no service request was received for the last 60 seconds\n");
    exit(0);
}

int main() {
    // printing the following steps, even though there is nothing to execute for them
    printf("end of stage 5a\n");
    printf("end of stage 5b\n");

    // in case we have "leftovers" from a previous run
    unlink("to_srv");
    printf("end of stage 5c\n");
    
    struct sigaction sa_calc;
    sa_calc.sa_handler = handle_client; // when the signal arrive - run this function
    sigemptyset(&sa_calc.sa_mask); // to not block other signals
    sa_calc.sa_flags = SA_RESTART; // if the system rejects this signal momentarily - don't give up
    sigaction(SIGUSR1, &sa_calc, NULL); // handling the signal

    struct sigaction sa_timeout;
    sa_timeout.sa_handler = timeout_handler; // when finishing the other task - run the timeout_handler function
    sigemptyset(&sa_timeout.sa_mask); // to not block other signals
    sa_timeout.sa_flags = 0;
    sigaction(SIGALRM, &sa_timeout, NULL); // handling the timeout

    // to prevent zombies
    struct sigaction sa_child;
    sa_child.sa_handler = SIG_IGN; // ignore children so they won't be stuck on a loop
    sigemptyset(&sa_child.sa_mask); // to not block other signals
    sa_child.sa_flags = SA_NOCLDWAIT; // don't wait for children
    sigaction(SIGCHLD, &sa_child, NULL);

    while (1) { // run endlessly
        alarm(TIMEOUT); // if nothing happens (for amount of seconds), trigger the alarm  and trigger the sa_timeout sigaction
        pause(); // wait for signal - either the sa_calc or sa_timeout
    }
    return 0;
}