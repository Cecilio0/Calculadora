# Development Guide & Usage

## Building the Project

### Using VS Code

1. **Open the project** in VS Code
2. **The project should auto-compile** when you save files
3. **Check the Problems panel** (Ctrl + Shift + M) for compilation errors

### Command Line (Windows PowerShell)

```powershell
# Navigate to project root
cd "c:\Users\drone\OneDrive\Documents\Calculadora\Calculadora"

# Compile
javac -d bin src\**\*.java

# Or with better organization
javac -d bin src\App.java src\services\Calculator.java src\ui\CalculatorGUI.java

# Run
java -cp bin App
```

## Running the Calculator

### From VS Code
- Click the **Run** button (play icon) in the top-right
- Or press **F5** to start debugging

### From Command Line
```powershell
java -cp bin App
```

## Project Structure Explanation

```
Calculadora/
├── README.md                      # Generic VS Code Java template
├── ARCHITECTURE.md               # High-level design (NEW)
├── CODE_GUIDE.md                 # Detailed code explanations (NEW)
├── src/
│   ├── App.java                  # Entry point
│   ├── services/
│   │   └── Calculator.java       # Business logic
│   └── ui/
│       └── CalculatorGUI.java    # User interface
├── bin/                          # Compiled .class files (auto-generated)
│   ├── App.class
│   ├── services/
│   │   └── Calculator.class
│   └── ui/
│       └── CalculatorGUI.class
└── lib/                          # External libraries (currently empty)
```

## Package Structure

The code uses two packages:

```java
package services;   // Calculator.java
package ui;         // CalculatorGUI.java
// App.java is in the default package
```

This organization separates concerns:
- **services**: Business logic, testable independently
- **ui**: User interface, depends on services

## Common Tasks

### Add a New Operation

To add a new operation (e.g., logarithm):

1. **Add button to GUI** in `CalculatorGUI.java`:
```java
String[][] buttonLabels = {
    // ... existing buttons ...
    {"log", "ln", ...}  // Add your button
};
```

2. **Handle the button click** in `handleButtonClick()`:
```java
case "log":
    result = calculator.applySingleOperation("log");
    break;
```

3. **Implement the operation** in `Calculator.java`:
```java
public String applySingleOperation(String operation) {
    // ... existing cases ...
    case "log":
        result = Math.log10(num);  // Base-10 logarithm
        break;
}
```

4. **Add button styling** in `createButton()`:
```java
} else if (label.equals("log") || label.equals("ln")) {
    button.setBackground(new Color(255, 152, 0)); // Orange with operations
    button.setForeground(Color.WHITE);
}
```

### Add a Binary Operation

Similar steps, but use `handleOperation()`:

```java
// In handleOperation() switch:
case "÷":
    result = calculator.handleOperation("÷");
    break;

// In calculate() switch:
case "÷":
    if (second == 0) {
        throw new ArithmeticException("Cannot divide by zero");
    }
    return first / second;
```

### Modify the Color Scheme

Edit `createButton()` in `CalculatorGUI.java`:

```java
if (label.equals("=")) {
    button.setBackground(new Color(76, 175, 80));    // Green
    button.setForeground(Color.WHITE);
} else if (label.equals("C") || label.equals("DEL")) {
    button.setBackground(new Color(244, 67, 54));    // Red
    button.setForeground(Color.WHITE);
}
// ... and so on
```

The RGB values are (R, G, B) where 0-255:
- `new Color(255, 0, 0)` = Pure red
- `new Color(0, 255, 0)` = Pure green
- `new Color(0, 0, 255)` = Pure blue

### Change Display Font

In `CalculatorGUI.java`, inside the `JDisplay` class:

```java
private Font defaultFont = new Font("Arial", Font.BOLD, 48);
//                                   ↑ Font name  ↑ Size
```

Options: "Arial", "Times New Roman", "Courier New", "Verdana", etc.

### Adjust Window Size

In `CalculatorGUI.java`, `initializeUI()` method:

```java
setSize(500, 700);  // width, height in pixels
// Current: 500 wide, 700 tall
// Try: 600, 800 for a larger calculator
```

