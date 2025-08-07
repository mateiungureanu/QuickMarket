package com.quickmarket.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AlertSender {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    public static boolean sendAlert(int userId, int alertId, String message) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            String command = "SEND_ALERT:" + userId + ":" + alertId + ":" + message;
            writer.println(command);
            writer.flush();
            
            try {
                String response = reader.readLine();
                return "DELIVERED".equals(response);
            } catch (Exception e) {
                return false;
            }
            
        } catch (IOException e) {
            return false;
        }
    }
}
