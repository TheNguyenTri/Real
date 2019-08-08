package android.trithe.real.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.InfoUserActivity;
import android.trithe.real.model.Users;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private final Context context;
    private List<Users> list;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView status;
        final ImageView avatar;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            avatar = view.findViewById(R.id.thumbnail);
            status = view.findViewById(R.id.weight);
        }
    }

    public UsersAdapter(Context mContext, List<Users> albumList) {
        this.context = mContext;
        this.list = albumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Users planss = list.get(position);
        holder.name.setText(planss.getName());
        holder.status.setText(planss.getStatus());
        Glide.with(context).load(planss.getImage()).into(holder.avatar);
        final String user_id = planss.userId;
        holder.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoUserActivity.class);
            intent.putExtra("user_id", user_id);
            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoUserActivity.class);
            intent.putExtra("user_id", user_id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
