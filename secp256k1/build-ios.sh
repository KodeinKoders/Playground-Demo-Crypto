#!/usr/bin/env bash
set -e

[[ -z "$ARCH" ]] && echo "Please set the ARCH variable" && exit 1

if [ "$ARCH" == "arm64" ]; then
  SDK="iphoneos"
  HOST_FLAGS="-arch arm64 -arch arm64e -miphoneos-version-min=10.0 -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
  CHOST="arm-apple-darwin"
elif [ "$ARCH" == "arm64-sim" ]; then
  SDK="iphonesimulator"
  HOST_FLAGS="-arch arm64 -arch arm64e -mios-simulator-version-min=10.0 -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
  CHOST="arm-apple-darwin"
elif [ "$ARCH" == "x86_64-sim" ]; then
  SDK="iphonesimulator"
  HOST_FLAGS="-arch x86_64 -mios-simulator-version-min=10.0 -isysroot $(xcrun --sdk ${SDK} --show-sdk-path)"
  CHOST="x86_64-apple-darwin"
else
  echo "Unsupported ARCH: $ARCH"
  exit 1
fi

export MACOSX_DEPLOYMENT_TARGET="10.4"
export CC=$(xcrun --find --sdk "${SDK}" clang)
export CXX=$(xcrun --find --sdk "${SDK}" clang++)
export CPP=$(xcrun --find --sdk "${SDK}" cpp)
export CFLAGS="${HOST_FLAGS} -O3 -g3 -fembed-bitcode"
export CXXFLAGS="${HOST_FLAGS} -O3 -g3 -fembed-bitcode"
export LDFLAGS="${HOST_FLAGS}"

cd secp256k1

./autogen.sh

./configure \
    --host="${CHOST}" \
    --enable-experimental \
    --enable-module_ecdh \
    --enable-module-recovery \
    --enable-benchmark=no \
    --enable-shared=no \
    --enable-exhaustive-tests=no \
    --enable-tests=no

make clean
make -j8

cd ..

mkdir -p build/ios/$ARCH
cp -v secp256k1/.libs/libsecp256k1.a build/ios/$ARCH
