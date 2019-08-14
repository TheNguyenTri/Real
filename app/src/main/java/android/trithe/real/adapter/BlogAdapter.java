package android.trithe.real.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.InfoPostActivity;
import android.trithe.real.model.BlogPost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.MyViewHolder> {

    private List<BlogPost> list;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef;
    private String currentUserId;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView desc;
        final ImageView imageView;
        final TextView time;
        final TextView usertext;
        final CircleImageView imageUser;
        final ImageView blogLike;
        final TextView blogTextLike;
        final TextView blogCountComment;
        final ImageView delete;

        MyViewHolder(View view) {
            super(view);
            desc = view.findViewById(R.id.blog_desc);
            imageView = view.findViewById(R.id.blog_image);
            time = view.findViewById(R.id.blog_date);
            usertext = view.findViewById(R.id.blog_user_name);
            delete = view.findViewById(R.id.deleteicon);
            imageUser = view.findViewById(R.id.imageprofile);
            blogLike = view.findViewById(R.id.blog_like);
            blogTextLike = view.findViewById(R.id.blog_text_like);
            blogCountComment = view.findViewById(R.id.blog_count_comment);
        }
    }

    public BlogAdapter(List<BlogPost> blogPosts, Context context) {
        this.list = blogPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blog_new, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        return new MyViewHolder(itemView);
    }

    @SuppressLint("CheckResult")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setIsRecyclable(false);
        final BlogPost planss = list.get(position);
        final String blogPostId = planss.BlogPostId;
        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        holder.desc.setText(planss.getDesc());
        try {
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String creationDate = dateFormat.format(planss.getTimestamp().getTime());
            holder.time.setText(creationDate);
        } catch (Exception ignored) {
        }
        final String user_id = planss.getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String username = task.getResult().getString("name");
                String userImage = task.getResult().getString("image");
                holder.usertext.setText(username);
                Glide.with(context).load(userImage).into(holder.imageUser);
            }
        });
        Glide.with(context).load(
                planss.getImage_url()).thumbnail(Glide.with(context).load(planss.getImage_thumb()))
                .placeholder(R.drawable.placeholder).into(holder.imageView);
        getTextLike(blogPostId, holder);
        if (firebaseAuth != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener((documentSnapshot, e) -> {
                if (documentSnapshot != null) {
                    try {
                        if (documentSnapshot.exists()) {
                            Glide.with(context).load(R.drawable.love).placeholder(R.drawable.chua_like).into(holder.blogLike);
                        } else {
                            Glide.with(context).load(R.drawable.chua_like).placeholder(R.drawable.chua_like).into(holder.blogLike);
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        }
        holder.blogLike.setOnClickListener(v -> firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                .document(currentUserId).get().addOnCompleteListener(task -> {
                    if (!task.getResult().exists()) {
                        Map<String, Object> likesMap = new HashMap<>();
                        likesMap.put("timestamp", FieldValue.serverTimestamp());
                        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        if (!currentUserId.equals(planss.getUser_id())) {
                            String current_user_ref = "Nofications/" + planss.getUser_id();
                            DatabaseReference user_notifi_push = mRootRef.child("Nofications").child(planss.getUser_id()).push();
                            String push_id = user_notifi_push.getKey();
                            Map<String, Object> notiMap = new HashMap<>();
                            notiMap.put("body", " đã thích ảnh của bạn");
                            notiMap.put("blog_id", blogPostId);
                            notiMap.put("timestamp", ServerValue.TIMESTAMP);
                            notiMap.put("from", currentUserId);
                            Map<String, Object> messageUserMap = new HashMap<>();
                            messageUserMap.put(current_user_ref + "/" + push_id, notiMap);
                            mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                            });
                        }
                    } else {
                        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                        getTextLike(blogPostId, holder);
                    }
                }));

        holder.imageView.setOnClickListener(v -> {
            Intent commentIntent = new Intent(context, InfoPostActivity.class);
            commentIntent.putExtra("user_id", blogPostId);
            context.startActivity(commentIntent);
        });

        holder.blogTextLike.setOnClickListener(v -> {
            Intent likeIntent = new Intent(context, InfoPostActivity.class);
            likeIntent.putExtra("user_id", blogPostId);
            context.startActivity(likeIntent);
        });
        //get count
        if (firebaseAuth != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener((documentSnapshots, e) -> {
                if (firebaseAuth.getCurrentUser() != null) {
                    if (documentSnapshots != null) {
                        int count = documentSnapshots.size();
                        if (count == 0) {
                            holder.blogCountComment.setText(R.string.comment);
                        } else {
                            holder.blogCountComment.setText(count + " Comments");
                        }
                    }
                }
            });
        }
        if (user_id.equals(currentUserId)) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Message");
            builder.setMessage("Do you want delete your post ?");
            builder.setPositiveButton("Yes", (dialog, which) ->
                    firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(aVoid -> {
                        list.remove(position);
                        notifyDataSetChanged();
                    }));
            builder.setNegativeButton("No", (dialog, which) -> {

            });
            builder.show();

        });
    }

    private void getTextLike(String blogPostId, @NonNull final MyViewHolder holder) {
        if (firebaseAuth != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    int count = queryDocumentSnapshots.size();
                    if (count == 0) {
                        holder.blogTextLike.setText(R.string.like);
                    } else if (count == 1) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String likeId = doc.getDocument().getId();
                                firebaseFirestore.collection("Users").document(likeId).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String username = task.getResult().getString("name");
                                        holder.blogTextLike.setText(username);
                                    }
                                });
                            }
                        }
                    } else {
                        holder.blogTextLike.setText(count + " Likes");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
