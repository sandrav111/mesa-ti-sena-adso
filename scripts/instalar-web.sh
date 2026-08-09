#!/usr/bin/env bash

set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/../aplicacion-web" && pwd)"

if ! command -v mvn >/dev/null 2>&1; then
    printf 'Maven no está instalado o no está en PATH.\n' >&2
    exit 1
fi

if [[ -z "${JAVA_HOME:-}" || ! -x "$JAVA_HOME/bin/java" ]]; then
    printf 'Define JAVA_HOME apuntando a Java 21 antes de ejecutar este script.\n' >&2
    exit 1
fi

cd "$project_dir"
mvn test package
printf 'Aplicación web compilada en %s/target/mesa-ti.war\n' "$project_dir"
