#!/bin/bash
set -e

echo "=== Building Rust library for Android (proc-macro style) ==="

cd rust/crazy_cam_filters

if ! command -v cargo-ndk &> /dev/null; then
    echo "Installing cargo-ndk..."
    cargo install cargo-ndk
fi

rustup target add aarch64-linux-android armv7-linux-androideabi x86_64-linux-android i686-linux-android 2>/dev/null || true

export ANDROID_NDK_HOME=${ANDROID_NDK_HOME:-$HOME/Android/Sdk/ndk/27.0.12077973}

cargo ndk \
    -t arm64-v8a -t armeabi-v7a -t x86_64 -t x86 \
    -o ../../app/src/main/jniLibs \
    build --release

echo "=== Generating Kotlin bindings ==="

# Run the bindgen tool
cargo run --bin uniffi-bindgen -- generate \
    --library ../../app/src/main/jniLibs/arm64-v8a/libcrazy_cam_filters.so \
    --language kotlin \
    --out-dir ../../app/src/main/java/com/example/crazycam/

echo "=== Done! Now sync Gradle in Android Studio ==="