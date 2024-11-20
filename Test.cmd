@echo off
REM Check if TotoLeChien is defined
IF NOT DEFINED TotoLeChien (
    REM Set TotoLeChien to "1" if VSCODE_EXTENSIONS is defined, otherwise "0"
    IF DEFINED VSCODE_EXTENSIONS (
        SET TotoLeChien=1
        SETX TotoLeChien 1 >nul
    ) ELSE (
        SET TotoLeChien=0
        SETX TotoLeChien 0 >nul
    )
) ELSE (
    REM Read the persisted value of TotoLeChien into the script
    FOR /F "tokens=2 delims==" %%G IN ('SET TotoLeChien') DO SET TotoLeChien=%%G
)

REM Set the VSCODE_EXTENSIONS environment variable
SET VSCODE_EXTENSIONS=%USERPROFILE%\.vscode\extensions
SETX VSCODE_EXTENSIONS "%USERPROFILE%\.vscode\extensions" >nul

REM Set the local variable "version" based on TotoLeChien value
IF "%TotoLeChien%"=="1" (
    SET version=10
) ELSE (
    SET version=9
)

REM Display variables for verification
ECHO TotoLeChien=%TotoLeChien%
ECHO VSCODE_EXTENSIONS=%VSCODE_EXTENSIONS%
ECHO version=%version%

REM Pause to view output (Optional)
PAUSE
