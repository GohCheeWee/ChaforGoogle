package com.jby.chafor.exportFeature.category;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.jby.chafor.R;
import com.jby.chafor.database.CustomSqliteHelper;
import com.jby.chafor.exportFeature.category.searchFeature.ExportCategorySearchDialog;
import com.jby.chafor.exportFeature.subcategory.SubCategoryActivity;
import com.jby.chafor.login.LoginActivity;
import com.jby.chafor.others.CustomListView;
import com.jby.chafor.others.SquareHeightLinearLayout;
import com.jby.chafor.packageTable.PackageTableDialog;
import com.jby.chafor.setting.SettingActivity;
import com.jby.chafor.shareObject.AnimationUtility;
import com.jby.chafor.sharePreference.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExportCategoryActivity extends AppCompatActivity implements View.OnClickListener,
        ExportCategoryInsertDialog.CreateDialogCallBack, ExportCategoryListViewAdapter.CategoryAdapterCallBack,
        ExportCategoryUpdateDialog.UpdateDialogCallBack,
        AbsListView.MultiChoiceModeListener, AbsListView.OnScrollListener,
        CustomListView.OnDetectScrollListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private TextView actionBarTitle;
    private SquareHeightLinearLayout actionBarSearch, actionbarSetting, actionbarBackButton, actionBarCancel, actionBarExportButton;
    private LinearLayout actionBarSearchLayout, actionBarDefaultLayout;
    private EditText actionBarSearchField;
    private View actionBarLayout;

    private ExportCategoryListViewAdapter categoryFragmentListViewAdapter;
    private ArrayList<ExportCategoryListViewObject> categoryFragmentListViewObjectArrayList;
    private CustomListView categoryFragmentListView;
    private ImageView categoryFragmentFloatingButton;
    private LinearLayout categoryFragmentResultNotFound;
    private SwipeRefreshLayout categoryFragmentSwipeRefreshLayout;
    private TextView categoryFragmentLabelProgressBar;
    private ProgressBar categoryFragmentProgressBar;
    private ProgressDialog pd;
    //ads purpose
    private AdView googleAdsView;
    private InterstitialAd mInterstitialAd;

    private Handler mHandler = new Handler();
    //    dialog
    DialogFragment dialogFragment;
    Bundle bundle;
    FragmentManager fm;
    //for delete purpose
    List<String> list = new ArrayList<String>();
    SparseBooleanArray checked;
    String selectedItem;
    ActionMode actionMode;
    //    for update purpose
    int editPosition;
    String subCategoryNum;
    String categoryID;
    //    load more data
    View listViewFooter;
    int page = 1;
    boolean isScroll = false;
    boolean successToGetDataBefore = false;
    boolean finishLoadAll = false;
    boolean isLoading = true;
    Intent intent;
//    actionbar purpose
    private InputMethodManager imm;
//    update quantity purpose
    private int selectedPosition = 0;
    private String selectedCategoryID, selectedCategoryName;
//    prevent reload the data
    private boolean load = false;
    CustomSqliteHelper customSqliteHelper;
    //permission
    public static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;
    //export purpose
    private View exportView;
    //closing purpose
    boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_category);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        actionBarTitle = (TextView)findViewById(R.id.actionBar_title);
        actionBarSearch = (SquareHeightLinearLayout)findViewById(R.id.actionBar_search);
        actionbarSetting = (SquareHeightLinearLayout)findViewById(R.id.actionBar_setting);
        actionbarBackButton = (SquareHeightLinearLayout)findViewById(R.id.actionBar_back_button);
        actionBarSearchLayout = (LinearLayout)findViewById(R.id.actionBar_search_layout);
        actionBarDefaultLayout = (LinearLayout)findViewById(R.id.actionBar_icon_layout);
        actionBarExportButton = findViewById(R.id.actionBar_upload);
        actionBarCancel = (SquareHeightLinearLayout) findViewById(R.id.actionBar_cancel);
        actionBarSearchField = (EditText) findViewById(R.id.action_bar_search_field);
        actionBarLayout = findViewById(R.id.activity_main_layout_action_bar);

        categoryFragmentListView = (CustomListView) findViewById(R.id.fragment_category_list_view);
        categoryFragmentFloatingButton = (ImageView) findViewById(R.id.fragment_category_floating_button);
        categoryFragmentResultNotFound = (LinearLayout)findViewById(R.id.not_found);
        categoryFragmentSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_layout);
        categoryFragmentProgressBar = findViewById(R.id.activity_category_progress_bar);
        categoryFragmentLabelProgressBar = findViewById(R.id.activity_category_progress_bar_label);
        googleAdsView = findViewById(R.id.adView);

        categoryFragmentListViewObjectArrayList = new ArrayList<>();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        listViewFooter = ((LayoutInflater) Objects.requireNonNull(this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
                .inflate(R.layout.fragment_category_list_view_footer, null, false);

        fm = getSupportFragmentManager();
        pd = new ProgressDialog(this);

        customSqliteHelper = new CustomSqliteHelper(this);

    }
    private void objectSetting() {
        AdRequest adRequest = new AdRequest.Builder().build();
        googleAdsView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ADMOB_INTERSTITIAL_ID));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        //set visibility
        actionBarExportButton.setVisibility(View.VISIBLE);

        actionBarSearch.setOnClickListener(this);
        actionbarSetting.setOnClickListener(this);
        actionbarBackButton.setOnClickListener(this);
        actionBarExportButton.setOnClickListener(this);
        actionBarCancel.setOnClickListener(this);
        categoryFragmentSwipeRefreshLayout.setOnRefreshListener(this);

        categoryFragmentListView.setOnScrollListener(this);
        categoryFragmentListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        categoryFragmentListView.setMultiChoiceModeListener(this);
        categoryFragmentListView.setOnDetectScrollListener(this);
        categoryFragmentListView.setOnItemClickListener(this);
        categoryFragmentFloatingButton.setOnClickListener(this);
        pd.setMessage("Loading...");
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!load){
            setActionBarTitle("Category");
            setUpListView();
            fetchAll();
            openPackageTableRandomly();//randomly
            load = true;
        }
    }

    private void openPackageTableRandomly() {
        boolean open = getRandomBoolean();
        if(open)
            openPackageTable();

    }

    private boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.actionBar_search:
