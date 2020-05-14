@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Set docker env script for Windows
@rem
@rem ##########################################################################

if "%DOCKER_HOST%" == "" setx DOCKER_HOST "localhost:2375"
echo DOCKER_HOST: %DOCKER_HOST%

if "%DOCKER_HOST_IP%" == "" for /f "tokens=2 delims=[]" %%i in ('ping -n 1 -4 "%ComputerName%" ^| findstr [') do setx DOCKER_HOST_IP "%%i"
echo DOCKER_HOST_IP: %DOCKER_HOST_IP%

if "%COMPOSE_HTTP_TIMEOUT%" == "" setx COMPOSE_HTTP_TIMEOUT "240"
echo COMPOSE_HTTP_TIMEOUT: %COMPOSE_HTTP_TIMEOUT%

refreshenv