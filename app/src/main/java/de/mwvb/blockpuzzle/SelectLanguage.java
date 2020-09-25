package de.mwvb.blockpuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectLanguage extends AppCompatActivity {

    Context context;Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Button button_english = findViewById(R.id.english);
        Button button_german = findViewById(R.id.german);
        Button button_greek = findViewById(R.id.greek);

        context = LocaleService.setLocale(SelectLanguage.this,"en");
        resources = context.getResources();



        button_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocaleService.setLocale(SelectLanguage.this,"en");
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });

        button_german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocaleService.setLocale(SelectLanguage.this,"ger");
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });

        button_greek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocaleService.setLocale(SelectLanguage.this,"el");
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });



    }

}