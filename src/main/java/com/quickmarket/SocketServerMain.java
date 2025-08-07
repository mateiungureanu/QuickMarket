package com.quickmarket;

import com.quickmarket.utils.SocketServer;

public class SocketServerMain {
    public static void main(String[] args) {
        try {
            SocketServer.start();

            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("Failed to start socket server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
