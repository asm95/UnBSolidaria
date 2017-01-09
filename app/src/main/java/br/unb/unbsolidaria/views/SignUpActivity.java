package br.unb.unbsolidaria.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.StringTokenizer;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.communication.UserService;
import br.unb.unbsolidaria.entities.FormValidation;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.entities.RetrofitResponse;
import br.unb.unbsolidaria.entities.Signup;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.persistence.DBHandler;
import br.unb.unbsolidaria.views.organization.OrganizationScreen;
import br.unb.unbsolidaria.views.voluntary.VoluntaryScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String SILENT_LOGIN = "br.unb.unbsolidaria.SILENTLOGIN";
    /**
     * The base_layout contains simply the spinner and the rest of formulary is added dynamically by
     * addView
     */
    private LinearLayout base_layout;
    private View orgForm;
    private View volForm;
    private EditText _usernameText;
    private EditText    _nameText;
    private EditText    _emailText;
    private EditText    _passwordText;
    private EditText    _rPasswordText;
    private Button      _signupButton;
    private TextView    _loginLink;
    private EditText    _addrText;
    private EditText    _cepText;
    private Spinner     _AccountTypeChooser;

    private EditText    _cpfcnpjText;
    private EditText    _websiteText;

    String username;
    String name;
    String email;
    String password;
    String rPassword;
    String cep;
    int tipo;

    private String cnpj;
    private String site;
    private String address;

    private String cpf;
    private String matricula;
    private String gender;

    private int lastSelectedItem = -1;

    private DBHandler db_interface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_base);

        base_layout = (LinearLayout) findViewById(R.id.su_baselayout);
        LayoutInflater inflater_service = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        orgForm = inflater_service.inflate(R.layout.activity_signup_ass, null);
        volForm = inflater_service.inflate(R.layout.activity_signup_vol, null);

        base_layout.addView(volForm, 1);
        EditText cpfText = (EditText) volForm.findViewById(R.id.input_cpf);
        EditText cpf_cnpjText = (EditText) orgForm.findViewById(R.id.input_cpf_cnpj);
        cpfText.addTextChangedListener(new LoginTextWatcher(cpfText));
        cpf_cnpjText.addTextChangedListener(new LoginTextWatcher(cpf_cnpjText));

        _AccountTypeChooser = (Spinner) findViewById(R.id.su_sAccountType);
        _AccountTypeChooser.setOnItemSelectedListener(this);

        Button btn_signup_org;
        Button btn_signup_vol;
        btn_signup_vol = (Button) volForm.findViewById(R.id.btn_signup);
        btn_signup_vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        btn_signup_org = (Button) orgForm.findViewById(R.id.btn_signup);
        btn_signup_org.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        _usernameText = (EditText) findViewById(R.id.user_name);
        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _rPasswordText = (EditText) findViewById(R.id.input_retype_password);
        _addrText = (EditText) findViewById(R.id.input_address);
        _cepText = (EditText) findViewById(R.id.input_cep);

        _cepText.addTextChangedListener(new LoginTextWatcher(_cepText));

        db_interface = DBHandler.getInstance();
    }

    public void signUp() {

        username = _usernameText.getText().toString();
        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        rPassword = _rPasswordText.getText().toString();
        cep = _cepText.getText().toString();

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.su_request_progress), true, false);
        // TODO: implement server communication

        UserService userService = RestCommunication.createService(UserService.class);
        final Signup signup = new Signup(username,email,password,rPassword);
        Call<User> call = userService.newUser(signup);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i("REST","Signup response: "+response.toString());
                final User user = response.body();
                if(user == null || user.getKey() == null || user.getKey().isEmpty()){
                    progressDialog.dismiss();
                    Log.i("REST","Signup deu ruim");
                    onSignupFailed();
                }else{
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setCnpj(cnpj);
                    user.setCpf(cpf);
                    user.setTipo(tipo);
                    user.setFirst_name(name);
                    user.setLast_name("lastname");

                    UserService userService1 = RestCommunication.createService(UserService.class);
                    Call<RetrofitResponse> call1 = userService1.setUser(user);
                    call1.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                            Log.i("REST","Signup response: "+response.toString());
                            progressDialog.dismiss();
                            RetrofitResponse retrofitResponse = response.body();
                            Log.i("REST","String response: "+retrofitResponse);
                            if(retrofitResponse.getResponse() != null && retrofitResponse.getResponse().equalsIgnoreCase("ok")){
                                onSignupSuccess(user);
                                Log.i("REST","Signup deu bom");
                            }else{
                                Log.i("REST","Signup deu ruim");
                                onSignupFailed();
                            }
                        }

                        @Override
                        public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.i("REST","Signup error response: "+t);
                            Log.i("REST","Signup deu ruim");
                            onSignupFailed();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("REST","Signup error response: "+t);
            }
        });
    }

    private User dbAddOrganization() {
        User usr_request;
        Organization org_request;

        usr_request = new User(email, password, User.UserType.organization, db_interface.getUserCount()+1);
        org_request = new Organization(db_interface.getOrganizationCount()+1,
                cnpj, "", name, email, "", site, "", address, cep);

        db_interface.addUser(usr_request);
        db_interface.addOrganization(org_request);

        return usr_request;
    }
    private User dbAddVoluntary() {
        User usr_request;
        Voluntary vol_request;

        usr_request = new User(email, password, User.UserType.voluntary, db_interface.getUserCount()+1);
        vol_request = new Voluntary(db_interface.getVoluntaryCount()+1,
                cpf, name, "", Calendar.getInstance(), email, "", "", matricula, address, gender, true);

        db_interface.addUser(usr_request);
        db_interface.addVoluntary(vol_request);

        return usr_request;
    }

    public void onSignupSuccess(User usr) {
        Intent nextIntent;

        switch (usr.getTipo()){
            case 1:
                nextIntent = new Intent(this, OrganizationScreen.class);
                break;
            case 0:
                nextIntent = new Intent(this, VoluntaryScreen.class);
                break;
            default:
                throw new RuntimeException("User type " + usr.getType() + "  login handler does not exist");
        }
        nextIntent.putExtra(SILENT_LOGIN, usr);
        startActivity(nextIntent);
        finish();
    }

    public void onSignupFailed() {}

    public boolean validate() {
        boolean valid = true;

        String errorText = getString(R.string.su_error);

        if (!FormValidation.isValidName(name, lastSelectedItem==1 )) { //quick-fix: if is organization, so permit digits also
            _nameText.setError("deve ter entre 3 e 20 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("endereço de email inválido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (!FormValidation.isValidCEP(cep)) {
            _cepText.setError("insira um CEP válido");
            valid = false;
        } else {
            _cepText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("deve ter entre 4 e 10 caracteres");
            valid = false;
        } else if (!rPassword.equals(password)) {
            _passwordText.setError("senha incompatível, tente novamente");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (lastSelectedItem == 0)
            valid = valid && validate_vol();
        else if (lastSelectedItem == 1)
            valid = valid && validate_ass();

        if (!valid) {
            Toast errorMessage = new Toast(getApplicationContext()).makeText(getApplicationContext(), errorText, Toast.LENGTH_SHORT);
            errorMessage.show();
        }

        return valid;
    }

    public boolean validate_ass(){
        boolean valid = true;

        _cpfcnpjText = (EditText) orgForm.findViewById(R.id.input_cpf_cnpj);
        _websiteText = (EditText) orgForm.findViewById(R.id.input_site);

        cnpj = _cpfcnpjText.getText().toString();
        site = _websiteText.getText().toString();
        address = ((EditText)findViewById(R.id.input_address)).toString();

        if (!FormValidation.isValidCNPJ(cnpj)) {
            _cpfcnpjText.setError("insira um CNPJ válido");
            valid = false;
        } else {
            _cpfcnpjText.setError(null);
        }

        if (!android.util.Patterns.WEB_URL.matcher(site).matches() || site.isEmpty()) {
            _websiteText.setError(null);
        } else {
            _websiteText.setError("insira um website válido");
            valid = false;
        }

        return valid;
    }

    public boolean validate_vol(){
        boolean valid = true;

        EditText cpfcpnjText = (EditText) volForm.findViewById(R.id.input_cpf);
        EditText matriculaText = (EditText) volForm.findViewById(R.id.input_matricula);
        EditText genderText = (EditText) volForm.findViewById(R.id.input_gender);

        cpf = cpfcpnjText.getText().toString();
        matricula = matriculaText.getText().toString();
        gender = genderText.getText().toString();

        if (!FormValidation.isValidCPF(cpf)) {
            cpfcpnjText.setError("insira um CPF válido");
            valid = false;
        } else {
            cpfcpnjText.setError(null);
        }

        if (!FormValidation.isValidMatricula(matricula)) {
            matriculaText.setError("matricula da UnB inválida");
            valid = false;
        } else {
            matriculaText.setError(null);
        }

        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.su_sAccountType:
                if (lastSelectedItem == position)
                    return;

                base_layout.removeViewAt(1);

                View curView;
                switch (position){
                    case 0:
                        tipo = 0;
                        base_layout.addView(volForm, 1);
                        _signupButton = (Button)volForm.findViewById(R.id.btn_signup);
                        curView = volForm;
                        break;
                    case 1:
                        tipo = 1;
                        base_layout.addView(orgForm, 1);
                        _signupButton = (Button)orgForm.findViewById(R.id.btn_signup);
                        curView = orgForm;
                        break;
                    default:
                        curView = volForm;
                }

                _nameText = (EditText) curView.findViewById(R.id.input_name);
                _emailText = (EditText) curView.findViewById(R.id.input_email);
                _passwordText = (EditText) curView.findViewById(R.id.input_password);
                _rPasswordText = (EditText) curView.findViewById(R.id.input_retype_password);
                _addrText = (EditText) curView.findViewById(R.id.input_address);
                _cepText = (EditText) curView.findViewById(R.id.input_cep);

                lastSelectedItem = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



class LoginTextWatcher implements TextWatcher {

    private View view;
    public LoginTextWatcher(View view) {
        this.view = view;
    }

    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        EditText model = (EditText) view;
        boolean valid = true;

        switch(view.getId()){
            case R.id.input_cpf:
                valid = FormValidation.isValidCPF(text);
                break;
            case R.id.input_cpf_cnpj:
                valid = FormValidation.isValidCPF(text) || FormValidation.isValidCNPJ(text);
                break;
            case R.id.input_cep:
                valid = FormValidation.isValidCEP(text);
                break;
        }

        if (valid)
            model.setTextColor(Color.BLACK);
        else
            model.setTextColor(Color.RED);
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
}
