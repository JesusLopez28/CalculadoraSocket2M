import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor extends Thread {
    protected Socket socket;
    protected int idSession;
    protected DataInputStream entrada;
    protected DataOutputStream salida;

    public HiloServidor(Socket socket, int idSession) {
        this.socket = socket;
        this.idSession = idSession;
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void desconectar() {
        try {
            socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String input = entrada.readUTF();
                boolean sendResult = false;
                double result = 0;

                synchronized (MultihiloServidor.class) {
                    if (input.matches("-?\\d+(\\.\\d+)?")) { // Es un número
                        double numero = Double.parseDouble(input);
                        if (MultihiloServidor.getNumero1() == null) {
                            MultihiloServidor.setNumero1(numero);
                            salida.writeUTF("Número recibido: " + numero);
                        } else if (MultihiloServidor.getNumero2() == null) {
                            MultihiloServidor.setNumero2(numero);
                            salida.writeUTF("Número recibido: " + numero);
                        }
                    } else {
                        if ("+-*/%^√".contains(input)) {
                            MultihiloServidor.setOperador(input);
                            salida.writeUTF("Operador recibido: " + input);
                        }
                    }

                    if (MultihiloServidor.getNumero1() != null && MultihiloServidor.getNumero2() != null && MultihiloServidor.getOperador() != null) {
                        result = realizarOperacion(MultihiloServidor.getNumero1(), MultihiloServidor.getNumero2(), MultihiloServidor.getOperador());
                        sendResult = true;
                        MultihiloServidor.incrementarResultadosEnviados();
                    }
                }

                if (sendResult) {
                    salida.writeUTF("Resultado: " + result);
                    synchronized (MultihiloServidor.class) {
                        if (MultihiloServidor.getResultadosEnviados() == 2) {
                            MultihiloServidor.reset();
                        }
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    private double realizarOperacion(double num1, double num2, String oper) {
        switch (oper) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                if (num2 != 0) {
                    return num1 / num2;
                } else {
                    JOptionPane.showMessageDialog(null, "Error: División por cero");
                    return 0;
                }
            case "%":
                return num1 % num2;
            case "^":
                return Math.pow(num1, num2);
            case "√":
                return Math.sqrt(num1 + num2);
            default:
                throw new IllegalArgumentException("Operador desconocido: " + oper);
        }
    }
}
