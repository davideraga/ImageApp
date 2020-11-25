package com.example.sendpicture.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import com.example.sendpicture.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AndroidCamera2 implements Camera {
    private int minInterval=1000;
    MainActivity activity;
    private static String fileFormat=".jpg";
    public AndroidCamera2(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public byte[] getPhoto() {
        byte[] photo=null;
        activity.takePicture();
        try {
            activity.getPhotoSemaphore().acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Photo captured");
        photo=activity.getPhoto();
        return photo;
    }
    public String getFileFormat(){
        return fileFormat;
    }

}
