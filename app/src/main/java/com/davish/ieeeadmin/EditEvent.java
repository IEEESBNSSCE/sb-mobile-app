package com.davish.ieeeadmin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditEvent extends AppCompatActivity implements View.OnClickListener{
    EditText content,date,title,venue;
    Button submit,existing;
    TextView society;
    ImageView imageView;
    String url;
    Uri filePath;
    String docname;
    final int PICK_IMAGE_REQUEST = 71;
    StorageReference ref;
    private FirebaseFirestore db,temp_db;
    String hash,soc,imgurl,currenthash;
    FirebaseStorage storage;
    int click=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        docname="123";
        imageView = findViewById(R.id.imgView);
        imageView.setOnClickListener(this);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        final ProgressDialog progressDialog = new ProgressDialog(EditEvent.this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        currenthash = extras.getString("Hash");
        soc = extras.getString("Soc");
        content = findViewById(R.id.conText);
        date = findViewById(R.id.dateText);
        society = findViewById(R.id.socText);
        title = findViewById(R.id.titleText);
        venue = findViewById(R.id.venueText);
        existing=findViewById(R.id.imgext);
        existing.setOnClickListener(this);
        final Spinner spinner = findViewById(R.id.spinner);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("UpcomingEvents").document(soc).collection("Events").document(currenthash);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       content.setText(document.getString("Content"));
                       date.setText(document.getString("Date"));
                       title.setText(document.getString("Title"));
                       venue.setText(document.getString("Venue"));
                       society.setText(soc);
                       imgurl= document.getString("ImageUrl");
                        Bitmap bmImg = null;
                        URL myfileurl = null;
                        try {
                            myfileurl = new URL(imgurl);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                            StrictMode.setThreadPolicy(policy);
                            HttpURLConnection conn = (HttpURLConnection) myfileurl.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            int length = conn.getContentLength();
                            if (length > 0) {
                                int[] bitmapData = new int[length];
                                byte[] bitmapData2 = new byte[length];
                                InputStream is = conn.getInputStream();
                                bmImg = BitmapFactory.decodeStream(is);
                                imageView.setImageBitmap(bmImg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();


                    } else {
                        Log.d("123", "No such document");
                    }
                } else {
                    Log.d("123", "get failed with ", task.getException());
                }
            }
        });

        FloatingActionButton myFab = (FloatingActionButton) this.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        EditEvent.this);

// Setting Dialog Title
                alertDialog2.setTitle("Delete Event");

// Setting Dialog Message
                alertDialog2.setMessage("Are you sure you want delete?");

// Setting Icon to Dialog

// Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                db.collection("UpcomingEvents").document(soc).collection("Events").document(currenthash)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent I = new Intent(EditEvent.this, EditViewActivity.class);
                                                startActivity(I);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("123", "Error deleting document", e);
                                            }
                                        });

                            }
                        });

// Setting Negative "NO" Btn
                alertDialog2.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog

                                dialog.cancel();
                            }
                        });

// Showing Alert Dialog
                alertDialog2.show();
            }
        });


/*        db = FirebaseFirestore.getInstance();
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
                    //String [] values ={"IEEE SB NSSCE","CS","PES","IAS","PELS","WIE","RAS",};
                    try {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditEvent.this, android.R.layout.simple_spinner_item, socList);
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
        });*/




    }
    @Override
    public void onClick(View view)
    {

        switch (view.getId()) {
            case R.id.submit:
                if(click==1) {
                    if (content.getText().toString().length() != 0 && date.getText().toString().length() != 0 &&
                            title.getText().toString().length() != 0 && venue.getText().toString().length() != 0 &&
                            filePath != null) {
                        final ProgressDialog progressDialog = new ProgressDialog(EditEvent.this);
                        progressDialog.setTitle("Loading...");
                        progressDialog.show();

                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
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
                                                url = uri.toString();
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                // Create a new user with a first and last name
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("Content", content.getText().toString());
                                                user.put("Date", date.getText().toString());
                                                user.put("ImageUrl", url);
                                                user.put("Title", title.getText().toString());
                                                user.put("Venue", venue.getText().toString());
// Add a new document with a generated ID
                                                db.collection("UpcomingEvents").document(soc).collection("Events").document(currenthash)
                                                        .update(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("123", "DocumentSnapshot successfully updated!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("123", "Error updating document", e);
                                                            }
                                                        });
                                                    /*.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("123", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            hash=documentReference.getId();
                                                            temp_db=FirebaseFirestore.getInstance();
                                                            Map<String, Object> favorite = new HashMap<>();
                                                            favorite.put("hash",hash);
                                                            temp_db.collection("Society").document(hash).set(favorite, SetOptions.merge());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("123", "Error adding document", e);
                                                        }
                                                    });*/


                                                Intent i = new Intent(EditEvent.this, EditViewActivity.class);
                                                startActivity(i);

                                                //Handle whatever you're going to do with the URL here
                                            }
                                        });


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditEvent.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                .getTotalByteCount());
                                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                    }
                                });


                    }
                    else if(filePath==null)
                        Toast.makeText(EditEvent.this, "Please add an image", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(EditEvent.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }

                else if(click==0){
                    if (content.getText().toString().length() != 0 && date.getText().toString().length() != 0 &&
                            title.getText().toString().length() != 0 && venue.getText().toString().length() != 0 ) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // Create a new user with a first and last name
                        Map<String, Object> user = new HashMap<>();
                        user.put("Content", content.getText().toString());
                        user.put("Date", date.getText().toString());
                        user.put("ImageUrl", imgurl);
                        user.put("Title", title.getText().toString());
                        user.put("Venue", venue.getText().toString());


// Add a new document with a generated ID
                        db.collection("UpcomingEvents").document(soc).collection("Events").document(currenthash)
                                .update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("123", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("123", "Error updating document", e);
                                    }
                                });
                                                     /*.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("123", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            hash=documentReference.getId();
                                                            temp_db=FirebaseFirestore.getInstance();
                                                            Map<String, Object> favorite = new HashMap<>();
                                                            favorite.put("hash",hash);
                                                            temp_db.collection("Society").document(hash).set(favorite, SetOptions.merge());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("123", "Error adding document", e);
                                                        }
                                                    });*/


                        Intent i = new Intent(EditEvent.this, EditViewActivity.class);
                        startActivity(i);
                    }
                    else
                        Toast.makeText(EditEvent.this, "Please enter all details", Toast.LENGTH_SHORT).show();

                }


                break;
            case R.id.imgView:
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;
            case R.id.imgext:
                if(click==0) {

                        imageView.setImageDrawable(ContextCompat.getDrawable(EditEvent.this, R.drawable.ic_add_a_photo_black_24dp));

                    existing.setText("Existing");
                    imageView.setClickable(true);
                    click++;
                }
                else{
                    ProgressDialog progressDialog = new ProgressDialog(EditEvent.this);
                    progressDialog.setTitle("Loading...");
                    progressDialog.show();
                    Bitmap bmImg = null;
                    URL myfileurl = null;
                    try {
                        myfileurl = new URL(imgurl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                        StrictMode.setThreadPolicy(policy);
                        HttpURLConnection conn = (HttpURLConnection) myfileurl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        int length = conn.getContentLength();
                        if (length > 0) {
                            int[] bitmapData = new int[length];
                            byte[] bitmapData2 = new byte[length];
                            InputStream is = conn.getInputStream();
                            bmImg = BitmapFactory.decodeStream(is);
                            imageView.setImageBitmap(bmImg);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    existing.setText("New");
                    imageView.setClickable(false);
                    click=0;
                }

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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditEvent.this.getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}
