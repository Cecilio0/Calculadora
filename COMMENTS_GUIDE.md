# Recommended Code Comments for Better Understanding

This document shows recommended inline comments to add to the source code for clarity. These comments explain the "why" behind complex logic.

## Calculator.java - Recommended Comments

### State Variables

```java
public class Calculator {
    // Two-stage state machine:
    // - isNewNumber=true: Next digit input will REPLACE display
    // - isNewNumber=false: Next digit input will APPEND to display
    // This enables natural left-to-right calculation without requiring = after each operation
    
    private double firstNumber = 0;      // First operand in pending binary operation
    private double secondNumber = 0;     // Second operand (set when operation completes)
    private String operation = "";       // Pending operation (+, -, *, /, ^, root, mcm, mcd)
    private boolean isNewNumber = true;  // Flag: is next digit input starting a new number?
    private String display = "0";        // What the user sees on screen
```

### handleNumberClick() Method

```java
public String handleNumberClick(String number) {
    if (isNewNumber) {
        // User just clicked an operation button, so next digit starts fresh display.
        // This prevents "5 [+] 3 [4]" from showing "34" when user meant "4"
        display = number;
        isNewNumber = false;
    } else {
        // User is continuing to enter digits into current number
        if (display.equals("0")) {
            // Replace leading zero with new digit
            display = number;
        } else {
            // Append to existing number
            display += number;
        }
    }
    return display;
}
```

### handleOperation() Method - Critical Logic

```java
public String handleOperation(String op) {
    try {
        if (!operation.isEmpty() && !isNewNumber) {
            // KEY FEATURE: Chained operation calculation
            // This is what makes "5 + 3 + 2 =" work naturally:
            // - "5 [+]" stores operation, sets isNewNumber=true
            // - User enters "3", isNewNumber becomes false
            // - User clicks "[+]" again
            // - THIS BLOCK executes: calculates 5+3=8 immediately
            // - Then prepares for next operation with 8 as the new firstNumber
            
            secondNumber = Double.parseDouble(display);
            firstNumber = calculate(firstNumber, secondNumber, operation);
            display = formatNumber(firstNumber);
        } else {
            // First operation in a sequence, just store the first operand
            firstNumber = Double.parseDouble(display);
        }
        
        operation = op;      // Store the new operation to wait for second operand
        isNewNumber = true;  // Next digit input will start a new number
        return display;
    } catch (NumberFormatException e) {
        return "0";
    }
}
```

### calculate() Method - Root Operation

```java
private double calculate(double first, double second, String op) {
    switch (op) {
        // ... other cases ...
        
        case "root":
            // Calculate: first^(1/second) => the second-root of first
            // User sees: [first] [root] [second] [=]
            // Math: result = ²ⁿᵈ√first
            // Example: 8 [root] 3 [=] => ³√8 = 2
            
            if (first < 0 && second % 2 == 0) {
                throw new ArithmeticException("Cannot take even root of negative number");
            }
            return Math.pow(first, 1.0 / second);
            
        // ... other cases ...
    }
}
```

### delete() Method - isNewNumber Protection

```java
public String delete() {
    if (!isNewNumber && display.length() > 1) {
        // Only allow deletion if we're currently entering a number (isNewNumber=false)
        // This protects against accidental deletion of the first operand
        // Example protection: 5 [+] [DEL] should NOT delete the 5
        display = display.substring(0, display.length() - 1);
    } else if (!isNewNumber && display.length() == 1) {
        // Last digit of current number: replace with "0"
        display = "0";
        isNewNumber = true;
    }
    // If isNewNumber=true, do nothing—DELETE button is disabled
    return display;
}
```

### formatNumber() Method - Number Formatting

```java
private String formatNumber(double num) {
    if (num == (long) num) {
        // Whole number: display without decimal point
        // 2.0 becomes "2" not "2.0"
        return String.format("%d", (long) num);
    } else {
        // Decimal number: format with up to 10 decimal places,
        // then remove trailing zeros and unnecessary decimal points
        // 0.5000000000 becomes "0.5"
        // 0.3333333333 becomes "0.3333333333"
        // 2.0 becomes "2"
        return String.format("%.10f", num)
            .replaceAll("0*$", "")        // Remove trailing zeros
            .replaceAll("\\.$", "");      // Remove trailing decimal point
    }
}
```

