package com.jby.chafor.packageTable;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.chafor.R;
import com.jby.chafor.others.SquareHeightLinearLayout;
import com.jby.chafor.shareObject.ApiDataObject;
import com.jby.chafor.shareObject.ApiManager;
import com.jby.chafor.shareObject.AsyncTaskManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PackageTableDialog extends DialogFragment implements View.OnClickListener {
    View rootView;
    private TextView actionBarTitle;
    private SquareHeightLinearLayout actionBarSearch, actionbarSetting, actionbarBackButton;

    private ListView packageTableDialogListView;
    private PackageTableListViewAdapter packageTableListViewAdapter;
    private ArrayList<PackageTableObject> packageTableObjectArrayList;
    private LinearLayout packageTableLabelLayout, packageTablePriceLayout;
    private ProgressBar packageTableProgressBar;
    private TextView packageTableBuyButton;

    private Handler handler = new Handler();
    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;

    public PackageTableDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.package_table_dialog, container);
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
        actionBarTitle = (TextView) rootView.findViewById(R.id.actionBar_title);
        actionbarSetting = (SquareHeightLinearLayout) rootView.findViewById(R.id.actionBar_setting);
        actionbarBackButton = (SquareHeightLinearLayout) rootView.findViewById(R.id.actionBar_back_button);
        actionBarSearch = (SquareHeightLinearLayout)rootView.findViewById(R.id.actionBar_search);

        packageTableDialogListView = rootView.findViewById(R.id.package_table_dialog_list_view);
        packageTableLabelLayout = rootView.findViewById(R.id.package_table_dialog_label_list_view);
        packageTablePriceLayout = rootView.findViewById(R.id.package_table_dialog_price_layout);
        packageTableBuyButton = rootView.findViewById(R.id.package_table_dialog_buy_button);
        packageTableProgressBar = rootView.findViewById(R.id.package_table_dialog_progress_bar);
        packageTableObjectArrayList = new ArrayList<>();

    }
    public void objectSetting(){
        actionbarSetting.setVisibility(View.GONE);
        actionBarSearch.setVisibility(View.GONE);
        actionBarTitle.setText(R.string.package_table_dialog_title);
        actionbarBackButton.setOnClickListener(this);
        packageTableBuyButton.setOnClickListener(this);

        packageTableListViewAdapter = new PackageTableListViewAdapter(getActivity(), packageTableObjectArrayList);
        packageTableDialogListView.setAdapter(packageTableListViewAdapter);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPackageFeature();
            }
        },300);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionBar_back_button:
                dismiss();
                break;
            case R.id.package_table_dialog_buy_button:
                openPlayStore();
                break;
        }
    }

    public void getPackageFeature(){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("getPackageFeature","1"));

        asyncTaskManager = new AsyncTaskManager(
                getContext(),
                new ApiManager().feature,
                new ApiManager().getResultParameter(
                        "",
                        new ApiManager().setData(apiDataObjectArrayList),
                        ""
                )
        );
        asyncTaskManager.execute();

        if (!asyncTaskManager.isCancelled()) {
            try {
                jsonObjectLoginResponse = asyncTaskManager.get(30000, TimeUnit.MILLISECONDS);

                if (jsonObjectLoginResponse != null) {
                    if (jsonObjectLoginResponse.getString("status").equals("1")) {
                        JSONArray jsonArray = jsonObjectLoginResponse.getJSONArray("value").getJSONObject(0).getJSONArray("package");
                        for(int i = 0 ; i < jsonArray.length(); i++){
                            packageTableObjectArrayList.add(new PackageTableObject(
                                    jsonArray.getJSONObject(i).getString("feature"),
                                    jsonArray.getJSONObject(i).getString("trial"),
                                    jsonArray.getJSONObject(i).getString("premium")
                            ));
                        }
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Network Error!", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException e) {
                Toast.makeText(getActivity(), "Interrupted Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(getActivity(), "Execution Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "JSON Exception!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(getActivity(), "Connection Time Out!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        setUpView();
    }

    private void setUpView(){
        if (packageTableObjectArrayList.size() > 0){
            packageTableDialogListView.setVisibility(View.VISIBLE);
            packageTableLabelLayout.setVisibility(View.VISIBLE);
            packageTableProgressBar.setVisibility(View.GONE);
            packageTablePriceLayout.setVisibility(View.VISIBLE);
        }
        packageTableListViewAdapter.notifyDataSetChanged();
    }

    private void openPlayStore(){
        String packageName = "com.google.android.apps.maps";
        try {
            Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            appStoreIntent.setPackage("com.android.vending");

            startActivity(appStoreIntent);
        } catch (android.content.ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}