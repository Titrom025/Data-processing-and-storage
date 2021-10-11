import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Lab15 {
    public static class Server {
        private final Selector selector;
        private final InetSocketAddress inputAddress, outputAddress;

        public Server(int inputPort, String address, int outputPort) throws IOException {
            selector = SelectorProvider.provider().openSelector();
            inputAddress = new InetSocketAddress(InetAddress.getLocalHost(), inputPort);
            outputAddress = new InetSocketAddress(address, outputPort);
        }

        private class Translator {
            final Socket input, output;
            final ByteBuffer buffer;

            private Translator(Socket input, Socket output) {
                this.input = input;
                this.output = output;
                buffer = ByteBuffer.allocate(1024);
            }

            private void transmit() throws IOException {
                SocketChannel inChannel = input.getChannel();
                SocketChannel outChannel = output.getChannel();

                buffer.clear();
                if (inChannel.read(buffer) == -1) {
                    closeConnection();
                    return;
                }

                buffer.flip();
                outChannel.write(buffer);
            }

            private void closeConnection() throws IOException {
                System.out.println("Closing the connection");

                for (Socket socket : Arrays.asList(input, output)) {
                    socket.getChannel().keyFor(selector).cancel();
                    socket.close();
                }
            }
        }

        private void acceptConnection(SelectionKey keyToAccept) throws IOException {
            System.out.println("Accepting a new connection");
            SocketChannel clientSocketChannel, serverSocketChannel;

            clientSocketChannel = ((ServerSocketChannel) keyToAccept.channel()).accept();
            clientSocketChannel.configureBlocking(false);

            try {
                serverSocketChannel = SocketChannel.open(outputAddress);
                serverSocketChannel.configureBlocking(false);
            } catch (ConnectException e) {
                System.out.println(e.getMessage());
                clientSocketChannel.close();
                return;
            }

            SelectionKey clientKey = clientSocketChannel.register(selector, SelectionKey.OP_READ);
            SelectionKey serverKey = serverSocketChannel.register(selector, SelectionKey.OP_READ);

            clientKey.attach(new Translator(clientSocketChannel.socket(), serverSocketChannel.socket()));
            serverKey.attach(new Translator(serverSocketChannel.socket(), clientSocketChannel.socket()));
        }

        public void acceptConnections() throws IOException {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(inputAddress);
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (selector.select() > 0) {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();

                    if (sk.isAcceptable()) {
                        acceptConnection(sk);
                    }

                    if (sk.isReadable()) {
                        Translator translator = (Translator) sk.attachment();
                        translator.transmit();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final int inputPort = 2000;
        final int outputPort = 2020;
        final String address = "localhost";

        Server server = new Server(inputPort, address, outputPort);
        server.acceptConnections();
    }
}