package com.example.sebastin.bina2;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 29/03/2016.
 */
public class RecordingsAdapter extends ArrayAdapter{
    List list = new ArrayList();
    String name;
    View layout;
    Context context;
    Typeface typeface;
    public RecordingsAdapter(Context context, int resource) {

        super(context, resource);
        this.context = context;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/OPTIMA.TTF");
    }
    static class DataHandler{
        ImageView poster ;
        TextView title;
        TextView data;

    }
    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        final DataHandler handler;
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_view_custom_layout,parent,false);
            handler = new DataHandler();
            handler.poster = (ImageView)row.findViewById(R.id.imageView);
            handler.title = (TextView)row.findViewById(R.id.list_item);
            handler.data = (TextView)row.findViewById(R.id.textView2);

//            layout = row.findViewById(R.layout.list_view_custom_layout);
            row.setTag(handler);
        }
        else{
            handler = (DataHandler)row.getTag();
        }
        RecordingsDataProvider dataProvider;
        dataProvider = (RecordingsDataProvider)this.getItem(position);
        handler.poster.setImageResource(dataProvider.getRecording_image_resources());
        handler.title.setText(dataProvider.getRecording_title_resources());
        handler.data.setText(dataProvider.getRecording_data_resources());
        handler.title.setTypeface(typeface);
        handler.data.setTypeface(typeface);
        name = dataProvider.getRecording_title_resources();
        return row;
    }

    public String getName() {
        return name;
    }
}
