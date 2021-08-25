package com.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.DB_Objects.Elemento;

import java.util.List;

public class ElementosAdapterCreador extends RecyclerView.Adapter<ElementosAdapterCreador.ElementoViewHolder>{
    private Context mCtx;
    private List<Elemento> elementoList;

    public ElementosAdapterCreador(Context mCtx, List<Elemento> elementoList) {
        this.mCtx = mCtx;
        this.elementoList = elementoList;
    }

    @NonNull
    @Override
    public ElementoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ElementoViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_elemento_creador, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ElementoViewHolder holder, int position) {
        Elemento element = elementoList.get(position);

        holder.textviewNombre.setText(element.getNombre());
        holder.textViewLabel.setText(element.getLabel());
        holder.textViewDesc.setText(element.getDesc());

    }

    @Override
    public int getItemCount() {
        return elementoList.size();
    }

    class ElementoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textviewNombre, textViewLabel, textViewDesc;

        public ElementoViewHolder(View itemView){
            super(itemView);

            textviewNombre = itemView.findViewById(R.id.textview_nombre);
            textViewLabel = itemView.findViewById(R.id.textview_label);
            textViewDesc = itemView.findViewById(R.id.textview_desc);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Elemento element = elementoList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, UpdateElementoActivity.class);
            intent.putExtra("elemento", element);
            mCtx.startActivity(intent);
        }
    }
}
