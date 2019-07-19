#!/usr/bin/env bash

cd `dirname $0`/..

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

update_version() {
    # Push the new tag with `-SNAPSHOT` as the current version
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion="${TRAVIS_TAG}-SNAPSHOT"

    # Increment the patch version
    mvn --settings .travis/settings.xml release:update-versions -B

    # Pull the value from the pom
    NEW_VERSION=$(mvn --settings .travis/settings.xml help:evaluate -Dexpression=project.version -q -DforceStdout)
}

commit_files() {

  # Use the version as the branch name
  git checkout -b ${NEW_VERSION}

  # Stage the modified files
  git add pom.xml */pom.xml

  # Create a new commit with a custom build message and Travis build number for reference
  git commit -m "build: $NEW_VERSION (Travis Build $TRAVIS_BUILD_NUMBER)"
}

upload_files() {

  tokenLink="https://${GITHUB_TOKEN}@github.com/ExpediaDotCom/graphql-kotlin.git"

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

setup_git

update_version

commit_files

# Attempt to push to git only if "git commit" succeeded
if [[ $? -eq 0 ]]; then
  echo "Uploading to GitHub"
  upload_files
else
  echo "Nothing to do"
fi



