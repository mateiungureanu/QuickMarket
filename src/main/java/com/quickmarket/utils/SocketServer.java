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
                        e.printStackTrace();
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
                    
                    String message;
                    while ((message = reader.readLine()) != null) {
                        handleMessage(message);
                    }
                } else if (firstMessage != null && firstMessage.startsWith("SEND_ALERT:")) {
                    handleAlertCommand(firstMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (userId != -1) {
                    userDisconnected(userId);
                }
                try {
                    if (reader != null) reader.close();
                    if (writer != null) writer.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void handleMessage(String message) {
            if (message.startsWith("SEND_ALERT:")) {
                handleAlertCommand(message);
            }
        }
        
        private void handleAlertCommand(String command) {
            try {
                String[] parts = command.split(":", 4);
                if (parts.length >= 4) {
                    int targetUserId = Integer.parseInt(parts[1]);
                    int alertId = Integer.parseInt(parts[2]);
                    String alertMessage = parts[3];
                    
                    boolean delivered = sendAlertDirectly(targetUserId, alertId, alertMessage);
                    
                    if (delivered) {
                        writer.println("DELIVERED");
                    } else {
                        writer.println("NOT_DELIVERED");
                    }
                    writer.flush();
                }
            } catch (Exception e) {
                if (writer != null) {
                    writer.println("ERROR");
                    writer.flush();
                }
            }
        }
        
        private boolean sendAlertDirectly(int userId, int alertId, String message) {
            PrintWriter targetWriter = connectedUsers.get(userId);
            if (targetWriter != null) {
                try {
                    String fullMessage = "ALERT:" + alertId + ":" + message;
                    targetWriter.println(fullMessage);
                    targetWriter.flush();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
} 