package com.jby.chaforgoogle.setting;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jby.chaforgoogle.R;
import com.jby.chaforgoogle.exportFeature.file.ExportFileActivity;
import com.jby.chaforgoogle.sharePreference.SharedPreferenceManager;

import java.util.Objects;

public class ExportTypeDialog extends DialogFragment implements View.OnClickListener {
    View rootView;
    private LinearLayout exportTypeDialogCsvButton, exportTypeDialogTextButton;
    private CheckBox exportTypeDialogCsvCheckBox, exportTypeDialogTextCheckBox;
    private Button exportTypeDialogConfirmButton, exportTypeDialogCancelButton ;
    ExportTypeDialogCallBack exportTypeDialogCallBack;
    public ExportTypeDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_setting_export_type_dialog, container);
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
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        //initialize checkbox setting
        int exportType = SharedPreferenceManager.getExportType(getActivity());
        if(exportType == 1)
            exportTypeDialogCsvCheckBox.setChecked(true);
        else
            exportTypeDialogTextCheckBox.setChecked(true);
    }

    private void objectInitialize() {
        exportTypeDialogCallBack = (ExportTypeDialogCallBack)getActivity();

        exportTypeDialogCsvButton = rootView.findViewById(R.id.export_file_type_dialog_label_csv_button);
        exportTypeDialogTextButton = rootView.findViewById(R.id.export_file_type_dialog_label_text_button);
        exportTypeDialogCsvCheckBox = rootView.findViewById(R.id.export_file_type_dialog_label_csv_checkbox);
        exportTypeDialogTextCheckBox = rootView.findViewById(R.id.export_file_type_dialog_label_text_checkbox);
        exportTypeDialogConfirmButton = rootView.findViewById(R.id.export_file_type_dialog_confirm_button);
        exportTypeDialogCancelButton = rootView.findViewById(R.id.export_file_type_dialog_cancel_button);
    }

    public void objectSetting(){
        exportTypeDialogCsvButton.setOnClickListener(this);
        exportTypeDialogTextButton.setOnClickListener(this);
        exportTypeDialogConfirmButton.setOnClickListener(this);
        exportTypeDialogCancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.export_file_type_dialog_label_csv_button:
                onClickSetting(true, false);
                break;
            case R.id.export_file_type_dialog_label_text_button:
                onClickSetting(false, true);
                break;
            case R.id.export_file_type_dialog_confirm_button:
                changeExportTypeSetting();
                exportTypeDialogCallBack.setExportType();
                dismiss();
                break;
            case R.id.export_file_type_dialog_cancel_button:
                dismiss();
                break;
        }
    }

    private void onClickSetting(boolean csv, boolean text){
        exportTypeDialogCsvCheckBox.setChecked(csv);
        exportTypeDialogTextCheckBox.setChecked(text);
    }

    private void changeExportTypeSetting(){
        if(exportTypeDialogTextCheckBox.isChecked())
            SharedPreferenceManager.setExportType(getActivity(), 2);
        else
            SharedPreferenceManager.setExportType(getActivity(), 1);
    }
    public interface ExportTypeDialogCallBack {
        void setExportType();
    }
}