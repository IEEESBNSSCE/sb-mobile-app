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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;



public class FEditEventView extends Fragment {

    ListView mMissionsListView;

    ProgressDialog progressDialog;
    List<String> list;
    //Firebase
    private FirebaseFirestore db;

    //Adapter
    private EventAdapter mEventAdapter;
    private ArrayList<Events> mEventList;
    public FEditEventView() {
        // Required empty public constructor
    }

    public static FEditEventView newInstance(String param1, String param2) {
        FEditEventView fragment = new FEditEventView();
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

        mMissionsListView = view.findViewById(R.id.missionList);


        //get Database
        db = FirebaseFirestore.getInstance();
        //Set up the ArrayList
        mEventList = new ArrayList<Events>();
        //set the Adapter
        mEventAdapter = new EventAdapter(getActivity(), mEventList);

        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data.\nConnect to your Internet");

        // Showing progress dialog.
        progressDialog.show();
        mMissionsListView.setAdapter(mEventAdapter);

        db.collection("Society")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> socList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Society miss = document.toObject(Society.class);
                        //Toast.makeText(getActivity(), miss.getName(), Toast.LENGTH_SHORT).show();
                        socList.add(miss.getName());
                    }
                    socList.add("IEEE SB NSSCE");
                    //Toast.makeText(getActivity(), socList.toString(), Toast.LENGTH_SHORT).show();
                    final  List<Events> mMissionsList = new ArrayList<>();
                    for (final String i : socList) {
                        db.collection("UpcomingEvents").document(i).collection("Events")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Events miss = document.toObject(Events.class);
                                        mMissionsList.add(miss);
                                    }try {
                                        progressDialog.dismiss();
                                        ListView mMissionsListView = view.findViewById(R.id.missionList);
                                        EventAdapter mMissionAdapter = new EventAdapter(getActivity(), mMissionsList);
                                        mMissionsListView.setAdapter(mMissionAdapter);
                                    }
                                    catch (NullPointerException e){

                                    }
                                } else {
                                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    }
                } else {

                }
            }
        });


       //add the whole Arraylist of Missions to the adapter
        mEventAdapter.addAll(mEventList);
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
