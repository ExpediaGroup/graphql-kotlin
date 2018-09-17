#!/bin/bash
cd `dirname $0`/..

if [ "$TRAVIS_BRANCH" != 'master' ] || [ "$TRAVIS_PULL_REQUEST" == 'true' ]; then
    echo "Skipping artifact deployment for a non-release build"
    exit 0
fi

if [[ -z "$SONATYPE_USERNAME" || -z "$SONATYPE_PASSWORD" ]]; then
    echo "ERROR! Please set SONATYPE_USERNAME and SONATYPE_PASSWORD environment variable"
    exit 1
fi

if [ ! -z "$TRAVIS_TAG" ]; then
    echo "travis tag is set -> updating pom.xml <version> attribute to $TRAVIS_TAG"
    ./mvnw --batch-mode --settings .travis/settings.xml --no-snapshot-updates -Prelease -DskipTests=true -DreleaseVersion=$TRAVIS_TAG release:prepare
else
    echo "no travis tag is set, hence keeping the snapshot version in pom.xml"
fi

./mvnw --batch-mode --settings .travis/settings.xml -Prelease clean deploy -DskipTests=true -B -U
SUCCESS=$?
if [ ${SUCCESS} -eq 0 ]; then
    echo "successfully deployed the jars to nexus"
fi

exit ${SUCCESS}