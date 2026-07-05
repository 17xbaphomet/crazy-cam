#!/bin/bash
set -e

echo "=== Building Rust library for Android ==="

cd rust/crazy_cam_filters

# Install dependencies if needed
if ! command -v cargo-ndk &> /dev/null; then
    echo "Installing cargo-ndk..."
    cargo install cargo-ndk
fi

rustup target add aarch64-linux-android armv7-linux-androideabi x86_64-linux-android i686-linux-android 2>/dev/null || true

# Set NDK path if not set
export ANDROID_NDK_HOME=${ANDROID_NDK_HOME:-$HOME/Android/Sdk/ndk/26.1.10909125}

cargo ndk \
    -t arm64-v8a -t armeabi-v7a -t x86_64 -t x86 \
    -o ../../app/src/main/jniLibs \
    build --release

echo "=== Generating Kotlin bindings ==="
cargo run --bin uniffi-bindgen generate \
    --library ../../app/src/main/jniLibs/arm64-v8a/libcrazy_cam_filters.so \
    --language kotlin \
    --out-dir ../../app/src/main/java/com/example/crazycam/

echo "=== Done! Now sync in Android Studio ==="