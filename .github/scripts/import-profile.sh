#!/bin/bash

set -euo pipefail

mkdir -p ~/Library/MobileDevice/Provisioning\ Profiles
FILE=profile.mobileprovision
echo "$PROVISIONING_PROFILE_DATA" | base64 --decode > $FILE
UUID=`/usr/libexec/PlistBuddy -c 'Print :UUID' /dev/stdin <<< $(security cms -D -i ./$FILE)`
mv $FILE ~/Library/MobileDevice/Provisioning\ Profiles/\$UUID.mobileprovision
#mkdir -p ~/Library/MobileDevice/Provisioning\ Profiles
#echo "$PROVISIONING_PROFILE_DATA" | base64 --decode > ~/Library/MobileDevice/Provisioning\ Profiles/$UUID.mobileprovision