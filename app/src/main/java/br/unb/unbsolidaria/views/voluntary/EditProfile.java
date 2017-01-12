package br.unb.unbsolidaria.views.voluntary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.communication.UserService;
import br.unb.unbsolidaria.entities.FormValidation;
import br.unb.unbsolidaria.entities.RetrofitResponse;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;
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
 * CPF, Email are fields that cannot be changed (see entities.Voluntary), thus disabled at UI
 */

public class EditProfile extends Fragment {

    private EditText et_first_name;
    private EditText et_last_name;
    private EditText et_cpf;
    private EditText et_matricula;
    private EditText et_address;
    private EditText et_cep;
    private EditText et_email;
    private EditText et_username;
    private EditText et_phonenumber;
    private Spinner et_sexo;
    private EditText et_description;

    private Singleton singleton = Singleton.getInstance();

    private Button btn_send;
    private ScrollView sv_mainPane;

    private User user;

    private UserProfileListener onUpdateCallback;

    public EditProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_vol_edit_profile, container, false);

        et_first_name = (EditText) parentView.findViewById(R.id.edit_vol_input_first_name);
        et_last_name = (EditText) parentView.findViewById(R.id.edit_vol_input_last_name);
        et_cpf = (EditText) parentView.findViewById(R.id.edit_vol_input_cpf);
        //et_matricula = (EditText) parentView.findViewById(R.id.input_matricula);
        et_address = (EditText) parentView.findViewById(R.id.edit_vol_input_address);
        et_email = (EditText) parentView.findViewById(R.id.edit_vol_input_email);
        sv_mainPane = (ScrollView) parentView.findViewById(R.id.ep_vol_scrollview);
        et_cep = (EditText) parentView.findViewById(R.id.edit_vol_input_cep);
        et_username = (EditText) parentView.findViewById(R.id.edit_vol_user_name);
        et_phonenumber = (EditText) parentView.findViewById(R.id.edit_vol_input_phonenumber);
        et_sexo = (Spinner) parentView.findViewById(R.id.su_edit_GenderOptions);
        et_description = (EditText) parentView.findViewById(R.id.edit_vol_input_description);

        btn_send = (Button) parentView.findViewById(R.id.ep_btn_send);

        et_cpf.setEnabled(false);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestSubmit();
            }
        });

        // Estabilish DB connection
        //db_interface = DBHandler.getInstance();

        // SetUp user fields
        Bundle bundle = this.getArguments();
        if (bundle == null)
            return parentView;

        user = singleton.getUser();

        /*try{
            mUserProfile = db_interface.getVoluntary(mLoggedUser.getId());
        } catch (IndexOutOfBoundsException e){
            return parentView;
        }*/

        et_username.setText(user.getUsername());
        et_first_name.setText(user.getFirst_name());
        et_last_name.setText(user.getLast_name());
        et_phonenumber.setText(user.getTelefone());
        et_cpf.setText(user.getCpf());
        //et_matricula.setText(mUserProfile.getUnbRegistrationNumber());
        //TODO: spinner sexo
        et_address.setText(user.getEndereco()); //TODO: SQLite has no entry for address
        et_cep.setText(user.getCep());
        et_description.setText(user.getDescricao());
        et_email.setText(user.getEmail());

        et_username.setEnabled(true);
        et_first_name.setEnabled(true);
        et_last_name.setEnabled(true);
        et_phonenumber.setEnabled(true);
        et_cpf.setEnabled(false);
        et_sexo.setEnabled(false);
        et_address.setEnabled(true);
        et_cep.setEnabled(true);
        et_description.setEnabled(true);
        et_email.setEnabled(true);

        return parentView;
    }

    @Override
    public void onAttach (Context context){
        super.onAttach(context);

        try {
            onUpdateCallback = (UserProfileListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private void onRequestSubmit(){
        boolean valid = true;
        String usernameText = et_username.getText().toString();
        String firstnameText = et_first_name.getText().toString();
        String lastnameText = et_last_name.getText().toString();
        String phonenumberText = et_phonenumber.getText().toString();
        String cpfText = et_cpf.getText().toString();
        String addressText = et_address.getText().toString();
        String cepText = et_cep.getText().toString();
        String descriptionText = et_description.getText().toString();
        String emailText = et_email.getText().toString();

        btn_send.setEnabled(false);
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, getString(R.string.su_request_progress), true, false);

        // Input Validation
        if (!FormValidation.isValidName(usernameText, false)){
            valid = false;
        }

        if (!valid) {
            Toast error_popup = Toast.makeText(getContext(), "Campos Inválidos", Toast.LENGTH_SHORT);
            error_popup.show();
            return;
        }

        User updateUser = new User(usernameText,firstnameText,lastnameText,emailText,phonenumberText,descriptionText,
                user.getTipo(),user.getSexo(),cpfText,user.getCnpj(),cepText);

        UserService userService = RestCommunication.createService(UserService.class);
        Call<RetrofitResponse> call = userService.updateUser(updateUser);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                RetrofitResponse retrofitResponse = response.body();
                if(retrofitResponse.getResponse().equalsIgnoreCase("edited")){
                    Log.i("REST","EditVolProfile deu bom");
                    btn_send.setEnabled(true);
                    progressDialog.dismiss();
                    onUpdateCallback.onVoluntaryUpdate();
                    sv_mainPane.requestFocus();
                }else {
                    Log.i("REST","EditVolProfile deu ruim");
                    btn_send.setEnabled(true);
                    progressDialog.dismiss();
                    onUpdateCallback.onVoluntaryUpdate();
                    sv_mainPane.requestFocus();
                }
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                btn_send.setEnabled(true);
                progressDialog.dismiss();
                onUpdateCallback.onVoluntaryUpdate();
                sv_mainPane.requestFocus();
            }
        });
    }

    public interface UserProfileListener {
        public void onVoluntaryUpdate();
    }
}
