package com.jby.chafor.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jby.chafor.R;
import com.jby.chafor.others.CustomViewPager;
import com.jby.chafor.others.ViewPagerAdapter;
import com.jby.chafor.others.ViewPagerObject;
import com.jby.chafor.sharePreference.SharedPreferenceManager;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private CustomViewPager loginActivityViewPager;
    ViewPagerAdapter loginActivityViewPagerAdapter;
    private ArrayList<ViewPagerObject> loginActivityViewPagerArrayList;
    boolean exit = false;
    Fragment fragment, fragment2, fragment3;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    ForgotPasswordFragment forgotPasswordFragment;
    private String imei;
    private int page = 0;
    private boolean isLoad = false;

    @Override
    public void onBackPressed() {
        if(loginActivityViewPager.getCurrentItem() == 2){

            forgotPasswordFragment.checkCurrentState();
        }
        else{
            if (loginActivityViewPager.getCurrentItem() != 0) {
                setCurrentPage(0);
            } else {
                exit();
            }
        }
    }

    public void exit() {
        if (exit) {
            System.exit(0); // finish activity
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        loginActivityViewPager = (CustomViewPager) findViewById(R.id.activity_login_viewpager);
        loginActivityViewPagerArrayList = new ArrayList<>();

    }

    private void objectSetting() {
        loginActivityViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), loginActivityViewPagerArrayList);
        loginActivityViewPager.setAdapter(loginActivityViewPagerAdapter);
        loginActivityViewPager.setOnPageChangeListener(this);
        setVersion();
    }

    public void setVersion(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String currentVersion = SharedPreferenceManager.getVersion(this);
            if(!currentVersion.equals(pInfo.packageName))
                SharedPreferenceManager.setVersion(this, pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {
        super.onStart();
        if(!isLoad){
            setPager();
            setCurrentPage(0);
            isLoad = true;
        }
    }

    public void setPager() {
        loginActivityViewPagerArrayList.add(new ViewPagerObject(new LoginFragment(), "Login"));
        loginActivityViewPagerArrayList.add(new ViewPagerObject(new RegisterFragment(), "Register"));
        loginActivityViewPagerArrayList.add(new ViewPagerObject(new ForgotPasswordFragment(), "Forgot"));

        fragment = loginActivityViewPagerAdapter.getItem(0);
        loginFragment = (LoginFragment) fragment;

        fragment2 = loginActivityViewPagerAdapter.getItem(1);
        registerFragment = (RegisterFragment) fragment2;

        fragment3 = loginActivityViewPagerAdapter.getItem(2);
        forgotPasswordFragment = (ForgotPasswordFragment)fragment3;

        loginActivityViewPager.setAdapter(loginActivityViewPagerAdapter);
        loginActivityViewPager.setCanScroll(false);
    }

    public void setCurrentPage(int page) {
        loginActivityViewPager.setCurrentItem(page);
    }

    public void alertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bad Request");
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "I Got It",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            loginFragment.setupLogo(true);
            registerFragment.setupLogo(false);
        } else {
            loginFragment.setupLogo(false);
            registerFragment.setupLogo(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
