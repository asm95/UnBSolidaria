package br.unb.unbsolidaria.views.organization;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.OpportunityService;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.utils.OpportunitiesAdapter;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.persistence.DBHandler;
import br.unb.unbsolidaria.views.voluntary.VoluntaryScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by criss on 04/12/2016.
 */

public class ViewOpportunities extends Fragment {

    View parentView;

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<Opportunity> mList;

    User mUserProfile;

    private Singleton singleton = Singleton.getInstance();
    private DBHandler db_interface;


    public ViewOpportunities() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_opportunities_list, container, false);

        mRecyclerView = (RecyclerView) parentView.findViewById(R.id.my_recycler_oportunity_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Resources res_interface = getResources();

        db_interface = DBHandler.getInstance();
        if (res_interface.getBoolean(R.bool.enableLocalDB)){
            mList = db_interface.getOpportunities();

            mAdapter = new OpportunitiesAdapter(getActivity(), mList, null);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            updateListOpportunities();
        }

        return parentView;
    }


    //NOTE: copied from voluntary.viewOpportunities
    void updateListOpportunities(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),R.style.ProgessDialogTheme);

        OpportunityService opportunityService = RestCommunication.createService(OpportunityService.class);
        Call<List<Opportunity>> call = opportunityService.getOpportunities();
        call.enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(Call<List<Opportunity>> call, Response<List<Opportunity>> response) {
                Log.i("RESTAPI","Trabalhos response: "+response.body());
                progressDialog.dismiss();

                mList = response.body();
                singleton.setOpportunityList(mList);

                if (mList == null){
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.rest_server_connection_error), Snackbar.LENGTH_LONG).show();
                    mList = new LinkedList<>();
                }

                //TODO: discuss user model implementation
                //Box is used only in Voluntary application side because the join button in
                //OpportunityActivity should be hidden to Organization.
                //Bundle box = br.unb.unbsolidaria.views.organization.ViewOpportunities.this.getArguments();

                mAdapter = new OpportunitiesAdapter(getActivity(), mList, null);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Opportunity>> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progressDialog.show();
    }
}
