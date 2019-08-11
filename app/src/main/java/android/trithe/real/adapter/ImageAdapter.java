package android.trithe.real.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.PostImageActivity;
import android.trithe.real.model.BlogPost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private List<BlogPost> list;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final CircleImageView imageUser;
        final TextView name;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.thumbnail);
            imageUser = view.findViewById(R.id.overflow);
            name = view.findViewById(R.id.title);
        }
    }


    public ImageAdapter(List<BlogPost> blogPosts, Context context) {
        this.list = blogPosts;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemnew, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final BlogPost planss = list.get(position);
        final String user_id = planss.getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String username = task.getResult().getString("name");
                String userImage = task.getResult().getString("image");
                holder.name.setText(username);
                Glide.with(context).load(userImage).into(holder.imageUser);
                Glide.with(context).load(planss.getImage_url()).thumbnail(
                        Glide.with(context).load(planss.getImage_thumb())).into(holder.imageView);
            }
        });
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        try {
            final String creationDate = dateFormat.format(planss.getTimestamp().getTime());
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostImageActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });
        } catch (Exception ignored) {
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
