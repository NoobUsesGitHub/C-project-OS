#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>

#define READSIZE 1




//closes two files
//input: two files
void closeFiles(int f1, int f2)
{
    close(f1);
    close(f2);
}

int main(int argc,char *argv[] )
{
    char buffer1[READSIZE+1];
    char buffer2[READSIZE+1];
    int i=0;
    int foundDiff=0;
    int readOutput1;
    int readOutput2;
    int x;
    
    memset(buffer2,0,READSIZE+1);//resetting the buffer
    memset(buffer1,0,READSIZE+1);
    if(argc!=3 ){
        //implement error catch of less or more than 2 files input
        perror("not correct amount of arguments");
        return 1;
    }
    int fd1=open(argv[1],O_RDONLY);
    int fd2=open(argv[2],O_RDONLY);
    if(fd1 == -1 ||fd2==-1 ){
        perror("open failed");
        return 1;
    }

    do{
        foundDiff=(buffer1[0]!=buffer2[0]);
        readOutput1=read(fd1,buffer1,READSIZE);
        readOutput2=read(fd2,buffer2,READSIZE);
    }while(readOutput1>0&&readOutput2>0&&foundDiff==0);//read(fd1,buffer1,READSIZE)>0 &&read(fd2,buffer2,READSIZE)>0

    if(readOutput1==-1 || readOutput2==-1){
        perror("failed reading");
        closeFiles(fd1,fd2);
        return 1;
    }

    if((readOutput1==0 && readOutput2>0)||(readOutput1>0 && readOutput2==0)){
        //not the same size, reached EOF before the other one
        perror("files not the same size");
        closeFiles(fd1,fd2);
        return 1;
    }
    
    closeFiles(fd1,fd2);
    return foundDiff==1?1:2;
}

