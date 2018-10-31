package android.trithe.real.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.trithe.real.R;
import android.trithe.real.database.DogDAO;
import android.trithe.real.database.UserDAO;
import android.trithe.real.model.Dog;
import android.trithe.real.model.User;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class DogAdapter extends BaseAdapter {
    private List<Dog> list;
    private Context context;
    DogDAO dogDao;
    // --Commented out by Inspection (12/10/2018 9:58 SA):public Activity context;
    private final LayoutInflater inflater;

    public DogAdapter(Activity context, List<Dog> list) {
        super();
        this.list = list;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    static class ViewHolder {
        TextView txtName;
        TextView txtPrice;
        ImageView avatar, imgEdit, imgDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        dogDao = new DogDAO(context);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder.txtName = convertView.findViewById(R.id.nameitem);
            holder.txtPrice = convertView.findViewById(R.id.priceitem);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
//            holder.imgDelete = (ImageView) convertView.findViewById(R.id.delete);
//            holder.imgEdit = convertView.findViewById(R.id.edit);
//            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Message");
//                    builder.setMessage("Do you want delete this item ?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dogDao.deleteDogByID(list.get(position).getDogid());
//                            list.remove(position);
//                            notifyDataSetChanged();
//                        }
//                    });
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    builder.show();
//                }
//            });
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        Dog _entry = list.get(position);
        holder.txtName.setText(_entry.getName());
        holder.txtPrice.setText(_entry.getPrice() + " VND");
        Bitmap bitmap = BitmapFactory.decodeByteArray(_entry.getImage(), 0, _entry.getImage().length);
        holder.avatar.setImageBitmap(bitmap);
//        SharedPreferences pref = context.getSharedPreferences("USERFILE", context.MODE_PRIVATE);
//        String strUserName = pref.getString("username", "");
//        if (strUserName.equals("admin")) {
//            holder.imgDelete.setVisibility(View.VISIBLE);
//        } else {
//            holder.imgDelete.setVisibility(View.GONE);
//            holder.imgEdit.setVisibility(View.GONE);
//        }
        return convertView;
    }

    public void changeDataset(List<Dog> items) {
        this.list = items;
        notifyDataSetChanged();
    }
}
