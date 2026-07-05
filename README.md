# crazy-cam

**Memory-safe camera app** using **Kotlin + Jetpack Compose** + **Rust + UniFFI** (proc-macro style).

## Quick Start (Arch Linux)

```bash
git clone https://github.com/17xbaphomet/crazy-cam.git
cd crazy-cam
chmod +x build_rust.sh
./build_rust.sh
```

Then open the project in Android Studio and run the app.

The `build_rust.sh` script now handles everything:
- Building the Rust library for Android
- Generating the Kotlin bindings

## Current Filter

Currently only `grayscale` is active in `process_frame`. You can easily add more filters using `#[uniffi::export]`.