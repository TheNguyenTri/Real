package android.trithe.real.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.model.Likes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    private List<Likes> likesList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    public LikeAdapter(List<Likes> likesList) {

        this.likesList = likesList;

    }

    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_likes, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new LikeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LikeAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        try {
            String dateString = android.text.format.DateFormat.format("HH:ss dd/MM/yyyy", new Date(String.valueOf(likesList.get(position).getTimestamp()))).toString();
            holder.setTime_message(dateString);

        } catch (Exception ignored) {
        }
        String user_id = likesList.get(position).likeUserId;
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setUserData(userName, userImage);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (likesList != null) {
            return likesList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView commentUserName;
        private TextView commentTime;
        private CircleImageView commentImageUser;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        void setTime_message(String time) {

            commentTime = mView.findViewById(R.id.text_time);
            commentTime.setText(time);

        }

        @SuppressLint("CheckResult")
        void setUserData(String name, String image) {

            commentImageUser = mView.findViewById(R.id.comment_image);
            commentUserName = mView.findViewById(R.id.comment_username);
            commentUserName.setText(name);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_avata);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentImageUser);

        }
    }


}
