import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ClientApp {

    public static JFrame setup = new JFrame("bit.wav (Desktop Client)");
    public static String songRequest;
    public static ProcessBuilder pb;
    public static Process start;

    private static void gameLauncher() {
        Font settingFonts = new Font("Nexa Bold", Font.PLAIN, 50);
        Font font2 = new Font ("Nexa Regular", Font.BOLD, 16);
        JLabel title = new JLabel("bit.wav",  SwingConstants.CENTER);
        JLabel label2 = new JLabel("Enter the name of the file to be played:");
        TextField field = new TextField(35);

        field.addKeyListener(new KeyAdapter () {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    songRequest = field.getText();
                    //System.out.println (songRequest);
                    setup.setVisible (false);
                    setup.dispose ();
                    return;
                    //setup.dispatchEvent (new WindowEvent (setup, WindowEvent.WINDOW_CLOSING));
                }
            }
        });

        setup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setup.getContentPane().setBackground(new Color(196,196,196));
        setup.setLayout(new FlowLayout());
        setup.getRootPane().setBorder(BorderFactory.createMatteBorder(6, 6, 6, 6, Color.BLACK));
        title.setFont(settingFonts);
        setup.add(Box.createVerticalStrut(80));
        setup.add(title);
        setup.add(Box.createHorizontalStrut(1000));
        label2.setFont(font2);
        setup.add (label2);
        setup.add(Box.createHorizontalStrut(5));
        field.setFont (new Font ("Nexa Regular", Font.PLAIN, 16));
        setup.add(field);

        setup.pack();
        setup.setSize(860, 200);
        setup.setVisible(true);
        setup.setLocationRelativeTo(null);
    }

    /**
     * Gets a filename and then gets the file from the client
     */
    public static void main(String [] args){

        byte[] out;

        gameLauncher ();
        System.out.println ("ended jframe");

        try {
       //     BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            //String fileName = songRequest;
String fileName = "newsong.wav";
            Socket client = new Socket(InetAddress.getByAddress(new byte[]{(byte) 172, (byte) 17, (byte) 51, (byte) 86}), 4321);

         //   PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
           // output.println(fileName);

            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());

            new File ("local_files/" + fileName).createNewFile();
            RandomAccessFile f = new RandomAccessFile(new File("local_files/" + fileName), "rw");
            f.setLength(2574 * 1024);


            Packet p;

            try {
                while ((p = (Packet) inputStream.readObject()) != null) {

                    out = p.getData();
                    int location = p.getNumber();

                    f.seek(location * 1024);
                    f.write(out);
                }
            } catch (EOFException e){}



        } catch (IOException e){
            System.out.println(e.getMessage());
        }   catch (Exception e){
            System.out.println("Error 444");
        }
    }
}
