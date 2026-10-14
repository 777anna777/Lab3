#!/bin/sh
# Lightweight bootstrap launcher for environments without a preinstalled Gradle.
# Uses the official Gradle distribution, kept outside the Git repository.
set -eu
VERSION=9.8.1
BASE="${GRADLE_USER_HOME:-$HOME/.gradle}/bootstrap-distributions"
DIST="$BASE/gradle-$VERSION"
if [ ! -x "$DIST/bin/gradle" ]; then
  mkdir -p "$BASE"
  ZIP="$BASE/gradle-$VERSION-bin.zip"
  curl -fL --retry 3 -o "$ZIP" "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip"
  unzip -oq "$ZIP" -d "$BASE"
  rm -f "$ZIP"
fi
exec "$DIST/bin/gradle" "$@"
