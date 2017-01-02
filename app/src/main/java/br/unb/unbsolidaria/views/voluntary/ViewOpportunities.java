package br.unb.unbsolidaria.views.voluntary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.communication.OpportunityService;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.utils.OpportunitiesAdapter;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.persistence.DBHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOpportunities extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static final String VIEW_MESSAGE = "br.unb.unbsolidaria.VIEWOPP";
    List<Opportunity> mList;

    User mUserProfile;
    DBHandler db_interface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_opportunities_list, container, false);
        OpportunityService opportunityService = RestCommunication.createService(OpportunityService.class);

        mRecyclerView = (RecyclerView) parentView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //DBHandler bd = DBHandler.getInstance();
        Call<List<Opportunity>> call = opportunityService.getOpportunities();

        call.enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(Call<List<Opportunity>> call, Response<List<Opportunity>> response) {
                mList = response.body();
            }

            @Override
            public void onFailure(Call<List<Opportunity>> call, Throwable t) {

            }
        });
        //mList = bd.getOpportunities();

        //db_interface = DBHandler.getInstance();

        Bundle box = this.getArguments();

        if (box == null){
            return parentView;
        }

        mUserProfile = (User)box.getSerializable(VoluntaryScreen.ENABLE_JOIN);
        mAdapter = new OpportunitiesAdapter(getActivity(), mList, db_interface.getVoluntary(mUserProfile.getId()));
        mRecyclerView.setAdapter(mAdapter);

        return parentView;
    }

}
