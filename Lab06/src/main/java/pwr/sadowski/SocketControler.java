package pwr.sadowski;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketControler {
    protected int port;
    protected ServerSocket s;

    public synchronized void send(String type, String message, String host, int port){
        Socket s;
        try{
            s = new Socket(host, port);
            OutputStream out = s.getOutputStream();
            PrintWriter pw = new PrintWriter(out, false);

            pw.println(type + ";" + message);

            pw.flush();
            pw.close();
            s.close();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public String receive(){
        String theLine = null;
        try {
            Socket sc = s.accept();
            InputStream is = sc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            theLine = br.readLine();

            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return theLine;
    }
}
