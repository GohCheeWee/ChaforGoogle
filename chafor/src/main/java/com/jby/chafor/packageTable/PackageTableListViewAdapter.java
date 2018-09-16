package com.jby.chafor.packageTable;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jby.chafor.R;
import com.jby.chafor.exportFeature.category.ExportCategoryListViewObject;

import java.util.ArrayList;

public class PackageTableListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PackageTableObject> packageTableObjectArrayList;

    PackageTableListViewAdapter(Context context, ArrayList<PackageTableObject> packageTableObjectArrayList)
    {
        this.context = context;
        this.packageTableObjectArrayList = packageTableObjectArrayList;
    }

    @Override
    public int getCount() {
        return packageTableObjectArrayList.size();
    }

    @Override
    public PackageTableObject getItem(int i) {
        return packageTableObjectArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(this.context, R.layout.package_table_dialog_list_view_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        PackageTableObject object = getItem(i);
        viewHolder.feature.setText(object.getFeature());
        String trial = object.getTrial();
        String premium = object.getPremium();

        if(trial.equals("1"))
            viewHolder.trial.setImageDrawable(context.getResources().getDrawable(R.drawable.tick_icon));
        else
            viewHolder.trial.setImageDrawable(context.getResources().getDrawable(R.drawable.wrong_icon));


        if(premium.equals("1"))
            viewHolder.premium.setImageDrawable(context.getResources().getDrawable(R.drawable.tick_icon));
        else
            viewHolder.premium.setImageDrawable(context.getResources().getDrawable(R.drawable.wrong_icon));

        return view;
    }

    private static class ViewHolder{
        private TextView feature;
        private ImageView trial, premium;

        ViewHolder (View view){
            feature = view.findViewById(R.id.package_table_dialog_label_list_view_feature);
            trial = view.findViewById(R.id.package_table_dialog_label_list_view_trial);
            premium = view.findViewById(R.id.package_table_dialog_label_list_view_premium);
        }
    }
}
