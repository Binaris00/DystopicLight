#!/usr/bin/env bash
set -euo pipefail

SPIGOT_API=$(find "$HOME/.gradle/caches/modules-2/files-2.1/org.spigotmc/spigot-api/1.21.1-R0.1-SNAPSHOT" -name "spigot-api-1.21.1-R0.1-SNAPSHOT.jar" | head -1)
if [ -z "$SPIGOT_API" ]; then
  echo "ERROR: spigot-api jar not found in gradle cache" >&2
  exit 1
fi

cd "$(dirname "$0")"
rm -rf build/out build/DiagnoseWeather.jar
mkdir -p build/out
javac -encoding UTF-8 -cp "$SPIGOT_API" -d build/out $(find src/main/java -name "*.java")
cp src/main/resources/plugin.yml build/out/
jar cf build/DiagnoseWeather.jar -C build/out .
echo "OK: build/DiagnoseWeather.jar"