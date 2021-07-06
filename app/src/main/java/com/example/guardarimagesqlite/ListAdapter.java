package com.example.guardarimagesqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter {

    private List<Modelo> mLista;
    private Context context;
    private int resourceLayout;

    public ListAdapter(@NonNull Context context, int resource, @NonNull List<Modelo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mLista = objects;
        this.resourceLayout = resource;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view==null){
            view = LayoutInflater.from(context).inflate(resourceLayout,null);
        }

        Modelo modelo = mLista.get(position);

        ImageView imageView = view.findViewById(R.id.image);
        imageView.setImageBitmap(modelo.getImagen());

        TextView id = view.findViewById(R.id.txtId);
        id.setText(modelo.getId());

        TextView ruta = view.findViewById(R.id.txtRuta);
        ruta.setText(modelo.getRuta());

        //return super.getView(position, convertView, parent);
        return view;
    }
}
