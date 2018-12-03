package android.trithe.real.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.PetsActivity;
import android.trithe.real.database.PetDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Pet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.MyViewHolder> implements Filterable {

    private final Context context;
    private List<Pet> list;
    private final List<Pet> listSort;
    private Filter Filter;
    private final OnClick onClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView avatar;
        final ImageView overflow;
        final TextView weight;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            avatar = view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);
            weight = view.findViewById(R.id.weight);
        }
    }


    public PetAdapter(Context mContext, List<Pet> albumList, OnClick onClick) {
        this.context = mContext;
        this.list = albumList;
        this.listSort = albumList;
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
        Pet pet = list.get(position);
        holder.name.setText(pet.getName());
        holder.weight.setText(pet.getWeight() + " kilogram");
        // loading album cover using Glide library
        Glide.with(context).load(pet.getImage()).into(holder.avatar);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PetsActivity.class);
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
                onClick.onItemClickClicked(position);
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
                    Intent intent = new Intent(context, PetsActivity.class);
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
                    onClick.onItemClickClicked(position);
                    context.startActivity(intent);
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


    @Override
    public Filter getFilter() {
        if (Filter == null)
            Filter = new CustomFilter();
        return Filter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                results.values = listSort;
                results.count = listSort.size();
            } else {
                List<Pet> users = new ArrayList<>();
                for (Pet p : list) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        users.add(p);
                }
                results.values = users;
                results.count = users.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
//                notifyDataSetInvalidated();
            } else {
                if (list.get(0) != null) {
                    list = (List<Pet>) results.values;
                    notifyDataSetChanged();
                }
            }
        }

    }
}
//@Override
//public Filter getFilter() {
//    return new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence charSequence) {
//            String charString = charSequence.toString();
//            if (charString.isEmpty()) {
//                listSort = list;
//            } else {
//                List<Pet> filteredList = new ArrayList<>();
//                for (Pet row : list) {
//
//                    // name match condition. this might differ depending on your requirement
//                    // here we are looking for name or phone number match
//                    if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
//                        filteredList.add(row);
//                    }
//                }
//
//                listSort = filteredList;
//            }
//
//            FilterResults filterResults = new FilterResults();
//            filterResults.values = listSort;
//            return filterResults;
//        }

//        @Override
////        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
////            listSort = (ArrayList<Pet>) filterResults.values;
////            notifyDataSetChanged();
////        }
////    };
//}
//}

