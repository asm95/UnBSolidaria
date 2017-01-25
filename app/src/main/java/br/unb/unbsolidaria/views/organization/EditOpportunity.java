package br.unb.unbsolidaria.views.organization;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class EditOpportunity extends AppCompatActivity {
    Fragment coFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if the androidOS needs to recreate the Activity's process the coFragment will be still saved
        FragmentManager fm = getSupportFragmentManager();
        coFragment = fm.findFragmentByTag("coFragment");
        if (coFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            coFragment = new CreateOpportunity();
            coFragment.setArguments(getIntent().getExtras());
            ft.add(android.R.id.content,coFragment,"coFragment").commit();
        }
    }

    public void onEditSucess() {
        setResult(RESULT_OK, null);
        finish();
    }
}