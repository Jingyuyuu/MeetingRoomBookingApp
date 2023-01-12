package com.example.meetingroombookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.meetingroombookingapp.databinding.ActivityRegistrantionBinding;

public class RegistrantionActivity extends AppCompatActivity {
    ActivityRegistrantionBinding binding;
    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrantion);
        binding=ActivityRegistrantionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        mainIntent=getIntent();
        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}