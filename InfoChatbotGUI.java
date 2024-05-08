import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class InfoChatbotGUI extends JFrame implements ActionListener {
    private final JTextField userInputField;
    private final JTextArea chatArea;
    private final Map<String, String> predefinedResponses;

    public InfoChatbotGUI() {
        setTitle("Info Chatbot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 400);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        userInputField = new JTextField();
        userInputField.addActionListener(this);
        inputPanel.add(userInputField, BorderLayout.CENTER);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        predefinedResponses = loadPredefinedResponses(); // Load predefined responses from file

        setVisible(true);
    }

    public static void main(String[] args) {
        new InfoChatbotGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == userInputField || e.getActionCommand().equals("Send")) {
            String userInput = userInputField.getText();
            chatArea.append("You: " + userInput + "\n");
            processInput(userInput);
            userInputField.setText("");
        }
    }

    private void processInput(String userInput) {
        String response;
        if (predefinedResponses.containsKey(userInput.toLowerCase())) {
            response = predefinedResponses.get(userInput.toLowerCase());
        } else if (userInput.toLowerCase().startsWith("save ")) {
            // Extract key-value pair from user input
            String[] parts = userInput.substring(5).split(" about ");
            if (parts.length == 2) {
                String key = parts[0].trim().toLowerCase();
                String value = parts[1].trim();
                predefinedResponses.put(key, value);
                response = "Got it! I'll remember that.";
                savePredefinedResponses();
            } else {
                response = "Sorry, I didn't understand that. Please provide the information in the format: 'save [key] about [value]'.";
            }
        } else {
            // Default response if no predefined response or "save" command matches
            response = "Sorry, I don't have information on that. You can save information using 'save [key] about [value]'.";
        }
        chatArea.append("Chatbot: " + response + "\n");
    }

    private Map<String, String> loadPredefinedResponses() {
        Map<String, String> responses = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("responses.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    responses.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responses;
    }

    private void savePredefinedResponses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("responses.txt"))) {
            for (Map.Entry<String, String> entry : predefinedResponses.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
