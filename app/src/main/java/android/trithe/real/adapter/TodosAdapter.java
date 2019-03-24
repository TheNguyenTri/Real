package android.trithe.real.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.database.PlanssDAO;
import android.trithe.real.model.Configuration;
import android.trithe.real.model.Planss;
import android.trithe.real.model.Todos;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.MyViewHolder> {

    private final Context context;
    private List<Todos> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView des;
        final TextView date;
        public final RelativeLayout viewForeground;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titleTodo);
            des = view.findViewById(R.id.desTodo);
            date = view.findViewById(R.id.dateTodo);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }


    public TodosAdapter(Context mContext, List<Todos> albumList) {
        this.context = mContext;
        this.list = albumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Todos planss = list.get(position);
        holder.title.setText(planss.getTitle());
        holder.des.setText(planss.getDescription());
        holder.date.setText(planss.getTimes());
    }
    public void changeDataset(List<Todos> items) {
        this.list = items;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public void removeItem(int position) {
        notifyItemRemoved(position);
    }

    public void restoreItem(Todos item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

}
