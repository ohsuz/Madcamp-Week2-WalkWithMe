package com.example.healthapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static String LOGIN =  "http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/login.php";
    private static String REGISTER =  "http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/register.php";
    String id, pwd;

    String email, name;

    // 페이스북 로그인에 필요한 변수
    private ImageView facebookLogin;
    private CallbackManager callbackManager;

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 로그인 레이아웃에서 필요한 변수 정의
        final EditText user_id=(EditText)findViewById(R.id.user_id);
        final EditText user_pwd=(EditText)findViewById(R.id.user_pwd);
        final Button login=(Button)findViewById(R.id.login);
        final TextView register=(TextView)findViewById(R.id.register);
        facebookLogin = (ImageView)findViewById(R.id.facebookLogin);


        // 페이스북 로그인
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        ContentValues values = new ContentValues();
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            if(object.has("email")){
                                                email = object.getString("email");
                                                UserId.id = object.getString("email");
                                            }
                                            if(object.has("last_name") && object.has("first_name")){
                                                name = object.getString("first_name")+" "+object.getString("last_name");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        values.put("pwd", "abc");
                                        values.put("id", email);
                                        values.put("name", name);
                                        values.put("phone", "010-0000-0000");

                                        RegisterRequest registerRequest = new RegisterRequest(REGISTER, values);
                                        registerRequest.execute();
                                    }
                                });
                        //파라매터 번들을 추가해서 필요한 요소들 받아오기
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // finish();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("test", "Error: " + error);
                    }
                });
            }
        });

        // 자체 로그인
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = user_id.getText().toString();
                pwd = user_pwd.getText().toString();
                // 생성한 ContentValues 객체에 put(key, value)를 이용해 아이디와 패스워드를 넣음
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("pwd", pwd);
                // LoginRequest 객체를 생성해 생성자 파라미터로 URL과 ContentValues 객체를 넣음
                LoginRequest loginRequest = new LoginRequest(LOGIN,  values);
                loginRequest.execute();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }


    // for facebook login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class LoginRequest extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        LoginRequest(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result; // 요청 결과를 저장할 변수
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String loginResult ="";
            if (result == null){
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
            else {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    loginResult = jsonObject.getString("result");
                    if(loginResult.equals("SUCCESS")){
                        SharedPreferences sf = getSharedPreferences("saveUser", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sf.edit();
                        editor.putString("id", id);
                        editor.commit();
                        UserId.setId(id);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        if (loginResult.equals("CASE1")) {
                            builder.setMessage("등록되지 않은 아이디입니다.").setNegativeButton("다시 시도",null).create().show();
                        } else {
                            builder.setMessage("잘못된 비밀번호입니다.").setNegativeButton("다시 시도",null).create().show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "Click listener start" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
            }
        }
    }

    void loginFacebook(String id, String pwd, String name, String phone){
        new Thread(){
            @Override
            public void run() {
                String parameters = "id="+id+"&pwd="+pwd+"&name="+name+"&phone="+phone;
                try {
                    URL url= new URL(REGISTER);
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(parameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                } catch (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) {e.printStackTrace();}
            }
        }.start();
    }

    public class RegisterRequest extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        RegisterRequest(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String registerResult ="";
            if (result == null){
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }else {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }try {
                    registerResult = jsonObject.getString("result");
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    if(registerResult.equals("SUCCESS")){
                        builder.setMessage("로그인되었습니다.").setNegativeButton("확인",null).create().show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        builder.setMessage("로그인에 실패했습니다.").setNegativeButton("다시 시도",null).create().show();
                    }
                } catch (JSONException e) {e.printStackTrace();}
            }
        }
    }
}