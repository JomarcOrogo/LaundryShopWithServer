import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LaundryServer {
    private static final int SERVER_PORT = 12345;
    private static Map<Integer, LaundryOrder> orders = new ConcurrentHashMap<>();
    private static int orderIdCounter = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started, waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

                String requestType = (String) in.readObject();

                if ("ADD_ORDER".equals(requestType)) {
                    LaundryOrder order = (LaundryOrder) in.readObject();
                    int orderId = orderIdCounter++;
                    orders.put(orderId, order);
                    out.writeObject("Order added with ID: " + orderId);
                } else if ("ADMIN".equals(requestType)) {
                    String adminCommand = (String) in.readObject();
                    if ("GET_ORDERS".equals(adminCommand)) {
                        out.writeObject(orders);
                    } else if (adminCommand.startsWith("COMPLETE_ORDER")) {
                        int orderId = Integer.parseInt(adminCommand.split(" ")[1]);
                        LaundryOrder order = orders.get(orderId);
                        if (order != null) {
                            order.setCompleted(true);
                            out.writeObject("Order " + orderId + " marked as completed.");
                        } else {
                            out.writeObject("Order not found.");
                        }
                    } else if (adminCommand.startsWith("READY_FOR_PICKUP")) {
                        int orderId = Integer.parseInt(adminCommand.split(" ")[1]);
                        LaundryOrder order = orders.get(orderId);
                        if (order != null && order.isCompleted()) {
                            order.setReadyForPickup(true);
                            out.writeObject("Order " + orderId + " is ready for pickup.");
                        } else {
                            out.writeObject("Order not found or not completed.");
                        }
                    }
                }

                socket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
