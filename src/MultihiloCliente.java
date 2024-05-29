import javax.imageio.IIOException;
import java.util.ArrayList;
import java.util.Random;

public class MultihiloCliente {
    static Random random = new Random();
    static final int id = random.nextInt(1000);
    static final String NOMBRE = "Calculadora " + id;
    public static final String HOST = "25.49.132.248";

    public static void main(String[] args) throws IIOException {
        Thread cliente = new Thread(new Cliente(HOST, id, NOMBRE));
        cliente.start();
    }
}
