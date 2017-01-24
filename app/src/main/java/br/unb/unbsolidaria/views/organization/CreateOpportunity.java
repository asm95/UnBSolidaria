package br.unb.unbsolidaria.views.organization;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.regex.Pattern;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.OpportunityService;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.persistence.DBHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOpportunity extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private Singleton singleton = Singleton.getInstance();

    private Calendar referenceCalendar;
    // each text field has its own date picker
    private DatePickerDialog startDatePicker;
    private DatePickerDialog endDatePicker;

    private EditText etTitle;
    private int minTitleLength;
    private EditText etLocal;
    private EditText etSpots;
    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etEmail;
    private EditText etAutor;

    private EditText etDescription;
    private int minDescLength;
    private int maxDescLength;

    private Button btSend;
    private LinearLayout llForm;

    // form checking related
    Pattern isTitleMinWords = Pattern.compile("^\\s*\\S+(?:\\s+\\S+){1,}\\s*$");

    private DBHandler dbInterface;
    private OrganizationScreen parentInterface;
    private int mFormDialogAction = -1;

    private long lminDate, lmaxDate;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        referenceCalendar = Calendar.getInstance();
        int currentYear = referenceCalendar.get(Calendar.YEAR);
        int currentMonth = referenceCalendar.get(Calendar.MONTH);
        int currentDay = referenceCalendar.get(Calendar.DAY_OF_MONTH);

        startDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etStartDate.setText(getString(R.string.co_tDateFormat, year, month+1, dayOfMonth));
                Calendar minDate = Calendar.getInstance();
                minDate.set(year, month, dayOfMonth);
                lminDate = minDate.getTimeInMillis();
                //endDatePicker.getDatePicker().setMinDate(lminDate);
            }
        }, currentYear, currentMonth, currentDay);

        endDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etEndDate.setText(getString(R.string.co_tDateFormat, year, month+1, dayOfMonth));
                Calendar maxDate = Calendar.getInstance();
                maxDate.set(year, month, dayOfMonth);
                lmaxDate = maxDate.getTimeInMillis();
                //startDatePicker.getDatePicker().setMaxDate(lmaxDate);
                if (etEmail.getText().toString().equals(""))
                    etEmail.requestFocus();
                else
                    llForm.requestFocus();
            }
        }, currentYear, currentMonth, currentDay);

        startDatePicker.getDatePicker().setMinDate(referenceCalendar.getTimeInMillis());
        endDatePicker.getDatePicker().setMinDate(referenceCalendar.getTimeInMillis());

        //load appConfiguration
        Resources res_interface = getResources();
        minDescLength = res_interface.getInteger(R.integer.co_minDescriptionLength);
        maxDescLength = res_interface.getInteger(R.integer.co_maxDescriptionLength);
        minTitleLength = res_interface.getInteger(R.integer.co_minTitleLength);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_create_opportunity, container, false);

        etStartDate = (EditText)parentView.findViewById(R.id.co_etDateStart);
        etStartDate.setOnClickListener(this);
        etStartDate.setOnFocusChangeListener(this);
        etEndDate = (EditText)parentView.findViewById(R.id.co_etDateEnd);
        etEndDate.setOnClickListener(this);
        etEndDate.setOnFocusChangeListener(this);
        etTitle = (EditText)parentView.findViewById(R.id.co_etTitle);
        etSpots = (EditText)parentView.findViewById(R.id.co_etSpots);
        etDescription = (EditText) parentView.findViewById(R.id.co_etDescription);
        etEmail = (EditText) parentView.findViewById(R.id.co_etEmail);
        etAutor = (EditText) parentView.findViewById(R.id.co_etAutor);

        User loggedUser = singleton.getUser();
        etAutor.setText(loggedUser.getFirst_name() + " " + loggedUser.getLast_name());
        etEmail.setText(loggedUser.getEmail());

        btSend = (Button)parentView.findViewById(R.id.co_btSend);
        btSend.setOnClickListener(this);

        llForm = (LinearLayout) parentView.findViewById(R.id.content_create_opportunity);

        parentInterface = (OrganizationScreen)getActivity();

        return parentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.co_etDateStart:
                startDatePicker.show();
                break;
            case R.id.co_etDateEnd:
                endDatePicker.show();
                break;
            case R.id.co_btSend:
                onSendClickHandler();
                break;
        }
    }

    private void onSendClickHandler() {
        String error_msg = null;
        btSend.setEnabled(false);

        String title = etTitle.getText().toString();
        String SpotsText = etSpots.getText().toString();
        String description = etDescription.getText().toString();

        int availableSpots;
        if (SpotsText != "")
            availableSpots = Integer.parseInt(SpotsText);
        else
            availableSpots = -1;

        // handle possible mistakes at input
        if (title.length() < minTitleLength)
            error_msg = getString(R.string.co_minTitleError, minTitleLength);
        /*if ( !titleCheckFormat(etTitle.getText()) )
            error_msg = getString(R.string.co_formatTitleError);*/

        if (availableSpots <= 0)
            error_msg = getString(R.string.co_spotsNumberError);

        if (description.length() < minDescLength || description.length() > maxDescLength)
            error_msg = getString(R.string.co_minDescError, minDescLength, maxDescLength);

        if (lminDate > lmaxDate)
            error_msg = getString(R.string.co_eventPeriodError);

        if(error_msg != null){
            mFormDialogAction = 0;
            setUpFormDialog(error_msg);
            return;
        }

        //String local = etLocal.getText().toString();
        String startDate = etStartDate.getText().toString();
        String endDate = etEndDate.getText().toString();
        String email = etEmail.getText().toString();
        String autor = etAutor.getText().toString();

        final String organizacaoID = Singleton.USERS_URL + singleton.getUser().getId()+"/";
        OpportunityService opportunityService = RestCommunication.createService(OpportunityService.class);
        Call<Opportunity> call = opportunityService.postOpportunities(
                new Opportunity(title,description,autor,email,availableSpots,startDate,endDate,organizacaoID));

        Log.i("Opportunity","titulo: "+title+" Descricao: "+description);
        Log.i("Opportunity","autor: "+autor+" email: "+email+" vagas: "+availableSpots);
        Log.i("Opportunity","Data inicio: "+startDate+" Data fim: "+endDate);
        Log.i("Opportunity","organizatioID: "+organizacaoID);
        call.enqueue(new Callback<Opportunity>() {
            @Override
            public void onResponse(Call<Opportunity> call, Response<Opportunity> response) {
                Log.i("REST","Opportunity code: " + response.code() + " response: "+response.message());
                Opportunity opportunity = response.body();
                if (opportunity != null){
                    onCreationSucess();
                    Log.i("REST","Opportunity " + opportunity.toString() + " Creation was approved by server.");
                } else {
                    Log.i("REST","Opportunity " + opportunity.toString() + " Creation has failed.");
                    onCreationFailure();
                }
            }

            @Override
            public void onFailure(Call<Opportunity> call, Throwable t) {

            }
        });
    }

    private void onCreationSucess(){
        setUpFormDialog(getString(R.string.co_publicationSucess));
        //TODO: show additional actions menu like sharing into the facebook
        parentInterface.restart();
    }

    private void onCreationFailure(){
        mFormDialogAction = 0;
        setUpFormDialog(getString(R.string.co_generalSendError));
    }

    private boolean titleCheckFormat(Editable text) {
        return isTitleMinWords.matcher(text).find();
    }

    private void setUpFormDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(getString(R.string.co_publicationSendError))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mFormDialogAction != -1){
                            switch (mFormDialogAction){
                                case 0:
                                    btSend.setEnabled(true); break;
                            }
                            mFormDialogAction = -1;
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus)
            return;

        switch (v.getId()) {
            case R.id.co_etDateStart:
                startDatePicker.show();
                break;
            case R.id.co_etDateEnd:
                endDatePicker.show();
                break;
        }
    }
}