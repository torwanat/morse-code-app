package com.example.morsecodetorch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class DecodeActivity extends AppCompatActivity {

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        Button btDecodeText = findViewById(R.id.btDecodeText);
        ImageButton ibDot = findViewById(R.id.ibDot);
        ImageButton ibPause = findViewById(R.id.ibPause);
        ImageButton ibSpace = findViewById(R.id.ibSpace);
        ImageButton ibClear = findViewById(R.id.ibClear);
        ImageButton ibPaste = findViewById(R.id.ibPaste);
        EditText mtInput = findViewById(R.id.mtInput);
        TextView tvDecodedText = findViewById(R.id.tvDecodedText);

        btDecodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mtInput.getText().toString();
                if(!code.equals("")){
                    String result = decode(code);
                    tvDecodedText.setText(result);
                }
            }
        });

        ibDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = mtInput.getText().toString();
                tmp += ".";
                mtInput.setText(tmp);
            }
        });

        ibPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = mtInput.getText().toString();
                tmp += "-";
                mtInput.setText(tmp);
            }
        });

        ibSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = mtInput.getText().toString();
                tmp += " ";
                mtInput.setText(tmp);
            }
        });

        ibClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtInput.setText("");
            }
        });

        ibPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = clipboard.getPrimaryClip();
                assert clip != null;
                String textFromClipboard = clip.getItemAt(0).getText().toString();
                mtInput.setText(textFromClipboard);
            }
        });

        tvDecodedText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", tvDecodedText.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DecodeActivity.this, "Skopiowano wiadomość do schowka", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private String decode(String code){
        String[] morseChars = code.split(" ");
        StringBuilder tmp = new StringBuilder();
        try{
            for (String morseChar : morseChars) {
                for (int j = 0; j < morse.length; j++) {
                    if (Objects.equals(morseChar, morse[j])) {
                        tmp.append(letters[j]);
                        break;
                    }
                }
            }
            return tmp.toString();
        }catch(Exception exception) {
            Toast.makeText(this, "Wykryto błędne znaki", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
}