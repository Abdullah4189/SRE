package com.example.sre.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sre.R;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<RequestModel> requestList;

    public RequestAdapter(List<RequestModel> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModel request = requestList.get(position);
        holder.tvSenderEmail.setText(request.getStatus());
        holder.title.setText(request.getSenderEmail());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderEmail, title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderEmail = itemView.findViewById(R.id.tvSenderEmail);
            title = itemView.findViewById(R.id.AdTitle);
        }
    }
}

