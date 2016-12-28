package br.unb.unbsolidaria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.unb.unbsolidaria.persistence.DBHandler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onResume(){
        super.onResume();
        //This triggers on app load and after user logout (sign-in screen is destroyed after login)

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

        //Load local database contents
        DBHandler.setUp(getApplicationContext());
    }
}
