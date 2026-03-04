# Calculator Application - Architecture Guide

## Overview

This is a Java Swing-based calculator application with a clean separation between the business logic (Calculator service) and the user interface (CalculatorGUI). The calculator supports both basic arithmetic operations and advanced mathematical functions.

## Project Structure

```
src/
├── App.java                    # Entry point - launches the GUI
├── services/
│   └── Calculator.java         # Core calculation logic
└── ui/
    └── CalculatorGUI.java      # Swing-based user interface
```

## Architecture Pattern

The application follows a **Model-View pattern**:

- **Model (Calculator)**: Manages calculation state and logic
- **View (CalculatorGUI)**: Handles user interface and user interactions

This separation allows the calculator logic to be tested and reused independently of the GUI.

## Component Descriptions

### 1. App.java
**Purpose**: Application entry point

- Launches the calculator GUI on the Event Dispatch Thread (EDT)
- Uses `SwingUtilities.invokeLater()` for thread-safe GUI initialization
- This is a common best practice in Swing applications

### 2. Calculator.java (Services)
**Purpose**: Core business logic for all calculations

**Key Responsibilities**:
- Manages internal state (numbers, operations, display)
- Performs arithmetic and mathematical operations
- Handles special cases (division by zero, negative roots, etc.)
- Single operand operations (trigonometric, factorial, fibonacci)
- Binary operations (addition, subtraction, power, MCM, MCD)

**State Variables**:
- `firstNumber`: The first operand in a binary operation
- `secondNumber`: The second operand in a binary operation
- `operation`: The current operation being performed
- `isNewNumber`: Flag indicating if the user is entering a new number
- `display`: The string shown to the user

### 3. CalculatorGUI.java (UI)
**Purpose**: User interface and event handling

**Components**:
- `JDisplay`: Custom panel for displaying numbers and results with adaptive font sizing
- Button grid (8 rows × 4 columns) containing all calculator operations
- Event listeners for each button that call the appropriate Calculator methods

**Color Scheme**:
- Orange buttons: Operations and advanced functions
- Red buttons: Clear (C) and Delete (DEL)
- Green button: Equals (=)
- Dark Gray buttons: Numbers and decimal point

## Data Flow

### Typical Operation Sequence

```
User clicks button
    ↓
CalculatorGUI.handleButtonClick(label)
    ↓
Calculator.handleNumberClick() or Calculator.handleOperation()
    ↓
Calculator updates internal state and display
    ↓
CalculatorGUI updates JDisplay with new value
```

### Example: Chain Operation (e.g., "5 + 3 + 2 =")

```
User enters: 5 → + → 3 → + → 2 → =

Step 1: handleNumberClick("5") → display = "5"
Step 2: handleOperation("+") → firstNumber = 5, operation = "+", isNewNumber = true
Step 3: handleNumberClick("3") → display = "3"
Step 4: handleOperation("+") 
        - Sees previous operation is not empty
        - Calculates: 5 + 3 = 8
        - firstNumber = 8, operation = "+", isNewNumber = true
Step 5: handleNumberClick("2") → display = "2"
Step 6: calculateResult()
        - Calculates: 8 + 2 = 10
        - display = "10"
```

This design allows natural left-to-right calculation without waiting for the equals button.

## Supported Operations

### Basic Arithmetic
- Addition (+)
- Subtraction (-)
- Multiplication (*)
- Division (/)
- Percentage (%)

### Advanced Operations
- Power (^): Raises first number to the power of second
- Root (root): Calculates the nth root
- Sign Toggle (+/-): Changes positive/negative
- Delete (DEL): Removes last digit

### Single Operand Operations
- sin, cos, tan: Trigonometric functions (input in degrees)
- Factorial (!): Only works for integers 0-20
- Fibonacci (fib): Calculates nth Fibonacci number

### Number Theory Operations
- MCM: Mínimo Común Múltiplo (Least Common Multiple / LCM)
- MCD: Máximo Común Divisor (Greatest Common Divisor / GCD)

## Error Handling

The calculator handles several error cases:

1. **Division by Zero**: Throws ArithmeticException with message
2. **Factorial of Negative Numbers**: Not allowed
3. **Factorial > 20**: Too large to calculate
4. **Even Root of Negative Number**: Mathematically undefined
5. **Invalid Input**: NumberFormatException caught and handled

All errors display an error message in the display and clear the calculator state.

## Display Formatting

The JDisplay component includes intelligent font size adjustment:
- **≤ 10 characters**: 48pt font (default)
- **11-15 characters**: 36pt font
- **16-20 characters**: 28pt font
- **21-30 characters**: 22pt font
- **> 31 characters**: 18pt font (for error messages)

The display text is right-aligned and uses white text on a dark background for better readability.

## GUI Layout

The calculator uses a GridLayout with 8 rows and 4 columns:

```
[sin ] [cos ] [tan ] [  /  ]
[ ^  ] [root] [ !  ] [fib  ]
[mcm ] [mcd ] [ %  ] [ *   ]
[ 7  ] [ 8  ] [ 9  ] [ -   ]
[ 4  ] [ 5  ] [ 6  ] [ +   ]
[ 1  ] [ 2  ] [ 3  ] [ .   ]
[+/- ] [ 0  ] [DEL ] [ =   ]
[ C  ] [    ] [    ] [     ]
```

Empty cells are filled with blank panels to maintain proper spacing.

## Key Design Features

1. **Immediate Result Display**: When an operation is clicked, the current display is updated immediately
2. **Chained Operations**: Multiple operations can be chained without clicking equals
3. **Adaptive UI**: Display font size adjusts based on content length
4. **Number Formatting**: Trailing zeros removed; whole numbers shown without decimal point
5. **Stateful Operations**: Internal state tracks the current operation context
6. **Thread Safety**: GUI initialization uses EDT for thread safety
