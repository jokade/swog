#! /bin/bash
#
# cleanup.sh
#

if [[ -e .ramdevice ]]
then
  ramdevice=$(cat .ramdevice) 
  umount ramdisk  
  hdiutil detach $ramdevice
  rm .ramdevice
  echo done
else
  echo "no .ramdevice file"
fi

