package android.trithe.real.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.trithe.real.R;
import android.trithe.real.database.UserDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.User;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private List<User> list;
    private Context context;
    UserDAO userDAO;
    private OnClick onClick;
    // --Commented out by Inspection (12/10/2018 9:58 SA):public Activity context;
    private final LayoutInflater inflater;

    public UserAdapter(Activity context, List<User> list, OnClick onClick) {
        super();
        this.list = list;
        this.context = context;
        this.onClick = onClick;
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
        TextView txtName, txtAge;
        TextView txtPhone;
        ImageView imgDelete, avatar;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        userDAO = new UserDAO(context);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            holder.txtName = convertView.findViewById(R.id.nameitem);
            holder.txtPhone = convertView.findViewById(R.id.phoneitem);
            holder.txtAge = convertView.findViewById(R.id.ageitem);
            holder.avatar = convertView.findViewById(R.id.img);
            holder.imgDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClickClicked(position);
                }
            });
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        User _entry = list.get(position);
        holder.txtName.setText(_entry.getName());
        holder.txtPhone.setText(_entry.getPhone());
        holder.txtAge.setText(String.valueOf(_entry.getAge()));
        Glide.with(context).load(_entry.getImage()).into(holder.avatar);
        return convertView;
    }

    public void changeDataset(List<User> items) {
        this.list = items;
        notifyDataSetChanged();
    }

}
