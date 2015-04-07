package com.nolanlawson.supersaiyan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SectionTitleAdapter extends ArrayAdapter<CharSequence> {
    
    private LayoutInflater inflater;
    private int layoutResId;
    
    public SectionTitleAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResId = layoutResId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        
        CharSequence item = getItem(position);
        
        if (view == null) {
           view = inflater.inflate(layoutResId, null, false);
        }
        
        TextView textView = (TextView) view;
        
        textView.setText(item);
        
        return view;
    }
}
