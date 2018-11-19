package android.trithe.real.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.database.HistoryDAO;
import android.trithe.real.database.PlanssDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.History;
import android.trithe.real.model.Planss;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class PlanssAdapter extends RecyclerView.Adapter<PlanssAdapter.MyViewHolder> {

    private final Context context;
    private List<Planss> list;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private final OnClick onClick;
    private final OnClick1 onClick1;
    private HistoryDAO historyDAO;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView planss;
        final TextView day;
        final ImageView avatar;
        final CheckBox checkBox;
        final RelativeLayout viewBackground;
        public final RelativeLayout viewForeground;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvNamepet);
            planss = view.findViewById(R.id.tvplanpet);
            day = view.findViewById(R.id.tvday);
            avatar = view.findViewById(R.id.avatar);
            checkBox = view.findViewById(R.id.cbo);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
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
        final Planss planss = list.get(position);
        holder.name.setText(planss.getIdpet());
        holder.planss.setText(planss.getName());
        holder.day.setText(planss.getTime() + " | " + sdf.format(planss.getDay()));
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            historyDAO = new HistoryDAO(context);
                            Random random = new Random();
                            String id = String.valueOf(random.nextInt());
                            History histories = new History(id, planss.getName(), planss.getIdpet(), planss.getDay(), planss.getTime());
                            if (historyDAO.insertHistory(histories) > 0) {
//                                Toast.makeText(context, context.getString(R.string.alertsuccessfully), Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, context.getString(R.string.finish_plan), Toast.LENGTH_SHORT).show();
                                onClick1.onItemClickClicked(position);
                            }

                        }
                    }, 1500);
                }
            }
        });
        holder.checkBox.setChecked(false);
        switch (planss.getName()) {
            case "Đi bộ":
                Glide.with(context).load(R.drawable.dibo).into(holder.avatar);
                break;
            case "Cho ăn":
                Glide.with(context).load(R.drawable.doan).into(holder.avatar);
                break;
            case "Tắm rửa":
                Glide.with(context).load(R.drawable.tam).into(holder.avatar);
                break;
            case "Đi khám thú y":
                Glide.with(context).load(R.drawable.kham).into(holder.avatar);
                break;
            case "Mua thức ăn, phụ kiện":
                Glide.with(context).load(R.drawable.shop).into(holder.avatar);
                break;
            case "Khác":
                Glide.with(context).load(R.drawable.cander).into(holder.avatar);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void changeDataset(List<Planss> items) {
        this.list = items;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        onClick1.onItemClickClicked(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Planss item, int position) {
        PlanssDAO planssDAO = new PlanssDAO(context);
        list.add(position, item);
        // notify item added by position
        planssDAO.insertPlanss(item);
        notifyItemInserted(position);
    }
}