//                showSearchView(true);
                openSearchDialog();

                break;
            case R.id.actionBar_setting:
                openSetting();
                break;
            case R.id.actionBar_back_button:
                onBackPressed();
                break;
            case R.id.actionBar_cancel:
//                showSearchView(false);
                break;
            case R.id.actionBar_upload:
                readPhonePermission(view);
                break;
            case R.id.fragment_category_floating_button:
                openInsertDialog();
                break;
        }
    }
    public void showSearchView(boolean show){
        if(show){
            new AnimationUtility().slideOut(this, actionBarDefaultLayout);
            new AnimationUtility().slideOut(this, actionBarTitle);
            new AnimationUtility().minimize(this, actionBarSearchLayout);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(imm != null)
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    new AnimationUtility().slideOut(ExportCategoryActivity.this, categoryFragmentFloatingButton);
                }
            }, 200);
            actionBarSearchField.requestFocus();
        }
        else{
            new AnimationUtility().minimize(this, actionBarDefaultLayout);
            new AnimationUtility().minimize(this, actionBarTitle);
            new AnimationUtility().slideOut(this, actionBarSearchLayout);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(imm != null)
                        imm.hideSoftInputFromWindow(actionBarSearchField.getWindowToken(),0);
                    new AnimationUtility().fastFadeInVisible(ExportCategoryActivity.this, categoryFragmentFloatingButton);
                }
            }, 200);
            actionBarSearchField.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        if(actionBarSearchLayout.getVisibility() == View.VISIBLE)
        {
            showSearchView(false);
        }else{
            exit();
        }
    }

    public void setActionBarTitle(String title){
        actionBarTitle.setText(title);
    }

    public void hideActionbar(boolean hide){
        if(hide)
            new AnimationUtility().slideOut(this, actionBarLayout);
        else
            new AnimationUtility().minimize(this, actionBarLayout);
    }

    public void openInsertDialog(){
        dialogFragment = new ExportCategoryInsertDialog();
        dialogFragment.show(fm, "");
        showKeyBoard();
    }

    public void openUpdateDialog(String category_id, String category_name, int position, String subCategory_num){
//        for update local purpose
        editPosition = position;
        subCategoryNum = subCategory_num;
        categoryID = category_id;

        dialogFragment = new ExportCategoryUpdateDialog();
        bundle = new Bundle();
        bundle.putString("category_id", category_id);
        bundle.putString("category_name", category_name);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
        showKeyBoard();
    }


    public void openSearchDialog(){
        dialogFragment = new ExportCategorySearchDialog();
        bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long l, boolean b) {
        final int checkedCount = categoryFragmentListView.getCheckedItemCount();
        // Set the  CAB title according to total checked items
        actionMode.setTitle(checkedCount + "  Selected");
        // Calls  toggleSelection method from ListViewAdapter Class
        categoryFragmentListViewAdapter.toggleSelection(position);
        checked = categoryFragmentListView.getCheckedItemPositions();
    }

    @Override
    public boolean onCreateActionMode (ActionMode actionMode, Menu menu){
        hideActionbar(true);
        actionMode.getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode (ActionMode actionMode, Menu menu){
        return false;
    }

    @Override
    public boolean onActionItemClicked (ActionMode actionMode, MenuItem menuItem){
        switch (menuItem.getItemId()) {
            case R.id.selectAll:
                //
                final int checkedCount = categoryFragmentListViewObjectArrayList.size();

                categoryFragmentListViewAdapter.removeSelection();
                for (int i = 0; i < checkedCount; i++) {
                    categoryFragmentListView.setItemChecked(i, true);
                }
                actionMode.setTitle(checkedCount + "  Selected");
                return true;

            case R.id.delete:
                alertMessage();
                this.actionMode = actionMode;
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        hideActionbar(false);
        categoryFragmentListViewAdapter.removeSelection();
        list.clear();
    }

    public void alertMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete these item?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "I'm Sure",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCategoryItem();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void alertMessageCategoryExisted(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("This category is existed!");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "I Got It",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        final int position = firstVisibleItem+visibleItemCount;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!finishLoadAll){
                    // Check if bottom has been reached
                    if (position >= totalItemCount && totalItemCount > 0 && !isLoading && isScroll) {
                        isLoading = true;
                        page++;
                        fetchAll();
                        categoryFragmentListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        },50);
        if(!isScroll){
            if (categoryFragmentListView.getChildAt(0) != null) {
                categoryFragmentSwipeRefreshLayout.setEnabled(categoryFragmentListView.getFirstVisiblePosition() == 0 && categoryFragmentListView.getChildAt(0).getTop() == 0);
            }
        }
    }

    @Override
    public void onUpScrolling() {
        showFloatingButton();
        isScroll = false;
    }

    @Override
    public void onDownScrolling() {
        hideFloatingButton();
        isScroll = true;
    }

    public void hideFloatingButton(){
        if(!isScroll)
        {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new AnimationUtility().slideOut(getApplicationContext(), categoryFragmentFloatingButton);
                }
            },100);
        }

    }

    public void showFloatingButton(){
        if(isScroll){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    categoryFragmentFloatingButton.setVisibility(View.VISIBLE);
                }
            },100);
        }
    }
    public int getUpdatePosition(){
        return editPosition;
    }

    public String getSubCategoryNum(){
        return subCategoryNum;
    }

    public String getCategoryID(){
        return categoryID;
    }

    public ActionMode getActionMode(){
        return actionMode;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.fragment_category_list_view:
                clickEffect(view);
                selectedCategoryID = categoryFragmentListViewObjectArrayList.get(i).getId();
                selectedCategoryName = categoryFragmentListViewObjectArrayList.get(i).getCategory();
                selectedPosition = i;
                Bundle bundle = new Bundle();
                intent = new Intent(this, SubCategoryActivity.class);
                bundle.putString("category_id", selectedCategoryID);
                bundle.putString("category_name", selectedCategoryName);
                bundle.putString("fromListView", "categoryLV");
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onRefresh() {
        //                reset everything
        isScroll = false;
        successToGetDataBefore = false;
        finishLoadAll = false;
        isLoading = true;
        page = 1;
        categoryFragmentListViewObjectArrayList.clear();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    categoryFragmentSwipeRefreshLayout.setRefreshing(false);
                    fetchAll();
                    categoryFragmentListViewAdapter.notifyDataSetChanged();
                }
            },50);
    }

    public void showKeyBoard(){
        final InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(resultCode==2)
        {
            String newQuantity = data.getStringExtra("quantity");
            updateSelectedItemQuantity(newQuantity);
        }
        else if(resultCode == 3)
        {
            logOutSetting();
        }
    }

    public void updateSelectedItemQuantity(String newQuantity){
        categoryFragmentListViewObjectArrayList.set(selectedPosition,new ExportCategoryListViewObject(
                selectedCategoryID,
                selectedCategoryName,
                newQuantity
        ));
        categoryFragmentListViewAdapter.notifyDataSetChanged();
    }

    public void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }


    public void logOutSetting(){
        SharedPreferenceManager.setUserID(this, "default");
        SharedPreferenceManager.setUserPassword(this, "default");
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void openSetting(){
        intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, 3);
    }

    @Override
    public void createNewCategoryItem(String category_name) {
        int currentSize = 3;
        if(categoryFragmentListViewObjectArrayList.size() < currentSize)
        {
            int saveFile = customSqliteHelper.saveCategory(category_name);
            if(saveFile == 1)
                fetchAll();
            else if(saveFile ==2)
                alertMessageCategoryExisted();
            else
                Toast.makeText(this, "Failed to store this file", Toast.LENGTH_SHORT).show();
        }
        else
            alertMessageWhenReachedTheLimitation();

    }

    @Override
    public void updateCategoryItem(String category_name, String category_id) {
        int updateCategory = customSqliteHelper.updateCategory(category_name, category_id);
        if(updateCategory == 1)
        {
            categoryFragmentListViewObjectArrayList.set(getUpdatePosition(), new ExportCategoryListViewObject(
                    getCategoryID(),
                    category_name,
                    getSubCategoryNum()));
            categoryFragmentListViewAdapter.notifyDataSetChanged();
        }
        else if(updateCategory == 2)
            alertMessageCategoryExisted();
        else
            Toast.makeText(this, "Failed to update this category", Toast.LENGTH_SHORT).show();
    }

    public List getDeleteItem(){
        for(int i = 0 ; i<categoryFragmentListView.getCount(); i++){
            if(checked.get(i)){
                list.add(categoryFragmentListViewObjectArrayList.get(i).getId());
            }
        }
        return list;
    }

    public void deleteCategoryItem(){
        boolean deleteCategory = customSqliteHelper.deleteCategory(getDeleteItem());
        if(deleteCategory){
            for(int i=categoryFragmentListView.getCount()-1; i >= 0; i--){
                if(categoryFragmentListViewAdapter.getSelectedIds().get(i)){
                    categoryFragmentListViewObjectArrayList.remove(i);
                }
            }
            getActionMode().finish();
            categoryFragmentListViewAdapter.notifyDataSetChanged();
            setListViewVisibility();
        }
        else
            Toast.makeText(this, "Failed to delete this file", Toast.LENGTH_SHORT).show();
        getActionMode().finish();
    }

    public void fetchAll(){
        //        initialize and assign data into array list
        categoryFragmentListViewObjectArrayList.clear();
        isLoading = false;
        categoryFragmentListViewObjectArrayList = customSqliteHelper.fetchAllCategory(page, categoryFragmentListViewObjectArrayList);
        setListViewVisibility();

    }

    public void setUpListView(){
        categoryFragmentListViewAdapter = new ExportCategoryListViewAdapter(this, categoryFragmentListViewObjectArrayList, this);
        categoryFragmentListView.setAdapter(categoryFragmentListViewAdapter);
    }

    public void setListViewVisibility(){
        if(categoryFragmentListViewObjectArrayList.size() > 0){
//            if data found
            categoryFragmentListView.setVisibility(View.VISIBLE);
            categoryFragmentResultNotFound.setVisibility(View.GONE);
        }
        else{
//            if not found
            categoryFragmentListView.setVisibility(View.INVISIBLE);
            categoryFragmentResultNotFound.setVisibility(View.VISIBLE);
        }
        setupProgressBar();
    }
    @Override
    protected void onDestroy() {
        customSqliteHelper.close();
        super.onDestroy();
    }

    public void setupProgressBar(){
        int fileMax = 3;
        int fileStorage = categoryFragmentListViewObjectArrayList.size();
        categoryFragmentProgressBar.setMax(fileMax);

        String fileStatus = "Category Storage: " + String.valueOf(categoryFragmentListViewObjectArrayList.size()) + "/" + String.valueOf(fileMax);
        categoryFragmentLabelProgressBar.setText(fileStatus);

        if(fileMax == fileStorage){
            categoryFragmentProgressBar.setProgressDrawable(getApplicationContext().getResources().getDrawable(R.drawable.edit_progress_bar_when_full));
            categoryFragmentProgressBar.setProgress(fileStorage);
        }
        else{
            categoryFragmentProgressBar.setProgressDrawable(getApplicationContext().getResources().getDrawable(R.drawable.edit_progress_bar));
            if(fileStorage> fileMax/2)
                categoryFragmentProgressBar.setSecondaryProgress(fileStorage);
            else{
                categoryFragmentProgressBar.setProgress(fileStorage);
                categoryFragmentProgressBar.setSecondaryProgress(fileStorage);
            }
        }
    }

    public void alertMessageWhenReachedTheLimitation(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("You have reached your maximum number of cateogory!");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Get Premium Now",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openPackageTable();
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openPackageTable(){
        dialogFragment = new PackageTableDialog();
        dialogFragment.show(fm, "");
    }

    public void exit(){
        if (exit) {
           openAdsWhenClosing();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    private void openAdsWhenClosing(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
               finish();
            }
        });
    }

    /*----------------------------------------------------------------------------------export purpose-------------------------------------------------*/
    public void exportCsv(){
        new CustomSqliteHelper(this).exportFile(exportView);
    }

    public void readPhonePermission(View view) {
        exportView = view;

        if (checkReadStatePermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                exportCsv();
            }
        }
    }

    public boolean checkReadStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    //Request location updates:
                    exportCsv();
                }
            } else {
                checkReadStatePermission();
            }
        }
        // If request is cancelled, the result arrays are empty.
    }
    /*-----------------------------------------------------------------------------end of export----------------------------------------------------------*/
}
