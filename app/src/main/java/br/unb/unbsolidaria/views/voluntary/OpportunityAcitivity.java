package br.unb.unbsolidaria.views.voluntary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.utils.ImageHelper;
import br.unb.unbsolidaria.views.organization.CreateOpportunity;
import br.unb.unbsolidaria.views.organization.EditOpportunity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;


public class OpportunityAcitivity extends AppCompatActivity {

    private float scale;
    private int width;
    private int height;

    private Voluntary loggedVoluntary;

    private Singleton singleton = Singleton.getInstance();

    private int mOptID;
    private int mOrgID;

    ImageView logo;
    TextView description;
    TextView org;
    TextView local;
    TextView vagas;
    TextView start;
    TextView end;

    Opportunity mOpportunityInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opportunity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.oportunidateToolbar_op);
        setSupportActionBar(toolbar);

        logo = (ImageView) findViewById(R.id.iv_op_logo);
        description = (TextView) findViewById(R.id.tv_op_description);
        org = (TextView) findViewById(R.id.tv_op_org);
        local = (TextView) findViewById(R.id.tv_op_local);
        vagas = (TextView) findViewById(R.id.tv_op_vaga);
        start = (TextView) findViewById(R.id.tv_op_start);
        end = (TextView) findViewById(R.id.tv_op_end);

        List<Opportunity> mList = singleton.getOpportunityList();
        int mOptID = getIntent().getIntExtra("id", 0); //- 1;

        Opportunity mOpportunityInfo = mList.get(mOptID);
        if (mOpportunityInfo == null){
            Log.e("ViewOpportunity", "Could not retrieve Opportunity ID (" + mOptID + ") from Singleton");
            setContentView(R.layout.activity_fatal_error);
            TextView errText = (TextView) findViewById(R.id.errText);
            errText.setText("Não foi possível obter dados desta oportunidade.");
            return;
        }

        mOrgID = -1;
        String orgText = mOpportunityInfo.getOrganizacao();
        if (orgText != null){
            Pattern pattern = Pattern.compile("(/users/)([0-9]*)");
            Matcher matcher = pattern.matcher(orgText);
            if (matcher.find()){
                mOrgID = Integer.parseInt(matcher.group(2));
                setOrganizationName(org, mOrgID);
            } else {
                org.setText( getString(R.string.ov_org, "") );
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            logo.setImageResource(mList.get(mOptID).getPhoto());
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), mList.get(mOptID).getPhoto());
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            bitmap = ImageHelper.getRoundedCornerBitmap(this, bitmap, 4, width, height, false, false, true, true);
            logo.setImageBitmap(bitmap);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mList.get(mOptID).getTitle());


        final Button joinButton = (Button) findViewById(R.id.oportunidadeActivity_actionConfirm);
        if ( mList.get(mOptID).getVagas() <= 0 )
            joinButton.setEnabled(false);
        final Button editButton = (Button) findViewById(R.id.oportunidadeActivity_actionEdit);


        updateOpportunityInfo(mOpportunityInfo);
        final User loggedUser = singleton.getUser();
        switch (loggedUser.getTipo()){
            case 0: //voluntary
                editButton.setVisibility(View.GONE); break;
            case 1: //organization
                joinButton.setVisibility(View.GONE);
                if (mOrgID != loggedUser.getId()){
                    editButton.setVisibility(View.GONE);
                } else {
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onOrganizationEditOpportunity();
                        }
                    });
                }
                break;
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Adicionar algum mecanismo para gerenciar eventos em que o usuário se candidatou (Activity, pop-up,...)
                String userUrl = Singleton.USERS_URL + loggedUser.getId() + "/";
                Snackbar.make(findViewById(android.R.id.content), "Participação Confirmada!", Snackbar.LENGTH_SHORT).show();
                return;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_opportunity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.oportunidade_share:
                Snackbar.make(findViewById(android.R.id.content), "Compartilhar Opportunity", Snackbar.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                return super.onOptionsItemSelected(item);
        }
    }

    void setOrganizationName(final TextView output, int orgID){
        OrganizationService orgHandler = RestCommunication.createService(OrganizationService.class);

        Call<User> userFetch = orgHandler.getOrganizationByID(orgID);
        userFetch.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User org = response.body();
                if (org != null){
                    Log.i("REST", "User response: " + response.toString());
                    String fn, ln;
                    fn = org.getFirst_name();
                    ln = org.getLast_name();
                    output.setText(getString(R.string.ov_org, fn + " " + ln));
                    ProgressBar progressStatus = (ProgressBar)findViewById(R.id.pb_orgNameProgressStatus);
                    if(progressStatus != null){
                        progressStatus.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                output.setText(getString(R.string.ov_undefinedField));
            }
        });
    }

    void updateOpportunityInfo(Opportunity mOpportunityInfo){
        description.setText( getString(R.string.ov_description, mOpportunityInfo.getDescription()) );

        local.setText( getString(R.string.ov_local, mOpportunityInfo.getLocal()) );
        if (mOpportunityInfo.getLocal() == null){
            local.setText(getString(R.string.ov_local, getString(R.string.ov_undefinedField)));
        }
        vagas.setText( getString(R.string.ov_vagas, String.valueOf(mOpportunityInfo.getVagas())) );

        start.setText( getString(R.string.ov_dateStart, mOpportunityInfo.getData_inicio()) );
        end.setText( getString(R.string.ov_dateEnd, mOpportunityInfo.getData_fim()) );
    }

    void onOrganizationEditOpportunity (){
        Bundle box = new Bundle();
        box.putBoolean("isEditing", true);
        box.putInt("mOptID", mOptID);

        //TODO: add functions to auto-select an fragment on organizationScreen
        Intent editOpportunity = new Intent (this, EditOpportunity.class);
        editOpportunity.putExtras(box);
        startActivityForResult(editOpportunity, 0, box);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            //Refresh OpportunityInfo
            mOpportunityInfo = singleton.getOpportunityList().get(mOptID);
            updateOpportunityInfo(mOpportunityInfo);
        }
    }
}

interface OrganizationService {
    @GET("users/{id}")
    Call<User> getOrganizationByID(@Path("id") int orgID);
}
