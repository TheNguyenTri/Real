package android.trithe.real.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.CommentsActivity;
import android.trithe.real.LikeActivity;
import android.trithe.real.R;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.BlogPost;
import android.trithe.real.model.InfoUserPost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoPostUserAdapter extends RecyclerView.Adapter<InfoPostUserAdapter.MyViewHolder> {

    private List<InfoUserPost> list;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView desc;
        final ImageView imageView;
        final TextView time;
        final TextView usertext;
        final CircleImageView imageUser;
        final ImageView blogLike;
        final ImageView blogComment;
        final TextView blogTextLike;
        final TextView blogTextComment;
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
            blogLike = (ImageView) view.findViewById(R.id.blog_like);
            blogComment = view.findViewById(R.id.blog_comment);
            blogTextLike = (TextView) view.findViewById(R.id.blog_text_like);
            blogTextComment = (TextView) view.findViewById(R.id.blog_text_comment);
            blogCountComment = (TextView) view.findViewById(R.id.blog_count_comment);
        }
    }


    public InfoPostUserAdapter(List<InfoUserPost> blogPosts, Context context) {
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
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final InfoUserPost planss = list.get(position);
        final String blogPostId = planss.InfoUserPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        holder.desc.setText(planss.getDesc());
        try {
            String dateString = android.text.format.DateFormat.format("HH:ss dd/MM/yyyy", new Date(planss.getTimestamp().getTime())).toString();

            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String creationDate = dateFormat.format(planss.getTimestamp().getTime());
            holder.time.setText(creationDate);
        } catch (Exception e) {
        }
        final String user_id = planss.getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String username = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.usertext.setText(username);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.default_image);
                    Glide.with(context).applyDefaultRequestOptions(requestOptions).load(userImage).into(holder.imageUser);
                }
            }
        });

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_image);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(planss.getImage_url()).thumbnail(Glide.with(context).load(planss.getImage_thumb())).into(holder.imageView);
//textLike
        if (firebaseAuth != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null) {
                        int count = queryDocumentSnapshots.size();
                        if (count != 1) {
                            holder.blogTextLike.setText(count + " Likes");
                        } else if (count == 1) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String likeId = doc.getDocument().getId();
                                    firebaseFirestore.collection("Users").document(likeId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String username = task.getResult().getString("name");
                                                holder.blogTextLike.setText(username);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });

        }
        //get like
        if (firebaseAuth != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot != null) {
                        try {
                            if (documentSnapshot.exists()) {
//               holder.blogLike.setImageDrawable(context.getDrawable(R.drawable.love));
                                Glide.with(context).load(R.drawable.love).into(holder.blogLike);
                            } else {
                                Glide.with(context).load(R.drawable.chua_like).into(holder.blogLike);
                            }
                        } catch (Exception a) {
                        }
                    }
                }
            });
        }

        holder.blogLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        } else {
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                    if (queryDocumentSnapshots != null) {
                                        int count = queryDocumentSnapshots.size();
                                        if (count == 0) {
                                            holder.blogTextLike.setText("Like");
                                        } else if (count != 1) {
                                            holder.blogTextLike.setText(count + " Likes");
                                        } else if (count == 1) {
                                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                                    String likeId = doc.getDocument().getId();
                                                    firebaseFirestore.collection("Users").document(likeId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                String username = task.getResult().getString("name");
                                                                holder.blogTextLike.setText(username);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });
//        holder.blogComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent commentIntent = new Intent(context, CommentsActivity.class);
//                commentIntent.putExtra("blog_post_id", blogPostId);
//                context.startActivity(commentIntent);
//            }
//        });
//        holder.blogTextComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent commentIntent = new Intent(context, CommentsActivity.class);
//                commentIntent.putExtra("blog_post_id", blogPostId);
//                context.startActivity(commentIntent);
//            }
//        });
        holder.blogCountComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);
            }
        });
        holder.blogTextLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, LikeActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);
            }
        });
        //get count
        if (firebaseAuth != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        if (documentSnapshots != null) {
                            int count = documentSnapshots.size();
                            if (count == 0) {
                                holder.blogCountComment.setText("Comment");
                            } else {
                                holder.blogCountComment.setText(count + " Comments");
                            }
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
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Message");
                builder.setMessage("Do you want delete your post ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                list.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    /**
     * Click listener for popup menu items
     */
}
