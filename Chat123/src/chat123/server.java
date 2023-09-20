
package chat123;

import java.io.*;
import java.net.*;
import java.util.*;

public class server {
    
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6001);
            System.out.println("Server is running and listening on port 6001...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

               
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                
                clients.add(clientHandler);

                
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    
    static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readUTF();
                System.out.println("Received: " + message);

                
                server.broadcastMessage(message, this);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientSocket);
        } finally {
            
            server.removeClient(this);

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

