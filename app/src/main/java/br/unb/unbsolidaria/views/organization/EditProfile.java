package br.unb.unbsolidaria.views.organization;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.communication.UserService;
import br.unb.unbsolidaria.entities.FormValidation;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.entities.RetrofitResponse;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.persistence.DBHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Edit Profile Alpha 0.1
 * Status:
 * (asm95) Testing user profile update within the local SQL Database
 * Info:
 * Some profile information are missing at the User Interface because they'll be added in a
 * future version
 * Warning:
 * CNPJ is a field that cannot be changed (see entities.Organization), thus disabled at UI
 */

public class EditProfile extends Fragment {

    private Singleton singleton = Singleton.getInstance();

    private EditText et_username;
    private EditText et_first_name;
    private EditText et_last_name;
    private EditText et_phonenumber;
    private EditText et_cnpj;
    private EditText et_address;
    private EditText et_cep;
    private EditText et_description;
    private EditText et_email;

    private Button btn_send;
    private ScrollView sv_mainPane;

    private User user;

    public EditProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_org_edit_profile, container, false);

        et_username = (EditText) parentView.findViewById(R.id.edit_org_user_name);
        et_first_name = (EditText) parentView.findViewById(R.id.edit_org_input_first_name);
        et_last_name = (EditText) parentView.findViewById(R.id.edit_org_input_last_name);
        et_phonenumber = (EditText) parentView.findViewById(R.id.edit_org_input_phonenumber);
        et_cnpj = (EditText) parentView.findViewById(R.id.edit_org_input_cpf_cnpj);
        et_address = (EditText) parentView.findViewById(R.id.edit_org_input_address);
        et_cep = (EditText) parentView.findViewById(R.id.edit_org_input_cep);
        et_description = (EditText) parentView.findViewById(R.id.edit_org_input_description);
        et_email = (EditText) parentView.findViewById(R.id.edit_org_input_email);
        sv_mainPane = (ScrollView) parentView.findViewById(R.id.ep_org_scrollview);

        btn_send = (Button) parentView.findViewById(R.id.ep_btn_send);

        et_cnpj.setEnabled(false);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestSubmit();
            }
        });


        // SetUp user fields
        Bundle bundle = this.getArguments();
        if (bundle == null)
            return parentView;

        user = singleton.getUser();

        et_username.setText(user.getUsername());
        et_first_name.setText(user.getFirst_name());
        et_last_name.setText(user.getLast_name());
        et_phonenumber.setText(user.getTelefone());
        et_cnpj.setText(user.getCnpj());
        et_address.setText(user.getEndereco()); //TODO: SQLite has no entry for address
        et_cep.setText(user.getCep());
        et_description.setText(user.getDescricao());
        et_email.setText(user.getEmail());

        et_username.setEnabled(false);
        et_first_name.setEnabled(true);
        et_last_name.setEnabled(true);
        et_phonenumber.setEnabled(true);
        et_cnpj.setEnabled(false);
        et_address.setEnabled(true);
        et_cep.setEnabled(true);
        et_description.setEnabled(true);
        et_email.setEnabled(false);

        return parentView;
    }

    @Override
    public void onAttach (Context context){
        super.onAttach(context);

        /*
        try {
            onUpdateCallback = (UserProfileListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }*/
    }

    private void onRequestSubmit(){
        boolean valid = true;
        String usernameText = et_username.getText().toString();
        String first_nameText = et_first_name.getText().toString();
        String last_nameText = et_last_name.getText().toString();
        String phonenumberText = et_phonenumber.getText().toString();
        String adressText = et_address.getText().toString();
        String cepText = et_cep.getText().toString();
        String descriptionText = et_description.getText().toString();
        String emailText = et_email.getText().toString();

        btn_send.setEnabled(false);
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, getString(R.string.su_request_progress), true, false);

        // Input Validation
        /*if (!FormValidation.isValidName(usernameText, true)){
            valid = false;
        }

        if (!valid) {
            progressDialog.dismiss();
            Toast error_popup = Toast.makeText(getContext(), "Campos Inválidos", Toast.LENGTH_SHORT);
            error_popup.show();
            return;
        }*/

        User updateUser = new User(usernameText,first_nameText,last_nameText,emailText,phonenumberText,descriptionText,
                user.getTipo(),user.getSexo(),user.getCpf(),user.getCnpj(),cepText);

        UserService userService = RestCommunication.createService(UserService.class);
        Call<RetrofitResponse> call = userService.updateUser(updateUser);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                RetrofitResponse retrofitResponse = response.body();
                if(retrofitResponse.getResponse().equalsIgnoreCase("edited")){
                    Log.i("REST","EditOrgProfile deu bom");
                    btn_send.setEnabled(true);
                    progressDialog.dismiss();
                    sv_mainPane.requestFocus();
                }else {
                    Log.i("REST","EditOrgProfile deu ruim");
                    btn_send.setEnabled(true);
                    progressDialog.dismiss();
                    sv_mainPane.requestFocus();
                }
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                btn_send.setEnabled(true);
                progressDialog.dismiss();
                sv_mainPane.requestFocus();
            }
        });

    }

    public interface UserProfileListener {
        public void onOrganizationUpdate();
    }
}
