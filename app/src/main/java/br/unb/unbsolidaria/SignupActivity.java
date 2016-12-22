package br.unb.unbsolidaria;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import br.unb.unbsolidaria.entities.FormValidation;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.persistence.Database;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String SILENT_LOGIN = "br.unb.unbsolidaria.SILENTLOGIN";
    /**
     * The base_layout contains simply the spinner and the rest of formulary is added dynamically by
     * addView
     */
    private LinearLayout base_layout;
    private View orgForm;
    private View volForm;
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

    String name;
    String email;
    String password;
    String rPassword;
    String cep;

    private String cnpj;
    private String site;
    private String address;

    private String cpf;
    private String matricula;
    private String gender;

    private int lastSelectedItem = -1;

    private Database db_interface;

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

        Button btn_signup;
        btn_signup = (Button) volForm.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);
        btn_signup = (Button) orgForm.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _rPasswordText = (EditText) findViewById(R.id.input_retype_password);
        _addrText = (EditText) findViewById(R.id.input_address);
        _cepText = (EditText) findViewById(R.id.input_cep);

        _cepText.addTextChangedListener(new LoginTextWatcher(_cepText));

        db_interface = Database.getInstance();
    }

    public void SignUp() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.su_request_progress), true, false);
        // TODO: implement server communication
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        User usr_response = null;

                        switch (lastSelectedItem){
                            case 0:
                                usr_response = dbAddVoluntary();
                                break;
                            case 1:
                                usr_response = dbAddOrganization();
                                break;
                        }
                        if (usr_response != null) {
                            onSignupSuccess(usr_response);
                        }
                        else
                            onSignupFailed();

                        progressDialog.dismiss();
                        _signupButton.setEnabled(true);
                    }
                }, 3000);
    }

    private User dbAddOrganization() {
        User usr_request;
        Organization org_request;

        usr_request = new User(email, password, User.UserType.organization, db_interface.getUserCount()+1);
        org_request = new Organization(db_interface.getOpportunityCount()+1,
                cnpj, "", name, email, "", site, "", address, cep);

        db_interface.addUserHelper(usr_request);
        db_interface.addOrganizationHelper(org_request);

        return usr_request;
    }
    private User dbAddVoluntary() {
        User usr_request;
        Voluntary vol_request;

        usr_request = new User(email, password, User.UserType.voluntary, db_interface.getUserCount()+1);
        vol_request = new Voluntary(db_interface.getVoluntaryCount()+1,
                cpf, name, "", Calendar.getInstance(), email, "", "", matricula, address, gender, true);

        db_interface.addUserHelper(usr_request);
        db_interface.addVoluntaryHelper(vol_request);

        return usr_request;
    }

    public void onSignupSuccess(User usr) {
        String successText = "";

        if (lastSelectedItem == 0)
            successText = getString(R.string.su_vol_success);
        else if (lastSelectedItem == 1)
            successText = getString(R.string.su_org_success);
        Toast.makeText(getApplicationContext(),  successText, Toast.LENGTH_LONG).show();

        Intent signup_hook = new Intent(this, SignUpActivity.class);
        signup_hook.putExtra(this.SILENT_LOGIN, true);
        signup_hook.putExtra(SignInActivity.LOGIN_MESSAGE, usr);
        startActivity(signup_hook);

        finish();
    }

    public void onSignupFailed() {}

    public boolean validate() {
        boolean valid = true;

        String errorText = getString(R.string.su_error);
        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        rPassword = _rPasswordText.getText().toString();
        cep = _cepText.getText().toString();

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
                        base_layout.addView(volForm, 1);
                        _signupButton = (Button)volForm.findViewById(R.id.btn_signup);
                        curView = volForm;
                        break;
                    case 1:
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_signup:
                SignUp();
        }
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