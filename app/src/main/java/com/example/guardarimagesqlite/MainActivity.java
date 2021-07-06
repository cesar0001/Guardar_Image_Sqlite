package com.example.guardarimagesqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.guardarimagesqlite.transacciones.Transacciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView objImagen;
    Button btn;
    static final int PETICION_ACCESO_CAM = 100;
    String currentPhotoPath;
    Bitmap decoded;
    byte[] blob = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btnfoto);

        objImagen = (ImageView) findViewById(R.id.Fotografia);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dispatchTakePictureIntent();
                permisos();
            }
        });

        findViewById(R.id.Ingresar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        findViewById(R.id.btnLista).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),Listado.class);
                startActivity(in);
            }
        });

        //objImagen.setImageBitmap(buscarImagen(2));

    }

    private void permisos()
    {


        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PETICION_ACCESO_CAM);
        }
        else
        {
            dispatchTakePictureIntent();
            //tomarfoto();
        }
    }


/*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }else{
            tomarfoto();
        }
    }
*/


    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESO_CAM)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
                //tomarfoto();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Se necesitan permisos de acceso", Toast.LENGTH_LONG).show();
        }
    }


    private void tomarfoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ObjImagen.setImageBitmap(imageBitmap);
            */
            File foto = new File(currentPhotoPath);
            objImagen.setImageURI(Uri.fromFile(foto));

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.fromFile(foto));

                //cambia el tamaño manteniendo la relacion del aspecto
                setToImageView(getResizeBitmap(bitmap,500));
            }catch (Exception e){

            }

            galleryAddPic();
        }

    }

    private Bitmap getResizeBitmap(Bitmap bitmap, int maxSize) {
            int wi = bitmap.getWidth();
            int he = bitmap.getHeight();

            if (wi<= maxSize && he<=maxSize){
                return bitmap;
            }

            float fabradio = (float)wi/(float)he;

            if(fabradio > 1){
                wi=maxSize;
                he = (int) (wi/fabradio);
            }else{
                he=maxSize;
                wi= (int) (he*fabradio);
            }

            return  Bitmap.createScaledBitmap(bitmap,wi,he,true);
    }

    private void setToImageView(Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        byte[] blob = bytes.toByteArray();
        setBlob(blob);
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    private void uploadImage() {

        //ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
        //decoded.compress(Bitmap.CompressFormat.PNG, 0, baos);
        //byte[] blob = baos.toByteArray();

        if (getBlob() != null) {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(Transacciones.imagen, getBlob());
            valores.put(Transacciones.ruta, currentPhotoPath);


            Long res = db.insert(Transacciones.tablaimagenes, Transacciones.id, valores);
            db.close();


            Toast.makeText(getApplicationContext(), "Se ha guardado exitosamente." + res.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se pudo guardar." , Toast.LENGTH_SHORT).show();

        }



        /*
        String sql = "INSERT INTO personas (imagen) VALUES(?)";
        SQLiteStatement insert = db.compileStatement(sql);
        insert.clearBindings();
        insert.bindBlob(1, blob);
        insert.executeInsert();
        db.close();
        Toast.makeText(getApplicationContext(), "Se ha guardado exitosamente.", Toast.LENGTH_SHORT).show();


         */

        // con este codigo muestra la imagen
        /*
        public Bitmap buscarImagen(long id){
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = String.format("SELECT * FROM imagenes WHERE id = %d", id);
        Cursor cursor = db.rawQuery(sql, new String[] {});
        Bitmap bitmap = null;
        if(cursor.moveToFirst()){
            byte[] blob = cursor.getBlob(1);
            ByteArrayInputStream bais = new ByteArrayInputStream(blob);
            bitmap = BitmapFactory.decodeStream(bais);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return bitmap;
    }
         */

    }


    public Bitmap buscarImagen(long id){
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        String sql = "SELECT * FROM imagenes where id = "+id;
        Cursor cursor = db.rawQuery(sql, new String[] {});
        Bitmap bitmap = null;
        if(cursor.moveToFirst()){
            byte[] blob = cursor.getBlob(1);
            ByteArrayInputStream bais = new ByteArrayInputStream(blob);
            bitmap = BitmapFactory.decodeStream(bais);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return bitmap;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.guardarimagesqlite.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}