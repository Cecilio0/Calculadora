package services;

public class Calculator {
    private double firstNumber = 0;
    private double secondNumber = 0;
    private String operation = "";
    private boolean isNewNumber = true;
    private String display = "0";

    public String handleNumberClick(String number) {
        if (isNewNumber) {
            display = number;
            isNewNumber = false;
        } else {
            if (display.equals("0")) {
                display = number;
            } else {
                display += number;
            }
        }
        return display;
    }

    public String handleDecimal() {
        if (isNewNumber) {
            display = "0.";
            isNewNumber = false;
        } else if (!display.contains(".")) {
            display += ".";
        }
        return display;
    }

    public String handleOperation(String op) {
        try {
            if (!operation.isEmpty() && !isNewNumber) {
                // Calculate the result of the previous operation
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

    public String calculateResult() {
        try {
            if (!operation.isEmpty() && !isNewNumber) {
                secondNumber = Double.parseDouble(display);
                firstNumber = calculate(firstNumber, secondNumber, operation);
                display = formatNumber(firstNumber);
                operation = "";
                isNewNumber = true;
            }
            return display;
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    private double calculate(double first, double second, String op) {
        switch (op) {
            case "+":
                return first + second;
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
                return Math.pow(first, second);
            case "root":
                if (first < 0 && second % 2 == 0) {
                    throw new ArithmeticException("Cannot take even root of negative number");
                }
                return Math.pow(first, 1.0 / second);
            case "mcm":
                return mcm((long) first, (long) second);
            case "mcd":
                return gcd((long) first, (long) second);
            default:
                return first;
        }
    }

    // Single operand operations (trigonometric, factorial, etc.)
    public String applySingleOperation(String operation) {
        try {
            double num = Double.parseDouble(display);
            double result = 0;

            switch (operation) {
                case "sin":
                    result = Math.sin(Math.toRadians(num));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(num));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(num));
                    break;
                case "!":
                    result = factorial((long) num);
                    break;
                case "fib":
                    result = fibonacci((long) num);
                    break;
                default:
                    return display;
            }

            display = formatNumber(result);
            isNewNumber = true;
            return display;
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    private long factorial(long n) {
        if (n < 0) {
            throw new ArithmeticException("Factorial is not defined for negative numbers");
        }
        if (n > 20) {
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

    private long gcd(long a, long b) {
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
        a = Math.abs(a);
        b = Math.abs(b);
        return (a * b) / gcd(a, b);
    }

    public String clear() {
        firstNumber = 0;
        secondNumber = 0;
        operation = "";
        display = "0";
        isNewNumber = true;
        return display;
    }

    public String delete() {
        if (!isNewNumber && display.length() > 1) {
            display = display.substring(0, display.length() - 1);
        } else if (!isNewNumber && display.length() == 1) {
            display = "0";
            isNewNumber = true;
        }
        return display;
    }

    public String toggleSign() {
        try {
            double num = Double.parseDouble(display);
            num = -num;
            display = formatNumber(num);
            return display;
        } catch (NumberFormatException e) {
            return display;
        }
    }

    public String percentage() {
        try {
            double num = Double.parseDouble(display);
            num = num / 100;
            display = formatNumber(num);
            return display;
        } catch (NumberFormatException e) {
            return display;
        }
    }

    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.format("%d", (long) num);
        } else {
            return String.format("%.10f", num).replaceAll("0*$", "").replaceAll("\\.$", "");
        }
    }

    public String getDisplay() {
        return display;
    }
}
