package com.example.cooperadora_escuela.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cooperadora_escuela.R;

import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {

private List<Activities> actividades;

public ActivitiesAdapter(List<Activities> actividades) {
    this.actividades = actividades;
}

@NonNull
@Override
public ActivitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.activity_item_activities, parent, false);
    return new ViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull ActivitiesAdapter.ViewHolder holder, int position) {
    Activities actividad = actividades.get(position);
    holder.tvTitulo.setText(actividad.getTitulo());
    holder.tvDescripcion.setText(actividad.getDescripcion());
}

@Override
public int getItemCount() {
    return actividades.size();
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitulo, tvDescripcion;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitulo = itemView.findViewById(R.id.tvTitulo);
        tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
    }
}
}
