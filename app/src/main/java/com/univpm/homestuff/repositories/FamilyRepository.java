package com.univpm.homestuff.repositories;

import com.google.firebase.firestore.DocumentSnapshot;
import com.univpm.homestuff.callbacks.DataCallBack;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.entities.Family;
import com.univpm.homestuff.entities.Location;
import com.univpm.homestuff.interfaces.IRepository;
import com.univpm.homestuff.services.FirebaseContext;

import java.util.ArrayList;
import java.util.List;

public class FamilyRepository implements IRepository<Family> {

    private static FirebaseContext dbContext=new FirebaseContext();
    private ArrayList<Family> data ;
    private String collection="families";

    @Override
    public void getData(final RepositoryCallBack<Family> myCallBack) {
        dbContext.readData(collection, new DataCallBack() {
            @Override
            public void onCallback(List<DocumentSnapshot> value) {
                for (DocumentSnapshot document :value) {
                    data.add(document.toObject(Family.class));
                }
                myCallBack.onCallback(data);
            }
        });
    }

    @Override
    public void addData(Family data, final ResponseCallBack myCallBack) {
        dbContext.writeData(collection, data.getFID(),data,new ResponseCallBack() {
            @Override
            public void onCallback(boolean value) {
                myCallBack.onCallback(value);
            }
        });
    }

    @Override
    public void getSingleData(final String id,final RepositoryCallBack<Family> myCallBack) {

        dbContext.readQueriedData(collection, "fid", id, new DataCallBack() {
            @Override
            public void onCallback(List<DocumentSnapshot> value) {
                Family f=null;
                for (DocumentSnapshot document :value) {
                    if (id.equals(document.getId())) {
                        f= document.toObject(Family.class);
                        f.setFID(document.getId());
                    }
                }
                ArrayList<Family> toShare=new ArrayList<Family>();
                toShare.add(f);
                myCallBack.onCallback(toShare);
            }
        });

    }

    public void leaveUserFromFamily(final String fid,final ArrayList<String> members, final ResponseCallBack myCallBack)
    {
        dbContext.updateData(collection ,fid,"members",members, new ResponseCallBack() {
            @Override
            public void onCallback(boolean value) {
                if (myCallBack!=null)
                    myCallBack.onCallback(value);
            }
        });
    }


}
