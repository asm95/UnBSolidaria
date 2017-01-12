package br.unb.unbsolidaria.views.voluntary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.entities.FormValidation;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.persistence.DBHandler;

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

    private EditText et_name;
    private EditText et_cpf;
    private EditText et_matricula;
    private EditText et_address;
    private EditText et_;
    private EditText et_email;

    private Button btn_send;
    private ScrollView sv_mainPane;

    private User mLoggedUser;
    private Voluntary mUserProfile;

    private DBHandler db_interface;

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

        et_name = (EditText) parentView.findViewById(R.id.input_name);
        et_cpf = (EditText) parentView.findViewById(R.id.input_cpf);
        et_matricula = (EditText) parentView.findViewById(R.id.input_matricula);
        et_address = (EditText) parentView.findViewById(R.id.input_address);
        et_email = (EditText) parentView.findViewById(R.id.input_email);
        sv_mainPane = (ScrollView) parentView.findViewById(R.id.ep_vol_scrollview);

        btn_send = (Button) parentView.findViewById(R.id.ep_btn_send);

        et_cpf.setEnabled(false);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestSubmit();
            }
        });

        // Estabilish DB connection
        db_interface = DBHandler.getInstance();

        // SetUp user fields
        Bundle bundle = this.getArguments();
        if (bundle == null)
            return parentView;

        mLoggedUser = (User) bundle.getSerializable("br.unb.unbsolidaria.LOGGEDUSER");

        try{
            mUserProfile = db_interface.getVoluntary(mLoggedUser.getId());
        } catch (IndexOutOfBoundsException e){
            return parentView;
        }

        et_name.setText(mUserProfile.getName());
        et_cpf.setText(mUserProfile.getCpf());
        et_matricula.setText(mUserProfile.getUnbRegistrationNumber());
        et_address.setText(mUserProfile.getAddress()); //TODO: SQLite has no entry for address
        et_email.setText(mUserProfile.getEmail());

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
        String nameText = et_name.getText().toString();
        String matriculaText = et_matricula.getText().toString();
        String addressText = et_address.getText().toString();
        String emailText = et_email.getText().toString();

        btn_send.setEnabled(false);
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, getString(R.string.su_request_progress), true, false);

        // Input Validation
        if (!FormValidation.isValidName(nameText, false)){
            valid = false;
        }

        if (!valid) {
            Toast error_popup = Toast.makeText(getContext(), "Campos Inválidos", Toast.LENGTH_SHORT);
            error_popup.show();
            return;
        }

        //TODO: Implement REST POST
        //TODO: Handle failed update attempt
        // Update Handler
        mUserProfile.setName(nameText);
        mUserProfile.setUnbRegistrationNumber(matriculaText);
        //mUserProfile.setAddress(addressText);
        mUserProfile.setEmail(emailText);

        String responseMessage;
        boolean requestSucess = db_interface.updateVoluntary(mUserProfile);
        if (requestSucess)
            responseMessage = getString(R.string.ep_submit_sucess);
        else
            responseMessage = getString(R.string.ep_submit_fail);

        Toast.makeText(getContext(), responseMessage, Toast.LENGTH_LONG).show();
        btn_send.setEnabled(true);
        progressDialog.dismiss();
        onUpdateCallback.onVoluntaryUpdate();
        sv_mainPane.requestFocus();
    }

    public interface UserProfileListener {
        public void onVoluntaryUpdate();
    }
}
