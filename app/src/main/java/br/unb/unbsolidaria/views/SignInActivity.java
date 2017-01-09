package br.unb.unbsolidaria.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.communication.UserService;
import br.unb.unbsolidaria.communication.VoluntaryService;
import br.unb.unbsolidaria.entities.FormValidation;
import br.unb.unbsolidaria.entities.Login;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.views.organization.OrganizationScreen;
import br.unb.unbsolidaria.persistence.DBHandler;
import br.unb.unbsolidaria.views.voluntary.VoluntaryScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignInActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;

    public static final String LOGIN_MESSAGE = "br.unb.unbsolidaria.LOADUSER";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getIntent().getBooleanExtra(SignUpActivity.SILENT_LOGIN, false) == true){
            onLoginSuccess((User)getIntent().getSerializableExtra(LOGIN_MESSAGE));
            return;
        }

        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login() {

        if (!validate()) {
            onLoginFailed(getString(R.string.error_wrong_fields));
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.process_autentication), true, false);

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        UserService userService = RestCommunication.createService(UserService.class);
        Call<User> call = userService.login(new Login(email,password,""));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i("REST","Login response: "+response.body());
                User user = response.body();
                if(user == null || user.getKey() == null || user.getKey().isEmpty()){
                    progressDialog.dismiss();
                    onLoginFailed(getString(R.string.error_wrong_credentials));
                }else{
                    user.setUsername(email);
                    UserService userService1 = RestCommunication.createService(UserService.class);
                    Call<User> call1 = userService1.getUser(user);
                    call1.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            String string = response.toString();
                            Log.i("REST","Login response: "+string);
                            User user1 = response.body();
                            if(user1 == null){
                                progressDialog.dismiss();
                            }else{
                                progressDialog.dismiss();
                                onLoginSuccess(user1);
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.i("REST","User error response: "+t);
                            onLoginFailed(getString(R.string.error_wrong_credentials));
                            progressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                onLoginFailed(getString(R.string.error_wrong_credentials));
                progressDialog.dismiss();
            }
        });

        /*Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: Implement REST OATH here
                // local user account DB (see .persistency.DBHandler)

                DBHandler instance;

                instance = DBHandler.getInstance();
                if (instance == null) {
                    onLoginFailed(getString(R.string.error_db_connection));
                    progressDialog.dismiss();
                    return;
                }

                User user = instance.getUserByCredentials(email, password);
                if (user == null){
                    onLoginFailed(getString(R.string.error_wrong_credentials));
                } else {
                    onLoginSuccess(user);
                }

                progressDialog.dismiss();
            }
        }, 1000);*/

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(User user) {
        Intent nextIntent;

        switch (user.getTipo()){
            case 1:
                nextIntent = new Intent(this, OrganizationScreen.class);
                break;
            case 0:
                nextIntent = new Intent(this, VoluntaryScreen.class);
                break;
            default:
                throw new RuntimeException("User type " + user.getType() + "  login handler does not exist");
        }
        nextIntent.putExtra(LOGIN_MESSAGE, user);
        startActivity(nextIntent);
        finish();
    }

    public void onLoginFailed(String Message) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_login_auth) + " " + Message, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        /*if (email.isEmpty()) {
            _emailText.setError(getString(R.string.error_field_required));
            valid = false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.error_invalid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }*/

        if (password.isEmpty()) {
            _passwordText.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else if (password.length() < FormValidation.PASS_MIN_LEN){
            _passwordText.setError(getString(R.string.error_short_password));
        }
        else if (password.length() > FormValidation.PASS_MAX_LEN){
            _passwordText.setError(getString(R.string.error_long_password));
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}

