package com.jby.chaforgoogle.exportFeature.file;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jby.chaforgoogle.R;
import com.jby.chaforgoogle.database.CustomSqliteHelper;
import com.jby.chaforgoogle.login.LoginActivity;

import java.util.ArrayList;

public class ExportFileListViewAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<ExportFileListViewObject> exportFileListViewObjectArrayList;
    private CategoryAdapterCallBack categoryAdapterCallBack;
    private SparseBooleanArray mSelectedItemsIds;

    ExportFileListViewAdapter(Activity context, ArrayList<ExportFileListViewObject> exportFileListViewObjectArrayList, CategoryAdapterCallBack categoryAdapterCallBack)
    {
        this.context = context;
        this.exportFileListViewObjectArrayList = exportFileListViewObjectArrayList;
        this.categoryAdapterCallBack = categoryAdapterCallBack;
        mSelectedItemsIds = new  SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return exportFileListViewObjectArrayList.size();
    }

    @Override
    public ExportFileListViewObject getItem(int i) {
        return exportFileListViewObjectArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(this.context, R.layout.activity_file_list_view_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ExportFileListViewObject object = getItem(i);
        String quantity = object.getCategory_numb() + " Items";
        viewHolder.categoryName.setText(object.getFile());
        viewHolder.subCategory_numb.setText(quantity);
        viewHolder.categoryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryAdapterCallBack.openUpdateDialog(exportFileListViewObjectArrayList.get(i).getId(),
                        exportFileListViewObjectArrayList.get(i).getFile(), i,
                        exportFileListViewObjectArrayList.get(i).getCategory_numb());
            }
        });

            if(mSelectedItemsIds.get(i)){
                viewHolder.categorInnerLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.list_view_background));
            }
            else{
                viewHolder.categorInnerLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.default_background));
            }

            viewHolder.shareFileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String fileID = exportFileListViewObjectArrayList.get(i).getId();
                    categoryAdapterCallBack.readPhonePermission(fileID, view);
                }
            });

        return view;
    }

    public void remove(int  position) {
        exportFileListViewObjectArrayList.remove(position);
        notifyDataSetChanged();
    }

//    // get List after update or delete
//    public List<String> getMyList() {
//        return DataList;
//    }

    public void  toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    // Remove selection after unchecked
    public void  removeSelection() {
        mSelectedItemsIds = new  SparseBooleanArray();
        notifyDataSetChanged();
    }

    // Item checked on selection
    private void selectView(int position, boolean value) {
        if (value){
            mSelectedItemsIds.put(position,  true);
        }
        else{
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    // Get number of selected item
    public int  getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public interface CategoryAdapterCallBack {
        void openUpdateDialog(String category_id, String category_name, int position, String subCategory_num);
        void readPhonePermission(String fileID, View view);
    }


    private static class ViewHolder{
        private TextView categoryName, subCategory_numb, categoryEdit;
        private LinearLayout categorInnerLayout;
        private ImageView shareFileIcon;

        ViewHolder (View view){
            categoryName = (TextView)view.findViewById(R.id.fragment_category_list_view_category);
            subCategory_numb = (TextView)view.findViewById(R.id.fragment_category_list_view_quantity);
            categoryEdit = (TextView)view.findViewById(R.id.fragment_category_list_view_edit);
            categorInnerLayout = (LinearLayout)view.findViewById(R.id.fragment_category_list_view_inner_layout);
            shareFileIcon = view.findViewById(R.id.activity_file_list_item_share_icon);

        }
    }
}
