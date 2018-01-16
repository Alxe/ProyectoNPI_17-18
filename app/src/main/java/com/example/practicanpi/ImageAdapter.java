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

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Integer> activos;

    // references to our images
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



    public ImageAdapter(SensorActivity c,List<Integer> activos) {
        mContext = c;
        this.activos = activos;
    }

    public void update(List<Integer> activos) {
        this.activos = activos;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_objeto_view, null);
        }


        imageView = (ImageView) convertView.findViewById(R.id.imageview_objeto);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_nombre_objeto);

        // 4
        int objeto = activos.size() > position ? activos.get(position) : 0 ;

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mThumbIds[objeto]);
            nameTextView.setText(mNameIds[objeto]);



        return convertView;
    }


}
