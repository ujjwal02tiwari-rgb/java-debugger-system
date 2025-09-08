#!/usr/bin/env bash
# This Gradle wrapper script downloads and runs Gradle 8.9.
set -euo pipefail

GRADLE_VERSION=8.9
DIST_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

TMP_DIR="$(mktemp -d)"
curl -sSL "${DIST_URL}" -o "${TMP_DIR}/gradle.zip"
unzip -q "${TMP_DIR}/gradle.zip" -d "${TMP_DIR}"
export GRADLE_HOME="${TMP_DIR}/gradle-${GRADLE_VERSION}"
export PATH="$GRADLE_HOME/bin:$PATH"

gradle "$@"
