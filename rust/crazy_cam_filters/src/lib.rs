use image::{ImageBuffer, Rgb, RgbImage};

uniffi::setup_scaffolding!();

#[uniffi::export]
pub fn process_frame(width: i32, height: i32, rgb_data: Vec<u8>) -> Vec<u8> {
    let img: RgbImage = match ImageBuffer::from_raw(
        width as u32,
        height as u32,
        rgb_data,
    ) {
        Some(i) => i,
        None => return vec![],
    };

    // Change filter here or add more exported functions
    let processed = apply_grayscale(&img);
    processed.into_raw()
}

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

// Example: You can add more exported functions like this:
// #[uniffi::export]
// pub fn apply_invert(width: i32, height: i32, rgb_data: Vec<u8>) -> Vec<u8> { ... }