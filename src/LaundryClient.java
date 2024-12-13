import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class LaundryClient {
    private static final String SERVER_ADDRESS = "localhost";  // Server address
    private static final int SERVER_PORT = 12345;              // Server port
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JFrame frame;
    private JTextField customerNameField;
    private JTextField contactField;
    private JTextField laundryWeightField;
    private JComboBox<String> packageTypeDropdown;

    public static void main(String[] args) {
        new LaundryClient().createAndShowGUI();
    }

    public LaundryClient() {
        // Initialize client components
        customerNameField = new JTextField(20);
        contactField = new JTextField(15);
        laundryWeightField = new JTextField(10);
        packageTypeDropdown = new JComboBox<>(new String[] {"Basic Package", "Premium Package"});
    }

    public void createAndShowGUI() {
        frame = new JFrame("Laundry Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        panel.add(new JLabel("Customer Name:"));
        panel.add(customerNameField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactField);
        panel.add(new JLabel("Package Type:"));
        panel.add(packageTypeDropdown);
        panel.add(new JLabel("Laundry Weight (kg):"));
        panel.add(laundryWeightField);

        JButton addOrderButton = new JButton("Add Order");
        addOrderButton.addActionListener(new AddOrderButtonListener());

        panel.add(addOrderButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private class AddOrderButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Connect to the server
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Get input values
                String customerName = customerNameField.getText().trim();
                String contactNumber = contactField.getText().trim();
                String packageType = (String) packageTypeDropdown.getSelectedItem();
                String weightText = laundryWeightField.getText().trim();

                if (customerName.isEmpty() || contactNumber.isEmpty() || weightText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill out all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create the LaundryOrder object
                double weight = Double.parseDouble(weightText);
                double price = PriceCalculator.calculatePrice(packageType, weight);
                LaundryOrder order = new LaundryOrder(customerName, contactNumber, packageType, weight, price);

                // Send the order to the server
                out.writeObject("ADD_ORDER"); // Send request type to server
                out.flush();
                out.writeObject(order); // Send order to the server
                out.flush();

                // Receive server response
                String response = (String) in.readObject();
                JOptionPane.showMessageDialog(frame, response, "Order Status", JOptionPane.INFORMATION_MESSAGE);

                // Close connection
                socket.close();
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "Error connecting to server: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