### applySingleOperation() Method

```java
public String applySingleOperation(String operation) {
    // Single operand operations (sin, cos, tan, !, fib) complete IMMEDIATELY
    // Unlike binary operations which wait for a second operand
    
    try {
        double num = Double.parseDouble(display);
        double result = 0;

        switch (operation) {
            case "sin":
                // Convert input from degrees to radians for Math.sin()
                // User enters 90 [sin] => sin(90°) = 1.0
                result = Math.sin(Math.toRadians(num));
                break;
            // ... other trig functions ...
            
            case "!":
                // Factorial: n! = n × (n-1) × ... × 1
                // Limited to n ≤ 20 to avoid overflow (21! exceeds long type max)
                result = factorial((long) num);
                break;
            // ... other operations ...
        }

        display = formatNumber(result);
        isNewNumber = true;  // Next input starts a new number
        return display;
    } catch (NumberFormatException e) {
        return "0";
    }
}
```

### factorial() Method - Limits

```java
private long factorial(long n) {
    if (n < 0) {
        throw new ArithmeticException("Factorial is not defined for negative numbers");
    }
    if (n > 20) {
        // Limit to 20: 20! = 2,432,902,008,176,640,000 (fits in long)
        //             21! = 51,090,942,171,709,440,000 (overflows!)
        // long type is 64-bit signed: max value ≈ 9.2 × 10^18
        throw new ArithmeticException("Factorial too large");
    }
    if (n == 0 || n == 1) {
        return 1;
    }
    long result = 1;
    for (long i = 2; i <= n; i++) {
        result *= i;
    }
    return result;
}
```

### MCM/GCD Methods - Spanish Terms

```java
private long gcd(long a, long b) {
    // MCD = Máximo Común Divisor (Greatest Common Divisor in English)
    // Euclidean algorithm: efficient way to find largest number that divides both
    // Example: GCD(12, 18) = 6
    
    a = Math.abs(a);  // Handle negative numbers
    b = Math.abs(b);
    while (b != 0) {
        long temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

private long mcm(long a, long b) {
    // MCM = Mínimo Común Múltiplo (Least Common Multiple in English)
    // Formula: LCM(a,b) = |a×b| / GCD(a,b)
    // Example: LCM(4, 6) = 12 (smallest number divisible by both 4 and 6)
    
    a = Math.abs(a);
    b = Math.abs(b);
    return (a * b) / gcd(a, b);
}
```

---

## CalculatorGUI.java - Recommended Comments

### initializeUI() Method

```java
private void initializeUI() {
    // ... display setup ...
    
    // Button Panel
    // Uses GridLayout(8, 4) to create 8 rows × 4 columns = 32 total cells
    // All 32 cells must be filled (including empty panels)
    buttonPanel = createButtonPanel();
    mainPanel.add(buttonPanel, BorderLayout.CENTER);
}
```

### createButtonPanel() Method - GridLayout Design

```java
private JPanel createButtonPanel() {
    // GridLayout(8, 4, 5, 5):
    // - 8 rows, 4 columns (32 cells total)
    // - 5px horizontal gap, 5px vertical gap between buttons
    
    // Last row has only "C" button + 3 empty panels:
    // This forces proper grid alignment and prevents GridLayout from
    // stretching the "C" button across multiple columns
    
    String[][] buttonLabels = {
        // ... buttons ...
        {"C", "", "", ""}  // ← Empty strings create empty panels
    };

    for (String[] row : buttonLabels) {
        for (String label : row) {
            if (label.isEmpty()) {
                // Create empty panel to maintain grid structure:
                // Without this, GridLayout can't fill all 32 cells
                JPanel emptyPanel = new JPanel();
                emptyPanel.setBackground(new Color(30, 30, 30));
                panel.add(emptyPanel);
            } else {
                JButton button = createButton(label);
                panel.add(button);
            }
        }
    }
    return panel;
}
```

### createButton() Method - Button Styling

