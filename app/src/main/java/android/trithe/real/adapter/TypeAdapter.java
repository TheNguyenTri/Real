package android.trithe.real.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.database.TypeDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.TypePet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.MyViewHolder> {

    private final Context context;
    private List<TypePet> list;
    private final OnClick onClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView avatar;
        final ImageView overflow;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            avatar = view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);
        }
    }
//
//
    public TypeAdapter(Context mContext, List<TypePet> albumList, OnClick onClick) {
// --Commented out by Inspection STOP (06/11/2018 9:18 SA)
        this.context = mContext;
        this.list = albumList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        TypePet typePet = list.get(position);
        holder.name.setText(typePet.getName());

        // loading album cover using Glide library
        Glide.with(context).load(typePet.getImage()).into(holder.avatar);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_type, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private final int position;

// --Commented out by Inspection START (06/11/2018 9:18 SA):
        MyMenuItemClickListener(int positon) {
            this.position = positon;
        }
// --Commented out by Inspection STOP (06/11/2018 9:18 SA)

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            TypeDAO typeDAO = new TypeDAO(context);
            switch (menuItem.getItemId()) {
                case R.id.type:
//                    Intent intent = new Intent(context, EditTypeActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("ID", list.get(position).getId());
//                    bundle.putString("NAME", list.get(position).getName());
//                    bundle.putByteArray("IMAGE", list.get(position).getImage());
//                    intent.putExtras(bundle);
//                    context.startActivity(intent);
                    onClick.onItemClickClicked(position);
                    return true;
                case R.id.delete:
//                    typeDAO.deleteTypeByID(list.get(position).getId());
                    list.clear();
                    list = typeDAO.getAllType();
                    notifyDataSetChanged();
                    return true;
                default:
            }
            return false;
        }
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

// --Commented out by Inspection START (06/11/2018 9:18 SA):
//    public void changeDataset(List<TypePet> items) {
//        this.list = items;
//        notifyDataSetChanged();
//    }
// --Commented out by Inspection STOP (06/11/2018 9:18 SA)
}
