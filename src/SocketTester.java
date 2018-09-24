import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
//import org.j

public class SocketTester extends Socket {

    public static void main(String[] args) {
        ServerSocket server = null;

        //Setup sever
        try {
            server = new ServerSocket(4321);
        } catch (IOException e){
            System.out.println("Error connecting on port 4321");
            System.exit(1);
        }

        Socket client = null;

        //Add client
        try {
            client = server.accept();
        } catch (IOException e){
            System.out.println("Accept failed");
            System.exit(1);
        }


        byte[] out;
        ObjectInputStream in;

        try {
            new File ("assets/newfile.wav").createNewFile();
            RandomAccessFile f = new RandomAccessFile(new File("assets/newfile.wav"), "rw");
            f.setLength(2574 * 1024);
            in = new ObjectInputStream(client.getInputStream());
            Packet p;

            try {
                while ((p = (Packet) in.readObject()) != null) {

                    out = p.getData();
                    int location = p.getNumber();

                    f.seek(location * 1024);
                    f.write(out);
                }
            } catch (EOFException e){}

            in.close();
            server.close();
            client.close();
        } catch (IOException e){
            //System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
