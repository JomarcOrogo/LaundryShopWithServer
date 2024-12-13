import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class AdminClient {
    private static final String SERVER_ADDRESS = "localhost";  // Server address
    private static final int SERVER_PORT = 12345;              // Server port
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JFrame frame;
    private JTextArea ordersArea;
    private JButton completeButton;
    private JButton readyForPickupButton;
    private JTextField orderIdField;

    public static void main(String[] args) {
        new AdminClient().createAndShowGUI();
    }

    public AdminClient() {
        ordersArea = new JTextArea(20, 50);
        ordersArea.setEditable(false);
        orderIdField = new JTextField(10);

        completeButton = new JButton("Mark as Completed");
        completeButton.addActionListener(new CompleteButtonListener());

        readyForPickupButton = new JButton("Mark as Ready for Pickup");
        readyForPickupButton.addActionListener(new ReadyForPickupButtonListener());
    }

    public void createAndShowGUI() {
        frame = new JFrame("Admin Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Order ID:"));
        inputPanel.add(orderIdField);
        inputPanel.add(completeButton);
        inputPanel.add(readyForPickupButton);

        panel.add(new JScrollPane(ordersArea), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        // Load orders from server
        loadOrders();
    }

    private void loadOrders() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("ADMIN");
            out.flush();
            out.writeObject("GET_ORDERS");
            out.flush();

            Map<Integer, LaundryOrder> orders = (Map<Integer, LaundryOrder>) in.readObject();

            StringBuilder ordersText = new StringBuilder();
            for (Map.Entry<Integer, LaundryOrder> entry : orders.entrySet()) {
                LaundryOrder order = entry.getValue();
                ordersText.append("Order ID: ").append(entry.getKey())
                        .append(" - ").append(order)
                        .append("\n");
            }

            ordersArea.setText(ordersText.toString());
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class CompleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int orderId = Integer.parseInt(orderIdField.getText().trim());
            if (orderId > 0) {
                try {
                    socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());

                    out.writeObject("ADMIN");
                    out.flush();
                    out.writeObject("COMPLETE_ORDER " + orderId);
                    out.flush();

                    String response = (String) in.readObject();
                    JOptionPane.showMessageDialog(frame, response);
                    loadOrders();  // Reload the order list
                    socket.close();
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class ReadyForPickupButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int orderId = Integer.parseInt(orderIdField.getText().trim());
            if (orderId > 0) {
                try {
                    socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());

                    out.writeObject("ADMIN");
                    out.flush();
                    out.writeObject("READY_FOR_PICKUP " + orderId);
                    out.flush();

                    String response = (String) in.readObject();
                    JOptionPane.showMessageDialog(frame, response);
                    loadOrders();  // Reload the order list
                    socket.close();
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
