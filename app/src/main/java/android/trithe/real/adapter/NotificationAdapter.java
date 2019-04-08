package android.trithe.real.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.InfoPostActivity;
import android.trithe.real.helper.GetTimeAgo;
import android.trithe.real.model.NotificationsModel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private final Context context;
    private List<NotificationsModel> list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final CircleImageView avatar;
        final TextView itemNameUser;
        final TextView txtBody;
        final TextView itemTimeUser;
        final ImageView imgPost;


        MyViewHolder(View view) {
            super(view);
            avatar = (CircleImageView) view.findViewById(R.id.avatar);
            itemNameUser = (TextView) view.findViewById(R.id.itemNameUser);
            txtBody = (TextView) view.findViewById(R.id.txtBody);
            itemTimeUser = (TextView) view.findViewById(R.id.itemTimeUser);
            imgPost = (ImageView) view.findViewById(R.id.imgPost);
        }
    }


    public NotificationAdapter(Context mContext, List<NotificationsModel> listss) {
        this.list = listss;
        this.context = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noti, parent, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final NotificationsModel planss = list.get(position);
        firebaseFirestore.collection("Users").document(planss.getFrom()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final String userName = task.getResult().getString("name");
                    final String userImage = task.getResult().getString("image");
                    holder.itemNameUser.setText(userName);
                    Glide.with(context).load(userImage).into(holder.avatar);
                }
            }
        });

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lasttime = planss.getTimestamp();
        holder.itemTimeUser.setText(getTimeAgo.getTimeAgo(lasttime, context));
        holder.txtBody.setText(planss.getBody());

        firebaseFirestore.collection("Posts").document(planss.getBlog_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final String PostImage = task.getResult().getString("image_url");
                    Glide.with(context).load(PostImage).into(holder.imgPost);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, InfoPostActivity.class);
                commentIntent.putExtra("user_id", planss.getBlog_id());
                context.startActivity(commentIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
