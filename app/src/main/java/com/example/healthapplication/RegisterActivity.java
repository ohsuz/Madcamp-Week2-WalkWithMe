package com.example.healthapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static String VALIDATE =  "http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/validate.php";
    private static String REGISTER =  "http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/register.php";
    private boolean isvalidate = false;
    String id, pwd, name, phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 회원가입 레이아웃에서 필요한 변수 정의
        final EditText user_id=(EditText)findViewById(R.id.user_id);
        final EditText user_pwd=(EditText)findViewById(R.id.user_pwd);
        final EditText user_name=(EditText)findViewById(R.id.user_name);
        final EditText user_phone=(EditText)findViewById(R.id.user_phone);
        final Button validate =(Button)findViewById(R.id.validate);
        final Button register =(Button)findViewById(R.id.register);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = user_id.getText().toString();
                if(isvalidate){
                    return;
                }
                if(id.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("아이디는 빈칸일 수 없습니다.").setPositiveButton("확인",null).create().show();
                    return;
                }
                ContentValues values = new ContentValues();
                values.put("id", id);
                ValidateRequest validateRequest = new ValidateRequest(VALIDATE, values);
                validateRequest.execute();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = user_id.getText().toString();
                pwd = user_pwd.getText().toString();
                name = user_name.getText().toString();
                phone = user_phone.getText().toString();
                if(!isvalidate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("먼저 중복 체크를 해주세요.").setNegativeButton("확인",null).create().show();
                    return;
                }
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("pwd", pwd);
                values.put("name", name);
                values.put("phone", phone);
                RegisterRequest registerRequest = new RegisterRequest(REGISTER, values);
                registerRequest.execute();
            }
        });
    }
    public class ValidateRequest extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        ValidateRequest(String url, ContentValues values){
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
            String validateResult ="";
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
                    validateResult = jsonObject.getString("result");
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    if(validateResult.equals("YES")){
                        builder.setMessage("사용 가능한 아이디입니다.").setNegativeButton("확인",null).create().show();
                        isvalidate=true;
                    }else {
                        builder.setMessage("이미 등록된 아이디입니다.").setNegativeButton("다시 시도",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    if(registerResult.equals("SUCCESS")){
                        builder.setMessage("등록되었습니다.").setNegativeButton("확인",null).create().show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        builder.setMessage("등록에 실패했습니다.").setNegativeButton("다시 시도",null).create().show();
                    }
                } catch (JSONException e) {e.printStackTrace();}
            }
        }
    }
}