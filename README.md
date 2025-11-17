# DrumScore Asset Generator

A Java application that generates high-quality musical notation assets for drum score editing applications using the Bravura music font.

## Overview

DSEAssetGen (DrumScore Editor Asset Generator) creates PNG images of time signatures and tempo markings in multiple sizes. These assets are designed for use in drum score editing applications and are rendered using the industry-standard Bravura music font, which follows the SMuFL (Standard Music Font Layout) specification.

## Features

- **Time Signature Generation**: Renders time signature images for common signatures:
  - 2/4, 3/4, 4/4
  - 6/8, 9/8, 12/8
  - 2/2
  - Common time (C)
  - Cut common time (¢)

- **Tempo Markings**: Generates note symbols for tempo indications:
  - Half note
  - Quarter note
  - Eighth note
  - Dotted quarter note

- **Multiple Resolutions**: Each asset is generated in multiple sizes:
  - 32px height (1x)
  - 40px height (1.25x)
  - 48px height (1.5x)
  - 64px height (2x)

- **Professional Quality**: Uses the Bravura font with consistent styling and proper glyph positioning

## Requirements

- Java 17 or higher (uses Java modules and records)
- Bravura.otf font file (included as a resource)

## Project Structure

```
drumscore-asset-gen/
├── src/
│   ├── module-info.java
│   └── xyz/arwhite/
│       ├── DSEAssetGen.java
│       └── Bravura.otf (font resource)
├── bin/                    # Compiled classes
└── .settings/              # Eclipse project settings
```

## Building and Running

### Using Eclipse

1. Import the project into Eclipse as an existing Java project
2. Ensure Java 17+ is configured
3. Run `DSEAssetGen.java` as a Java application

### Using Command Line

```bash
# Compile
javac -d bin src/module-info.java src/xyz/arwhite/DSEAssetGen.java

# Run
java --module-path bin -m DSEAssetGen/xyz.arwhite.DSEAssetGen
```

## Output

Generated assets are saved to `~/Downloads/dse_assets/` with the following naming convention:

- Time signatures: `timesig-{upper}-{lower}-{size}.png`
  - Example: `timesig-4-4-32.png`, `timesig-6-8-64.png`
- Common time: `timesig-common-{size}.png`
- Cut common: `timesig-cut-common-{size}.png`
- Tempo notes: `tempo-{type}-{size}.png`
  - Example: `tempo-4-32.png` (quarter note)
  - Example: `tempo-4-dot-32.png` (dotted quarter note)

## Technical Details

### Font Rendering

- Base font size: 46pt for time signatures, 32pt for tempo markings
- Color: Custom ink color (#373d3f)
- Background: White for time signatures, transparent for tempo markings
- Glyph positioning uses precise SMuFL coordinates for professional alignment

### SMuFL Characters Used

The application uses Unicode characters from the SMuFL specification:
- Time signature digits: U+E080 through U+E089
- Common time: U+E08A
- Cut common: U+E08B
- Tempo notes: U+ECA3, U+ECA5, U+ECA7
- Augmentation dot: U+ECB7

## License

This project uses the Bravura font, which is licensed under the SIL Open Font License.

## Author

Alan White
