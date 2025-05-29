package com.example.morsecodetorch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.i("MAIN", "onCreate: main");

        Button btEncode = findViewById(R.id.btEncode);
        Button btDecode = findViewById(R.id.btDecode);

        btEncode.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CodeActivity.class);
            startActivity(intent);
        });

        btDecode.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DecodeActivity.class);
            startActivity(intent);
        });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i("MAIN", "onPause: main");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i("MAIN", "onStop: main");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.i("MAIN", "onDestroy: main");
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        Log.i("MAIN", "onSaveInstanceState: main");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.i("MAIN", "onRestart: main");
//    }
//
//    protected void onStart() {
//        super.onStart();
//        Log.i("MAIN", "onStart: main");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i("MAIN", "onResume: main");
//    }
}