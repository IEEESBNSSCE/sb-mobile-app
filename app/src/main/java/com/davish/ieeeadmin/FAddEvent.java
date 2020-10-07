package com.davish.ieeeadmin;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class FAddEvent extends Fragment implements View.OnClickListener {

    EditText content,date,title,venue;
    Button submit;
    ImageView imageView;
    String url;
    Uri filePath;
    String docname;
    final int PICK_IMAGE_REQUEST = 71;
    StorageReference ref;
    private FirebaseFirestore db,temp_db;
    String hash;
    FirebaseStorage storage;


    public FAddEvent() {
    }


    public static FAddEvent newInstance(String param1, String param2) {
        FAddEvent fragment = new FAddEvent();
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
        View view =  inflater.inflate(R.layout.fragment_add_event, container, false);
        //Initialize Views
        docname="123";
        imageView = view.findViewById(R.id.imgView);
        imageView.setOnClickListener(this);
        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        content = view.findViewById(R.id.conText);
        date = view.findViewById(R.id.dateText);
        title = view.findViewById(R.id.titleText);
        venue = view.findViewById(R.id.venueText);
        final Spinner spinner = view.findViewById(R.id.spinner);

        storage = FirebaseStorage.getInstance();

        db = FirebaseFirestore.getInstance();
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
                        //String [] values ={"IEEE SB NSSCE","CS","PES","IAS","PELS","WIE","RAS",};
                    try {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, socList);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        spinner.setAdapter(adapter);


                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                Object item = parent.getItemAtPosition(position);
                                docname = item.toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    catch (NullPointerException e ){

                    }
                    //ListView mMissionsListView = view.findViewById(R.id.missionList);
                    //EventAdapter mMissionAdapter = new EventAdapter(getActivity(), mMissionsList);
                    //mMissionsListView.setAdapter(mMissionAdapter);
                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });



        //Toast.makeText(getActivity(),content.getText().toString() , Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void onClick(View view)
    {

        switch (view.getId()) {
            case R.id.submit:
                if(content.getText().toString().length()!=0&& date.getText().toString().length()!=0&&
                title.getText().toString().length()!=0&& venue.getText().toString().length()!=0&&
                filePath != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();


                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    //here you can choose quality factor in third parameter(ex. i choosen 25)
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] fileInBytes = baos.toByteArray();

                    ref = storage.getReference().child("images/events/" + UUID.randomUUID().toString());
                    //here i am uploading
                    ref.putBytes(fileInBytes)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            url=uri.toString();
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            // Create a new user with a first and last name
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("Content",content.getText().toString() );
                                            user.put("Date",date.getText().toString());
                                            user.put("ImageUrl",url);
                                            user.put("Title",title.getText().toString());
                                            user.put("Venue",venue.getText().toString());
                                            user.put("Soc",docname);
// Add a new document with a generated ID
                                            db.collection("UpcomingEvents").document(docname).collection("Events")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("123", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            hash=documentReference.getId();
                                                            temp_db=FirebaseFirestore.getInstance();
                                                            Map<String, Object> favorite = new HashMap<>();
                                                            favorite.put("hash",hash);
                                                            temp_db.collection("UpcomingEvents").document(docname).collection("Events").document(hash).set(favorite, SetOptions.merge());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("123", "Error adding document", e);
                                                        }
                                                    });




                                            content.setText("");
                                            title.setText("");
                                            date.setText("");
                                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
                                            venue.setText("");

                                            //Handle whatever you're going to do with the URL here
                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            });



                }
                else if(filePath==null)
                    Toast.makeText(getActivity(), "Please add an image", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Please enter all details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imgView:
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;

                // Do something
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

      //  Toast.makeText(getActivity(),"4" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    //    Toast.makeText(getActivity(),"5" , Toast.LENGTH_SHORT).show();
    }

}
