#!/bin/bash

if [ $# != 1 ]; then
  echo "You need to pass the Github access token as a parameter."
  exit 1
fi

mvn build-helper:parse-version versions:set@patch
git add pom.xml
git commit -m "Patch version bump"
git push https://"$1"@github.com/clockify/addon-java-sdk.git
mvn deploy