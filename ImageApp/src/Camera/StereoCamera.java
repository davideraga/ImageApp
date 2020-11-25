package Camera;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class StereoCamera implements CameraDevice {
	private String name;
	private static String serverURL="http://localhost:8000";
	XmlRpcClient client;
	public StereoCamera(String name) {
		super();
		this.name = name;
	}


	@Override
	public boolean takePhoto(File folder) {
		Object[] params = new Object[1];
		params[0]=folder.getAbsolutePath();
	    try {
			boolean  result = (boolean) client.execute("takePhoto", params);
			System.out.println(result);
			return result;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void close() {

	}

	@Override
	public boolean connect() {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	    try {
			config.setServerURL(new URL(serverURL));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		}
	    try{
	    client = new XmlRpcClient();
	    client.setConfig(config);
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return false;
	    }
	    Object[] params = new Object[0];
	    try {
			boolean  result = (boolean) client.execute("check", params);
			return result;
		} catch (XmlRpcException e) {
			return false;
		}
	}



	@Override
	public String toString() {
		return this.name;
	}
	@Override
	protected void finalize(){
		this.close();
	}
}
