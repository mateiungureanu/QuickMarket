package com.quickmarket.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class AlertSender {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    public static boolean sendAlert(int userId, int alertId, String message) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            
            String command = "SEND_ALERT:" + userId + ":" + alertId + ":" + message;
            writer.println(command);
            writer.flush();
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("DEBUG: Alert sent to server for user " + userId);
            return true;
            
        } catch (IOException e) {
            System.out.println("DEBUG: Could not connect to socket server: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean isServerAvailable() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