```java
private JButton createButton(String label) {
    // ... button setup ...
    
    // Color scheme indicates function type:
    if (label.equals("=")) {
        button.setBackground(new Color(76, 175, 80));    // Green: "result"
        button.setForeground(Color.WHITE);
    } else if (label.equals("C") || label.equals("DEL")) {
        button.setBackground(new Color(244, 67, 54));    // Red: "danger" actions
        button.setForeground(Color.WHITE);
    } else if ("/%+-^*".contains(label) || /* advanced ops */) {
        button.setBackground(new Color(255, 152, 0));    // Orange: all operations
        button.setForeground(Color.WHITE);
    } else {
        button.setBackground(new Color(66, 66, 66));     // Dark gray: numbers
        button.setForeground(Color.WHITE);
    }
}
```

### JDisplay Class - Font Sizing Logic

```java
private static class JDisplay extends JPanel {
    private void adjustFontSize() {
        // Display font adapts to text length to keep everything readable:
        // - Short numbers (≤10 chars) use large font
        // - Error messages (>30 chars) use small font
        // This prevents overflow and maintains usability
        
        int textLength = text.length();
        int fontSize;
        
        if (textLength <= 10) {
            fontSize = 48;  // Default: "123456789" in large text
        } else if (textLength <= 15) {
            fontSize = 36;  // Longer numbers
        } else if (textLength <= 20) {
            fontSize = 28;  // Scientific notation appearing
        } else if (textLength <= 30) {
            fontSize = 22;  // Error messages fit
        } else {
            fontSize = 18;  // Very long messages (fallback)
        }
        
        currentFont = new Font("Arial", Font.BOLD, fontSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Custom rendering for right-aligned, fixed-width display
        // Mimics traditional calculator display (numbers come from right)
        
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Dark background
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Right-aligned white text
        g2.setFont(currentFont);
        g2.setColor(Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        // Position text right-aligned with 20px padding from right edge
        int x = getWidth() - textWidth - 20;
        int y = getHeight() - 20;

        g2.drawString(text, x, y);
    }
}
```

### handleButtonClick() Method - Event Routing

```java
private void handleButtonClick(String label) {
    String result;

    try {
        switch (label) {
            case "C":
                // Clear: Reset all state, start over
                result = calculator.clear();
                break;
            case "DEL":
                // Delete: Remove last digit
                result = calculator.delete();
                break;
            case "/":
            case "*":
            case "-":
            case "+":
                // Binary operations: Stored for later execution
                result = calculator.handleOperation(label);
                break;
            case "^":
                // Power operation: Treated as binary
                result = calculator.handleOperation("^");
                break;
            case "=":
                // Execute pending operation with entered second operand
                result = calculator.calculateResult();
                break;
            case ".":
                // Decimal point: Handled specially (only add once per number)
                result = calculator.handleDecimal();
                break;
            case "sin":
            case "cos":
            case "tan":
            case "!":
            case "fib":
                // Single operand: Complete immediately
                result = calculator.applySingleOperation(label);
                break;
            default:
                // Number button (0-9)
                result = calculator.handleNumberClick(label);
                break;
        }
    } catch (ArithmeticException ex) {
        // Display error message and clear calculator state
        result = "Error: " + ex.getMessage();
        calculator.clear();
    }

    // Update display with new result or error message
    display.setText(result);
}
```

---

## App.java - Recommended Comments

```java
public class App {
    public static void main(String[] args) throws Exception {
        // Launch GUI on Event Dispatch Thread (EDT)
        // Swing requires all GUI operations to happen on EDT for thread safety
        // SwingUtilities.invokeLater() ensures this requirement is met
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CalculatorGUI();  // Creates and displays the calculator window
            }
        });
    }
}
```

---

## Summary

These comments should be added to:
1. All state variables (explain purpose and relationship)
2. Complex conditional logic (explain the "why" and trade-offs)
3. Non-obvious algorithms (explain the mathematical or design principle)
4. Boundary cases (explain limits and why they exist)
5. Integration points between Calculator and GUI

The goal is to help future developers (including yourself 6 months later!) understand not just what the code does, but WHY it's designed that way.
