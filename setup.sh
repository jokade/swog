#! /bin/bash
#
# setup.sh
# 
# Sets this project up using a RAM disk for builds
#
RAMDISKSIZE=512  # in MB
mkdir -p ramdisk
if [ -e .ramdevice ]
then
  echo ".ramdevice already exists!"
  exit 1
fi

size=$(($RAMDISKSIZE * 1024 * 2))  # size in 512 blocks
hdiutil attach -nomount -nobrowse ram://$size > .ramdevice
ramdevice=$(cat .ramdevice)
newfs_hfs -v swog $ramdevice
mount -t hfs -v $ramdevice ramdisk/

echo "mounted ramdisk"

rootdir=$(realpath .)

for proj in target\
  project/target\
  cobj/jvm/target cobj/native/target cobj/shared/target\
  cobjTests/jvm/target cobjTests/native/target cobjTests/shared/target\
  cxx/target\
  cxxTests/target\
  cxxlib/target\
  interop/jvm/target interop/native/target interop/shared/target\
  interopTests/jvm/target interopTests/native/target interopTests/shared/target\
  objc/target\
  objcTests/target
do
  echo "setting up $proj"
  mkdir -p ramdisk/$proj
  rm -rf $proj
  ln -s $rootdir/ramdisk/$proj $proj
done

echo "created build directories"
