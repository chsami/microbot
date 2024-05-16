# Rs2Food Enumeration Documentation
## [Back](development.md)
## Overview
The `Rs2Food` enumeration defines constants for various food items used in the game. Each food item is associated with an identifier (ID), the amount it heals (heal), and a descriptive name.

## Enumeration Constants

### Food Items
Each constant in the `Rs2Food` enumeration represents a specific type of food, including its healing properties and item ID. Below are a few examples from the enumeration:

- **Dark_Crab**: Represents a Dark Crab which heals 27 points.
- **ROCKTAIL**: Represents a Rocktail which heals 23 points.
- **MANTA**: Represents a Manta Ray which heals 22 points.
- **SHARK**: Represents a Shark which heals 20 points.
- **LOBSTER**: Represents a Lobster which heals 12 points.
- **TROUT**: Represents a Trout which heals 7 points.
- **SALMON**: Represents a Salmon which heals 9 points.
- **SWORDFISH**: Represents a Swordfish which heals 14 points.
- **TUNA**: Represents a Tuna which heals 10 points.
- **MONKFISH**: Represents a Monkfish which heals 16 points.
- **SEA_TURTLE**: Represents a Sea Turtle which heals 21 points.
- **CAKE**: Represents a Cake which heals 4 points.
- **CHOCOLATE_CAKE**: Represents a Chocolate Cake which heals 5 points.
- **PLAIN_PIZZA**: Represents a Plain Pizza which heals 7 points.
- **MEAT_PIZZA**: Represents a Meat Pizza which heals 8 points.
- **ANCHOVY_PIZZA**: Represents an Anchovy Pizza which heals 9 points.
- **PINEAPPLE_PIZZA**: Represents a Pineapple Pizza which heals 11 points.
- **BREAD**: Represents Bread which heals 5 points.
- **APPLE_PIE**: Represents an Apple Pie which heals 7 points.
- **MEAT_PIE**: Represents a Meat Pie which heals 6 points.

... and many more.

## Methods

### `getId`
- **Description**: Returns the ID of the food item, which is used within the game to identify different types of food.

### `getHeal`
- **Description**: Returns the amount of health points the food item can restore when consumed.

### `getName`
- **Description**: Provides the name of the food item, which can be used in game interfaces and scripts.

### `toString`
- **Description**: Returns a string representation of the food item, combining its name and healing value, useful for display or debugging purposes.

## Usage
This enumeration is particularly useful for scripts dealing with health management, where food items are needed to restore health points. It simplifies the process of selecting appropriate food based on its healing capabilities and provides an easy way to reference them by name or ID.

