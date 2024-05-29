import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente implements Runnable {
    private JTextArea textArea;
    private JTextField textField;
    private JButton[] numberButtons;
    private JButton[] functionButtons;
    private JButton addButton, subButton, mulButton, divButton, clearButton, sendButton, dotButton;
    private JButton modButton, powButton, sqrtButton, backButton;
    private JPanel panel, northPanel;
    private final String host;
    private final int id;
    private final String nombre;
    private static final int PUERTO = 8080;

    public Cliente(String host, int id, String nombre) {
        this.host = host;
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public void run() {
        JFrame frame = new JFrame(nombre);
        frame.setSize(300, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        northPanel = new JPanel(new GridLayout(2, 1));

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(300, 50));
        textField.setFont(new Font("Arial", Font.BOLD, 20));
        textField.setEditable(false);

        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(300, 100));
        textArea.setFont(new Font("Arial", Font.BOLD, 20));
        textArea.setEditable(false);

        northPanel.add(textField);
        northPanel.add(textArea);

        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].setFont(new Font("Arial", Font.PLAIN, 18));
            int finalI = i;
            numberButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textField.setText(textField.getText() + finalI);
                }
            });
        }

        functionButtons = new JButton[11];
        addButton = new JButton("+");
        subButton = new JButton("-");
        mulButton = new JButton("*");
        divButton = new JButton("/");
        clearButton = new JButton("C");
        sendButton = new JButton("→");
        dotButton = new JButton(".");
        modButton = new JButton("%");
        powButton = new JButton("^");
        sqrtButton = new JButton("√");
        backButton = new JButton("←");

        functionButtons[0] = addButton;
        functionButtons[1] = subButton;
        functionButtons[2] = mulButton;
        functionButtons[3] = divButton;
        functionButtons[4] = clearButton;
        functionButtons[5] = sendButton;
        functionButtons[6] = dotButton;
        functionButtons[7] = modButton;
        functionButtons[8] = powButton;
        functionButtons[9] = sqrtButton;
        functionButtons[10] = backButton;

        for (int i = 0; i < 11; i++) {
            functionButtons[i].setFont(new Font("Arial", Font.PLAIN, 18));
            functionButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String command = e.getActionCommand();
                    switch (command) {
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                        case "%":
                        case "^":
                        case "√":
                            textField.setText(command);
                            break;
                        case "C":
                            textArea.setText("");
                            textField.setText("");
                            break;
                        case "←":
                            String text = textField.getText();
                            if (text.length() > 0) {
                                textField.setText(text.substring(0, text.length() - 1));
                            }
                            break;
                    }
                }
            });
        }

        panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 10, 10));

        panel.add(backButton);
        panel.add(clearButton);
        panel.add(modButton);
        panel.add(divButton);

        panel.add(numberButtons[7]);
        panel.add(numberButtons[8]);
        panel.add(numberButtons[9]);
        panel.add(mulButton);

        panel.add(numberButtons[4]);
        panel.add(numberButtons[5]);
        panel.add(numberButtons[6]);
        panel.add(subButton);

        panel.add(numberButtons[1]);
        panel.add(numberButtons[2]);
        panel.add(numberButtons[3]);
        panel.add(addButton);

        panel.add(sqrtButton);
        panel.add(numberButtons[0]);
        panel.add(dotButton);
        panel.add(sendButton);

        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(panel);

        frame.setVisible(true);

        try {
            Socket socket = new Socket(host, PUERTO);
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String input = textField.getText();
                        salida.writeUTF(input);
                        textField.setText("");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            new Thread(() -> {
                try {
                    while (true) {
                        String response = entrada.readUTF();
                        textArea.append(response + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
