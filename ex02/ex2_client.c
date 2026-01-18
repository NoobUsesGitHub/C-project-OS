// Names: Oz Ben Moshe, David Norman, IDs: TODO, 206395592
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <fcntl.h>
#include <sys/random.h>
#include <string.h>

// buffer sizes
#define BUFFER_SIZE 128
#define FILE_NAME_SIZE 64

// permissions
#define CLIENT_FILE_PERMISSION 0666

// time
#define TIMEOUT 30

int has_responded = 0;

// flag if the server had responsded
void handle_server_response(int sig) {
    has_responded = 1;
}

// throw an error if reached timeout
void handle_timeout(int sig) {
    if (!has_responded) {
        printf("Client closed because no response was received from the server for 30 seconds\n");
        exit(0);
    }
}


int main(int argc, char *argv[]) {
    // verifies arguments
    if (argc != 5) {
        printf("ERROR_FROM_EX2\n");
        exit(1);
    }

    int srv_pid = atoi(argv[1]); // ascii to integer
    int attempts = 0;
    int fd;


    // try to reach the server
    while ((fd = open("to_srv", O_WRONLY | O_CREAT | O_EXCL, CLIENT_FILE_PERMISSION)) < 0) {
        attempts++;
        // after ten attempts - stop
        if (attempts >= 10) {
            printf("ERROR_FROM_EX2\n");
            exit(1);
        }
        // generate a random number
        unsigned int random_num;
        // if random generation fails
        if (getrandom(&random_num, sizeof(random_num), 0) < 0) {
            printf("ERROR_FROM_EX2\n");
            exit(1);
        }
        // sleep for a random number of seconds - use random_num with modulu 5 + 1. If we were to use muludu 6 as suggested in the instructions, we would have a change for 0 seconds waiting time
        sleep((random_num % 5) + 1);
    }

    // get input and write it to a file
    char buffer[BUFFER_SIZE];
    sprintf(buffer, "%d %s %s %s", getpid(), argv[2], argv[3], argv[4]);
    write(fd, buffer, strlen(buffer));
    close(fd);
    printf("end of stage 5d\n");

    // set up an SA to handle the server response
    struct sigaction sa;
    sa.sa_handler = handle_server_response;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    sigaction(SIGUSR1, &sa, NULL);

    // set up a second SA, now to handle timeouts
    struct sigaction sa_to;
    sa_to.sa_handler = handle_timeout;
    sigemptyset(&sa_to.sa_mask);
    sa_to.sa_flags = 0;
    sigaction(SIGALRM, &sa_to, NULL);

    kill(srv_pid, SIGUSR1);
    printf("end of stage 5e\n");
    
    alarm(TIMEOUT); // if 30 seconds had passed - alarm to trigger the timeout
    pause();

    // when the server responds
    if (has_responded) {
        // open the file to read the result
        char client_file[FILE_NAME_SIZE];
        sprintf(client_file, "to_client_%d", getpid());
        int fd_res = open(client_file, O_RDONLY);
        // if there was a problem
        if (fd_res < 0) {
            printf("ERROR_FROM_EX2\n");
            exit(1);
        }
        
        char res_buf[BUFFER_SIZE] = {0};
        read(fd_res, res_buf, sizeof(res_buf));
        printf("Result: %s\n", res_buf);
        
        // clean our environment, delete the file
        close(fd_res);
        unlink(client_file);
        printf("end of stage 5j\n");
    }

    return 0;
}