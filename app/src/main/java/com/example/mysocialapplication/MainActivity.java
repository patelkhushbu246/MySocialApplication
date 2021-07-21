package com.example.mysocialapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.GraphJSONObjectCallback;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private LoginButton loginbtn;
    private CircleImageView image_view;
    private TextView profiletv,emailtv;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        loginbtn=findViewById(R.id.login_button);
        profiletv=findViewById(R.id.tv_profilename);
        emailtv=findViewById(R.id.tv_email);
        image_view=findViewById(R.id.imageview);
        
        callbackManager=CallbackManager.Factory.create();
        loginbtn.setReadPermissions(Arrays.asList("email","public_profile"));
        checkLoginStatus();
        loginbtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                profiletv.setText("");
                emailtv.setText("");
                image_view.setImageResource(0);
                Toast.makeText(MainActivity.this,"User logout..",Toast.LENGTH_SHORT).show();
            }
            else {
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken) {
        GraphRequest request= GraphRequest.newMeRequest(newAccessToken, new GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String f_name=object.getString("f_name");
                    String l_name=object.getString("l_name");
                    String email=object.getString("email");
                    String id=object.getString("id");
                    String image_url="https://graps.fackbook.com/" +id+ "/picture?type=normal";

                    emailtv.setText(email);
                    profiletv.setText(f_name+""+l_name);
                    RequestOptions requestoptions=new RequestOptions();
                    requestoptions.dontAnimate();

                    Glide.with(MainActivity.this).load(image_url).into(image_view);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("fields","f_name,l_name,email,id");
        request.setParameters(bundle);
        request.executeAsync();
    }


    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken()!=null){
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }
}