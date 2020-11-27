from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler
import pyzed.sl as sl
import os
import sys
import datetime
def main():
    zed = sl.Camera()

    # Create a InitParameters object and set configuration parameters
    init_params = sl.InitParameters()
    init_params.camera_resolution = sl.RESOLUTION.HD2K  # Use HD1080 video mode
    init_params.camera_fps = 15  # Set fps at 15
    init_params.depth_mode = sl.DEPTH_MODE.ULTRA  # Use PERFORMANCE depth mode

    # Open the camera
    err = zed.open(init_params)
    if err != sl.ERROR_CODE.SUCCESS:
        exit(1)

    image = sl.Mat()
    runtime_parameters = sl.RuntimeParameters()
    
    with SimpleXMLRPCServer(('localhost', 8000)) as server:

        def check():#per controllare che il server esista
            return True

        def takePhoto(dir):
            if not os.path.isdir(dir):   #nel caso ci sia un errore sulla directory o sia in remoto 
                if not os.path.isdir("photos"):#viene usata una directory di default
                    os.mkdir("photos")
                dir="photos"
            
            if zed.grab(runtime_parameters) == sl.ERROR_CODE.SUCCESS:
                timestamp=zed.get_timestamp(sl.TIME_REFERENCE.IMAGE)
                ts = datetime.datetime.fromtimestamp(timestamp.get_seconds()).strftime('%Y-%m-%d-%H-%M-%S')#format timestamp
                ts=ts+"-"+str(timestamp.get_milliseconds()%1000)
                zed.retrieve_image(image, sl.VIEW.LEFT)
                image.write(dir+"/l"+ts+".jpeg", sl.MEM.CPU, 50)
                zed.retrieve_image(image, sl.VIEW.RIGHT)
                image.write(dir+"/r"+ts+".jpeg", sl.MEM.CPU, 50)
                return True
            else:
                return False
                
        server.register_function(check, "check")
        server.register_function(takePhoto, "takePhoto")
        print("server is listening")
        try:    
            server.serve_forever()
        except KeyboardInterrupt:
                zed.close()
                print("\nKeyboard interrupt received, exiting.")
                sys.exit(0)

if __name__ == "__main__":
    main()