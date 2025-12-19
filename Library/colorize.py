#!/usr/bin/env python3

import argparse
import warnings
from pathlib import Path

from deoldify.visualize import (
    get_image_colorizer,
    get_video_colorizer
)

# ----------------------------------------------------------------------
# Optional: silence known non-actionable warnings
# ----------------------------------------------------------------------

warnings.filterwarnings(
    "ignore",
    message="Your training set is empty.*"
)

warnings.filterwarnings(
    "ignore",
    message="Your validation set is empty.*"
)

warnings.filterwarnings(
    "ignore",
    message="The parameter 'pretrained' is deprecated.*"
)

warnings.filterwarnings(
    "ignore",
    message="Arguments other than a weight enum.*"
)

warnings.filterwarnings(
    "ignore",
    message="torch.nn.utils.weight_norm is deprecated.*"
)

# ----------------------------------------------------------------------
# CLI
# ----------------------------------------------------------------------

def parse_args():
    parser = argparse.ArgumentParser(
        description="DeOldify CLI (image or video via explicit --model)"
    )

    parser.add_argument(
        "input",
        type=Path,
        help="Input image or video file"
    )

    parser.add_argument(
        "--model",
        choices=["artistic", "stable", "video"],
        default="artistic",
        help="Colorization model to use"
    )

    parser.add_argument(
        "--render-factor",
        type=int,
        help="Render factor (image default: 35, video default: 21)"
    )

    parser.add_argument(
        "--no-watermark",
        action="store_true",
        help="Disable watermark"
    )

    parser.add_argument(
        "--no-post-process",
        action="store_true",
        help="Disable post-processing (video only)"
    )

    return parser.parse_args()


# ----------------------------------------------------------------------
# Main
# ----------------------------------------------------------------------

def main():
    args = parse_args()

    input_path = args.input

    if not input_path.exists():
        raise FileNotFoundError(f"Input file not found: {input_path}")

    # --------------------------------------------------------------
    # VIDEO MODE (explicit)
    # --------------------------------------------------------------
    if args.model == "video":

        colorizer = get_video_colorizer()

        colorizer.colorize_from_file_name(
            file_name=str(input_path),
            render_factor=args.render_factor or 21,
            watermarked=not args.no_watermark,
            post_process=not args.no_post_process
        )

        print("Video frames colorized (video build disabled)")
        return

    # --------------------------------------------------------------
    # IMAGE MODE (default)
    # --------------------------------------------------------------
    artistic = (args.model == "artistic")

    colorizer = get_image_colorizer(artistic=artistic)

    colorizer.plot_transformed_image(
        path=str(input_path),
        render_factor=args.render_factor or 35,
        compare=False,
        watermarked=not args.no_watermark
    )

    print("Image colorized")


# ----------------------------------------------------------------------
# Entrypoint
# ----------------------------------------------------------------------

if __name__ == "__main__":
    main()
