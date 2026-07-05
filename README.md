# crazy-cam

Camera app for Android that applies memory-safe filters to the live camera stream using Rust + UniFFI.

## Architecture

- **Kotlin / Jetpack Compose** frontend + CameraX
- **Rust** (via UniFFI) for all image filter logic (memory safety + performance)

## Project Structure

```
crazy-cam/
├── app/                    # Android app (initialize with Android Studio)
├── rust/
│   └── crazy_cam_filters/   # UniFFI Rust library
│       ├── Cargo.toml
│       ├── uniffi.toml
│       └── src/
│           ├── crazy_cam_filters.udl
│           └── lib.rs
└── README.md
```

## Quick Start

### 1. Rust Library (already set up)

```bash
cd rust/crazy_cam_filters

# Build for Android
cargo ndk \
    -t arm64-v8a -t armeabi-v7a -t x86_64 -t x86 \
    -o ../../app/src/main/jniLibs \
    build --release

# Generate Kotlin bindings
cargo run --bin uniffi-bindgen generate \
    --library ../../app/src/main/jniLibs/arm64-v8a/libcrazy_cam_filters.so \
    --language kotlin \
    --out-dir ../../app/src/main/java/com/example/crazycam/
```

### 2. Android App

1. Open the `app/` folder in Android Studio (or create a new Android project and copy the rust/ folder next to it).
2. Add CameraX dependencies.
3. Use the generated `CrazyCamFilters` object to call `processFrame(...)` from your `ImageAnalysis.Analyzer`.

See the Rust code for available filters (grayscale, invert, brightness). Easy to extend.

## Next Steps

- Add more filters in `rust/crazy_cam_filters/src/lib.rs`
- Improve YUV → RGB conversion (currently expects RGB bytes)
- Add Gradle plugin to automate build + bindgen
- Consider switching some effects to GPU (wgpu) for "crazy" real-time performance

Built with memory safety in mind using Rust + UniFFI.