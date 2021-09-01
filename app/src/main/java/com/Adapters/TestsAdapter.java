package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Activities.JuegoActivity;
import com.Activities.R;
import com.DB_Objects.Test;


import java.util.List;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.TestsViewHolder> {

    private Context mCtx;
    private List<Test> testList;

    public TestsAdapter(Context mCtx, List<Test> testList) {
        this.mCtx = mCtx;
        this.testList = testList;
    }

    @NonNull
    @Override
    public TestsAdapter.TestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestsAdapter.TestsViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_tests, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TestsAdapter.TestsViewHolder holder, int position) {
        Test test = testList.get(position);

        holder.testsBtn.setText(test.getNombreTest());
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    class TestsViewHolder extends RecyclerView.ViewHolder {

        Button testsBtn;

        public TestsViewHolder(View itemView) {
            super(itemView);

            testsBtn = itemView.findViewById(R.id.testsBtn);

            testsBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Test test = testList.get(getAdapterPosition()); //Pasamos los elementos contenidos en el test elegido
                    Intent intent = new Intent(mCtx.getApplicationContext(), JuegoActivity.class);
                    intent.putExtra("Test", test.getElementosSelec());
                    mCtx.startActivity(intent);
                }
            });


        }
    }
}
