REM @ECHO off & setlocal enabledelayedexpansion 
setlocal enabledelayedexpansion 

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
IF NOT EXIST ".."\%debug-dir% MD ".."\%debug-dir%

REM Execute the JMP normally

REM mvn jsparrow:refactor -X > %debug-dir%\jmp-standard.log 2> %debug-dir%\jmp-standard-errors.log

REM Collect .project files necessary files

REM MOVE  ".settings" ".."\%debug-dir%\ 

REM Collect .classpath files
call :Move_files %classpath-file%, %debug-dir%
call :Move_files %project-file%, %debug-dir%
call :Move_files %settings-dir%, %debug-dir%

EXIT /B %ERRORLEVEL%


REM This function moves files matching a regex to a destination
REM 1 - the regex matching files to be moved
REM 2 - the destination directory
:Move_files 
    SETLOCAL
    SET filenamepattern=%~1
    SET destination=%~2
    FOR /r . %%z in (*%filenamepattern%*) DO (
        SET full-file-path=%%z
        ECHO moving "%%z"
        ECHO relative path: !full-file-path:%CD%\=!
        XCOPY "%%z" ".."\%debug-dir%\!full-file-path:%CD%\=!
        
        IF not "%full-file-path%:backup"=="%full-file-path%"  ECHO deleting %%z
    )
    ENDLOCAL
    EXIT /B 0
