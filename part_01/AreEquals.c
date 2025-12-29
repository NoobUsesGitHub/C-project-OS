#include "../config/config.h"
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>

/**
 * This function compares two files by actually comparing their bytes, and returns either they are equal or not.
 * Input: 
 *      - const char *file_path1: file path, as a string
 *      - const char *file_path2: file path, as a string
 * Output:
 *      - FILES_NOT_IDENTICAL value if the files are different
 *      - FILES_IDENTICAL value if the files are the same
 */
int compare_by_bytes(const char *file_path1, const char *file_path2) {
    int fd1, fd2;
    char bfr1[BUFFER_SIZE];
    char bfr2[BUFFER_SIZE];
    ssize_t read_bytes1, read_bytes2;
    //O_RDONLY = open as read only
    fd1 = open(file_path1, O_RDONLY);
    fd2 = open(file_path2, O_RDONLY);
    //run over the file
    //check for failures
    if (fd1 < 0 || fd2 < 0) {
        if (fd1 >= 0)
            close(fd1);
        if (fd2 >= 0)
            close(fd2);
        return(ERROR);
    }
    while(1) {
        // run over the file
        read_bytes1 = read(fd1, bfr1, BUFFER_SIZE);
        read_bytes2 = read(fd2, bfr2, BUFFER_SIZE);
        //if read fails
        if (read_bytes1 < 0 || read_bytes2 < 0)
            return ERROR;
        // if the read chunks are not equal
        if (read_bytes1 != read_bytes2 || memcmp(bfr1, bfr2, read_bytes1) != 0) {
            if (fd1 >= 0)
                close(fd1);
            if (fd2 >= 0)
                close(fd2);
            return(FILES_NOT_IDENTICAL);
        }
        //break condition
        if (read_bytes1 == 0 && read_bytes2 == 0)
            break;
    }
    // if we got to the end, they are equal
    close(fd1);
    close(fd2);
    return FILES_IDENTICAL;
}

int main(int argc, char *argv[]) {
    return compare_by_bytes(argv[1], argv[2]);
} 