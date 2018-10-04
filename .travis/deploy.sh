#!/bin/bash -ex
cd `dirname $0`/..

echo TRAVIS_BRANCH=$TRAVIS_BRANCH
echo TRAVIS_PULL_REQUEST=$TRAVIS_PULL_REQUEST
echo TRAVIS_TAG=$TRAVIS_TAG

if [ "$TRAVIS_BRANCH" == 'master' ] || [ "$TRAVIS_PULL_REQUEST" == 'true' ] || [ -z "$TRAVIS_TAG" ]; then
    echo "Skipping artifact deployment for a non-release build"
    exit 0
fi

if [[ -z "$SONATYPE_USERNAME" || -z "$SONATYPE_PASSWORD" ]]; then
    echo "ERROR! Please set SONATYPE_USERNAME and SONATYPE_PASSWORD environment variable"
    exit 1
fi

echo "travis tag is set -> updating pom.xml <version> attribute to $TRAVIS_TAG"
./mvnw --batch-mode release:update-versions -DdevelopmentVersion=$TRAVIS_TAG-SNAPSHOT
git checkout -b release-$TRAVIS_TAG
./mvnw --batch-mode --settings .travis/settings.xml --no-snapshot-updates -Prelease -DpushChanges=false -DskipTests=true -DreleaseVersion=$TRAVIS_TAG -DcheckModificationExcludeList=pom.xml release:prepare
./mvnw --batch-mode --settings .travis/settings.xml -Prelease clean deploy -DpushChanges=false -DskipTests=true -B -U
echo "successfully deployed the jars to nexus"
