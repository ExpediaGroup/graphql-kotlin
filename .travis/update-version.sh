#!/usr/bin/env bash

setup_git() {
    echo "Setting up git config"
    git config --global user.email "travis@travis-ci.org"
    git config --global user.name "Travis CI"
}

update_version() {
    echo "Updating the version in the POMs"

    # Push the tag with `-SNAPSHOT` as the current version
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion="${TRAVIS_TAG}-SNAPSHOT"

    # Increment the patch version
    mvn --settings .travis/settings.xml release:update-versions -B -PdefaultBuild

    # Pull the new value from the pom
    NEW_VERSION=$(mvn --settings .travis/settings.xml help:evaluate -Dexpression=project.version -q -DforceStdout)
}

commit_files() {
    echo "Commiting the changes to git"

    # Use the version as the branch name
    git checkout -b ${NEW_VERSION}

    # Stage the modified files
    git add ./pom.xml ./**/*/pom.xml

    # Print the current files we are going to commit
    git status

    # Create a new commit with a custom build message and Travis build number for reference
    git commit -m "build: $NEW_VERSION (Travis Build $TRAVIS_BUILD_NUMBER)"
}

upload_files() {
    echo "Pushing the changes to git remote"

    tokenLink="https://${GITHUB_TOKEN}@github.com/ExpediaGroup/graphql-kotlin.git"

    # Add new "origin" with access token in the git URL for authentication
    git remote add token-origin ${tokenLink} > /dev/null 2>&1

    # Push changes to the new branch
    git push --quiet --set-upstream token-origin ${NEW_VERSION}

    # Remove the origin to hide the token
    git remote rm token-origin > /dev/null 2>&1
}

if [[ -z "$TRAVIS_TAG" ]]
then
    echo "ERROR! Please set TRAVIS_TAG environment variable"
    exit 1
fi

if [[ -z "$GITHUB_TOKEN" ]]
then
    echo "ERROR! Please set GITHUB_TOKEN environment variable"
    exit 1
fi

NEW_VERSION=""

cd `dirname $0`/..

pwd

setup_git

update_version

commit_files

# Attempt to push to git only if "git commit" succeeded
if [[ $? -eq 0 ]]; then
  upload_files
else
  echo "No files commited. Not pushing anything to git."
fi
