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
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FEditExcomView extends Fragment {

    ListView mExcomListView;

    ProgressDialog progressDialog;
    //Firebase
    private FirebaseFirestore db;

    //Adapter
    private ExcomAdapter mExcomAdapter;
    private ArrayList<Excom> mExcomList;
    public FEditExcomView() {
        // Required empty public constructor
    }

    public static FEditExcomView newInstance(String param1, String param2) {
        FEditExcomView fragment = new FEditExcomView();
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

        mExcomListView = view.findViewById(R.id.missionList);


        //get Database
        db = FirebaseFirestore.getInstance();
        //Set up the ArrayList
        mExcomList = new ArrayList<Excom>();
        //set the Adapter
        mExcomAdapter = new ExcomAdapter(getActivity(), mExcomList);

        mExcomListView.setAdapter(mExcomAdapter);

        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data.\nConnect to your Internet");

        // Showing progress dialog.
        progressDialog.show();

        final List<Excom> mMissionsList = new ArrayList<>();
        db.collection("Members").document("ExCom").collection("Members")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Excom miss = document.toObject(Excom.class);
                        mMissionsList.add(miss);
                    }try {
                        ListView mMissionsListView = view.findViewById(R.id.missionList);
                        ExcomAdapter mMissionAdapter = new ExcomAdapter(getActivity(), mMissionsList);
                        progressDialog.dismiss();
                        mMissionsListView.setAdapter(mMissionAdapter);
                    }
                    catch (NullPointerException e){

                    }
                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });



       //add the whole Arraylist of Missions to the adapter
        mExcomAdapter.addAll(mExcomList);
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
