# crazy-cam

**Memory-safe camera app** using **Kotlin + Jetpack Compose** + **Rust + UniFFI** for live image filtering.

## Features
- Live camera preview with real-time Rust filters
- Memory-safe image processing in Rust
- Easy to extend with new filters

## Quick Start (Arch Linux)

### 1. Prerequisites

```bash
# Install Rust
yay -S rustup
rustup default stable

# Install Android Studio + NDK (via SDK Manager)
```

### 2. Clone & Setup

```bash
git clone https://github.com/17xbaphomet/crazy-cam.git
cd crazy-cam

# Make build script executable
chmod +x build_rust.sh

# Build Rust library + generate bindings
./build_rust.sh
```

### 3. Open in Android Studio

1. Open Android Studio
2. **File → Open** → select the `crazy-cam` folder
3. Wait for Gradle sync
4. Run the app on a device/emulator

The app will request camera permission and show live filtered camera frames.

## How it works

- CameraX captures frames
- Frames are passed to Rust via UniFFI
- Rust applies filters using the `image` crate (safe & fast)
- Processed frames are displayed in Compose

## Adding New Filters

Edit `rust/crazy_cam_filters/src/lib.rs` and add new functions.
Then re-run `./build_rust.sh`.

## Project Structure

```
crazy-cam/
├── app/                    # Android app (Kotlin + Compose + CameraX)
├── rust/crazy_cam_filters/ # Rust + UniFFI filter library
├── build_rust.sh           # One-command build for Rust part
└── README.md
```