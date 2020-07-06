package com.secureandroid.secdroid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.secureandroid.secdroid.Model.avResults;
import com.secureandroid.secdroid.R;

import java.util.List;

public class VtAdapter extends RecyclerView.Adapter<VtAdapter.VtViewHolder> {

    private Context context;
    private List<avResults> avResultList;

    public VtAdapter(Context context, List<avResults> avResultList) {
        this.context = context;
        this.avResultList = avResultList;
    }

    @NonNull
    @Override
    public VtAdapter.VtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_row, parent,false);
        return new VtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VtAdapter.VtViewHolder holder, int position) {

        avResults item = avResultList.get(position);

        holder.avName.setText(item.getAvName());
        holder.vName.setText(item.getVName());
        holder.symbol.setBackground(item.getSymbol());
    }

    @Override
    public int getItemCount() {
        return avResultList.size();
    }

    public class VtViewHolder extends RecyclerView.ViewHolder {
        public TextView avName;
        public TextView vName;
        public ImageView symbol;

        public VtViewHolder(@NonNull View itemView) {
            super(itemView);

            avName = (TextView) itemView.findViewById(R.id.avNameId);
            vName = (TextView) itemView.findViewById(R.id.vNameId);
            symbol = (ImageView) itemView.findViewById(R.id.symbolId);
        }
    }
}