## Testing and Debugging

### Using VS Code Debugger

1. **Set a breakpoint** by clicking on the line number (red dot appears)
2. **Press F5** to start debugging
3. **Step through code**:
   - F10: Step Over (next line)
   - F11: Step Into (enter method)
   - Shift + F11: Step Out (exit method)
4. **Inspect variables** in the left panel

### Example: Debug a Calculation

Set breakpoints in `Calculator.handleOperation()` to trace state changes:

```java
public String handleOperation(String op) {
    // ← Set breakpoint here
    try {
        if (!operation.isEmpty() && !isNewNumber) {
            // ← And here to see the calculation
            secondNumber = Double.parseDouble(display);
            firstNumber = calculate(firstNumber, secondNumber, operation);
```

Then perform `5 + 3 +` and inspect the variables in the Debug panel.

## Common Bug Fixes

### Problem: "Error: Decimal separated by comma in some locales"

**Symptom**: Entering decimal numbers fails in some system locales

**Fix**: Ensure your system locale uses periods (.) for decimals, or modify `formatNumber()` to handle both:

```java
private String formatNumber(double num) {
    // ... existing code ...
    // Replace commas with periods for consistency
    return result.replace(",", ".");
}
```

### Problem: Operations don't display intermediate results

**Symptom**: `5 + 3 +` displays "3" instead of "8"

**Cause**: `handleOperation()` might not be calculating intermediate results correctly

**Check**: Ensure this code exists in `handleOperation()`:
```java
if (!operation.isEmpty() && !isNewNumber) {
    // ... calculate intermediate result ...
}
```

### Problem: Font is too small/large

**Check**: Edit the `adjustFontSize()` method in the `JDisplay` class to change thresholds:

```java
if (textLength <= 10) {
    fontSize = 48;  // Try 56 for larger
} else if (textLength <= 15) {
    fontSize = 36;
}
```

## Code Style Notes

The codebase follows these conventions:

- **Naming**: camelCase for variables/methods, PascalCase for classes
- **Indentation**: 4 spaces
- **Imports**: Organized by package `java.*`, then `javax.*`
- **Exception Handling**: Catch specific exceptions, provide meaningful messages
- **Comments**: Used sparingly; code should be self-documenting

## Performance Notes

**Current Calculator Performance**: Excellent for manual use
- No lag or latency issues expected
- All operations complete < 1ms
- GUI responsive even with long calculations

**Potential Optimizations** (not currently needed):
- Cache trigonometric function results
- Pre-compute Fibonacci numbers (unlikely to reach computational limits)
- Implement faster GCD algorithm (Euclidean is already fast)

## Known Limitations

1. **Number Precision**: Uses Java `double` (64-bit floating-point)
   - Max value: ~1.8 × 10^308
   - Precision: ~15-17 decimal digits
   - No arbitrary-precision arithmetic (like BigDecimal)

2. **Factorial Limit**: Cap at n=20 to prevent overflow
   - 21! = 51,090,942,171,709,440,000 (exceeds long)

3. **Trigonometric Functions**: Input in degrees, not radians
   - `sin(30°) = 0.5`
   - Not applicable to complex numbers

4. **No History**: Cannot undo operations
   - Clear (C) resets completely
   - No operation stack or memory functions (M+, M-, MR)

5. **Integer-Only**: MCM and MCD convert to long
   - Decimal inputs truncated
   - Example: `2.7 MCM 3.9` becomes `GCD(2, 3)`

## Future Enhancements

Potential improvements:

1. **Memory Functions**: M+, M-, MR, MC for storing values
2. **Operation History**: Display previous calculations
3. **Keyboard Support**: Allow numeric keypad input
4. **Scientific Notation**: Display very large/small numbers as 1.23e10
5. **Theme Support**: Dark mode toggle, customizable colors
6. **Undo/Redo**: Navigate through calculation history
7. **Unit Conversion**: Convert between different units
8. **Polynomial Solver**: Solve equations like x² + 2x + 1 = 0
