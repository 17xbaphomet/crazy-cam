use image::{ImageBuffer, Rgb, RgbImage};

uniffi::include_scaffolding!("crazy_cam_filters");

pub fn process_frame(width: i32, height: i32, rgb_data: Vec<u8>) -> Vec<u8> {
    let img: RgbImage = match ImageBuffer::from_raw(
        width as u32,
        height as u32,
        rgb_data,
    ) {
        Some(i) => i,
        None => return vec![],
    };

    // === Change this line to switch filters ===
    let processed = apply_grayscale(&img);
    // let processed = apply_invert(&img);
    // let processed = apply_brightness(&img, 1.4);

    processed.into_raw()
}

// ==================== Filter implementations ====================

fn apply_grayscale(img: &RgbImage) -> RgbImage {
    let mut out = img.clone();
    for pixel in out.pixels_mut() {
        let luma = (0.299 * pixel[0] as f32 +
                    0.587 * pixel[1] as f32 +
                    0.114 * pixel[2] as f32) as u8;
        *pixel = Rgb([luma, luma, luma]);
    }
    out
}

fn apply_invert(img: &RgbImage) -> RgbImage {
    let mut out = img.clone();
    for pixel in out.pixels_mut() {
        pixel[0] = 255 - pixel[0];
        pixel[1] = 255 - pixel[1];
        pixel[2] = 255 - pixel[2];
    }
    out
}

fn apply_brightness(img: &RgbImage, factor: f32) -> RgbImage {
    let mut out = img.clone();
    for pixel in out.pixels_mut() {
        for c in 0..3 {
            let val = (pixel[c] as f32 * factor).clamp(0.0, 255.0) as u8;
            pixel[c] = val;
        }
    }
    out
}