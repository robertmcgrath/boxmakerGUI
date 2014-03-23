//
//  main.c
//  BoxmakerLauncher
//      This launcher encapsulates the java launch for Mac platforms.  The app works from commandline,
//      but needs a program to launch on a click.
//
//      Install the compiled program in the directory where 'boxGUI.jar' and 'docs' are installed. Use
//      a link/shortcut/alias to the launcher.
//
//  Copyright (c) 2013, 2014 Robert E. McGrath
//
//  Created for use by the Champaign Urbana Community Fab Lab
//
//  boxmakerGUI by Robert E. McGrath is licensed under a
//  Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
//  http://creativecommons.org/licenses/by-nc-sa/4.0/
//

#include <stdio.h>
#include <libgen.h>

int main(int argc, const char * argv[])
{
    int res;
    
    chdir(dirname((char *)argv[0]));
    res = system("java -XstartOnFirstThread -jar boxGUI.jar");
    exit(0);
}

