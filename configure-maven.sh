#!/bin/bash
if [ $# != 2 ]; then
  echo "You need to pass the Github access token and the repository path as parameters."
  exit 1
fi

m2="<settings>
  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/$2</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>OWNER</username>
      <password>$1</password>
    </server>
  </servers>
</settings>"

echo "$m2" > ~/.m2/settings.xml