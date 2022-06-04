#!/bin/sh
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$ANDROID_KEYS_ZIP_PASSPHRASE" \
--output key_files.zip key_files.zip.gpg && jar xvf key_files.zip && cd -
ls -d $PWD/*
mv google-services.json ./app
rm -rf __MACOSX key_files.zip
# mv ./android/expensemanager.jks ./android/app
# move your file according to path in key.properties