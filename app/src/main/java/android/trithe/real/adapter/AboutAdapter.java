package android.trithe.real.adapter;

import android.app.Activity;
import android.content.Context;
import android.trithe.real.R;
import android.trithe.real.model.About1;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AboutAdapter extends BaseAdapter {
    private final List<About1> list;
    private final LayoutInflater inflater;

    public AboutAdapter(List<About1> list, Activity context) {
        super();
        this.list = list;
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
        TextView txtname, txtsubname;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_about_1, parent,false);
            holder.txtname=convertView.findViewById(R.id.aboutname1);
            holder.txtsubname=convertView.findViewById(R.id.aboutsubname1);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        About1 _entry = list.get(position);
        holder.txtname.setText(_entry.getName());
        holder.txtsubname.setText(_entry.getSubname());

        return convertView;
    }
}
