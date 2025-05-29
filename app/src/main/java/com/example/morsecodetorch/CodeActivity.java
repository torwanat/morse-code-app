package com.example.morsecodetorch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CodeActivity extends AppCompatActivity {

    private final char[] letters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            ',', '.', '?', ' ' };

    private final String[] morse
            = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
            ".---", "-.-", ".-..", "--", "-.", "---", ".---.", "--.-", ".-.",
            "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
            "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.",
            "-----", "--..--", ".-.-.-", "..--..", "|" };

    private CameraManager cameraManager;
    private String cameraID;
    private String codeToFlash;
    private Boolean locked = false;
    private EditText mtInput;
    private Button btEncodeText;
    private Button btSignal;
    private ProgressBar pbProgress;
    private double progress = 0.0;
    private AtomicBoolean isThreadRunning = new AtomicBoolean(true);
    private Button btStopSignal;
    Thread signalThread;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

//        Log.i("CODE", "onCreate: code");

        btEncodeText = findViewById(R.id.btEncodeText);
        Button btClear = findViewById(R.id.btClearEncode);
        Button btPaste = findViewById(R.id.btPaste);
        btSignal = findViewById(R.id.btSignal);
        TextView tvResult = findViewById(R.id.tvResult);
        mtInput = findViewById(R.id.mtInput);
        pbProgress = findViewById(R.id.pbProgress);
        btStopSignal = findViewById(R.id.btStopSignal);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        btEncodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToEncode = mtInput.getText().toString().toLowerCase();
                if(!textToEncode.equals("")){
                    String result = encode(textToEncode);
                    tvResult.setText(result);
                }
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtInput.setText("");
            }
        });

        btPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = clipboard.getPrimaryClip();
                assert clip != null;
                String textFromClipboard = clip.getItemAt(0).getText().toString();
                mtInput.setText(textFromClipboard);
            }
        });

        tvResult.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", tvResult.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(CodeActivity.this, "Skopiowano wiadomość do schowka", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        btSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToEncode = mtInput.getText().toString().toLowerCase();
                if(!textToEncode.equals("")){
                    String result = encode(textToEncode);
                    if(!result.equals("")){
                        codeToFlash = result;
                        isThreadRunning.set(true);
                        signalThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            lockActivity();
                                        }
                                    });
                                    String[] chars = codeToFlash.split("");
                                    double unit = Math.ceil(100.0/(double) chars.length);
                                    for (String aChar : chars) {
                                        if(!isThreadRunning.get()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addProgress(0);
                                                }
                                            });
                                            progress = 0;
                                            return;
                                        }
                                        switch (aChar) {
                                            case ".":
                                                cameraManager.setTorchMode(cameraID, true);
                                                TimeUnit.MILLISECONDS.sleep(100);
                                                cameraManager.setTorchMode(cameraID, false);
                                                TimeUnit.MILLISECONDS.sleep(100);
                                                break;
                                            case "-":
                                                cameraManager.setTorchMode(cameraID, true);
                                                TimeUnit.MILLISECONDS.sleep(200);
                                                cameraManager.setTorchMode(cameraID, false);
                                                TimeUnit.MILLISECONDS.sleep(100);
                                                break;
                                            case " ":
                                                TimeUnit.MILLISECONDS.sleep(400);
                                                TimeUnit.MILLISECONDS.sleep(100);
                                                break;
                                            case "|":
                                                TimeUnit.MILLISECONDS.sleep(700);
                                                TimeUnit.MILLISECONDS.sleep(100);
                                                break;
                                            default:
                                                break;
                                        }
                                        progress += unit;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                addProgress(progress);
                                            }
                                        });
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addProgress(0);
                                            lockActivity();
                                        }
                                    });
                                    progress = 0;
                                    Log.i("TAG", "run: morb");
                                } catch (CameraAccessException | InterruptedException e) {
                                    Log.i("TAG", "run: exception");
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        Log.i("TAG", signalThread.getState().toString());
                        signalThread.start();
                    }
                }
            }
        });

        btStopSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isThreadRunning.set(false);
                lockActivity();
                try {
                    cameraManager.setTorchMode(cameraID, false);
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private String encode(String code){
        StringBuilder tmp = new StringBuilder();
        try {
            for (int i = 0; i < code.length(); i++) {
                for (int j = 0; j < letters.length; j++) {
                    if (code.charAt(i) == letters[j]) {
                        tmp.append(morse[j]);
                        tmp.append(" ");
                        break;
                    }
                }
            }
            return tmp.toString();
        }catch(Exception exception){
            Toast.makeText(this, "Wykryto błędne znaki", Toast.LENGTH_SHORT).show();
            return  "";
        }

    }

    private void lockActivity(){
        mtInput.setEnabled(locked);
        btEncodeText.setEnabled(locked);
        btSignal.setEnabled(locked);
        btStopSignal.setEnabled(!locked);
        locked = !locked;
    }

    private void addProgress(double progress){
        pbProgress.setProgress((int) Math.round(progress));
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i("CODE", "onStart: code");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i("CODE", "onResume: code");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i("CODE", "onPause: code");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i("CODE", "onStop: code");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.i("CODE", "onDestroy: code");
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        Log.i("CODE", "onSaveInstanceState: code");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.i("CODE", "onRestart: code");
//    }
}