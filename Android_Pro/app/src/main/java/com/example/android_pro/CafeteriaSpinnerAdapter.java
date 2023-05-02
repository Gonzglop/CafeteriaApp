package com.example.android_pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CafeteriaSpinnerAdapter extends ArrayAdapter<Cafeteria> implements SpinnerAdapter {
    private Context context;
    private ArrayList<Cafeteria> listaCafeterias;

    public CafeteriaSpinnerAdapter(Context context, ArrayList<Cafeteria> listaCafeterias) {
        super(context, R.layout.item_cafeteria_spinner, listaCafeterias);
        this.context = context;
        this.listaCafeterias = listaCafeterias;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(listaCafeterias.get(position).getNombre());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_cafeteria_spinner, null);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(listaCafeterias.get(position).getNombre());
        return view;
    }

    @Override
    public long getItemId(int position) {
        return listaCafeterias.get(position).getId();
    }
}
