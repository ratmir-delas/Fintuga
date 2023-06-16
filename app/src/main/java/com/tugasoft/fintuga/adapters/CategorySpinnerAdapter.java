package com.tugasoft.fintuga.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.R;

import java.util.ArrayList;
import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {
    private final List<Category> items;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final int mResource;

    public CategorySpinnerAdapter(Context context, int i, ArrayList<Category> arrayList) {
        super(context, i, 0, arrayList);
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mResource = i;
        this.items = arrayList;
    }

    public boolean isEnabled(int i) {
        return true;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        return createItemView(i, view, viewGroup);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        return createItemView(i, view, viewGroup);
    }

    private View createItemView(int i, View view, ViewGroup viewGroup) {
        int i2;
        View inflate = this.mInflater.inflate(this.mResource, viewGroup, false);
        Category category = this.items.get(i);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_color);
        ((TextView) inflate.findViewById(R.id.tv_desc)).setText(category.getCategory());
        String color = category.getColor();
        if (color != null) {
            i2 = Color.parseColor(color);
        } else {
            i2 = ContextCompat.getColor(this.mContext, R.color.colorAccent);
        }
        imageView.setColorFilter(i2);
        return inflate;
    }
}
