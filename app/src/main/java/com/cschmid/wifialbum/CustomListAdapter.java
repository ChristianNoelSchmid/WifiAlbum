package com.cschmid.wifialbum;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.function.Function;

public class CustomListAdapter extends ArrayAdapter {

    private final Activity context;
    private final String[] nameArray;
    private final Integer[] imageIDArray;

    private final View.OnClickListener[] onClickListener;

    public CustomListAdapter(Activity context, String[] nameArray, Integer[] imageIDArray, View.OnClickListener[] onClickListener) {

        super(context, R.layout.activity_menu_row, nameArray);

        this.context = context;
        this.nameArray = nameArray;
        this.imageIDArray = imageIDArray;
        this.onClickListener = onClickListener;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_menu_row, null, true);

        if(position % 2 == 0) rowView.setBackgroundColor(Color.GRAY);

        TextView nameTextField = rowView.findViewById(R.id.textViewHeader);
        ImageView imageViewField = rowView.findViewById(R.id.imageViewThumbnail);

        nameTextField.setText(nameArray[position]);
        imageViewField.setImageResource(imageIDArray[position]);

        rowView.setOnClickListener(onClickListener[position]);

        return rowView;

    }

}
