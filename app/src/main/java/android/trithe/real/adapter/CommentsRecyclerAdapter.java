package android.trithe.real.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.model.BlogPost;
import android.trithe.real.model.Comments;
import android.trithe.real.model.Planss;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CommentsRecyclerAdapter(List<Comments> commentsList) {

        this.commentsList = commentsList;

    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);
//        Toast.makeText(context,String.valueOf(commentsList.get(position).getTimestamp()),Toast.LENGTH_SHORT).show();
        try {
            String dateString = android.text.format.DateFormat.format("HH:ss dd/MM/yyyy", new Date(String.valueOf(commentsList.get(position).getTimestamp()))).toString();
            holder.setTime_message(dateString);

        } catch (Exception e) {
        }
        String user_id = commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setUserData(userName, userImage);


                } else {

                    //Firebase Exception

                }

            }
        });
    }

    @Override
    public int getItemCount() {

        if (commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;
        private TextView commentUserName;
        private TextView commentTime;
        private CircleImageView commentImageUser;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message) {

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }

        public void setTime_message(String time) {

            commentTime = mView.findViewById(R.id.text_time);
            commentTime.setText(time);

        }

        public void setUserData(String name, String image) {

            commentImageUser = mView.findViewById(R.id.comment_image);
            commentUserName = mView.findViewById(R.id.comment_username);

            commentUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_avata);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentImageUser);

        }
    }
    public void changeDataset(List<Comments> items) {
        this.commentsList = items;
        notifyDataSetChanged();
    }


}
