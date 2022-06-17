import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String args[]) {
        Scanner input = new Scanner(System.in);

        System.out.println("Entre com a porta:");
        String s1 = input.nextLine();

        Server server = new Server(s1);

        try {
            server.connection();
            System.out.println("Conexão aberta!");
            System.out.println("Aguardando mensagens...");
        } catch (IOException e){
            System.out.println("Erro de conexão!");
            e.printStackTrace();
        }

        try {
            server.receive();
        } catch (IOException e){
            System.out.println("Erro no recebimento de dados!");
            e.printStackTrace();
        }
    }
}