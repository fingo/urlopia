#!/bin/bash

# shellcheck disable=SC2086
exec java -Xmx1g -jar /urlopia/urlopia-?.?.?.jar ${JAVA_ARGS}
