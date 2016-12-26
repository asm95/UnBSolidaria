package br.unb.unbsolidaria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.unb.unbsolidaria.persistence.DBHandler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start SignInActivity
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

        //Load local database contents
        DBHandler.setUp(getApplicationContext());
    }

}
