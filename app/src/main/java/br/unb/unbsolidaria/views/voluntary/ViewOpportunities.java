package br.unb.unbsolidaria.views.voluntary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import br.unb.unbsolidaria.utils.OpportunitiesAdapter;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.persistence.DBHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOpportunities extends Fragment {

    private View parentView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog progressDialog;

    private Singleton singleton = Singleton.getInstance();


    public static final String VIEW_MESSAGE = "br.unb.unbsolidaria.VIEWOPP";
    List<Opportunity> mList;

    User mUserProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_opportunities_list, container, false);
        progressDialog = new ProgressDialog(getContext());

        updateListOpportunities();

        return parentView;
    }

    void updateListOpportunities(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Carregando...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OpportunityService opportunityService = RestCommunication.createService(OpportunityService.class);
        Call<List<Opportunity>> call = opportunityService.getOpportunities();
        call.enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(Call<List<Opportunity>> call, Response<List<Opportunity>> response) {
                Log.i("RESTAPI","Trabalhos response: "+response.body());
                progressDialog.dismiss();
                mRecyclerView = (RecyclerView) parentView.findViewById(R.id.my_recycler_oportunity_view);
                mRecyclerView.setHasFixedSize(true);

                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mList = response.body();
                singleton.setOpportunityList(mList);

                Bundle box = ViewOpportunities.this.getArguments();

                if (mList == null){
                    Snackbar.make(mRecyclerView, getString(R.string.rest_server_connection_error), Snackbar.LENGTH_LONG).show();
                    mList = new LinkedList<>();
                }

                if (box != null){
                    mUserProfile = (User)box.getSerializable(VoluntaryScreen.ENABLE_JOIN);
                    mAdapter = new OpportunitiesAdapter(getActivity(), mList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Opportunity>> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

}
