# Code Commentary - Confusing Parts Explained

## 1. State Management in Calculator

### The Problem: Tracking Context Between Operations

The Calculator class uses several internal state variables that work together. Understanding their relationship is crucial:

```java
private double firstNumber = 0;      // First operand in a binary operation
private double secondNumber = 0;     // Second operand in a binary operation
private String operation = "";       // Current pending operation (+, -, *, /, etc.)
private boolean isNewNumber = true;  // Indicates if next digit input starts a new number
private String display = "0";        // What the user sees
```

### Why `isNewNumber`?

The `isNewNumber` flag prevents the user from accidentally appending digits to the result of a previous operation:

```
Without isNewNumber:
User clicks: 5 → + → 3 → 1 → display shows "31" (wrong!)

With isNewNumber:
User clicks: 5 → + → 3 → 1 → display shows "1" (correct!)
```

When an operation button is clicked, `isNewNumber` is set to `true`, which means the next digit click will replace the display instead of appending to it.

### State Diagram: How Variables Change

```
Initial State:
firstNumber = 0, secondNumber = 0, operation = "", isNewNumber = true, display = "0"

User clicks "5":
→ handleNumberClick("5")
→ Since isNewNumber=true: display = "5", isNewNumber = false

User clicks "+":
→ handleOperation("+")
→ Since operation="": firstNumber = 5.0, operation = "+", isNewNumber = true
→ display = "5" (unchanged)

User clicks "3":
→ handleNumberClick("3")
→ Since isNewNumber=true: display = "3", isNewNumber = false

User clicks "=" or another operator:
→ calculateResult() or handleOperation()
→ secondNumber = 3.0
→ firstNumber = calculate(5.0, 3.0, "+") = 8.0
→ display = "8", operation = "", isNewNumber = true
```

## 2. The `handleOperation` Method - Handling Chained Operations

### The Tricky Part: Why calculation happens when clicking a new operation?

```java
public String handleOperation(String op) {
    try {
        if (!operation.isEmpty() && !isNewNumber) {
            // ← THIS is the key: if there's a previous operation AND
            //   the user has entered a number since then,
            //   calculate the intermediate result
            secondNumber = Double.parseDouble(display);
            firstNumber = calculate(firstNumber, secondNumber, operation);
            display = formatNumber(firstNumber);
        } else {
            firstNumber = Double.parseDouble(display);
        }
        operation = op;
        isNewNumber = true;
        return display;
    } catch (NumberFormatException e) {
        return "0";
    }
}
```

### Why This Design?

This allows the calculator to handle sequences like `5 + 3 + 2 =` naturally:

| Step | Input | operation (before) | Display | Calculation | firstNumber | operation (after) |
|------|-------|-------------------|---------|-------------|-------------|-------------------|
| 1 | 5 | "" | 5 | none | 5 | "" |
| 2 | + | "" | 5 | none | 5 | "+" |
| 3 | 3 | "+" | 3 | none | 5 | "+" |
| 4 | + | "+" | 8 | 5+3=8 | 8 | "+" |
| 5 | 2 | "+" | 2 | none | 8 | "+" |
| 6 | = | "+" | 10 | 8+2=10 | 10 | "" |

**Without this logic**, step 4 wouldn't calculate anything, and you'd get `5 + 3 =` showing "5" instead of "8".

## 3. The `calculate` Method - Multiple Operation Types

### The Challenge: Different Signatures

Most operations take two operands (binary operations), but we use one method for all:

```java
private double calculate(double first, double second, String op) {
    switch (op) {
        case "+":
            return first + second;    // Standard binary
        case "-":
            return first - second;
        case "*":
            return first * second;
        case "/":
            if (second == 0) {
                throw new ArithmeticException("Cannot divide by zero");
            }
            return first / second;
        case "^":
            return Math.pow(first, second);  // Power: 2^3 = 8
        case "root":
            // ← IMPORTANT: This calculates the second root of first
            // Example: first=8, second=3 means cube root of 8 = 2
            if (first < 0 && second % 2 == 0) {
                throw new ArithmeticException("Cannot take even root of negative number");
            }
            return Math.pow(first, 1.0 / second);  // first^(1/second)
        case "mcm":
            return mcm((long) first, (long) second);
        case "mcd":
            return gcd((long) first, (long) second);
        default:
            return first;
    }
}
```

