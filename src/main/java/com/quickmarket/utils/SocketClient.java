package com.quickmarket.utils;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Thread listenerThread;
    private boolean connected = false;
    private final int userId;

    public SocketClient(int userId) {
        this.userId = userId;
    }

    public boolean connect() {
        try {
            socket = new Socket("localhost", 8080);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            writer.println("USER:" + userId);
            
            connected = true;
            
            startListener();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error connecting to socket server: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    private void startListener() {
        listenerThread = new Thread(() -> {
            try {
                String message;
                while (connected && (message = reader.readLine()) != null) {
                    if (message.startsWith("ALERT:")) {
                        String[] parts = message.split(":", 3);
                        String alertMessage;
                        
                        if (parts.length >= 3) {
                            alertMessage = parts[2];
                        } else {
                            alertMessage = message.substring(6);
                        }
                        
                        System.out.println("\n=== NEW ALERT ===");
                        System.out.println(alertMessage);
                        System.out.println("==================");
                        System.out.print("Choose an option: ");
                    }
                }
            } catch (IOException e) {
                if (connected) {
                    System.err.println("Error reading from socket: " + e.getMessage());
                }
            }
        });
        listenerThread.start();
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
} 