@ECHO off & setlocal enabledelayedexpansion 

REM This batch script executes the jSparrow Maven Plugin in different ways.
REM It collects the logs and the Eclipse project files and the generated *.backup files,
REM and zips the result.

REM Variables
SET debug-dir=jsparrow-debug
SET root-dir=%CD%
SET project-file=.project
SET classpath-file=.classpath
SET settings-dir=.settings
SET backup-extension=.backup

ECHO root: %root-dir%

REM Create directory for saving the collected files
IF NOT EXIST %debug-dir% MD %debug-dir%

REM Execute the JMP normally

REM mvn jsparrow:refactor -X > %debug-dir%\jmp-standard.log 2> %debug-dir%\jmp-standard-errors.log

REM Collect .project files necessary files

REM Collect .project files necessary files
FOR /r . %%x in (*%project-file%*) DO (
    SETLOCAL
    SET B=%%x
    SET filename1="%%x"
    SET filename2=%filename1:\=--%
    SET filename3=%filename2::=_%
    ECHO f1: %filename1% f2: %filename2%  f3: %filename3%
    ECHO moving "%%x"
    ECHO relative path: !B:%CD%\=!
    MOVE /Y "%%x" %debug-dir%\!B:%CD%\=!
)

REM Collect .classpath files
call :Move_files %classpath-file%, %debug-dir%

EXIT /B %ERRORLEVEL%


REM This function moves files matching a regex to a destination
REM 1 - the regex matching files to be moved
REM 2 - the destination directory
:Move_files 
    SETLOCAL
    SET filenamepattern=%~1
    SET destination=%~2
    FOR /r %%z in (*%filenamepattern%*) DO (
        SET filename4="%%z"
        SET filename5=%filename4:\=--%
        SET filename6=%filename5::=_%
        MOVE /Y %%z %destination%\%filename6%
    )
    EXIT /B 0