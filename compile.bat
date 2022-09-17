@echo off
cd /D "%~dp0target\classes"
set /p version=<..\..\.github\.version

sed -i "s/0.0.0/%version%/g" plugin.yml
del sed*
jar cvf "..\EazyNick-%version%-dev.jar" .