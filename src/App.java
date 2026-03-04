import ui.CalculatorGUI;

public class App {
    public static void main(String[] args) throws Exception {
        // Launch GUI on Event Dispatch Thread (EDT) for thread-safe Swing operations
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CalculatorGUI();
            }
        });
    }
}
