# Quick Reference: Confusing Parts Explained

Quick answers to the most confusing aspects of this calculator.

## ❓ Why does `5 + + 3` not give an error?

The calculator allows you to press an operation button twice. When you do:
- `5` [+] [+] → The first [+] stores the operation, the second [+] just changes it to [+]
- The display stays "5"
- Result: No crashes, just overwriting the pending operation

This is **by design** for flexibility, though not typical calculator behavior.

---

## ❓ Why does `5 +` → click number `3` show "3" not "5"?

Look at this code in `handleNumberClick()`:

```java
if (isNewNumber) {
    display = number;  // ← Replaces entire display
    isNewNumber = false;
} else {
    display += number; // ← Appends to display
}
```

When you click an operation, `isNewNumber` is set to `true`. So the **next number replaces** the display instead of appending.

**Why?** So you don't accidentally append to the first operand:
```
Without this: 5 [+] 3 [4] → display "34" (WRONG!)
With this:    5 [+] 3 [4] → display "4" (correct!)
```

---

## ❓ What's the difference between single operations and binary operations?

| Feature | Binary (`+`, `-`, `*`, `/`) | Single (`sin`, `!`, `fib`) |
|---------|-------|--------|
| **Operands** | 2 (e.g., 5 + 3) | 1 (e.g., sin 45) |
| **When completes** | Never, until `=` or next operation | Immediately |
| **State after** | `operation` set, awaiting second number | `operation` cleared, ready for new number |
| **Example** | `5 [+]` → "5" waits → `3 [=]` → "8" | `45 [sin]` → "0.707" immediate |

---

## ❓ Why remove trailing zeros in `formatNumber()`?

```
Without formatting:  "2.5000000000000004" (ugly, precision errors)
With formatting:     "2.5" (clean)

1/2 using:
  Math.round(): 1     (loses precision)
  formatNumber(): 0.5 (perfect!)
```

The method:
1. Formats to 10 decimals: `"2.5000000000"`
2. Removes trailing zeros: `"2.5"`
3. Removes trailing decimal point if needed: `"2"`

---

## ❓ What's "root" operation doing?

The "root" button calculates: **first^(1/second)** = **second root of first**

```
Input:   8 [root] 3 [=]
Meaning: 8^(1/3) = ³√8 = 2
Result:  2

Input:   16 [root] 4 [=]
Meaning: 16^(1/4) = ⁴√16 = 2
Result:  2
```

