package android.trithe.real.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.HelloActivity;
import android.trithe.real.activity.HomeActivity;
import android.trithe.real.activity.PetActivity;
import android.trithe.real.activity.PlanActivity;
import android.trithe.real.database.PetDAO;
import android.trithe.real.database.PlanssDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Pet;
import android.trithe.real.model.Planss;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlanssAdapter extends RecyclerView.Adapter<PlanssAdapter.MyViewHolder> {

    private final Context context;
    private List<Planss> list;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private final SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
    private OnClick onClick;
    private OnClick1 onClick1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView day;
        final ImageView avatar;
        final CheckBox checkBox;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvNamepet);
            day = view.findViewById(R.id.tvday);
            avatar = view.findViewById(R.id.avatar);
            checkBox = view.findViewById(R.id.cbo);
        }
    }


    public PlanssAdapter(Context mContext, List<Planss> albumList, OnClick onClick, OnClick1 onClick1) {
        this.context = mContext;
        this.list = albumList;
        this.onClick = onClick;
        this.onClick1 = onClick1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planss, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Planss planss = list.get(position);
        holder.name.setText(planss.getIdpet()+" | "+planss.getName());
        holder.day.setText(planss.getTime()+ " | " + sdf.format(planss.getDay()));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClick.onItemClickClicked(position);
                return false;
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    onClick1.onItemClickClicked(position);
                }
            }
        });

        Glide.with(context).load(R.drawable.cander).into(holder.avatar);
        // loading album cover using Glide library
//        holder.avatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, PetActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("ID", list.get(position).getId());
//                bundle.putString("NAME", list.get(position).getName());
//                bundle.putString("LOAI", list.get(position).getGiongloai());
//                bundle.putString("AGE", String.valueOf(list.get(position).getAge()));
//                bundle.putString("WEIGHT", String.valueOf(list.get(position).getWeight()));
//                bundle.putString("HEALTH", list.get(position).getHealth());
//                bundle.putString("GENDER", list.get(position).getGender());
//                bundle.putByteArray("IMAGE", list.get(position).getImage());
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void changeDataset(List<Planss> items) {
        this.list = items;
        notifyDataSetChanged();
    }
}
