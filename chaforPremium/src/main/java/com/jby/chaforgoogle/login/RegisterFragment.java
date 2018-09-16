package com.jby.chaforgoogle.login;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jby.chaforgoogle.R;
import com.jby.chaforgoogle.shareObject.AnimationUtility;
import com.jby.chaforgoogle.shareObject.ApiDataObject;
import com.jby.chaforgoogle.shareObject.ApiManager;
import com.jby.chaforgoogle.shareObject.AsyncTaskManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class RegisterFragment extends Fragment implements View.OnClickListener {
    View rootView;
    private EditText registerFragmentEditTextEmail;
    private EditText registerFragmentEditTextPassword;
    private EditText registerFragmentEditTextConfirmPassword;
    private ImageView registerFragmentImageViewBackButton, registerFragmentImageViewLogo;
    private Button registerFragmentSignUpButton;

    AsyncTaskManager asyncTaskManager;
    JSONObject jsonObjectLoginResponse;
    ArrayList<ApiDataObject> apiDataObjectArrayList;
    private Handler handler = new Handler();
    private String emailFormat, passwordFormat;



    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        objectInitialize();
        objectSetting();
        return rootView;
    }

    private void objectInitialize() {
        registerFragmentEditTextEmail = (EditText) rootView.findViewById(R.id.fragment_register_editText_email);
        registerFragmentEditTextPassword = (EditText) rootView.findViewById(R.id.fragment_register_editText_password);
        registerFragmentEditTextConfirmPassword = (EditText) rootView.findViewById(R.id.fragment_register_editText_confirm_password);

        registerFragmentImageViewBackButton = (ImageView) rootView.findViewById(R.id.fragment_register_imageView_back_button);
        registerFragmentImageViewLogo = (ImageView) rootView.findViewById(R.id.fragment_register_imageView_logo);
        registerFragmentSignUpButton = (Button) rootView.findViewById(R.id.fragment_register_button_signUp);

        passwordFormat = "^(.{6,20})";
    }

    private void objectSetting() {
        registerFragmentImageViewBackButton.setOnClickListener(this);
        registerFragmentSignUpButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_register_imageView_back_button:
                ((LoginActivity)getActivity()).setCurrentPage(0);
                break;
            case R.id.fragment_register_button_signUp:
               checkingInput();
                break;

        }
    }
    public void checkingInput(){
        final String email = registerFragmentEditTextEmail.getText().toString();
        final String password = registerFragmentEditTextPassword.getText().toString();
        final String confirmPassword = registerFragmentEditTextConfirmPassword.getText().toString();

        if(!email.equals("") && !password.equals("") && !confirmPassword.equals("")){

            if(!email.matches(android.util.Patterns.EMAIL_ADDRESS.pattern())){
                registerFragmentEditTextEmail.setError("Invalid Email");
                registerFragmentEditTextEmail.setHintTextColor(ContextCompat.getColor(getActivity(),R.color.error_message));
            }

            if(!password.matches(passwordFormat)){
                registerFragmentEditTextPassword.setError("Password must at least 6 characters");
                registerFragmentEditTextEmail.setHintTextColor(ContextCompat.getColor(getActivity(),R.color.error_message));
            }

            if(!password.equals(confirmPassword)){
                registerFragmentEditTextConfirmPassword.setError("Password not match");
            }

            if(email.matches(android.util.Patterns.EMAIL_ADDRESS.pattern()) && password.matches(passwordFormat) && password.equals(confirmPassword)){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        registerID(email , password);
                    }
                }, 200);
            }


        }else{
            ((LoginActivity)getActivity()).alertMessage("All the field above is required");
        }
    }
    public void registerID( String email, String password){
        apiDataObjectArrayList = new ArrayList<>();
        apiDataObjectArrayList.add(new ApiDataObject("email",email));
        apiDataObjectArrayList.add(new ApiDataObject("password",password));
        ;
        asyncTaskManager = new AsyncTaskManager(
                getContext(),
                new ApiManager().register,
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
                        clearAll();
                        Toast.makeText(getActivity(), "Register Successful!", Toast.LENGTH_SHORT).show();
                        ((LoginActivity) getActivity()).setCurrentPage(0);

                    }
                    else if (jsonObjectLoginResponse.getString("status").equals("2")) {
                        Toast.makeText(getActivity(), "Email already existed!", Toast.LENGTH_SHORT).show();

                    }
                    else if (jsonObjectLoginResponse.getString("status").equals("3")) {
                        ((LoginActivity)getActivity()).alertMessage("Something error with server! Try it later!");
                    }
                    else if(jsonObjectLoginResponse.getString("status").equals("4")){
                        Toast.makeText(getActivity(), "Something error with server! Try it later!", Toast.LENGTH_SHORT).show();
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
    }
    public void clearAll(){
        registerFragmentEditTextEmail.setText("");
        registerFragmentEditTextConfirmPassword.setText("");
        registerFragmentEditTextPassword.setText("");
    }
    public void setupLogo(final boolean hide){
        if(hide){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new AnimationUtility().fadeInVisible(getActivity(), registerFragmentImageViewLogo);
                }
            },200);
        }
        else{
            registerFragmentImageViewLogo.setVisibility(View.GONE);
        }

    }
}
