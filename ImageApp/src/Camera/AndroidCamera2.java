package Camera;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;




public class AndroidCamera2 implements CameraDevice {
	private Socket socket=null;
	private DataInputStream inSock = null;
	private DataOutputStream outSock = null;
	private static String fileFormat=".jpg";
	private String name;
	private static int serverPort=3000;
	private static String serverIP = "192.168.1.29";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	public AndroidCamera2(String name) {
		this.name=name;
	}



	public boolean connect() {
		int port = 3000;
		InetAddress addr = null;
        if (!serverIP.isEmpty() && serverPort > 0) {
            try {
                addr = InetAddress.getByName(serverIP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            }
            port = serverPort;
        }
        try {
            socket = new Socket(addr, port);
            System.out.println("Connessione avviata...\n");
        } catch (Exception e) {
            System.out.println("Problemi nella creazione della socket: " + e.getMessage());
            return false;
        }
		try {
			inSock = new DataInputStream(socket.getInputStream());
			outSock = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Problemi nella creazione degli stream su socket: " + e.getMessage());
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			return false;
		}
		return true;
	}


	@Override
	public boolean takePhoto(File folder) {
		FileOutputStream fos=null;
		try {
			outSock.writeUTF("REQPHOTO");
			long lenght=inSock.readLong();
			String filename="placeholder";
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			filename=sdf.format(timestamp)+fileFormat;
			System.out.println("ricevo il file: "+filename);
			File curr = new File(folder.getAbsoluteFile() + "\\" + filename);
			fos = new FileOutputStream(curr);
			trasferisci_foto(inSock, fos, lenght);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	public void close() {
		try {
			inSock.close();
			outSock.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void trasferisci_foto(InputStream src, OutputStream dest, long lsrc) throws IOException {
		int buffer;
		try {
			for (long l = 0; l < lsrc; l++) {
				buffer = src.read();
				dest.write(buffer);
			}
			dest.flush();
		} catch (EOFException e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
		}
	}
	@Override
	public String toString(){
		return this.name;
	}
	@Override
	protected void finalize(){
		this.close();
	}
}
