#!/bin/bash -x

if [[ ! -z "$GPG_SECRET_KEYS" ]]
then
    echo $GPG_SECRET_KEYS | base64 --decode | $GPG_EXECUTABLE --import;
fi

if [[ ! -z "$GPG_OWNERTRUST" ]]
then
    echo $GPG_OWNERTRUST | base64 --decode | $GPG_EXECUTABLE --import-ownertrust;
fi