### Special Case: Root Operation

The "root" operation is **backwards from typical notation**:
```
Input: 8 [root] 3 [=]
Displays: 2 (because 2³ = 8)

NOT: 3√8 (third root of 8)
But rather: "8 raised to power (1/3)" = the third root of 8
```

The order is: `firstNumber [root] secondNumber` = `firstNumber ^ (1/secondNumber)`

## 4. Single Operand Operations - Different State Transition

### The Key Difference

Single operations (sin, cos, tan, !, fib) don't follow the same pattern as binary operations:

```java
public String applySingleOperation(String operation) {
    try {
        double num = Double.parseDouble(display);
        // ... calculation ...
        display = formatNumber(result);
        isNewNumber = true;            // ← Ready for next number
        return display;
    } catch (NumberFormatException e) {
        return "0";
    }
}
```

vs. binary operations:

```java
public String handleOperation(String op) {
    // ... calculation ...
    operation = op;                    // ← Stores the operation for later
    isNewNumber = true;
    return display;
}
```

**Single operations complete immediately**, whereas **binary operations wait for a second operand**.

### Example:
```
Sequence: 5 [!] [+] [3] [=]

Step 1: applySingleOperation("!") → display = "120" (5! = 120)
Step 2: handleOperation("+") → firstNumber = 120, operation = "+"
Step 3: handleNumberClick("3") → display = "3"
Step 4: calculateResult() → display = "123" (120 + 3)
```

## 5. The `formatNumber` Method - Removing Trailing Zeros

### The Problem

After calculations, numbers may have many decimal places:
```
5 / 2 = 2.5000000000000004  (floating-point precision error)
```

### The Solution

```java
private String formatNumber(double num) {
    if (num == (long) num) {
        // If it's a whole number, display without decimals
        return String.format("%d", (long) num);
    } else {
        // Otherwise, format with up to 10 decimals, then remove trailing zeros
        return String.format("%.10f", num)
            .replaceAll("0*$", "")        // Remove trailing zeros
            .replaceAll("\\.$", "");      // Remove trailing decimal point
    }
}
```

**Examples:**
```
2.0 → "2"
2.5 → "2.5"
2.333333333333 → "2.3333333333"
0.00001 → "0.00001"
```

## 6. The `delete` Method - Handling Edge Cases

### Why So Complicated?

```java
public String delete() {
    if (!isNewNumber && display.length() > 1) {
        // Delete last character
        display = display.substring(0, display.length() - 1);
    } else if (!isNewNumber && display.length() == 1) {
        // If it's the last digit, show "0"
        display = "0";
        isNewNumber = true;
    }
    // If isNewNumber is true, don't delete (no change)
    return display;
}
```

### Why Check `isNewNumber`?

After clicking an operation button, `isNewNumber = true`. If the user clicks DELETE:
- **Without the check**: You could delete the first operand (wrong!)
- **With the check**: DELETE does nothing (correct!)

```
Example: 5 [+] [DEL] 
Without check: display becomes "0", accidentally clearing the first operand
With check: display stays "5", DEL is ignored
```

## 7. Display Font Sizing - Why Adaptive Sizing?

### The Challenge

With a fixed font size, long error messages overflow the display area:
```
Fixed 48pt: "Error: Cannot divide by zero" (TOO BIG, doesn't fit!)
```

### The Solution: Adaptive Font

