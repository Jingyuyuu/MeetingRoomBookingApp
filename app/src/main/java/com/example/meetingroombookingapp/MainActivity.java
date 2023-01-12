package com.example.meetingroombookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.meetingroombookingapp.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ExecutorService executor;
    SharedPreferences userData;

    Handler loginResultHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle.getInt("status") == 11) {
                SharedPreferences.Editor contextEditor=MainActivity.this.getSharedPreferences("user_info",MODE_PRIVATE).edit();

                contextEditor.putString("account",binding.accountText.getText().toString());
                contextEditor.putString("passwd",binding.PasswordText.getText().toString());
                contextEditor.apply();


                Toast.makeText(MainActivity.this, "登入成功", Toast.LENGTH_LONG).show();
                Intent intentToMeetingRoom = new Intent(MainActivity.this, MeetingRoomListActivity.class);
                startActivity(intentToMeetingRoom);
            } else {
                Toast.makeText(MainActivity.this, bundle.getString("mesg"), Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executor = Executors.newSingleThreadExecutor();

        userData=this.getPreferences(MODE_PRIVATE);

        boolean isRememberMe=userData.getBoolean("isRememberMe",false);
        if(isRememberMe){
            binding.accountText.setText(userData.getString("account",""));
            binding.PasswordText.setText(userData.getString("passwd",""));
            userData.getBoolean("isRememberMe",true);
        }else{
            binding.accountText.setText("");
            binding.PasswordText.setText("");
            userData.getBoolean("isRememberMe",false);
        }

        binding.checkLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    SharedPreferences.Editor editor=userData.edit();
                    editor.putString("account",binding.accountText.getText().toString());
                    editor.putString("passwd",binding.PasswordText.getText().toString());
                    editor.putBoolean("isRememberMe",true);
                    editor.apply();
                }else{
                    SharedPreferences.Editor editor=userData.edit();
                    editor.putString("account","");
                    editor.remove("passwd");
                    editor.putBoolean("isRememberMe", false);
                    editor.apply();
                }
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request;
                Intent intent = new Intent(MainActivity.this, RegistrantionActivity.class);
                Bundle datas = new Bundle();
                datas.putString("key1", "test1");
                datas.putString("key2", "test2");
                datas.putString("key3", "test3");
                intent.putExtra("datas", datas);
                startActivity(intent);

            }
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject packet = new JSONObject();
                try {
                    packet.put("type", 1);
                    packet.put("status", 10);
                    packet.put("mesg", "client 測試封包");
                    JSONObject data = new JSONObject();
                    data.put("user", binding.accountText.getText().toString());
                    data.put("pass", binding.PasswordText.getText().toString());
                    packet.put("data", data);

                    Log.w("API格式", packet.toString(4));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "輸入格式錯誤，請重新操作", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 使用網路通訊
                MediaType mType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(packet.toString(), mType);
                Request request = new Request.Builder().url("http://192.168.255.123:7856/api/member/login").post(body).build();
                SimpleAPIWorker apiCaller = new SimpleAPIWorker(request);
                executor.execute(apiCaller);

                // 根據登入結果來決定是否開啟 MeetingRoomListActivity


            }
        });


    }


    class SimpleAPIWorker implements Runnable {
        OkHttpClient client;
        Request request;

        public SimpleAPIWorker(Request request) {
            client = new OkHttpClient();
            this.request = request;
        }

        @Override
        public void run() {
            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                Log.w("api回應", responseString);
                // Response 也應該是 JSON格式回傳後 由 app端進行分析 確認登入結果
                JSONObject result = new JSONObject(responseString);
                Message m = loginResultHandler.obtainMessage();
                Bundle bundle = new Bundle();
                if (result.getInt("status") == 11) {
                    bundle.putString("mesg", result.getString("mesg"));
                    bundle.putInt("status", result.getInt("status"));
                } else {
                    bundle.putString("mesg", "登入失敗,請確認有無帳號,或密碼是否有誤");
                    bundle.putInt("status", result.getInt("status"));
                }
                m.setData(bundle);
                loginResultHandler.sendMessage(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}