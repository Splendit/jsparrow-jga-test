@ECHO off & setlocal enabledelayedexpansion 

REM This batch script executes the jSparrow Maven Plugin in different ways.
REM It collects the logs and the Eclipse project files and the generated *.backup files,
REM and zips the result.

REM Variables
SET debug-dir=jsparrow-debug
SET root-dir=%CD%
SET debug-no-d-flags=no-d-flags
SET debug-maven-update=maven-update
SET debug-build-workspace=refresh-workspace
SET debug-update-and-build-workspace=maven-update-and-build-workspace
SET project-file=.project
SET classpath-file=.classpath
SET settings-dir=.settings
SET settings-dir-fullpath=%root-dir%\%settings-dir%

REM Backup files which should be deleted
SET classpath-file_backup=%classpath-file%.backup
SET project-file-backup=%project-file%.backup
SET settings-dir-backup=%settings-dir%.backup

REM Command for delating files
SET DELETE_FILE_COMMAND=DEL

REM Command for deleting a folder tree /S= all sub directories as well /Q=Non interactive  mode
SET REMOVE_FOLDER_COMMAND="RMDIR /S /Q" 
SET backup-extension=.backup

ECHO root: %root-dir%


REM Create directory for saving the collected files
IF NOT EXIST ".."\%debug-dir% MD ".."\%debug-dir%


REM 1
REM Execute the JMP normally
REM ECHO mvn jsparrow:refactor -X -Dlicense=I2ZGK32V2
IF NOT EXIST ".."\%debug-dir%\%debug-no-d-flags% MD ".."\%debug-dir%\%debug-no-d-flags%
CALL mvn jsparrow:refactor -X -Dlicense=I2ZGK32V2 > ".."\%debug-dir%\%debug-no-d-flags%\jmp-standard.log 2> ".."\%debug-dir%\%debug-no-d-flags%\jmp-standard-errors.log
CALL :Move_eclipse_files %debug-dir%\%debug-no-d-flags%

REM 2
REM Execute the JMP -Db flag 
ECHO mvn jsparrow:refactor -X -Db -Dlicense=I2ZGK32V2
IF NOT EXIST ".."\%debug-dir%\%debug-build-workspace% MD ".."\%debug-dir%\%debug-build-workspace%
CALL mvn jsparrow:refactor -X -Db -Dlicense=I2ZGK32V2 > ".."\%debug-dir%\%debug-build-workspace%\jmp-standard.log 2> ".."\%debug-dir%\%debug-build-workspace%\jmp-standard-errors.log
CALL :Move_eclipse_files %debug-dir%\%debug-build-workspace%


REM 3
REM Execute the JMP -Du flag 
ECHO mvn jsparrow:refactor -X -Du -Dlicense=I2ZGK32V2
IF NOT EXIST ".."\%debug-dir%\%debug-maven-update% MD ".."\%debug-dir%\%debug-maven-update%
CALL mvn jsparrow:refactor -X -Du -Dlicense=I2ZGK32V2 > ".."\%debug-dir%\%debug-maven-update%\jmp-standard.log 2> ".."\%debug-dir%\%debug-maven-update%\jmp-standard-errors.log
CALL :Move_eclipse_files %debug-dir%\%debug-maven-update%

REM 4
REM Execute the JMP -Db and -Du flag 
ECHO mvn jsparrow:refactor -X -Db -Du -Dlicense=I2ZGK32V2
IF NOT EXIST ".."\%debug-dir%\%debug-update-and-build-workspace% MD ".."\%debug-dir%\%debug-update-and-build-workspace%
CALL mvn jsparrow:refactor -X -Db -Du -Dlicense=I2ZGK32V2 > ".."\%debug-dir%\%debug-update-and-build-workspace%\jmp-standard.log 2> ".."\%debug-dir%\%debug-update-and-build-workspace%\jmp-standard-errors.log
CALL :Move_eclipse_files %debug-dir%\%debug-update-and-build-workspace%


EXIT /B %ERRORLEVEL%

	
:Move_eclipse_files	
	CALL :Copy_files %classpath-file%, %~1
	CALL :Copy_files %project-file%, %~1
	CALL :Copy_.SettingsFolder %~1
	CALL :Delete_Backup_Files %classpath-file_backup%, %DELETE_FILE_COMMAND%
	CALL :Delete_Backup_Files %project-file-backup%, %DELETE_FILE_COMMAND%
	CALL :Delete_Backup_Folders %settings-dir-backup%, %REMOVE_FOLDER_COMMAND%
	EXIT /B 0


REM This function moves files matching a regex to a destination
REM 1 - the regex matching files to be moved
REM 2 - the destination directory
:Copy_files 
    SET filenamepattern=%~1
    SET destination=%~2
    FOR /r %%z in (*%filenamepattern%*) DO (
        SET full-file-path=%%z
        ECHO copying "%%z"
        ECHO relative path: !full-file-path:%CD%\=!
		REM the * at the end of the following statement forces the shell to assume it's copying a file!
		REM Copy and overwrite
        XCOPY "%%z" ".."\%destination%\!full-file-path:%CD%\=!*
    )
    EXIT /B 0
	
:Copy_.SettingsFolder
	SETLOCAL
    SET destination-dir=".."\%~1
	REM Create a .settings folder if not available, then copy and overwrite all files from the old to the new .settings directory
	FOR /r /d %%x in (*%settings-dir%) DO (
		ECHO copying "%%x"
		SET full-dir-path=%%x 
		ECHO copying contents of %%x to %destination-dir%\!full-dir-path:%CD%\=!
		XCOPY %%x %destination-dir%\!full-dir-path:%CD%\=! /I /Y
	)
	
	FOR /r /d %%y in (*%settings-dir-backup%) DO (
		SET settings-backup-relative-path=!%%y:%root-dir%\=!
		SET full-dir-backup-path=%%y
		ECHO copying contents of %%y to %destination-dir%\!full-dir-backup-path:%CD%\=!
		XCOPY %%y %destination-dir%\!full-dir-backup-path:%CD%\=! /I /Y
	)
    ENDLOCAL
    EXIT /B 0

:Delete_Backup_Files
	SETLOCAL
    SET backup-file-to-delete=%~1
	SET file_delete_command=%~2
	FOR /r %%z in (*%backup-file-to-delete%*) DO (
        ECHO deleting "%%z"        		
        %file_delete_command% "%%z"
    )
	ENDLOCAL
    EXIT /B 0

:Delete_Backup_Folders
	SETLOCAL
    SET backup-folder-to-delete=%~1
	SET folder_delete_command=%~2
	REM Recursively go through directories only and delete the folder matching the given name
	FOR /r /d %%z in (*%backup-folder-to-delete%) DO (        
        ECHO deleting "%%z"        		
        %folder_delete_command% "%%z"
    )
	ENDLOCAL
    EXIT /B 0	