```java
private void adjustFontSize() {
    int textLength = text.length();
    int fontSize;
    
    if (textLength <= 10) {
        fontSize = 48;      // "123456789"
    } else if (textLength <= 15) {
        fontSize = 36;      // Longer numbers
    } else if (textLength <= 20) {
        fontSize = 28;      // Scientific notation might appear
    } else if (textLength <= 30) {
        fontSize = 22;      // Error messages
    } else {
        fontSize = 18;      // Very long error messages
    }
}
```

Each text update triggers recalculation of appropriate font size.

## 8. MCM vs MCD - International Terminology

The calculator uses Spanish mathematical terms:

| Code | Spanish Name | English Name | Definition |
|------|--------------|--------------|-----------|
| **mcm** | **Mínimo Común Múltiplo** | **Least Common Multiple (LCM)** | Smallest number divisible by both operands |
| **mcd** | **Máximo Común Divisor** | **Greatest Common Divisor (GCD)** | Largest number that divides both operands |

### Implementation Details

```java
private long gcd(long a, long b) {
    // Euclidean algorithm - efficient GCD calculation
    a = Math.abs(a);
    b = Math.abs(b);
    while (b != 0) {
        long temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

private long mcm(long a, long b) {
    // Formula: LCM(a,b) = |a*b| / GCD(a,b)
    a = Math.abs(a);
    b = Math.abs(b);
    return (a * b) / gcd(a, b);
}
```

### Examples:
```
GCD(12, 18) = 6      (12 = 2×6, 18 = 3×6)
LCM(12, 18) = 36     (36 = 3×12 = 2×18)
```

## 9. Fibonacci Calculation - Why Limit to 20?

```java
private long fibonacci(long n) {
    if (n < 0) {
        throw new ArithmeticException("Fibonacci is not defined for negative numbers");
    }
    if (n == 0) return 0;
    if (n == 1) return 1;
    
    long a = 0, b = 1;
    for (long i = 2; i <= n; i++) {
        long temp = a + b;
        a = b;
        b = temp;
    }
    return b;
}
```

Fibonacci numbers grow **exponentially**:
```
F(10) = 55
F(20) = 6,765
F(30) = 832,040
F(50) = 12,586,269,025
F(92) = 7,540,113,804,746,346,429  ← Max for long type
F(93) = overflow!
```

The limit isn't enforced in this implementation, but practically:
- UI only allows integer input
- Large Fibonacci numbers would exceed `long` type

## 10. GridLayout with Empty Panels

```java
private JPanel createButtonPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(8, 4, 5, 5));
    
    String[][] buttonLabels = {
        {"sin", "cos", "tan", "/"},
        {"^", "root", "!", "fib"},
        {"mcm", "mcd", "%", "*"},
        {"7", "8", "9", "-"},
        {"4", "5", "6", "+"},
        {"1", "2", "3", "."},
        {"+/-", "0", "DEL", "="},
        {"C", "", "", ""}  // ← Last row has empty strings
    };

    for (String[] row : buttonLabels) {
        for (String label : row) {
            if (label.isEmpty()) {
                // Create empty panel to maintain grid structure
                JPanel emptyPanel = new JPanel();
                emptyPanel.setBackground(new Color(30, 30, 30));
                panel.add(emptyPanel);
            } else {
                JButton button = createButton(label);
                panel.add(button);
            }
        }
    }
}
```

### Why Empty Panels?

GridLayout divides space equally. With 8×4 grid, we need exactly 32 components:
- Last row only has "C" button (need 3 more components)
- Empty panels fill those spaces without adding buttons
- Result: Proper grid alignment with "C" spanning only first column

Without this, GridLayout would try to stretch "C" across multiple columns (wrong layout).

---

## Summary: The Mental Model

Think of the calculator like a **two-stage processor**:

1. **Stage 1: Number Input** (`isNewNumber = false`)
   - Digits append to display
   - Operations save the number and wait

2. **Stage 2: Operation Pending** (`isNewNumber = true`)
   - Next digit replaces display
   - Operations calculate intermediate result (if any)

This two-stage design enables intuitive left-to-right calculation without requiring equals for every operation.
