package com.example.meetingroombookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.meetingroombookingapp.databinding.ActivityMeetingRoomListBinding;
import com.example.meetingroombookingapp.databinding.ActivityRegistrantionBinding;

public class MeetingRoomListActivity extends AppCompatActivity {
    ActivityMeetingRoomListBinding binding;
    Intent mainIntent;
    SharedPreferences uData;

    private final static int MENU_MyAccount=10;
    private final static int MENU_Settings=20;
    private final static int MENU_BookingDetail=30;
    private final static int MENU_Leave=4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_room_list);
        binding=ActivityMeetingRoomListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainIntent=getIntent();

        uData=this.getSharedPreferences("user_info",MODE_PRIVATE);
        String user=uData.getString("account","尚未登入");
        Toast.makeText(this, "歡迎使用者"+user, Toast.LENGTH_LONG).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,MENU_MyAccount,0,"我的帳號");
        menu.add(0,MENU_Settings,2,"設定");
        menu.add(0,MENU_BookingDetail,1,"我的預約");
        menu.add(0,MENU_Leave,4,"離開本頁面");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case MENU_Leave:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}