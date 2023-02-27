#!/bin/bash

VERSION_TYPE="patch"

if [ $# != 2 ]; then
  echo "Required parameters: githubAccessToken pullRequestLabels"
  exit 1
fi

case "$2" in
*patch*)
  VERSION_TYPE="patch"
  ;;
*minor*)
  VERSION_TYPE="minor"
  ;;
*major*)
  VERSION_TYPE="major"
  ;;
esac

mvn build-helper:parse-version versions:set@$VERSION_TYPE
git add pom.xml
git commit -c user.name="Clockify Bot" -c user.email="clockify-boy@clockify.me" -m "$VERSION_TYPE version bump"
git push https://"$1"@github.com/clockify/addon-java-sdk.git
mvn deploy

