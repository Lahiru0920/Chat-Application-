package chat123;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class client1 {
    JTextField text;
    static JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JFrame f = new JFrame();
    static DataOutputStream dout;
    static String clientName;
    static String chatRoom;

    client1() {
        clientName = JOptionPane.showInputDialog(f, "Enter Your Name:");
        if (clientName == null || clientName.isEmpty()) {
            clientName = "Anonymous";
        }

        Object[] chatRooms = {"Chat Room 1", "Chat Room 2", "Chat Room 3"};
        chatRoom = (String) JOptionPane.showInputDialog(f, "Choose a Chat Room:", "Chat Room Selection",
                JOptionPane.QUESTION_MESSAGE, null, chatRooms, chatRooms[0]);
        if (chatRoom == null || chatRoom.isEmpty()) {
            chatRoom = "Chat Room 1"; 
        }

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 600);
        f.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JLabel nameLabel = new JLabel("Name: " + clientName);
        JLabel roomLabel = new JLabel("Chat Room: " + chatRoom);
        nameLabel.setForeground(Color.BLUE); 
        roomLabel.setForeground(Color.RED); 
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16)); 
        roomLabel.setFont(new Font("Arial", Font.BOLD, 16)); 
        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(roomLabel, BorderLayout.EAST);
        headerPanel.setBackground(Color.LIGHT_GRAY); 
        f.add(headerPanel, BorderLayout.NORTH);

        a1 = new JPanel();
        a1.setLayout(new BoxLayout(a1, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(a1);
        f.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        text = new JTextField();
        text.setFont(new Font("Arial", Font.PLAIN, 14)); 
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14)); 
        sendButton.setForeground(Color.WHITE); 
        sendButton.setBackground(Color.BLUE); 
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    String message = text.getText();
                    sendMessage(clientName + ": " + message);
                    text.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        inputPanel.add(text, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBackground(Color.LIGHT_GRAY); 
        f.add(inputPanel, BorderLayout.SOUTH);

        f.setVisible(true);
    }

    private void sendMessage(String message) {
        try {
            JPanel p2 = formatLabel(message);

            a1.setLayout(new BorderLayout());

            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);
            dout.writeUTF(message);
            dout.writeUTF(chatRoom);
            f.repaint();
            f.invalidate();
            f.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String message) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.LIGHT_GRAY); 

        JLabel output = new JLabel("<html><p style=\"width:150px\">" + message + "</html>");
        output.setFont(new Font("Arial", Font.PLAIN, 14)); 
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(10, 10, 10, 30)); 
        output.setBackground(Color.WHITE); 
        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel("12:00");
        time.setText(sdf.format(cal.getTime()));
        time.setFont(new Font("Arial", Font.ITALIC, 12)); 
        time.setForeground(Color.GRAY); 
        panel.add(time);

        return panel;
    }

    public static void main(String[] args) {
        new client1();

        try {
            Socket s = new Socket("127.0.0.1", 6001);
            DataInputStream din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            while (true) {
                a1.setLayout(new BorderLayout());
                String msg = din.readUTF();
                String receivedChatRoom = din.readUTF();

                if (chatRoom.equals(receivedChatRoom)) {
                    JPanel panel = formatLabel(msg);

                    JPanel left = new JPanel(new BorderLayout());
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left);

                    vertical.add(Box.createVerticalStrut(15));
                    a1.add(vertical, BorderLayout.PAGE_START);
                    f.validate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
