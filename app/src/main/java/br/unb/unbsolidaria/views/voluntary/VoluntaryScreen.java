package br.unb.unbsolidaria.views.voluntary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.unb.unbsolidaria.views.SignInActivity;
import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.views.ViewNews;
import br.unb.unbsolidaria.views.organization.EditProfile;
import br.unb.unbsolidaria.persistence.DBHandler;

public class VoluntaryScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private int lastSelectedItem;
    private Toolbar mActivityToolbar;

    private User mLoggedUser;
    private Voluntary mUserProfile;
    private NavigationView mNavigationView;

    public static String ENABLE_JOIN = "br.unb.unbsolidaria.ENABLEJOIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_vol);
        mActivityToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActivityToolbar.setTitle("");
        setSupportActionBar(mActivityToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.vo_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mActivityToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.vo_nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        setUpUserProfile();

        lastSelectedItem = -1;
        MenuItem newsItem = mNavigationView.getMenu().getItem(1);
        newsItem.setChecked(true);
        onNavigationItemSelected(newsItem);
    }

    private void setUpUserProfile() {
        Intent thisIntent = getIntent();
        TextView nav_UserName;

        mLoggedUser = (User)thisIntent.getSerializableExtra(SignInActivity.LOGIN_MESSAGE);
        if (mLoggedUser == null)
            return;

        try{
            mUserProfile = DBHandler.getInstance().getVoluntary(mLoggedUser.getId());
        } catch (IndexOutOfBoundsException e){
            setUpUserProfileDialogError();
            return;
        }

        nav_UserName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.vo_navTitle);
        nav_UserName.setText(mUserProfile.getName());
        //TODO: set-up also picture (maybe it is in User class)
    }

    private void setUpUserProfileDialogError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Erro ao carregar informações de Perfil. Opções de Menu serão desativadas até Logout.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mLoggedUser = null;
                    }
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.vo_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.organization_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment userFragment;
        FragmentTransaction ft;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.vo_drawer_layout);

        if (mLoggedUser == null)
            return true;

        if (lastSelectedItem == id){
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        ft = fragmentManager.beginTransaction();
        userFragment = fragmentManager.findFragmentById(R.id.ch_frameLayout);
        if (userFragment != null){
            ft.remove(userFragment);
        }
        lastSelectedItem = id;

        if (id == R.id.volv_sbNewsItem) {
            userFragment = new ViewNews();
            ft.add(R.id.ch_frameLayout,userFragment).commit();
            Bundle bundle = new Bundle();
            userFragment.setArguments(bundle);
            bundle.putSerializable(ENABLE_JOIN,mLoggedUser);
            mActivityToolbar.setTitle("Novidades");
        } else if (id == R.id.volv_sbViewOpportunityItem) {
            userFragment = new ViewOpportunities();
            ft.add(R.id.ch_frameLayout, userFragment).commit();
            Bundle bundle = new Bundle();
            userFragment.setArguments(bundle);
            bundle.putSerializable(ENABLE_JOIN, mLoggedUser);
            mActivityToolbar.setTitle("Ver Oportunidades");

        } else if (id == R.id.orgv_sbEditProfileItem) {
            userFragment = new EditProfile();
            ft.add(R.id.ch_frameLayout, userFragment).commit();
            mActivityToolbar.setTitle("Editar Perfil");
        } else if (id == R.id.volv_sbExitItem) {
            exitHandler();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void exitHandler() {
        //DBHandler.getInstance().saveLocalState(getApplicationContext());
        finish();
    }

    public Voluntary getUserProfile(){
        return mUserProfile;
    }

    public void restart() {
        int homeFragmentID = 0;

        if (homeFragmentID == lastSelectedItem)
            lastSelectedItem = -1;

        MenuItem newsItem = mNavigationView.getMenu().getItem(homeFragmentID);
        newsItem.setChecked(true);
        onNavigationItemSelected(newsItem);
    }
}
