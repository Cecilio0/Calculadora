package ui;

import services.Calculator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI extends JFrame {
    private Calculator calculator;
    private JDisplay display;
    private JPanel buttonPanel;

    public CalculatorGUI() {
        calculator = new Calculator();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(new Color(30, 30, 30));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display
        display = new JDisplay();
        display.setText(calculator.getDisplay());
        mainPanel.add(display, BorderLayout.NORTH);

        // Button Panel
        buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 4, 5, 5));
        panel.setBackground(new Color(30, 30, 30));

        // Define buttons
        String[][] buttonLabels = {
            {"sin", "cos", "tan", "/"},
            {"^", "root", "!", "fib"},
            {"mcm", "mcd", "%", "*"},
            {"7", "8", "9", "-"},
            {"4", "5", "6", "+"},
            {"1", "2", "3", "."},
            {"+/-", "0", "DEL", "="},
            {"C", "", "", ""}
        };

        for (String[] row : buttonLabels) {
            for (String label : row) {
                if (label.isEmpty()) {
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

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Color scheme
        if (label.equals("=")) {
            button.setBackground(new Color(76, 175, 80)); // Green
            button.setForeground(Color.WHITE);
        } else if (label.equals("C") || label.equals("DEL")) {
            button.setBackground(new Color(244, 67, 54)); // Red
            button.setForeground(Color.WHITE);
        } else if ("/%+-^*".contains(label) || label.equals("+/-") || label.equals("root") || 
                   label.equals("!") || label.equals("fib") || label.equals("mcm") || 
                   label.equals("mcd") || label.equals("sin") || label.equals("cos") || 
                   label.equals("tan")) {
            button.setBackground(new Color(255, 152, 0)); // Orange
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(66, 66, 66)); // Dark Gray
            button.setForeground(Color.WHITE);
        }

        button.addActionListener(createActionListener(label));
        return button;
    }

    private ActionListener createActionListener(String label) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonClick(label);
            }
        };
    }

    private void handleButtonClick(String label) {
        String result;

        try {
            switch (label) {
                case "C":
                    result = calculator.clear();
                    break;
                case "DEL":
                    result = calculator.delete();
                    break;
                case "%":
                    result = calculator.percentage();
                    break;
                case "/":
                case "*":
                case "-":
                case "+":
                    result = calculator.handleOperation(label);
                    break;
                case "^":
                    result = calculator.handleOperation("^");
                    break;
                case "root":
                    result = calculator.handleOperation("root");
                    break;
                case "mcm":
                    result = calculator.handleOperation("mcm");
                    break;
                case "mcd":
                    result = calculator.handleOperation("mcd");
                    break;
                case "=":
                    result = calculator.calculateResult();
                    break;
                case ".":
                    result = calculator.handleDecimal();
                    break;
                case "+/-":
                    result = calculator.toggleSign();
                    break;
                case "sin":
                case "cos":
                case "tan":
                case "!":
                case "fib":
                    result = calculator.applySingleOperation(label);
                    break;
                default:
                    // Number button
                    result = calculator.handleNumberClick(label);
                    break;
            }
        } catch (ArithmeticException ex) {
            result = "Error: " + ex.getMessage();
            calculator.clear(); // Clear calculator state on error
        }

        display.setText(result);
    }

    // Custom Display Panel
    private static class JDisplay extends JPanel {
        private String text = "0";
        private Font defaultFont = new Font("Arial", Font.BOLD, 48);
        private Font currentFont = defaultFont;

        public void setText(String text) {
            this.text = text;
            adjustFontSize();
            repaint();
        }

        private void adjustFontSize() {
            // Calculate appropriate font size based on text length
            int textLength = text.length();
            int fontSize;
            
            if (textLength <= 10) {
                fontSize = 48; // Default size for normal numbers
            } else if (textLength <= 15) {
                fontSize = 36; // Medium size
            } else if (textLength <= 20) {
                fontSize = 28; // Smaller for longer text
            } else if (textLength <= 30) {
                fontSize = 22; // Even smaller for error messages
            } else {
                fontSize = 18; // Smallest size for very long messages
            }
            
            currentFont = new Font("Arial", Font.BOLD, fontSize);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(currentFont);
            g2.setColor(Color.WHITE);

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);

            int x = getWidth() - textWidth - 20;
            int y = getHeight() - 20;

            g2.drawString(text, x, y);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 100);
        }
    }
}
