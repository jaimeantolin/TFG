package com.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.DB_Objects.User;

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

        holder.textViewfullName.setText(user.getfullName());
        holder.textuserEmail.setText(user.getuserEmail());
        holder.textisValidated.setText(user.getIsValidated());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewfullName, textuserEmail, textisValidated;

        public UserViewHolder(View itemView) {
            super(itemView);

            textViewfullName = itemView.findViewById(R.id.textview_fullName);
            textuserEmail = itemView.findViewById(R.id.textview_email);
            textisValidated = itemView.findViewById(R.id.textview_isValidated);

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