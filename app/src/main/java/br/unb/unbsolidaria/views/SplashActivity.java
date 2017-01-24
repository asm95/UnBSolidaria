package br.unb.unbsolidaria.views;

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

        //DBHandler is extremely slow at creating the local BD
        //Maybe it is because of DBMocker
        //Anyway, this will be removed soon anyway
        /*//Load local database contents
        DBHandler.setUp(getApplicationContext());*/
    }
}
