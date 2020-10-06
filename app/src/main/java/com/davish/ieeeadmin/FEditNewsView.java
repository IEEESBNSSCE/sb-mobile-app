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


public class FEditNewsView extends Fragment {

    ListView mNewsListView;

    ProgressDialog progressDialog;
    //Firebase
    private FirebaseFirestore db;

    //Adapter
    private NewsAdapter mNewsAdapter;
    private ArrayList<Events> mNewsList;
    public FEditNewsView() {
        // Required empty public constructor
    }

    public static FEditNewsView newInstance(String param1, String param2) {
        FEditNewsView fragment = new FEditNewsView();
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

        mNewsListView = view.findViewById(R.id.missionList);


        //get Database
        db = FirebaseFirestore.getInstance();
        //Set up the ArrayList
        mNewsList = new ArrayList<Events>();
        //set the Adapter
        mNewsAdapter = new NewsAdapter(getActivity(), mNewsList);

        mNewsListView.setAdapter(mNewsAdapter);

        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data.\nConnect to your Internet");

        // Showing progress dialog.
        progressDialog.show();

        final List<Events> mMissionsList = new ArrayList<>();
        db.collection("Blogs")
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
                        NewsAdapter mMissionAdapter = new NewsAdapter(getActivity(), mMissionsList);
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
        mNewsAdapter.addAll(mNewsList);
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
