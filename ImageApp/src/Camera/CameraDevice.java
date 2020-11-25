package Camera;

import java.io.File;

public interface CameraDevice {
	void close();
	boolean connect();
	//@Override
	//String toString();
	boolean takePhoto(File folder);
}
