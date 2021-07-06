package com.example.guardarimagesqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guardarimagesqlite.transacciones.Transacciones;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class Listado extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private List<Modelo> mLista = new ArrayList<>();
    private ListView mlistView;
    ListAdapter mAdapter;
    public static Bitmap enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mlistView =(ListView)findViewById(R.id.listview);
        mlistView.setOnItemClickListener(this);

        //mLista.add(new Modelo("cesar","23",R.drawable.ic_launcher_background));

        /*
        mLista.add(new Modelo("cesar","23", buscarImagen(1)));

        mAdapter = new ListAdapter(this,R.layout.item_row, mLista);

        mlistView.setAdapter(mAdapter);


         */

        cargarDatos();

        findViewById(R.id.main_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

    }

    private void cargarDatos(){

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from "+Transacciones.tablaimagenes,null);

        while (cursor.moveToNext()){

            int id = cursor.getInt(0);
            String ruta = cursor.getString(2);

            mLista.add(new Modelo(String.valueOf(id), ruta, buscarImagen(id)));
            mAdapter = new ListAdapter(this,R.layout.item_row, mLista);

            mlistView.setAdapter(mAdapter);


        }

        cursor.close();

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

    public static int resultado=1;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
        intent.putExtra("id", mLista.get(position).getId());
        intent.putExtra("ruta", mLista.get(position).getRuta());
        enviar= mLista.get(position).getImagen();
        startActivityForResult(intent,resultado);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultado && requestCode == RESULT_OK) {
            cargarDatos();
        }
    }



    public static Bitmap enviar(){
        return enviar;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}