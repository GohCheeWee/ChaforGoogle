package com.jby.chaforgoogle.exportFeature.subcategory.scanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.jby.chaforgoogle.R;
import com.jby.chaforgoogle.database.CustomSqliteHelper;
import com.jby.chaforgoogle.exportFeature.subcategory.subcategory.SubCategoryExistedDialog;
import com.jby.chaforgoogle.exportFeature.subcategory.subcategory.SubCategoryInsertDialog;
import com.jby.chaforgoogle.exportFeature.subcategory.subcategory.takeAction.TakeActionActivity;
import com.jby.chaforgoogle.others.SquareHeightLinearLayout;
import com.jby.chaforgoogle.sharePreference.SharedPreferenceManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.jby.chaforgoogle.exportFeature.subcategory.SubCategoryActivity.REFRESH_LIST_VIEW;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class CaptureActivity extends AppCompatActivity implements SubCategoryInsertDialog.CreateDialogCallBack,
        SubCategoryExistedDialog.ExistedDialogCallBack, View.OnClickListener,
        DecoratedBarcodeView.TorchListener{
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private ImageView captureActivityFlashLightIcon;
    private BeepManager beepManager;
    private String lastText;
    private FragmentManager fm;
    private DialogFragment dialogFragment;
    private Collection<BarcodeFormat> formats;
    private String fileID, categoryID;
    private int count;
    private Bundle bundle;
    // flash light purpose
    private boolean flash = false;

    //action bar
    private SquareHeightLinearLayout captureActivityBackButton, captureActivityFlashButton;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null) {
                // Prevent duplicate scans
                return;
            }
            pause();

//            barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();
            scanSuccess(result.getText());

            //Added preview of scanned barcode
/*            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));*/
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capture);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.activity_capture_barcode_view);
        captureActivityBackButton = findViewById(R.id.activity_capture_action_bar_back_button);
        captureActivityFlashButton = findViewById(R.id.activity_capture_action_bar_flash_light);
        captureActivityFlashLightIcon = findViewById(R.id.activity_capture_action_bar_flash_light_icon);
        formats = Arrays.asList(
                BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.UPC_A,BarcodeFormat.AZTEC,
                BarcodeFormat.CODABAR, BarcodeFormat.CODE_93, BarcodeFormat.CODE_128,BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.EAN_8, BarcodeFormat.EAN_13, BarcodeFormat.ITF, BarcodeFormat.MAXICODE, BarcodeFormat.PDF_417,
                BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED, BarcodeFormat.UPC_E, BarcodeFormat.UPC_EAN_EXTENSION
        );
        beepManager = new BeepManager(this);
        fm = getSupportFragmentManager();
    }

    private void objectSetting() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            fileID = bundle.getString("file_id");
            categoryID = bundle.getString("category_id");
            count = bundle.getInt("count");
        }
        if(!hasFlash()){
            captureActivityFlashButton.setVisibility(View.GONE);
        }
        captureActivityBackButton.setOnClickListener(this);
        captureActivityFlashButton.setOnClickListener(this);
        barcodeView.decodeContinuous(callback);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.setTorchListener(this);
    }

    @Override
    public void insertSubCategoryItem(String barcode, String quantity) {
        int scanQuantity = Integer.valueOf(quantity);
        int insertSubCategory = new CustomSqliteHelper(this).saveSubCategory(fileID, count, categoryID, barcode, scanQuantity);
        if(insertSubCategory == 1){
            Toast.makeText(this, "Record Store Successful!", Toast.LENGTH_SHORT).show();
        }
        else if(insertSubCategory == 2)
            Toast.makeText(this, "Failed to store this record", Toast.LENGTH_SHORT).show();
        else
            openExistedDialog(barcode, quantity);

        resume();
    }

    @Override
    public void resetScanResult() {
/*        lastText = null;
        resume();*/
    }

    @Override
    public void setCount() {
        count = 1;
    }

    @Override
    public void takeActionDialog(String barcode, String quantity, String categoryID) {
        Intent intent = new Intent(this, TakeActionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("barcode", barcode);
        bundle.putString("quantity", quantity);
        bundle.putString("category_id", categoryID);
        bundle.putString("file_id", fileID);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }

    private void scanSuccess(String scanResult){
        if(SharedPreferenceManager.getQuickScan(this).equals("0")){
            openInsertDialog(scanResult);
        }
        else{
            insertSubCategoryItem(scanResult, SharedPreferenceManager.getQuickScanQuantity(this));
        }
    }

    //    open insert dialog
    public void openInsertDialog(String scanResult){
        dialogFragment = new SubCategoryInsertDialog();
        bundle = new Bundle();
        bundle.putString("barcode", scanResult);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    //    existed dialog
    public void openExistedDialog(String barcode, String quantity){
        dialogFragment = new SubCategoryExistedDialog();
        bundle = new Bundle();
        bundle.putString("barcode", barcode);
        bundle.putString("quantity", quantity);
        bundle.putString("category_id", categoryID);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.activity_capture_action_bar_back_button:
                onBackPressed();
                break;
            case R.id.activity_capture_action_bar_flash_light:
                switchFlashlight();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(REFRESH_LIST_VIEW, intent);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause() {
        barcodeView.pauseAndWait();
    }

    public void resume() {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
/*-------------------------------------------------------------flash light purpose----------------------------------------------------*/
    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight() {
        if (!flash) {
            barcodeView.setTorchOn();
            captureActivityFlashLightIcon.setImageDrawable(getResources().getDrawable(R.drawable.open_flash_icon));
            flash = true;
        } else {
            barcodeView.setTorchOff();
            captureActivityFlashLightIcon.setImageDrawable(getResources().getDrawable(R.drawable.close_flash_icon));
            flash = false;
        }
    }
    /*------------------------------------------------------------end of flash light--------------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(resultCode==2)
        {
            //do the things u wanted
            dialogFragment.dismiss();
        }
    }
}
