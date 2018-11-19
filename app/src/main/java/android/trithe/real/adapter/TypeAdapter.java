package android.trithe.real.adapter;

import android.content.Context;
import android.trithe.real.R;
import android.trithe.real.model.TypePet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.List;

class TypeAdapter extends BaseAdapter {

    private final Context context;
    private final List<TypePet> list;
    private final LayoutInflater inflater;

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

    class ViewHolder {
//      TextView name;
      ImageView avatar;

    }
    public TypeAdapter(Context mContext, List<TypePet> albumList) {
        this.context = mContext;
        this.list = albumList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_type, parent, false);
//            holder.name = convertView.findViewById(R.id.tvname);
            holder.avatar = convertView.findViewById(R.id.imgAnh);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        TypePet _entry = list.get(position);
//        holder.name.setText(_entry.getName());
        Glide.with(context).load(_entry.getImage()).into(holder.avatar);
        return convertView;
    }
}
