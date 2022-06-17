import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import com.sun.nio.sctp.MessageInfo;

public class Server {

    private InetSocketAddress portServer;
    private String addressServer;
    private ByteBuffer buffer;
    private SctpServerChannel ssc;
    private SctpChannel sc;
    private Charset charset;
    private CharsetEncoder encoder;
    private CharsetDecoder decoder;
    private CharBuffer cBuffer;


    public Server(String port) {
        int intPort = Integer.parseInt(port);
        this.portServer = new InetSocketAddress(intPort);
        this.buffer = ByteBuffer.allocate(1024);
        this.cBuffer = CharBuffer.allocate(1024);

        this.charset = Charset.forName("ISO-8859-1");
        this.decoder = charset.newDecoder();
        this.encoder = charset.newEncoder();

        try {
            this.addressServer = InetAddress.getByName("localhost").getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Erro em localizar host!");
            e.printStackTrace();
        }
    }

    public void connection() throws IOException {
            ssc = SctpServerChannel.open();
            ssc.bind(portServer);

            System.out.println("Endereço: " + addressServer + ":" + portServer.getPort());
            System.out.println();
    }

    public void receive() throws IOException {
        sc = ssc.accept();
        //if((sc = ssc.accept())!= null){
            while(true) {
                MessageInfo messageInfo = sc.receive(buffer, null, null);
                buffer.flip();

                String message = decoder.decode(buffer).toString();

                System.out.print(messageInfo.address() + " -> ");
                System.out.println(message);

                if(message.equalsIgnoreCase("exit")){
                    sc.close();
                    System.out.println("Conexão encerrada!");
                    break;
                } else {
                    try {
                        send(messageInfo.address(), message);
                        System.out.println("Rebatendo... \n");
                    } catch(IOException e){
                        System.out.println("Erro no envio de dados!");
                        e.printStackTrace();
                    }
                }
            }
    }

    public void send(SocketAddress addressClient, String message) throws IOException{
            buffer.clear();

            cBuffer.put(message);
            cBuffer.flip();
            encoder.encode(cBuffer, buffer, true);
            buffer.flip();

            MessageInfo messageInfo = MessageInfo.createOutgoing(addressClient, 0);

            sc.send(buffer, messageInfo);

            cBuffer.clear();
            buffer.clear();
    }
}
