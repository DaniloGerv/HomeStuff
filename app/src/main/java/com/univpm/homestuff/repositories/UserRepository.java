package com.univpm.homestuff.repositories;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.DataCallBack;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.entities.Location;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.interfaces.IRepository;
import com.univpm.homestuff.services.AlertService;
import com.univpm.homestuff.services.FirebaseContext;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IRepository<User> {
    private static FirebaseContext dbContext=new FirebaseContext();
    private ArrayList<User> data ;
    public static User currentUser;
    private String collection="users";
    private AlertService as;

    public UserRepository(Context ct)
    {
        as=new AlertService(ct);
        data=new ArrayList<User>();
    }


    @Override
    public void getData(final RepositoryCallBack<User> myCallBack) {
        dbContext.readData(collection, new DataCallBack() {
            @Override
            public void onCallback(List<DocumentSnapshot> value) {
                for (DocumentSnapshot document :value) {
                    data.add(document.toObject(User.class));
                    data.get(data.size()-1).setUID(document.getId());
                }
                myCallBack.onCallback(data);
            }
        });
    }

    @Override
    public void addData(User data, final ResponseCallBack myCallBack) {
        dbContext.writeData(collection, data.getUID(), data, new ResponseCallBack() {
            @Override
            public void onCallback(boolean value) {
                myCallBack.onCallback(value);
            }
        });
    }

    @Override
    public void getSingleData(final String uid,final RepositoryCallBack<User> myCallBack) {
        dbContext.readQueriedData(collection ,"uid",uid, new DataCallBack() {
            @Override
            public void onCallback(List<DocumentSnapshot> value) {
                User u=null;
                for (DocumentSnapshot document :value) {
                    if (uid.equals(document.getId())) {
                        u = document.toObject(User.class);
                        u.setUID(document.getId());
                    }
                }
                ArrayList<User> toShare=new ArrayList<User>();
                toShare.add(u);
                myCallBack.onCallback(toShare);
            }
        });
    }

    public void editFamilyId(final String uid,final String fid,final ResponseCallBack myCallBack)
    {
        dbContext.updateData(collection ,uid, "familyID", fid, new ResponseCallBack() {
            @Override
            public void onCallback(boolean value) {
                if (myCallBack!=null)
                myCallBack.onCallback(value);
            }
        });
    }

    public void getDataFromPlace(final Location place, final RepositoryCallBack<User> myCallBack) {
        dbContext.readDoubleQueriedData(collection ,"place",place,"familyID",null,new DataCallBack() {
            @Override
            public void onCallback(List<DocumentSnapshot> value) {
                ArrayList<User> toShare=new ArrayList<User>();
                for (DocumentSnapshot document :value) {
                        toShare.add(document.toObject(User.class));
                        toShare.get(toShare.size()-1).setUID(document.getId());

                }
                myCallBack.onCallback(toShare);
            }
        });
    }

    public void updateProfilePhotoURL(User u)
    {
        if (u.getPhotoURL()!=null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(u.getPhotoURL()))
                    .build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileChangeRequest);
            this.addData(u, new ResponseCallBack() {
                @Override
                public void onCallback(boolean value) {
                    if (!value) {
                        as.defaultErrorData();
                    }
                }
            });
        }
    }

    public void updateToken(String uid,String token,final ResponseCallBack myCallBack)
    {
        dbContext.updateData(collection, uid, "token", token, new ResponseCallBack() {
            @Override
            public void onCallback(boolean value) {
                if (myCallBack!=null)
                    myCallBack.onCallback(value);
            }
        });
    }

    public void updateProfile(final User u, final Fragment sender)
    {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(u.getFirstName()+" "+u.getLastName())
                .build();
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileChangeRequest);
        this.addData(u, new ResponseCallBack() {
            @Override
            public void onCallback(boolean value) {
                if (!value) {
                    as.defaultErrorData();
                }else
                {
                    as.successAlert(R.string.profiloTitolo,R.string.profiloAggiornato);

                }
            }
        });
    }

}
