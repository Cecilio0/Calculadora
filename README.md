# Calculator Application

A feature-rich Java Swing calculator with support for basic arithmetic, advanced mathematical functions, and number theory operations.

## Features

### Basic Operations
- Addition, Subtraction, Multiplication, Division
- Percentage calculations
- Sign toggle (+/-)
- Delete and Clear functions

### Advanced Functions
- **Trigonometric**: sin, cos, tan (input in degrees)
- **Power Operations**: x^y (exponentiation), nth root
- **Number Theory**: MCM (LCM) and MCD (GCD)
- **Special Functions**: Factorial (!), Fibonacci (fib)

### User Interface
- Modern dark theme with intuitive button layout
- Adaptive display font sizing (readable for both short numbers and error messages)
- Color-coded buttons (orange for operations, red for clear/delete, green for equals)
- Responsive event handling with immediate result display

## Documentation

This project includes comprehensive documentation:

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - High-level design and component overview
  - Project structure and design patterns
  - Data flow and state management
  - Supported operations reference
  - GUI layout specification

- **[CODE_GUIDE.md](CODE_GUIDE.md)** - Deep dive into confusing code sections
  - State management explained with examples
  - How chained operations work
  - Special cases and edge cases
  - Number formatting and font sizing logic

- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Development and usage guide
  - Building and running the project
  - How to add new operations
  - Debugging techniques
  - Common issues and fixes
  - Performance notes and limitations

## Project Structure

```
src/
├── App.java                    # Application entry point
├── services/
│   └── Calculator.java         # Calculation engine (business logic)
└── ui/
    └── CalculatorGUI.java      # Swing-based user interface

bin/                            # Compiled classes (auto-generated)
lib/                            # Dependencies (currently none)
```

## Quick Start

### Running the Calculator

**From VS Code:**
- Press F5 to run with debugging
- Or click the Run button in the top-right

**From Command Line:**
```powershell
# Compile
javac -d bin src\**\*.java

# Run
java -cp bin App
```

### First Calculation

1. Click "5" → click "+" → click "3" → click "=" → Result: 8
2. Click "C" to clear and start over

## Technical Details

- **Language**: Java 11+
- **UI Framework**: Swing (javax.swing)
- **Architecture**: Model-View pattern (Calculator service + GUI)
- **Number Type**: Double precision floating-point
- **Trigonometry**: All inputs/outputs in degrees

## Key Design Features

✓ **Chained Operations**: `5 + 3 + 2 =` works naturally without extra equals buttons
✓ **State Management**: Smart tracking of operands and pending operations
✓ **Error Handling**: Graceful handling of division by zero and invalid operations
✓ **Adaptive Display**: Font sizes adjust based on text length
✓ **Thread-Safe Initialization**: Proper EDT usage for Swing

## Known Limitations

- Factorial limited to 0-20 (computational limits)
- No arbitrary-precision arithmetic (uses double)
- No calculation history or undo/redo
- No keyboard input support (currently mouse-only)
- MCM/MCD work only with integers (decimals are truncated)

## Learning Resources

This project is excellent for learning:

- **Java Swing GUI Development**: Event handling, layout managers, custom components
- **State Machine Design**: Understanding stateful calculator logic
- **Mathematical Algorithms**: GCD, LCM, Fibonacci, factorial, roots
- **Code Organization**: Separation of concerns with service and UI layers

Refer to [CODE_GUIDE.md](CODE_GUIDE.md) for detailed explanations of confusing design patterns.
