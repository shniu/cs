#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

extern int create_process(char* program, char** args_list);

int main() {

    char* args_list[] = {
        "ls",
        "-l",
        "/Users/shniu",
        NULL
    };
    create_process("ls", args_list);
    return 0;
}
