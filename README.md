# Shutter Encoder

[![GitHub stars](https://img.shields.io/github/stars/paulpacifico/shutter-encoder?style=flat-square)](https://github.com/paulpacifico/shutter-encoder/stargazers)
[![GitHub license](https://img.shields.io/github/license/paulpacifico/shutter-encoder?style=flat-square)](LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/paulpacifico/shutter-encoder?style=flat-square)](https://github.com/paulpacifico/shutter-encoder/commits)

<img src="https://www.shutterencoder.com/images/SocialBanner_2025.jpg">

## Overview

Shutter Encoder is a free and open-source media transcoding, conversion, and processing application built on top of **FFmpeg**.  
It is designed for video editors, post-production professionals, and advanced users who require reliable, reproducible, and transparent media workflows through a graphical interface.

The application supports batch processing, presets, and lossless operations, making it suitable for both quick conversions and complex production pipelines.

---

## Features

### Media Encoding and Conversion

- Video, audio, and image transcoding using FFmpeg
- Support for common and professional codecs (H.264, H.265, ProRes, DNxHR, AV1, VP9, etc.)
- Container remuxing without re-encoding
- Image sequence generation and conversion

### Lossless Operations

- Lossless cut and trim
- Audio track replacement and removal
- Stream extraction and remuxing
- Subtitle extraction and multiplexing

### Editing and Processing Tools

- Subtitle embedding, burn-in, and editing (.srt, .vtt, .ass)
- Loudness analysis and normalization
- Black frame and Media offline detection
- Cropping, scaling, padding, and aspect-ratio control
- LUT support and color adjustments
- Watermark and overlay insertion

### Workflow and Automation

- Batch queue processing
- Preset creation and reuse
- File renaming rules
- Media information and metadata inspection
- Optional FTP upload on completion
- EDL export for detected cuts

### Additional Utilities

- Web video download (via yt-dlp)
- RAW image processing (LibRaw)
- MediaInfo integration
- EXIF metadata handling

### Prebuilt Binaries

Installers and portable versions for Windows, macOS, and Linux are available from the official website:

https://www.shutterencoder.com/#downloads

### Changelog

https://www.shutterencoder.com/changelog/

### Build from Source

- Required third-party dependencies must be downloaded into the `Library` folder.
- Fonts are bundled directly within the custom Java Runtime Environment (JRE).

Shutter Encoder uses a custom Java runtime built with **jlink**, based on **Java 25**, using the following configuration:

<code>--compress 0 --strip-debug --no-header-files --no-man-pages --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.security.sasl,java.xml,jdk.crypto.ec --output JRE</code>

---
