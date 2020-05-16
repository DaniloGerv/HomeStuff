package com.univpm.homestuff.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.LocationCallBack;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.repositories.UserRepository;
import com.univpm.homestuff.services.Geolocation;
import com.univpm.homestuff.utilities.Codes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentProfile extends Fragment implements View.OnClickListener {

    private Geolocation geoService;

    private UserRepository userRepository;
    private User user;

    private StorageReference storage,childStorage;
    private FirebaseUser currentUser;

    private ImageView proPic;
    private ImageButton geoButton;
    private TextView textEmail,textFirstName,textLastName,textGeo;

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
        userRepository=new UserRepository();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Getting user data from data base
        userRepository.getSingleData(currentUser.getUid(), new RepositoryCallBack<User>() {
             @Override
             public void onCallback(ArrayList<User> value) {
                 if (value.size()>0) {
                     user = value.get(0);
                     storage = FirebaseStorage.getInstance().getReference();
                     childStorage = storage.child("profileImages/"+user.getUID()+".png");

                     textEmail.setText(user.getEmail());
                     textFirstName.setText(user.getFirstName());
                     textLastName.setText(user.getLastName());
                     if (user.getPlace()!=null)
                     {
                         geoButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                     }else
                     {
                         geoButton.setBackgroundResource(R.drawable.ic_cancel_black_24dp);
                     }


                     //loading the profile image
                     if(user.getPhotoURL() != null ) {
                         try {
                             File proImage = File.createTempFile(user.getUID(), ".png");
                             if (!proImage.exists()) //Image is not into the device, it is needded to download it from the storage
                             {
                                 final File localFile = File.createTempFile(user.getUID(), ".png");
                                 childStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                     @Override
                                     public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                         Bitmap my_image;
                                         my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                         Glide.with(FragmentProfile.this)
                                                 .load(my_image)
                                                 .centerCrop()
                                                 .into(proPic);
                                         user.setPhotoURL(localFile.toURI().toString());
                                         userRepository.updateProfilePhotoURL(user);
                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                     }
                                 });

                             } else //The image is already on the device, it is not necessary to download it
                             {
                                 Glide.with(FragmentProfile.this)
                                         .load(user.getPhotoURL())
                                         .centerCrop()
                                         .into(proPic);
                             }

                         }catch (Exception e) {
                         }
                     }
                     else {//loading default profile image
                         loadDefaultImage();
                     }
                 }
                 else
                 {
                     Log.d("FUORI","AAA");
                     //no user
                 }
             }
         });

        // getActivity()..setTitle(R.string.profiloTitle);
        proPic = view.findViewById(R.id.propic);
        proPic.setClipToOutline(true);
        geoButton=view.findViewById(R.id.img_location);
        textEmail = view.findViewById(R.id.text_email_profile);
        textFirstName=view.findViewById(R.id.text_firstName_profile);
        textGeo=view.findViewById(R.id.text_geo);
        textGeo.setOnClickListener(this);
        textLastName=view.findViewById(R.id.text_lastName_profile);


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_geo:
                geoService=new Geolocation(getActivity());
                geoService.getLastLocation(getActivity(), new LocationCallBack() {
                    @Override
                    public void onCallbackLocation(Location l) {
                            user.setPlace(new com.univpm.homestuff.entities.Location(l.getLatitude(),l.getLongitude(),getActivity()));
                            userRepository.addData(user, new ResponseCallBack() {
                                @Override
                                public void onCallback(boolean value) {
                                    if (value) {
                                        Toast.makeText(getContext(), R.string.geoOk, Toast.LENGTH_SHORT).show();
                                        geoButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                                    } else {
                                        Toast.makeText(getContext(), R.string.geoError, Toast.LENGTH_SHORT).show();
                                        geoButton.setBackgroundResource(R.drawable.ic_cancel_black_24dp);
                                    }
                                }
                            });
                    }
                });
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode==Codes.PICK_IMAGE) {
            Bitmap bitmap = null;
            if(resultCode == RESULT_OK) {
                if(getPickImageResultUri(intent) != null) {
                   final Uri picUri = getPickImageResultUri(intent);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                        Glide.with(this)
                                .load(bitmap)
                                .centerCrop()
                                .into(proPic);

                       childStorage.delete(); //Deleting the old image
                        user.setPhotoURL(picUri.toString());
                        //Loading into the storage the new profile image
                        childStorage.putFile(picUri).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("UPLOAD","NO");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                userRepository.updateProfilePhotoURL(user);
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
                if(user.getPhotoURL() != null) {
                        Glide.with(FragmentProfile.this)
                                .load(user.getPhotoURL())
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
