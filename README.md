# C-project-OS
OS course C project

How to run:
1. Compile the first part from this 'root' directory:
    gcc -I. part_01/AreEquals.c -o part_01/AreEquals && gcc -I. part_01/CompareFiles.c -o comp.out

    To run comp.out:
    ./comp.out ./example_files/cool_video_1.mp4 ./example_files/cool_video_2.mp4
    Of course, you may change the last two arguments and pick your own files.

2. Compile the second part:
    gcc part_02/CodeTester.c -o a.out