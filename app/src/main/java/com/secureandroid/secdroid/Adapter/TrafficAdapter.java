package com.secureandroid.secdroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.secureandroid.secdroid.IpLookupActivity;
import com.secureandroid.secdroid.Model.TrafficList;
import com.secureandroid.secdroid.R;

import java.util.List;

public class TrafficAdapter extends RecyclerView.Adapter<TrafficAdapter.TrafficViewHolder>{

    private Context context;
    private List<TrafficList> trafficLists;

    public TrafficAdapter(Context context, List<TrafficList> trafficLists) {
        this.context = context;
        this.trafficLists = trafficLists;
    }

    @NonNull
    @Override
    public TrafficAdapter.TrafficViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //view object holds the entire trafficlistview xml file and we can interact with
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trafficlistview, parent,false);
        return new TrafficViewHolder(view);
    }

    // we have to bind our viewholder with adapter and datasource
    @Override
    public void onBindViewHolder(@NonNull TrafficAdapter.TrafficViewHolder holder, int position) {

        TrafficList item = trafficLists.get(position);

        holder.tAppName.setText(item.gettAppName());
        holder.tAppIcon.setBackground(item.gettAppIcon());
        holder.tDestIp.setText(item.gettDestIp());
        holder.tDestPort.setText(item.gettDestPort());
        holder.tProtocol.setText(item.gettProtocol());
        holder.tTimeStamp.setText(item.gettTimeStamp());

    }

    @Override
    public int getItemCount() {
        return trafficLists.size();
    }

    public class TrafficViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tAppName;
        public ImageView tAppIcon;
        public TextView tDestIp;
        public TextView tDestPort;
        public TextView tProtocol;
        public TextView tTimeStamp;

        public TrafficViewHolder(@NonNull View itemView)  {
            super(itemView);

            tAppName = (TextView) itemView.findViewById(R.id.tappNameId);
            tAppIcon = (ImageView) itemView.findViewById(R.id.tappIconId);
            tDestIp = (TextView) itemView.findViewById(R.id.tdestIpId);
            tDestPort = (TextView) itemView.findViewById(R.id.tdestPortId);
            tProtocol = (TextView) itemView.findViewById(R.id.tprotocol);
            tTimeStamp = (TextView) itemView.findViewById(R.id.ttimeId);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TrafficList item = trafficLists.get(position);

            Intent intent = new Intent(context, IpLookupActivity.class);
            intent.putExtra("IP", item.gettDestIp());
            context.startActivity(intent);

        }
    }
}
