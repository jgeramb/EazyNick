#!/bin/bash
cd "$(dirname "$0")"/target/classes || exit

version=$(cat ../../.github/.version)

sed -i '' "s/0.0.0/$version/g" plugin.yml
jar cvf "../EazyNick-$version-dev.jar" ./*
exit