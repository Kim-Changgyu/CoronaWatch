package com.team12.coronawatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterKR extends BaseAdapter {
    ArrayList<CustomList> items;
    Context context;
    LayoutInflater inflater;

    public CustomAdapterKR(ArrayList<CustomList> items) {
        this.items = items;
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null) {
            context = viewGroup.getContext();
            if(inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            view = inflater.inflate(R.layout.listview_custom, viewGroup, false);
        }

        TextView nameText = view.findViewById(R.id.cv_region);
        TextView isolText = view.findViewById(R.id.cv_isol);
        TextView defText = view.findViewById(R.id.cv_def);
        TextView clearText = view.findViewById(R.id.cv_clear);
        TextView deathText = view.findViewById(R.id.cv_death);

        CustomList customList = items.get(position);
        nameText.setText(customList.getName());
        isolText.setText(customList.getIsol());
        defText.setText(customList.getDef() + "(+" + customList.getDefInc() + ")");
        clearText.setText(customList.getClear());
        deathText.setText(customList.getDeath());

        return view;
    }
}
