package com.example.practicanpi;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by soler on 16/01/2018.
 */

/*
    ImageAdapter: Esta clase se usa para generar el contenido de la GridView que encontramos en sensorActivity con layout: activity_sensor.xml
    - Datos de la clase:
        - mContext: Contexto
        - activos: Lista de objetos activos
        - mThumbsIds: Guarda Ids de las imagenes de los objetos
        - mNameIds: Guarda Ids de los nombres de los objetos

 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Integer> activos;

    private Integer[] mThumbIds = {
            R.drawable.blank,
            R.drawable.o1,
            R.drawable.o2,
            R.drawable.o3,
            R.drawable.o4,
            R.drawable.cuadro
    };
    private Integer[] mNameIds = {
            R.string.empty,
            R.string.o1,
            R.string.o2,
            R.string.o3,
            R.string.o4,
            R.string.cuadro
    };


    /*
        Constructor de clase:
            - Asignamos el contexto y los elementos activos que recibimos
     */
    public ImageAdapter(SensorActivity c,List<Integer> activos) {
        mContext = c;
        this.activos = activos;
    }
    /*
        Update
            - Se actualiza la lista de objetos activos
            - Se llama a notifyDataSetChanged() para actializar la view
     */
    public void update(List<Integer> activos) {
        this.activos = activos;
        notifyDataSetChanged();
    }

    /*
        getCount
            Devuelve numero de objetos
     */
    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    /*
        getView : Es llamado por sensorActivity para rellenar cada hueco del gridView con layout: layout_objeto_view.xml
            - Recibe: posicion , convertView, parent
            - Si la converView que recibe esta vacia crea una nueva con el layout
            - Si existe un objeto activo para la posicion asigna su imagen y texto
                - Sino asigna imagen en blanco y texto "Vacio"
            - Devuelve el nuevo converView
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_objeto_view, null);
        }


        imageView = (ImageView) convertView.findViewById(R.id.imageview_objeto);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_nombre_objeto);

        int objeto = activos.size() > position ? activos.get(position) : 0 ;

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mThumbIds[objeto]);
            nameTextView.setText(mNameIds[objeto]);

        return convertView;
    }


}
