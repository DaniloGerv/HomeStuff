package com.univpm.homestuff.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.univpm.homestuff.R;
import com.univpm.homestuff.utilities.Codes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentProfile extends Fragment {


    private FirebaseUser currentUser;

    private StorageReference storage,childStorage;
    private ImageView proPic;
    private TextView textEmail,textNome,textCognome;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();


  @Override
   public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
         currentUser = FirebaseAuth.getInstance().getCurrentUser();
         storage = FirebaseStorage.getInstance().getReference();
         childStorage = storage.child("profileImages/"+currentUser.getUid()+".png");

        // getActivity()..setTitle(R.string.profiloTitle);

        proPic = view.findViewById(R.id.propic);
        textEmail = view.findViewById(R.id.text_email_profile);
        textNome=view.findViewById(R.id.text_nome_profile);
        textCognome=view.findViewById(R.id.text_cognome_profile);
        proPic.setClipToOutline(true);

        textEmail.setText(currentUser.getEmail());

        //loading the profile image
        if(currentUser.getPhotoUrl() != null) {
                    Glide.with(FragmentProfile.this)
                            .load(currentUser.getPhotoUrl())
                            .centerCrop()
                            .into(proPic);
        }
        else {
           loadDefaultImage();
        }

        proPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest = findUnaskedPermissions(permissions);
                if(permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), Codes.ALL_PERMISSIONS_RESULT);
                } else {
                    startActivityForResult(getPickImageChooserIntent(), Codes.PICK_IMAGE);
                }
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode==Codes.PICK_IMAGE) {
            Bitmap bitmap = null;
            if(resultCode == RESULT_OK) {
                if(getPickImageResultUri(intent) != null) {
                    Uri picUri = getPickImageResultUri(intent);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                        Glide.with(this)
                                .load(bitmap)
                                .centerCrop()
                                .into(proPic);
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(picUri)
                                .build();
                        currentUser.updateProfile(profileChangeRequest);
                        childStorage.delete(); //Deleting the old image
                        //Loading into the storage the new profile image
                        childStorage.putFile(picUri).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("UPLOAD","NO");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("UPLOAD","OK");
                            }
                        });
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    bitmap = (Bitmap) intent.getExtras().get("data");
                }

            }else
            {
             //When the user leaves from the selection of the image
                if(currentUser.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(childStorage)
                            .centerCrop()
                            .into(proPic);
                }
                else {
                    loadDefaultImage();
                }
            }
        }
    }

    private void loadDefaultImage()
    {
        Glide.with(this)
                .load(getActivity().getDrawable(R.drawable.placeholder_profile))
                .centerCrop()
                .into(proPic);
    }

    private Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        return isCamera ? getCaptureImageOutputUri() : data.getData();

    }

    private Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if(outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for(ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size()-1);
        for(Intent intent : allIntents) {
            if(intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, getString(R.string.sorgenteImmagineProfilo));

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if(getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "propic.png"));
        }
        return outputFileUri;
    }

    private ArrayList findUnaskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for(String perm : wanted) {
            if(!(getActivity().checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) {
                result.add(perm);
            }
        }

        return result;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == Codes.ALL_PERMISSIONS_RESULT) {
            for(String perm: permissionsToRequest) {
                if(!(getActivity().checkSelfPermission(perm)==PackageManager.PERMISSION_GRANTED)) {
                    permissionsRejected.add(perm);
                }
            }
            if(permissionsRejected.size() > 0) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    Toast.makeText(getContext(),"Approva tutto", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                startActivityForResult(getPickImageChooserIntent(), Codes.PICK_IMAGE);
            }
        }
    }
}
