package android.trithe.real.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.SendActivity;
import android.trithe.real.helper.GetTimeAgo;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Conv;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private OnClick onClick;
    private OnClick1 onClick1;
    private final Context context;
    private List<Conv> list;
    private DatabaseReference mCovDatabase;
    private DatabaseReference mMessageDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private FirebaseFirestore firebaseFirestore;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView status;
        final ImageView avatar;
        final ImageView online;
        final TextView time;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemNameUser);
            avatar = view.findViewById(R.id.avatar);
            status = view.findViewById(R.id.itemStatusUser);
            online = view.findViewById(R.id.imgOnline);
            time = view.findViewById(R.id.itemTimeUser);
        }
    }


    public ChatAdapter(Context mContext, List<Conv> albumList, OnClick onClick, OnClick1 onClick1) {
        this.context = mContext;
        this.list = albumList;
        this.onClick = onClick;
        this.onClick1 = onClick1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrent_user_id);
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrent_user_id);
        mMessageDatabase.keepSynced(true);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        final Conv planss = list.get(position);
        firebaseFirestore.collection("Users").document(planss.userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final String userName = task.getResult().getString("name");
                    final String userImage = task.getResult().getString("image");
                    boolean online = task.getResult().getBoolean("online");
                    if (online) {
                        viewHolder.online.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.online.setVisibility(View.INVISIBLE);
                    }
                    viewHolder.name.setText(userName);
                    Glide.with(context).load(userImage).into(viewHolder.avatar);

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(context, SendActivity.class);
                            chatIntent.putExtra("user_id", planss.userId);
                            chatIntent.putExtra("user_name", userName);
                            chatIntent.putExtra("user_image", userImage);
                            context.startActivity(chatIntent);
                            onClick.onItemClickClicked(position);
                            mCovDatabase.child(planss.userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        mCovDatabase.child(planss.userId).child("seen").setValue(true);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    });
                }

            }
        });

        Query converstationQuery = mCovDatabase.orderByChild("timestamp");
        converstationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                String id = dataSnapshot.getKey();
                Query lastMessageQuery = mMessageDatabase.child(planss.userId).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                        final String user = dataSnapshot.child("from").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();
//                        final GetTimeAgo getTimeAgo = new GetTimeAgo();
//                        final long lasttime = Long.parseLong(time);

                        long times = Long.parseLong(dataSnapshot.child("time").getValue().toString());
                        final GetTimeAgo getTimeAgo = new GetTimeAgo();
                        final long lasttime = Long.parseLong(String.valueOf(times));
                        final String lastSeentime = getTimeAgo.getTimeAgo(lasttime, context);
                        firebaseFirestore.collection("Users").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    //settim
                                    //
                                    final String userName = task.getResult().getString("name");
                                    if (dataSnapshot.child("type").getValue().toString().equals("text")) {
                                        if (mCurrent_user_id.equals(user)) {
                                            viewHolder.status.setText("Bạn : " + dataSnapshot.child("message").getValue().toString());
                                            viewHolder.time.setText(" - " + lastSeentime);
                                        } else {
                                            viewHolder.status.setText(dataSnapshot.child("message").getValue().toString());
                                            viewHolder.time.setText(" - " + lastSeentime);
                                            if (planss.isSeen() == false) {
                                                viewHolder.status.setTypeface(viewHolder.status.getTypeface(), Typeface.BOLD);
                                            } else {
                                                viewHolder.status.setTypeface(viewHolder.status.getTypeface(), Typeface.NORMAL);
                                            }
                                        }
                                    } else {
                                        if (mCurrent_user_id.equals(user)) {
                                            viewHolder.status.setText("Bạn đã gửi 1 ảnh ");
                                            viewHolder.time.setText(" - " + lastSeentime);
                                        } else {
                                            viewHolder.status.setText(userName + " đã gửi 1 ảnh ");
                                            viewHolder.time.setText(" - " + lastSeentime);
                                            if (planss.isSeen() == false) {
                                                viewHolder.status.setTypeface(viewHolder.status.getTypeface(), Typeface.BOLD);
                                            } else {
                                                viewHolder.status.setTypeface(viewHolder.status.getTypeface(), Typeface.NORMAL);
                                            }
                                        }
                                    }
                                }

                            }
                        });

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Message");
                builder.setMessage("Do you want to delete this conversation ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMessageDatabase.child(planss.userId).setValue(null);
                        mCovDatabase.child(planss.userId).setValue(null);
                        onClick1.onItemClickClicked(position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
