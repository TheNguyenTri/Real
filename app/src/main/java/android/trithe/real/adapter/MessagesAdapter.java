package android.trithe.real.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.trithe.real.R;
import android.trithe.real.helper.GetTimeAgo;
import android.trithe.real.model.Messages;
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

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private final Context context;
    private List<Messages> list;
    private FirebaseAuth mAuth;
    private static final int MES_TYPE_LEFT = 0;
    private static final int MES_TYPE_RIGHT = 1;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView message;
        final TextView timesago;
        final CardView card_view;
        final CircleImageView avatar;
        final ImageView message_image;

        MyViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.text_chat);
            avatar = view.findViewById(R.id.image_user);
            timesago = view.findViewById(R.id.text_time);
            message_image = view.findViewById(R.id.message_image);
            card_view = view.findViewById(R.id.card_view);
        }
    }


    public MessagesAdapter(Context mContext, List<Messages> albumList) {
        this.context = mContext;
        this.list = albumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MES_TYPE_RIGHT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Messages messages = list.get(position);
        String from_user = messages.getFrom();
        String message_type = messages.getType();
        if (from_user.equals(current_user_id)) {
            holder.message.setBackgroundResource(R.drawable.message_text_background_mind);
            holder.message.setTextColor(Color.BLACK);
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Glide.with(context).load(task.getResult().getString("image")).into(holder.avatar);
                    }
                }
            });
        } else {
            holder.message.setBackgroundResource(R.drawable.message_text_background);
            holder.message.setTextColor(Color.WHITE);
            firebaseFirestore.collection("Users").document(from_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Glide.with(context).load(task.getResult().getString("image")).into(holder.avatar);
                    }
                }
            });
        }
        long times = messages.getTime();
        long lasttime = Long.parseLong(String.valueOf(times));
        String lastSeentime = GetTimeAgo.getTimeAgo(lasttime, context);
        holder.timesago.setText(lastSeentime);

        if (message_type.equals("text")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(messages.getMessage());
            holder.card_view.setVisibility(View.GONE);
            holder.message_image.setVisibility(View.GONE);
        } else {
            holder.message.setVisibility(View.GONE);
            Glide.with(context).load(messages.getMessage()).into(holder.message_image);
            holder.card_view.setVisibility(View.VISIBLE);
            holder.message_image.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void changeDataset(List<Messages> items) {
        this.list = items;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        Messages messages = list.get(position);
        String from_user = messages.getFrom();
        String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        if (from_user.equals(current_user_id)) {
            return MES_TYPE_RIGHT;
        } else {
            return MES_TYPE_LEFT;
        }
    }
}
