package com.jby.chaforgoogle.exportFeature.exportDialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.chaforgoogle.R;
import com.jby.chaforgoogle.database.CustomSqliteHelper;
import com.jby.chaforgoogle.exportFeature.file.ExportFileUpdateDialog;

import java.util.Objects;


public class ExportSettingDialog extends DialogFragment implements View.OnClickListener{
    View rootView;
    private CheckBox exportSettingDialogCategoryCheckBox, exportSettingDialogBarcodeCheckBox, exportSettingDialogQuantityCheckBox;
    private CheckBox exportSettingDialogDateCheckBox, exportSettingDialogTimeCheckBox;
    private TextView exportSettingDialogCategoryCount,exportSettingDialogBarcodeCount, exportSettingDialogQuantityCount;
    private TextView exportSettingDialogDateCount, exportSettingDialogTimeCount;
    private LinearLayout exportSettingDialogCategoryLayout, exportSettingDialogBarcodeLayout, exportSettingDialogQuantityLayout;
    private LinearLayout exportSettingDialogDateLayout, exportSettingDialogTimeLayout;
    private TextView exportSettingDialogCancelButton, exportSettingDialogConfirmButton;

    private String fileID;
    private String exportQuery = "";

    public ExportSettingDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.export_setting_dialog, container);
        objectInitialize();
        objectSetting();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(d.getWindow()).setLayout(width, height);
            Objects.requireNonNull(d.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;//animation purpose
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void objectInitialize() {
        exportSettingDialogCategoryCheckBox = rootView.findViewById(R.id.export_setting_dialog_category_checkbox);
        exportSettingDialogBarcodeCheckBox = rootView.findViewById(R.id.export_setting_dialog_barcode_checkbox);
        exportSettingDialogQuantityCheckBox = rootView.findViewById(R.id.export_setting_dialog_quantity_checkbox);
        exportSettingDialogDateCheckBox = rootView.findViewById(R.id.export_setting_dialog_date_checkbox);
        exportSettingDialogTimeCheckBox = rootView.findViewById(R.id.export_setting_dialog_time_checkbox);

        exportSettingDialogCategoryCount = rootView.findViewById(R.id.export_setting_dialog_category_count);
        exportSettingDialogBarcodeCount = rootView.findViewById(R.id.export_setting_dialog_barcode_count);
        exportSettingDialogQuantityCount = rootView.findViewById(R.id.export_setting_dialog_quantity_count);
        exportSettingDialogDateCount = rootView.findViewById(R.id.export_setting_dialog_date_count);
        exportSettingDialogTimeCount = rootView.findViewById(R.id.export_setting_dialog_time_count);

        exportSettingDialogCategoryLayout = rootView.findViewById(R.id.export_setting_dialog_category_layout);
        exportSettingDialogBarcodeLayout = rootView.findViewById(R.id.export_setting_dialog_barcode_layout);
        exportSettingDialogQuantityLayout = rootView.findViewById(R.id.export_setting_dialog_quantity_layout);
        exportSettingDialogDateLayout = rootView.findViewById(R.id.export_setting_dialog_date_layout);
        exportSettingDialogTimeLayout = rootView.findViewById(R.id.export_setting_dialog_time_layout);

        exportSettingDialogCancelButton = rootView.findViewById(R.id.export_setting_dialog_cancel_button);
        exportSettingDialogConfirmButton = rootView.findViewById(R.id.export_setting_dialog_export_button);
    }

    public void objectSetting(){
        Bundle mArgs = getArguments();
        if (mArgs != null) {
            fileID = mArgs.getString("file_id");
        };
        exportSettingDialogCategoryLayout.setOnClickListener(this);
        exportSettingDialogBarcodeLayout.setOnClickListener(this);
        exportSettingDialogQuantityLayout.setOnClickListener(this);
        exportSettingDialogDateLayout.setOnClickListener(this);
        exportSettingDialogTimeLayout.setOnClickListener(this);

        exportSettingDialogCancelButton.setOnClickListener(this);
        exportSettingDialogConfirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.export_setting_dialog_category_layout:
                //checkbox
                if(exportSettingDialogCategoryCheckBox.isChecked()){
                    String sqlLength = String.valueOf(splitExportQuery().length);
                    String count = exportSettingDialogCategoryCount.getText().toString().trim();

                    if(sqlLength.equals(count)){
                        exportSettingDialogCategoryCheckBox.setChecked(false);
                        exportSettingDialogCategoryCount.setText("");
                        removeSQL();
                    }
                }
                else{
                    exportSettingDialogCategoryCheckBox.setChecked(true);
                    //sql
                    addSQlQuery("tb_export_category.category_name");
                    // count
                    exportSettingDialogCategoryCount.setText(String.valueOf(splitExportQuery().length));
                }
                break;
            case R.id.export_setting_dialog_barcode_layout:
                //checkbox
                if(exportSettingDialogBarcodeCheckBox.isChecked()){
                    String sqlLength = String.valueOf(splitExportQuery().length);
                    String count = exportSettingDialogBarcodeCount.getText().toString().trim();

                    if(sqlLength.equals(count)){
                        exportSettingDialogBarcodeCheckBox.setChecked(false);
                        exportSettingDialogBarcodeCount.setText("");
                        removeSQL();
                    }
                }
                else{
                    exportSettingDialogBarcodeCheckBox.setChecked(true);
                    //sql
                    addSQlQuery("tb_export_sub_category.barcode");
                    // count
                    exportSettingDialogBarcodeCount.setText(String.valueOf(splitExportQuery().length));
                }
                break;
            case R.id.export_setting_dialog_quantity_layout:
                if(exportSettingDialogQuantityCheckBox.isChecked()){
                    String sqlLength = String.valueOf(splitExportQuery().length);
                    String count = exportSettingDialogQuantityCount.getText().toString().trim();

                    if(sqlLength.equals(count)){
                        exportSettingDialogQuantityCheckBox.setChecked(false);
                        exportSettingDialogQuantityCount.setText("");
                        removeSQL();
                    }
                }
                else{
                    exportSettingDialogQuantityCheckBox.setChecked(true);
                    //sql
                    addSQlQuery("tb_export_sub_category.quantity");
                    // count
                    exportSettingDialogQuantityCount.setText(String.valueOf(splitExportQuery().length));
                }

                break;
            case R.id.export_setting_dialog_date_layout:
                //checkbox
                if(exportSettingDialogDateCheckBox.isChecked()){
                    String sqlLength = String.valueOf(splitExportQuery().length);
                    String count = exportSettingDialogDateCount.getText().toString().trim();

                    if(sqlLength.equals(count)){
                        exportSettingDialogDateCheckBox.setChecked(false);
                        exportSettingDialogDateCount.setText("");
                        removeSQL();
                    }

                }
                else{
                    exportSettingDialogDateCheckBox.setChecked(true);
                    //sql
                    addSQlQuery("tb_export_sub_category.date_create");
                    // count
                    exportSettingDialogDateCount.setText(String.valueOf(splitExportQuery().length));
                }
                break;
            case R.id.export_setting_dialog_time_layout:
                //checkbox
                if(exportSettingDialogTimeCheckBox.isChecked()){
                    String sqlLength = String.valueOf(splitExportQuery().length);
                    String count = exportSettingDialogTimeCount.getText().toString().trim();

                    if(sqlLength.equals(count)){
                        exportSettingDialogTimeCheckBox.setChecked(false);
                        exportSettingDialogTimeCount.setText("");
                        removeSQL();
                    }
                }
                else{
                    exportSettingDialogTimeCheckBox.setChecked(true);
                    //sql
                    addSQlQuery("tb_export_sub_category.time_create");
                    // count
                    exportSettingDialogTimeCount.setText(String.valueOf(splitExportQuery().length));
                }
                break;
            case R.id.export_setting_dialog_cancel_button:
                dismiss();
                break;
            case R.id.export_setting_dialog_export_button:
                if(exportQuery.equals(""))
                    Toast.makeText(getActivity(), "Must select at least one field!", Toast.LENGTH_SHORT).show();
                else
                    exportCsv();
                break;
        }
    }

    private void addSQlQuery(String query){
        if(exportQuery.length() > 0)
            exportQuery = exportQuery + "," + query;
        else
            exportQuery = query;

        Log.d("Export", "Query: " +exportQuery);
    }

    private void removeSQL(){
        String sqlArray[] = splitExportQuery();
        StringBuilder str = new StringBuilder();

        for(int i = 0 ; i < sqlArray.length-1; i++){
            //when the sql array length more than one and not the first loop then add ","
            if(sqlArray.length != 1 && i != 0){
                str.append(",");
            }
            str.append(sqlArray[i]);
        }
        exportQuery = str.toString();
    }

    private String[] splitExportQuery(){
        return exportQuery.split(",");
    }

    public void exportCsv(){
        new CustomSqliteHelper(getActivity()).exportFile(fileID, exportQuery);
    }
}