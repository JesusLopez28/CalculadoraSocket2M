import javax.imageio.IIOException;
import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MultihiloServidor {
    static final int PUERTO = 8080;
    private static Double numero1 = null;
    private static Double numero2 = null;
    private static String operador = null;
    private static int resultadosEnviados = 0;

    public static void main(String[] args) throws IIOException {
        ServerSocket ss;
        JOptionPane.showMessageDialog(null, "Servidor iniciado");
        int idSession = 0;
        try {
            ss = new ServerSocket(PUERTO);
            while (true) {
                Socket socket = ss.accept();
                JOptionPane.showMessageDialog(null, "Nueva conexión entrante: " + socket);
                HiloServidor hilo = new HiloServidor(socket, idSession);
                hilo.start();
                idSession++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public static synchronized Double getNumero1() {
        return numero1;
    }

    public static synchronized void setNumero1(Double numero1) {
        MultihiloServidor.numero1 = numero1;
    }

    public static synchronized Double getNumero2() {
        return numero2;
    }

    public static synchronized void setNumero2(Double numero2) {
        MultihiloServidor.numero2 = numero2;
    }

    public static synchronized String getOperador() {
        return operador;
    }

    public static synchronized void setOperador(String operador) {
        MultihiloServidor.operador = operador;
    }

    public static synchronized void incrementarResultadosEnviados() {
        resultadosEnviados++;
    }

    public static synchronized int getResultadosEnviados() {
        return resultadosEnviados;
    }

    public static synchronized void reset() {
        numero1 = null;
        numero2 = null;
        operador = null;
        resultadosEnviados = 0;
    }
}
