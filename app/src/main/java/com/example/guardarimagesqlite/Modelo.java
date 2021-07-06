package com.example.guardarimagesqlite;

import android.graphics.Bitmap;

public class Modelo {

    private String id;
    private String ruta;
    private Bitmap imagen;

    public Modelo() {
    }

    public Modelo(String id, String ruta, Bitmap imagen) {
        this.id = id;
        this.ruta = ruta;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }
}
