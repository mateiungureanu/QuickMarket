package com.quickmarket.utils;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SocketServer {
    private static final int PORT = 8080;
    private static final Map<Integer, PrintWriter> connectedUsers = new ConcurrentHashMap<>();
    private static ServerSocket serverSocket;
    private static boolean running = false;

    public static void start() {
        if (running) return;
        
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                running = true;
                System.out.println("Socket server started on port " + PORT);
                
                while (running && !serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new ClientHandler(clientSocket).start();
                    } catch (IOException e) {
                        if (running) {
                            System.err.println("Error accepting client connection: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error starting socket server: " + e.getMessage());
                running = false;
            }
        });
        
        serverThread.setDaemon(true);
        serverThread.start();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping socket server: " + e.getMessage());
        }
    }

    public static void sendAlertToUser(int userId, String message) {
        if (!running) return;
        
        PrintWriter writer = connectedUsers.get(userId);
        if (writer != null) {
            try {
                writer.println("ALERT:" + message);
                writer.flush();
            } catch (Exception e) {
                System.err.println("Error sending alert to user " + userId + ": " + e.getMessage());
            }
        }
    }

    public static boolean sendAlertToUser(int userId, int alertId, String message) {
        System.out.println("DEBUG: Server running status: " + running);
        System.out.println("DEBUG: Connected users count: " + connectedUsers.size());
        System.out.println("DEBUG: Connected user IDs: " + connectedUsers.keySet());
        
        if (!running) {
            System.out.println("DEBUG: Socket server not running - alert will be stored in database only");
            return false;
        }
        
        System.out.println("DEBUG: Looking for connected user " + userId + " among " + connectedUsers.size() + " connected users");
        PrintWriter writer = connectedUsers.get(userId);
        if (writer != null) {
            try {
                String fullMessage = "ALERT:" + alertId + ":" + message;
                System.out.println("DEBUG: Sending message: " + fullMessage);
                writer.println(fullMessage);
                writer.flush();
                System.out.println("DEBUG: Message sent successfully");
                return true; // Successfully delivered
            } catch (Exception e) {
                System.err.println("Error sending alert to user " + userId + ": " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("DEBUG: User " + userId + " not found in connected users map");
            return false; // User not connected
        }
    }

    public static boolean isRunning() {
        return running;
    }

    public static boolean tryConnect() {
        try {
            // Try to connect to see if server is running
            Socket testSocket = new Socket("localhost", PORT);
            testSocket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void userConnected(int userId, PrintWriter writer) {
        connectedUsers.put(userId, writer);
    }

    public static void userDisconnected(int userId) {
        connectedUsers.remove(userId);
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private int userId = -1;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String firstMessage = reader.readLine();
                if (firstMessage != null && firstMessage.startsWith("USER:")) {
                    userId = Integer.parseInt(firstMessage.substring(5));
                    userConnected(userId, writer);
                    System.out.println("User " + userId + " connected");
                    
                    String message;
                    while ((message = reader.readLine()) != null) {
                        handleMessage(message);
                    }
                } else if (firstMessage != null && firstMessage.startsWith("SEND_ALERT:")) {
                    handleAlertCommand(firstMessage);
                    return;
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                if (userId != -1) {
                    userDisconnected(userId);
                    System.out.println("User " + userId + " disconnected");
                }
                try {
                    if (reader != null) reader.close();
                    if (writer != null) writer.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client connection: " + e.getMessage());
                }
            }
        }
        
        private void handleMessage(String message) {
            if (message.startsWith("SEND_ALERT:")) {
                handleAlertCommand(message);
            } else {
                System.out.println("Received from user " + userId + ": " + message);
            }
        }
        
        private void handleAlertCommand(String command) {
            try {
                String[] parts = command.split(":", 4);
                if (parts.length >= 4) {
                    int targetUserId = Integer.parseInt(parts[1]);
                    int alertId = Integer.parseInt(parts[2]);
                    String alertMessage = parts[3];
                    
                    System.out.println("DEBUG: Received alert command for user " + targetUserId + ": " + alertMessage);
                    boolean delivered = sendAlertDirectly(targetUserId, alertId, alertMessage);
                    System.out.println("DEBUG: Alert delivery result: " + delivered);
                }
            } catch (Exception e) {
                System.err.println("Error handling alert command: " + e.getMessage());
            }
        }
        
        private boolean sendAlertDirectly(int userId, int alertId, String message) {
            PrintWriter targetWriter = connectedUsers.get(userId);
            if (targetWriter != null) {
                try {
                    String fullMessage = "ALERT:" + alertId + ":" + message;
                    System.out.println("DEBUG: Sending message to user " + userId + ": " + fullMessage);
                    targetWriter.println(fullMessage);
                    targetWriter.flush();
                    return true;
                } catch (Exception e) {
                    System.err.println("Error sending alert to user " + userId + ": " + e.getMessage());
                    return false;
                }
            } else {
                System.out.println("DEBUG: User " + userId + " not connected");
                return false;
            }
        }
    }
} 