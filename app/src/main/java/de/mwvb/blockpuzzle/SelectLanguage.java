package de.mwvb.blockpuzzle;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectLanguage extends AppCompatActivity {

    Context context;Resources resources;
=======
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

>>>>>>> ef5b411824affd9f776ec739ab362193ccd36197

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Button button_english = findViewById(R.id.english);
        Button button_german = findViewById(R.id.german);
<<<<<<< HEAD
        Button button_greek = findViewById(R.id.greek);

        context = LocaleService.setLocale(SelectLanguage.this,"en");
=======

        context =LocaleHelper.setLocale(SelectLanguage.this,"en");
>>>>>>> ef5b411824affd9f776ec739ab362193ccd36197
        resources = context.getResources();



        button_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
<<<<<<< HEAD
                context = LocaleService.setLocale(SelectLanguage.this,"en");
=======
                context =LocaleHelper.setLocale(SelectLanguage.this,"en");
>>>>>>> ef5b411824affd9f776ec739ab362193ccd36197
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });

        button_german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
<<<<<<< HEAD
                context = LocaleService.setLocale(SelectLanguage.this,"ger");
=======
                context =LocaleHelper.setLocale(SelectLanguage.this,"ger ");
>>>>>>> ef5b411824affd9f776ec739ab362193ccd36197
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });

<<<<<<< HEAD
        button_greek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocaleService.setLocale(SelectLanguage.this,"el");
                resources = context.getResources();
                Intent intent=new Intent(SelectLanguage.this, MainActivity.class);//open MainActivity.class
                startActivity(intent);
            }
        });
=======
>>>>>>> ef5b411824affd9f776ec739ab362193ccd36197



    }

}