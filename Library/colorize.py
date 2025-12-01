import argparse
import os
import sys
from pathlib import Path
from deoldify.visualize import *
import warnings
warnings.filterwarnings("ignore")

# Enable progress bars
from fastprogress import master_bar, progress_bar
import fastai

def main():
    parser = argparse.ArgumentParser(description='Colorize images with DeOldify')
    parser.add_argument('input', help='Input image path')
    parser.add_argument('-m', '--model', 
                        choices=['artistic', 'stable', 'video'],
                        default='artistic',
                        help='Model to use: artistic (vibrant), stable (realistic), or video (temporal consistency)')
    parser.add_argument('--model-path', help='Custom path to model file (.pth) - overrides --model')
    parser.add_argument('-o', '--output', help='Output directory', default='result_images')
    parser.add_argument('-r', '--render-factor', type=int, default=35,
                        help='Render factor (10-40, higher=better quality)')
    
    args = parser.parse_args()
    
    # If custom model path provided, use that
    if args.model_path:
        model_path = Path(args.model_path).resolve()
        if not model_path.exists():
            print(f"ERROR: Model file not found at {model_path}")
            return
        
        print(f"Using custom model: {model_path}")
        
        # Create models directory
        deoldify_models = Path('./models')
        deoldify_models.mkdir(exist_ok=True)
        
        # Determine target name from custom model filename
        if 'artistic' in model_path.name.lower():
            target_name = 'ColorizeArtistic_gen.pth'
            model_type = 'artistic'
        elif 'video' in model_path.name.lower():
            target_name = 'ColorizeVideo_gen.pth'
            model_type = 'video'
        else:
            target_name = 'ColorizeStable_gen.pth'
            model_type = 'stable'
        
        target_path = deoldify_models / target_name
        
        # Create symlink or copy
        if not target_path.exists() or target_path.resolve() != model_path:
            if target_path.exists():
                target_path.unlink()
            try:
                os.symlink(model_path, target_path)
                print(f"Created symlink: {target_path} -> {model_path}")
            except OSError:
                import shutil
                shutil.copy(model_path, target_path)
                print(f"Copied model to: {target_path}")
    else:
        # Use built-in model selection
        model_type = args.model
        print(f"Using {args.model} model")
    
    print(f"Colorizing: {args.input}")
    print(f"Render factor: {args.render_factor}")
    print("Processing...")
    sys.stdout.flush()
    
    # Initialize colorizer based on model type
    if model_type == 'artistic':
        colorizer = get_image_colorizer(artistic=True)
    elif model_type == 'video':
        colorizer = get_video_colorizer()
    else:  # stable
        colorizer = get_stable_image_colorizer()
    
    # Colorize
    colorizer.plot_transformed_image(
        path=args.input,
        render_factor=args.render_factor,
        results_dir=Path(args.output)
    )
    
    print(f"Done! Check {args.output}/ for your colorized image")

if __name__ == '__main__':
    main()