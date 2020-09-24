package de.mwvb.blockpuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Locale;

import de.mwvb.blockpuzzle.logic.Game;

public class SelectLanguage extends AppCompatActivity {

    boolean lang_selected = true;
    Context context;
    Resources resources;
    final  String[] language = {"english","german"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Button button_english = findViewById(R.id.english);
        Button button_german = findViewById(R.id.german);

        context =LocaleHelper.setLocale(SelectLanguage.this,"en");
        resources = context.getResources();



        button_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context =LocaleHelper.setLocale(SelectLanguage.this,"en");
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });

        button_german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context =LocaleHelper.setLocale(SelectLanguage.this,"ger ");
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });




    }

}