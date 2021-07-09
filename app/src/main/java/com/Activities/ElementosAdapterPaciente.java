package com.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.DB_Objects.Elemento;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ElementosAdapterPaciente extends RecyclerView.Adapter<ElementosAdapterPaciente.ElementoViewHolder>{
    private Context mCtx;
    private List<Elemento> elementoList;

    public ElementosAdapterPaciente(Context mCtx, List<Elemento> elementoList) {
        this.mCtx = mCtx;
        this.elementoList = elementoList;
    }

    @NonNull
    @Override
    public ElementoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ElementoViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_elemento_paciente, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ElementoViewHolder holder, int position) {
        Elemento element = elementoList.get(position);

        holder.textviewNombre.setText(element.getNombre());
        holder.textViewSensorID.setText(element.getLabel());
        holder.textViewDesc.setText(element.getDesc());
        Picasso.get().load(element.getFoto()).into(holder.image);
    }



    @Override
    public int getItemCount() {
        return elementoList.size();
    }

    class ElementoViewHolder extends RecyclerView.ViewHolder {

        TextView textviewNombre, textViewSensorID, textViewDesc;
        ImageView image;

        public ElementoViewHolder(View itemView){
            super(itemView);

            textviewNombre = itemView.findViewById(R.id.textview_nombre2);
            textViewSensorID = itemView.findViewById(R.id.textview_sensorID2);
            textViewDesc = itemView.findViewById(R.id.textview_desc2);
            image = itemView.findViewById(R.id.imageView2);

        }

    }
}
