#!/bin/bash

set -euo pipefail

mkdir -p ~/Library/MobileDevice/Provisioning\ Profiles
echo "$PROVISIONING_PROFILE_DATA" | base64 --decode > ~/Library/MobileDevice/Provisioning\ Profiles/profile.mobileprovision
UUID=´/usr/libexec/PlistBuddy -c 'Print :UUID' /dev/stdin <<< $(security cms -D -i -./$FILE)´
mv profile.mobileprovision ~/Library/MobileDevice/Provisioning\ Profiles\$UUID.mobileprovision
#mkdir -p ~/Library/MobileDevice/Provisioning\ Profiles
#echo "$PROVISIONING_PROFILE_DATA" | base64 --decode > ~/Library/MobileDevice/Provisioning\ Profiles/$UUID.mobileprovision