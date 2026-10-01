#!/bin/sh
# Dev entrypoint: recompile on source changes and let Spring Boot DevTools
# restart the app when build/classes changes.
set -e

./gradlew classes -x test

./gradlew --continuous classes -x test &

exec ./gradlew bootRun -x test
