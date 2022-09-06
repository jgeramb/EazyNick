@echo off
cd /D "%~dp0target\classes"

set version=<..\..\.github\.version

sed -i '' "s/0.0.0/%version%/g" plugin.yml
jar cvf "..\EazyNick-%version%-dev.jar" .