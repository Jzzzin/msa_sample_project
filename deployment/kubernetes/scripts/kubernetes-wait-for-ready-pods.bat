@echo off

:_loop
for %%i in (%*) do (
    for /f "tokens=*" %%F in ('kubectl get pod %%i -o^=jsonpath^={.status.containerStatuses[0].ready}') do (
        if NOT "%%F" == "true" (
            echo |set /p s=.
            timeout 1 > NUL
            goto _loop
        )
    )
)

echo connected

