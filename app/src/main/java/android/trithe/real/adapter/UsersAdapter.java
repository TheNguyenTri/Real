package android.trithe.real.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.InfoUserActivity;
import android.trithe.real.activity.SendActivity;
import android.trithe.real.model.Users;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private final Context context;
    private List<Users> list;
    private FirebaseAuth mAuth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        //        final TextView status;
        final ImageView avatar;
        final CircleImageView overflow;


        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            avatar = view.findViewById(R.id.thumbnail);
//            status = view.findViewById(R.id.weight);
            overflow = (CircleImageView) view.findViewById(R.id.overflow);
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
                .inflate(R.layout.itemnew, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        mAuth = FirebaseAuth.getInstance();
        final Users planss = list.get(position);
        holder.name.setText(planss.getName());
        Glide.with(context).load(planss.getImage()).into(holder.avatar);
        Glide.with(context).load(planss.getImage()).into(holder.overflow);
        final String user_id = planss.userId;
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, InfoUserActivity.class);
                    intent.putExtra("user_id", user_id);
                    context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
