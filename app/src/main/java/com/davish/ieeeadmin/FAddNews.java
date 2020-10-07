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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class FAddNews extends Fragment implements View.OnClickListener{


    EditText content,date,title,tag;
    Button submit;
    Uri filePath;
    ImageView imageView;
    StorageReference ref;
    final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    String url,hash;
    FirebaseFirestore temp_db;

    public FAddNews() {
        // Required empty public constructor
    }

    public static FAddNews newInstance(String param1, String param2) {
        FAddNews fragment = new FAddNews();
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
        View view =  inflater.inflate(R.layout.fragment_add_news, container, false);
        imageView = view.findViewById(R.id.imgView);
        imageView.setOnClickListener(this);
        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        content = view.findViewById(R.id.conText);
        date = view.findViewById(R.id.dateText);
        title = view.findViewById(R.id.titleText);
        tag = view.findViewById(R.id.tagText);

        storage = FirebaseStorage.getInstance();


        return view;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.submit:
                if(content.getText().toString().length()!=0&& date.getText().toString().length()!=0&&
                        title.getText().toString().length()!=0&& tag.getText().toString().length()!=0&&
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

                    ref = storage.getReference().child("images/news/" + UUID.randomUUID().toString());
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
                                            user.put("Tag",tag.getText().toString());
                                            user.put("Title",title.getText().toString());
// Add a new document with a generated ID
                                            db.collection("Blogs")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("123", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            hash=documentReference.getId();
                                                            temp_db=FirebaseFirestore.getInstance();
                                                            Map<String, Object> favorite = new HashMap<>();
                                                            favorite.put("hash",hash);
                                                            temp_db.collection("Blogs").document(hash).set(favorite, SetOptions.merge());
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
                                            tag.setText("");

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

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
