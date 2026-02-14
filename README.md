# 🎬 Shutter Encoder

[![GitHub stars](https://img.shields.io/github/stars/paulpacifico/shutter-encoder?style=flat-square)](https://github.com/paulpacifico/shutter-encoder/stargazers)
[![GitHub license](https://img.shields.io/github/license/paulpacifico/shutter-encoder?style=flat-square)](LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/paulpacifico/shutter-encoder?style=flat-square)](https://github.com/paulpacifico/shutter-encoder/commits)

<img src="https://www.shutterencoder.com/images/SocialBanner_2025.jpg">

## 📌 Overview

Shutter Encoder is a **free and open-source** media transcoding, conversion, and processing application built on top of **FFmpeg**.  
It is designed for video editors, post-production professionals, and advanced users who require reliable, reproducible, and transparent media workflows through a graphical interface.

The application supports **batch processing**, **presets**, and **lossless operations**, making it suitable for both quick conversions and complex production pipelines.

---

## ✨ Features

### 🎞️ Media Encoding and Conversion

- Video, audio, and image transcoding using FFmpeg
- Support for common and professional codecs (H.264, H.265, ProRes, DNxHR, AV1, VP9, etc.)
- Container remuxing without re-encoding
- Image sequence generation and conversion

### ♾️ Lossless Operations

- Lossless cut and trim
- Audio track replacement and removal
- Stream extraction and remuxing
- Subtitle extraction and multiplexing

### 🛠️ Editing and Processing Tools

- Subtitle embedding, burn-in, and editing (.srt, .vtt, .ass)
- Loudness analysis and normalization
- Black frame and Media offline detection
- Cropping, scaling, padding, and aspect-ratio control
- LUT support and color adjustments
- Watermark and overlay insertion

### 🔄 Workflow and Automation

- Batch queue processing
- Preset creation and reuse
- File renaming rules
- Media information and metadata inspection
- Optional FTP upload on completion
- EDL export for detected cuts

### 🧩 Additional Utilities

- Web video download (via yt-dlp)
- RAW image processing (LibRaw)
- MediaInfo integration
- EXIF metadata handling

---

### 📦 Prebuilt Binaries

Installers and portable versions for **Windows**, **macOS**, and **Linux** are available from the official website:

🔗 https://www.shutterencoder.com/#downloads

---

### 📝 Changelog

🔗 https://www.shutterencoder.com/changelog/

---

### 🧱 Build from Source

- Required third-party dependencies must be downloaded into the `Library` folder.
- Fonts are bundled directly within the custom Java Runtime Environment (JRE).

Shutter Encoder uses a **custom Java runtime** built with **jlink**, based on **Java 25**, using the following configuration:

<code>--compress 0 --strip-debug --no-header-files --no-man-pages --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.security.sasl,java.xml,jdk.crypto.ec --output JRE</code>

---

### 📚 Open-Source Code Used

- [Real-ESRGAN-ncnn-vulkan](https://github.com/xinntao/Real-ESRGAN-ncnn-vulkan) — High-performance neural network inference framework (used internally for AI-based upscaling)
- [Whisper-Ctranslate2](https://github.com/Softcatala/whisper-ctranslate2) — High-Performance Speech-to-Text (used for audio transcription)
- [BackgroundRemover](https://github.com/nadermx/backgroundremover) — AI-Powered Image/Video Matting (used for background removal)
- [Demucs](https://github.com/facebookresearch/demucs) — Music Source Separation (used for audio separation)
- [DeOldify](https://github.com/jantic/DeOldify) — Image and Video Restoration (used of colorization)
- [FFmpeg](https://ffmpeg.org/) — Audio/video encoding, decoding, filtering, muxing and transcoding (core processing engine of Shutter Encoder)
- [7-Zip](https://www.7-zip.org/) — Archive extraction and compression
- [yt-dlp](https://github.com/yt-dlp/yt-dlp) — Web video downloading engine
- [LibRaw](https://www.libraw.org/) - RAW image decoding (used to import and convert digital camera RAW files)
- [ExifTool](https://exiftool.org/) - Metadata reading and writing (used for extracting, editing and preserving metadata)
- [tsMuxeR](https://github.com/justdan96/tsMuxer) - Transport stream muxing (used for Blu-ray and AVCHD structure creation)
- [MediaInfo](https://mediaarea.net/en/MediaInfo) - Media file technical analysis (used to display detailed file information)
- [dvdauthor](https://dvdauthor.sourceforge.net/) — DVD structure authoring (used for creating DVD-Video structures)
- [bmxtranswrap](https://github.com/bbc/bmx) - MXF rewrapping and OP-Atom/OP1a handling (used for broadcast MXF workflows)
  
---