This is **not** the traditional notation (where you'd write ³√8), but mathematically equivalent.

---

## ❓ Why does the font size change?

```
"300" displayed in 48pt font → Readable
"Error: Cannot divide by zero" in 48pt → Overflows the display area!
```

Solution: Display panel automatically shrinks font for long text:

```
≤10 chars: 48pt
11-15 chars: 36pt
16-20 chars: 28pt
21-30 chars: 22pt
>31 chars: 18pt
```

This keeps everything readable and within bounds.

---

## ❓ What's MCM and MCD?

Spanish abbreviations for mathematical operations:

| Code | Spanish | English | Example |
|------|---------|---------|---------|
| **MCM** | Mínimo Común Múltiplo | Least Common Multiple (LCM) | MCM(4,6) = 12 |
| **MCD** | Máximo Común Divisor | Greatest Common Divisor (GCD) | MCD(12,18) = 6 |

Both are binary operations:
```
4 [MCM] 6 [=] → 12  (smallest multiple of both 4 and 6)
12 [MCD] 18 [=] → 6 (largest divisor of both 12 and 18)
```

---

## ❓ Why does DEL do nothing after clicking an operation?

When you click [+], the calculator sets `isNewNumber = true`. Then:

```java
public String delete() {
    if (!isNewNumber && display.length() > 1) {
        // Only delete if we're in the middle of entering a number
        display = display.substring(0, display.length() - 1);
    }
    // If isNewNumber is true, don't delete
    return display;
}
```

```
5 [+] [DEL]
        ↑ isNewNumber=true, so DEL does nothing
        ↑ (protects first operand from accidental deletion)
```

Without this protection:
```
User intends:   5 [+] 3 [=]
User actually:  5 [+] [DEL] 3 [=] → "0 + 3 = 3" (lost the 5!)
```

---

## ❓ How does `5 + 3 + 2 =` work without pressing equals between operations?

The magic happens in `handleOperation()`:

```java
if (!operation.isEmpty() && !isNewNumber) {
    // If there's a stored operation AND we've entered a new number,
    // calculate the intermediate result RIGHT NOW
    secondNumber = Double.parseDouble(display);
    firstNumber = calculate(firstNumber, secondNumber, operation);
    display = formatNumber(firstNumber);
}
```

Step-by-step:
```
5      → display="5"
[+]    → firstNumber=5, operation="+", display="5"
3      → display="3"
[+]    → Sees operation="+" is not empty!
         Calculates: 5 + 3 = 8
         firstNumber=8, operation="+", display="8"
2      → display="2"
[=]    → Calculates: 8 + 2 = 10, display="10"
```

This is **automatic chaining** so you don't need to press [=] after each operation.

---

## ❓ Why is the grid layout 8×4 with empty panels?

```java
String[][] buttonLabels = {
    {"sin", "cos", "tan", "/"},      // Row 1: 4 buttons
    {"^", "root", "!", "fib"},       // Row 2: 4 buttons
    // ... 5 more rows of 4 buttons ...
    {"C", "", "", ""}                // Row 8: 1 button + 3 empties
};
```

GridLayout distributes space equally among all components:
- 8 rows × 4 columns = 32 slots total
- Last row adds: 1 real button + 3 empty panels
- Result: Perfect 32-component grid with proper alignment

Without the empty panels, GridLayout would stretch "C" button across multiple columns (wrong layout).

---

## ❓ What happens if you try to calculate `factorial(50)`?

The code doesn't explicitly check, but:

```java
if (n > 20) {
    throw new ArithmeticException("Factorial too large");
}
```

Why 20? 
```
20! = 2,432,902,008,176,640,000  ← Fits in long
21! = 51,090,942,171,709,440,000 ← Overflows!
```

The `long` type (64-bit) can't store larger numbers.

---

## ❓ Why convert toRadians() before sin/cos/tan?

```
Math.sin() input: Radians (0 to 2π for a full circle)
User inputs: Degrees (0 to 360 for a full circle)

User clicks: 90 [sin]
Code does:  Math.sin(Math.toRadians(90))
            = Math.sin(π/2)
            = 1.0 ✓ (correct!)

Without toRadians():
            Math.sin(90)
            = sin(90 radians)
            = -0.894 (wrong!)
```

Conversion formula: `radians = degrees × (π/180)`

---

## ❓ What's the difference between `formatNumber()` return values?

```java
if (num == (long) num) {
    return String.format("%d", (long) num);  // "5" not "5.0"
} else {
    return String.format("%.10f", num)       // "5.5" or "5.333333333"
        .replaceAll("0*$", "")
        .replaceAll("\\.$", "");
}
```

Examples:
```
2.0 → Check: is 2.0 == (long)2.0? YES → Return "2"
2.5 → Check: is 2.5 == (long)2? NO → Return "2.5"
```

This prevents ugly output like "2.0" when the result is a whole number.

---

## ❓ Why is `display` a String and not a double?

Because:
1. **Precision**: Strings preserve exact decimal representation
2. **Display**: Numbers with many digits need to be shown as entered
3. **Parsing**: Can easily validate before converting to double
4. **Formatting**: Easy to manipulate (add dots, remove zeros, etc.)

```java
String display = "0.5";  // Exact representation
double num = Double.parseDouble(display);  // Convert when needed
```

If `display` was a `double`, formatting and consistency would be much harder.

---

## 🎯 **The Core Mental Model**

Think of the calculator having **two modes**:

### Mode A: Entering a Number (`isNewNumber = false`)
- Digits **append** to display
- Operations set `isNewNumber = true` and wait

### Mode B: Waiting for Next Number (`isNewNumber = true`)
- Next digit **replaces** display
- Operations compute intermediate result (if needed)

This **two-mode system** is the key to understanding almost everything else in the code.
