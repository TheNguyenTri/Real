package android.trithe.real.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.PetActivity;
import android.trithe.real.database.PetDAO;
import android.trithe.real.model.Pet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.MyViewHolder> {

    private final Context context;
    private List<Pet> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView avatar;
        final ImageView overflow;
        final ImageView star;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            avatar = view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);
            star = view.findViewById(R.id.imgStar);
        }
    }


    public PetAdapter(Context mContext, List<Pet> albumList) {
        this.context = mContext;
        this.list = albumList;
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
        Pet pet = list.get(position);
        holder.name.setText(pet.getName());
        if (list.get(position).getHealth().equalsIgnoreCase("Weak")) {
            Glide.with(context).load(R.drawable.saoden).into(holder.star);
        }
        if (list.get(position).getHealth().equalsIgnoreCase("Normal")) {
            Glide.with(context).load(R.drawable.saobac).into(holder.star);
        }
        if (list.get(position).getHealth().equalsIgnoreCase("Strong")) {
            Glide.with(context).load(R.drawable.saovang).into(holder.star);
        }
        // loading album cover using Glide library
        Glide.with(context).load(pet.getImage()).into(holder.avatar);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID", list.get(position).getId());
                bundle.putString("NAME", list.get(position).getName());
                bundle.putString("LOAI", list.get(position).getGiongloai());
                bundle.putString("AGE", String.valueOf(list.get(position).getAge()));
                bundle.putString("WEIGHT", String.valueOf(list.get(position).getWeight()));
                bundle.putString("HEALTH", list.get(position).getHealth());
                bundle.putString("GENDER", list.get(position).getGender());
                bundle.putByteArray("IMAGE", list.get(position).getImage());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
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

        MyMenuItemClickListener(int positon) {
            this.position = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            PetDAO petDAO = new PetDAO(context);
            switch (menuItem.getItemId()) {
                case R.id.type:
//                    Intent intent = new Intent(context, EditTypeActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("ID", list.get(position).getId());
//                    bundle.putString("NAME", list.get(position).getName());
//                    bundle.putByteArray("IMAGE", list.get(position).getImage());
//                    intent.putExtras(bundle);
//                    context.startActivity(intent);
                    return true;
                case R.id.delete:
                    petDAO.deleteTypeByID(list.get(position).getId());
                    list.clear();
                    list = petDAO.getAllPet();
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

    public void changeDataset(List<Pet> items) {
        this.list = items;
        notifyDataSetChanged();
    }
}
