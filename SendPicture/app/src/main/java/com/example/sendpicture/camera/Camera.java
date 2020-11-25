package com.example.sendpicture.camera;

public interface Camera {
    byte[] getPhoto();
    String getFileFormat();
   // public int getMinInterval();//(msec)intervallo minimo tra l'acquisizione di una photo e l'altra
    //tipo di file
}
