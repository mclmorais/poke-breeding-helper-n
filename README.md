# Pokemon Breeding Helper

An Android application designed to assist Pokemon trainers in selecting optimal breeding pairs to maximize the chances of obtaining Pokemon with higher Individual Values (IVs).

## Overview

This application helps users make informed decisions when breeding Pokemon by calculating the probability of inheriting desired IVs from parent Pokemon. The tool takes into account various breeding mechanics including IV inheritance, egg group compatibility, ability inheritance, and the use of breeding items like the Destiny Knot.

## Features

- **IV Chance Calculator**: Calculates the probability of obtaining a Pokemon with desired IVs based on the IVs of the parent Pokemon. Supports calculations both with and without the Destiny Knot item.

- **Breeding Compatibility Checker**: Verifies whether two Pokemon can breed together to produce a specific target Pokemon, taking into account:
  - Egg group compatibility
  - Evolution chain relationships
  - Gender requirements
  - Special cases (Ditto, genderless Pokemon, etc.)

- **Ability Inheritance Calculator**: Determines the probability of inheriting specific abilities from parent Pokemon, considering gender-based inheritance rules and hidden abilities.

- **Pokemon Data Management**: Includes comprehensive Pokemon data including:
  - Pokedex information
  - Egg groups
  - Abilities
  - Natures
  - Type information
  - Learnsets

- **Storage System**: Allows users to store and manage their Pokemon collection for easy reference during breeding calculations.

## Technical Details

This is an Android application built with:
- Java
- Android SDK (minSdkVersion 16, targetSdkVersion 25)
- Gradle build system
- SQLite database for Pokemon data
- JSON files for additional Pokemon information

## Project Status

This repository represents the first iteration of the Pokemon Breeding Helper project. The application is in active development and may undergo significant changes in future versions.

## Building the Project

To build this project, you will need:
- Android Studio or the Android SDK
- Gradle
- Java Development Kit (JDK)

1. Clone the repository
2. Open the project in Android Studio or use Gradle from the command line
3. Build and run the application on an Android device or emulator

## License

[License information to be added]

