package com.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.DB_Objects.Elemento;

import java.util.ArrayList;
import java.util.List;

public class ElementosAdapterCreador extends RecyclerView.Adapter<ElementosAdapterCreador.ElementoViewHolder>{
    private Context mCtx;
    private List<Elemento> elementoList;
    private boolean haySeleccion = false;
    private ArrayList<Elemento> elementosSelec = new ArrayList<>();

    public ElementosAdapterCreador(Context mCtx, List<Elemento> elementoList) {
        this.mCtx = mCtx;
        this.elementoList = elementoList;
    }

    public ArrayList<Elemento> getElementosSelec(){
        return elementosSelec;
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

    class ElementoViewHolder extends RecyclerView.ViewHolder{

        TextView textviewNombre, textViewLabel, textViewDesc;

        public ElementoViewHolder(View itemView){
            super(itemView);

            textviewNombre = itemView.findViewById(R.id.textview_nombre);
            textViewLabel = itemView.findViewById(R.id.textview_label);
            textViewDesc = itemView.findViewById(R.id.textview_desc);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    haySeleccion = true;
                    if(elementosSelec.contains(elementoList.get(getAdapterPosition()))){
                        itemView.setBackgroundColor(Color.TRANSPARENT);
                        elementosSelec.remove(elementoList.get(getAdapterPosition()));
                    } else {
                        itemView.setBackgroundColor(Color.parseColor("#F3FF00"));
                        elementosSelec.add(elementoList.get(getAdapterPosition()));
                    }
                    if(elementosSelec.size() == 0)
                        haySeleccion = false;
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(haySeleccion){
                        if(elementosSelec.contains(elementoList.get(getAdapterPosition()))){
                            itemView.setBackgroundColor(Color.TRANSPARENT);
                            elementosSelec.remove(elementoList.get(getAdapterPosition()));
                        } else {
                            itemView.setBackgroundColor(Color.parseColor("#F3FF00"));
                            elementosSelec.add(elementoList.get(getAdapterPosition()));
                        }
                        if(elementosSelec.size() == 0)
                            haySeleccion = false;

                    } else {
                        Elemento element = elementoList.get(getAdapterPosition());
                        Intent intent = new Intent(mCtx, UpdateElementoActivity.class);
                        intent.putExtra("elemento", element);
                        mCtx.startActivity(intent);
                    }


                }
            });
        }


    }
}
