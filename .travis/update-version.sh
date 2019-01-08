#!/usr/bin/env bash

cd `dirname $0`/..

if [[ -z "$TRAVIS_TAG" ]]
then
    echo "ERROR! Please set TRAVIS_TAG environment variable"
    exit 1
fi

if [[ -z "$GH_TOKEN" ]]
then
    echo "ERROR! Please set GH_TOKEN environment variable"
    exit 1
fi

BRANCH_NAME=""

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

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

update_version() {

    exampleProperty="graphql-kotlin.version"

    # Push the new as tag with `-SNAPSHOT`
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion="${TRAVIS_TAG}-SNAPSHOT"

    # Increment the patch version
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.7:set -DnextSnapshot=true

    # Pull the number from the pom
    NEW_VERSION=$(mvn --settings .travis/settings.xml help:evaluate -Dexpression=project.version -q -DforceStdout)

    # Update the example version
    cd example/
    mvn --settings ../.travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.7:set-property -Dproperty=${exampleProperty} -DnewVersion=${NEW_VERSION}
}

commit_files() {

  BRANCH_NAME=${NEW_VERSION}

  git checkout -b ${BRANCH_NAME}

  # Stage the modified files
  git add pom.xml example/pom.xml

  # Create a new commit with a custom build message and Travis build number for reference
  git commit -m "build: Upgrade to next snapshot (Build $TRAVIS_BUILD_NUMBER)"
}

upload_files() {

  origin="https://${GH_TOKEN}@github.com/ExpediaDotCom/graphql-kotlin.git"

  # Remove existing "origin"
  git remote rm origin

  # Add new "origin" with access token in the git URL for authentication
  git remote add origin ${origin} > /dev/null 2>&1

  # Push changes to the new branch
  git push --quiet --set-upstream origin ${BRANCH_NAME}
}

