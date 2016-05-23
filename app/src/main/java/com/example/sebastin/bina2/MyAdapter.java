package com.example.sebastin.bina2;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sebastian on 15/03/2016.
 */
public class MyAdapter extends BaseExpandableListAdapter {
    private List<String>header_titles;
    private HashMap<String,List<String>>child_titles;
    private Context ctx;
    Typeface typeface;
    RadioGroup radioGroup;
    MyAdapter (Context ctx,List<String>header_titles,HashMap<String,List<String>>child_titles){
        this.ctx = ctx;
        this.header_titles = header_titles;
        this.child_titles = child_titles;
        typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/OPTIMA.TTF");
    }
    @Override
    public int getGroupCount() {
        return header_titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_titles.get(header_titles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return header_titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child_titles.get(header_titles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String)this.getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_layout,null);
        }
//        radioGroup = (RadioGroup)convertView.findViewById(R.id.childGroup);
        TextView textView= (TextView) convertView.findViewById(R.id.headingItem);

        textView.setTypeface(typeface, Typeface.BOLD);
        textView.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String title = (String)this.getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_layout, null);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.childItem);
        textView.setText(title);
        textView.setTypeface(typeface, Typeface.BOLD);
//        RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.childItem);
//        radioButton.setText(title);
//        radioGroup.addView(radioButton);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
