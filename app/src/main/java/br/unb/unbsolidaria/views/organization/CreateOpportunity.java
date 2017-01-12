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
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.regex.Pattern;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.OpportunityService;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.Opportunity;
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

    private RelativeLayout rlForm;

    // form checking related
    Pattern isTitleMinWords = Pattern.compile("^\\s*\\S+(?:\\s+\\S+){1,}\\s*$");

    private DBHandler dbInterface;
    private OrganizationScreen parentInterface;
    private int mFormDialogAction = -1;

    public CreateOpportunity() {
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        referenceCalendar = Calendar.getInstance();
        int currentYear = referenceCalendar.get(Calendar.YEAR);
        int currentMonth = referenceCalendar.get(Calendar.MONTH);
        int currentDay = referenceCalendar.get(Calendar.DAY_OF_MONTH);

        // All of this just because setOnDateSetListener is only supported on API 24
        startDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etStartDate.setText(getString(R.string.co_tDateFormat, dayOfMonth, month+1, year));
            }
        }, currentYear, currentMonth, currentDay);

        endDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etEndDate.setText(getString(R.string.co_tDateFormat, dayOfMonth, month+1, year));
                if (etDescription.getText().toString().equals(""))
                    etDescription.requestFocus();
                else
                    rlForm.requestFocus();
            }
        }, currentYear, currentMonth, currentDay);

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

        btSend = (Button)parentView.findViewById(R.id.co_btSend);
        btSend.setOnClickListener(this);

        rlForm = (RelativeLayout) parentView.findViewById(R.id.content_create_opportunity);

        dbInterface = DBHandler.getInstance();
        if(dbInterface == null) {
            setUpFormDialog("Banco de dados se encontra offline. Tente novamente mais tarde.");
            parentInterface.restart();
        }

        parentInterface = (OrganizationScreen)getActivity();

        return parentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.co_etDateStart:
                //startDatePicker.show();
                break;
            case R.id.co_etDateEnd:
                //endDatePicker.show();
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
        int spots = Integer.parseInt(etSpots.getText().toString());
        String description = etDescription.getText().toString();

        // handle possible mistakes at input
        if (title.length() < minTitleLength)
            error_msg = "Título muito curto (Mínimo "+minTitleLength+" caracteres)";
        if ( !titleCheckFormat(etTitle.getText()) )
            error_msg = "Título não atende aos padrões";

        if (spots <= 0)
            error_msg = "Número de participantes permitidos inválido.";

        if (description.length() < minDescLength || description.length() > maxDescLength)
            error_msg = "Tamanho da Descrição fora do intervalo permitido: " + minDescLength + " - " + maxDescLength + " caracters";

        //TODO: verificar seleção de data

        if(error_msg != null){
            mFormDialogAction = 0;
            setUpFormDialog(error_msg);
            return;
        }

        //proceed with opportunity creation with Database
        //String local = etLocal.getText().toString();
        String startDate = etStartDate.getText().toString();
        String endDate = etEndDate.getText().toString();
        String email = etEmail.getText().toString();
        String autor = etAutor.getText().toString();

        /*Opportunity deploy = new Opportunity(dbInterface.getOpportunityCount()+1, local, spots,
                title, description, DBHandler.getCalendar(startDate), DBHandler.getCalendar(endDate),
                parentInterface.getUserProfile());

        boolean db_sucess = dbInterface.addOpportunity(deploy);
        if (!db_sucess){
            setUpFormDialog("Ocorreu um erro na comunicação com o Banco de Dados. Tente novamente mais tarde.");
            parentInterface.restart();
        }*/

        String organizacaoID = Singleton.url + singleton.getUser().getId()+"/";
        OpportunityService opportunityService = RestCommunication.createService(OpportunityService.class);
        Call<Opportunity> call = opportunityService.postOpportunities(
                new Opportunity(title,description,autor,email,spots,startDate,endDate,organizacaoID));
        Log.i("Opportunity","titulo: "+title+" Descricao: "+description);
        Log.i("Opportunity","autor: "+autor+" email: "+email+" vagas: "+spots);
        Log.i("Opportunity","Data inicio: "+startDate+" Data fim: "+endDate);
        Log.i("Opportunity","organizatioID: "+organizacaoID);
        call.enqueue(new Callback<Opportunity>() {
            @Override
            public void onResponse(Call<Opportunity> call, Response<Opportunity> response) {
                Log.i("REST","Opportunity code: "+response.code()+" response: "+response.message());
                Opportunity opportunity = response.body();
                if(opportunity == null){
                    Log.i("REST","Opportunity response body: "+response.body());
                    Log.i("REST","Opportnity response: "+response);
                }else{
                    Log.i("REST","Opportunity deu bom");
                }
            }

            @Override
            public void onFailure(Call<Opportunity> call, Throwable t) {

            }
        });

        //handle sucess feedback to user
        setUpFormDialog("Oportunidade publicada com sucesso.");
        //TODO: show additional actions menu like sharing into the facebook
        parentInterface.restart();
    }

    private boolean titleCheckFormat(Editable text) {
        return isTitleMinWords.matcher(text).find();
    }

    private void setUpFormDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
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