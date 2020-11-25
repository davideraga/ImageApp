package com.example.sendpicture.sender;

import com.example.sendpicture.MainActivity;
import com.example.sendpicture.camera.AndroidCamera2;
import com.example.sendpicture.camera.Camera;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;


public class Sender extends Thread {
    private static int serverPort = 3000;
    MainActivity activity;
    String fileFormat;
    public Sender(MainActivity activity){
        this.activity= activity;
    }
    @Override
    public void  run() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Sender started");
        Camera camera = new AndroidCamera2(activity);
        fileFormat = camera.getFileFormat();
        ServerSocket serverSocket=null;
        Socket clientSocket=null;
        Socket socket;
        DataInputStream inSock=null;
        DataOutputStream outSock=null;
        try {
            serverSocket=new ServerSocket(serverPort,1);
            serverSocket.setReuseAddress(true);
        } catch(Exception e) {
            System.out.println("Problemi nella creazione della server socket: "+ e.getMessage());
            return;
        }
        while (true) {
            System.out.println("Sender: in attesa di richieste...\n");
            try {
                clientSocket=serverSocket.accept();
                clientSocket.setSoTimeout(60000);
                System.out.println("connessione accettata: " + clientSocket);
            } catch(Exception e) {
                System.out.println("Problemi nella accettazione della connessione: " + e.getMessage());
                continue;
            }
            socket=clientSocket;
            try {
                inSock = new DataInputStream(socket.getInputStream());
                outSock = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Problemi nella creazione degli stream su socket: " + e.getMessage());
                try {
                    socket.close();
                    continue;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    continue;
                }
            }
            try {
                String request = null;
                while ((request = inSock.readUTF()).equals("REQPHOTO")) {
                    byte[] bytes = camera.getPhoto();
                    if (bytes != null) {
                        send_image(outSock, bytes);
                    } else {
                        System.out.println("Error null image");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private boolean send_image( DataOutputStream outSock, byte[] bytes) {
        try {
            outSock.writeLong(bytes.length);
            /* for(byte b : bytes){
                    outSock.write(b);
            }*/
            outSock.write(bytes);
            outSock.flush();
        }catch(IOException e) {
            System.out.println("Problemi, i seguenti: ");
            e.printStackTrace();
        }
        return true;
    }
}