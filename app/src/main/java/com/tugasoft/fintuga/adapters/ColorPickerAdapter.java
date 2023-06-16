package com.tugasoft.fintuga.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.R;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

    public OnChooseColorListener chooseColorListener;
    Context mContext;
    String[] colorList;

    public ColorPickerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public ColorPickerAdapter(Context mContext, String[] colorList) {
        this.mContext = mContext;
        this.colorList = colorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.color_file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cardView.setCardBackgroundColor(Color.parseColor(colorList[position]));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseColorListener.onChoose(colorList[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
        }
    }

    public ColorPickerAdapter setOnColorChooser(OnChooseColorListener onChooseColorListener){
        this.chooseColorListener = onChooseColorListener;
        return this;
    }

    public interface OnChooseColorListener {
        void onChoose(String color);

        void onCancel();
    }
}
