package com.example.guardarimagesqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guardarimagesqlite.transacciones.Transacciones;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imagenSecond = (ImageView)findViewById(R.id.imagenSecond);
        TextView id = (TextView)findViewById(R.id.IDSegundo);
        TextView ruta = (TextView)findViewById(R.id.rutasegundo);

        Intent intent = getIntent();
        id.setText(intent.getStringExtra("id"));
        ruta.setText(intent.getStringExtra("ruta"));
        //Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
        imagenSecond.setImageBitmap(Listado.enviar());

        findViewById(R.id.btnEliminar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar(Integer.parseInt(id.getText().toString()));
            }
        });

    }

    private void eliminar(int posicion) {
        SQLiteConexion conexion;
        conexion = new SQLiteConexion(this, Transacciones.NameDataBase,null,1);

        SQLiteDatabase db = conexion.getWritableDatabase();
        String[] params = {String.valueOf(posicion)};

        String wherecod = Transacciones.id + "=?";

        db.delete(Transacciones.tablaimagenes,wherecod,params);

        Toast.makeText(getApplicationContext(),"Datos Eliminado",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(),Listado.class);
        startActivity(i);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}