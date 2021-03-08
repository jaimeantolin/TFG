package com.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Adapters.User;

import java.io.Serializable;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private Context mCtx;
    private List<User> userList;

    public UsersAdapter(Context mCtx, List<User> userList) {
        this.mCtx = mCtx;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_product, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.textViewFullName.setText(user.getFullName());
        holder.textUserEmail.setText(user.getUserEmail());
        holder.textIsValidated.setText(user.getIsValidated());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewFullName, textUserEmail, textIsValidated;

        public UserViewHolder(View itemView) {
            super(itemView);

            textViewFullName = itemView.findViewById(R.id.textview_fullName);
            textUserEmail = itemView.findViewById(R.id.textview_email);
            textIsValidated = itemView.findViewById(R.id.textview_isValidated);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            User user = userList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, UpdateUserActivity.class);
            intent.putExtra("user", user);
            mCtx.startActivity(intent);
        }
    }
}