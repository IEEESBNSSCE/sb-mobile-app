package com.davish.ieeeadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FEditSocietyView extends Fragment {

    ListView mSocietyListView;

    ProgressDialog progressDialog;
    //Firebase
    private FirebaseFirestore db;

    //Adapter
    private SocietyAdapter mSocietyAdapter;
    private ArrayList<Society> mSocietyList;
    public FEditSocietyView() {
        // Required empty public constructor
    }

    public static FEditSocietyView newInstance(String param1, String param2) {
        FEditSocietyView fragment = new FEditSocietyView();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fedit_view_event, container, false);

        mSocietyListView = view.findViewById(R.id.missionList);


        //get Database
        db = FirebaseFirestore.getInstance();
        //Set up the ArrayList
        mSocietyList = new ArrayList<Society>();
        //set the Adapter
        mSocietyAdapter = new SocietyAdapter(getActivity(), mSocietyList);

        mSocietyListView.setAdapter(mSocietyAdapter);

        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data.\nConnect to your Internet");

        // Showing progress dialog.
        progressDialog.show();

        final List<Society> mMissionsList = new ArrayList<>();
        db.collection("Society")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Society miss = document.toObject(Society.class);
                        mMissionsList.add(miss);
                    }try {
                        progressDialog.dismiss();
                        ListView mMissionsListView = view.findViewById(R.id.missionList);
                        final SocietyAdapter mMissionAdapter = new SocietyAdapter(getActivity(), mMissionsList);
                        mMissionsListView.setAdapter(mMissionAdapter);
                        mMissionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                    long arg3) {
                                // TODO Auto-generated method stub

                                Log.d("############","Items " +  mMissionsList.get(arg2) );
                            }

                        });
                    }
                    catch (NullPointerException e){

                    }
                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });



       //add the whole Arraylist of Missions to the adapter
        mSocietyAdapter.addAll(mSocietyList);
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
