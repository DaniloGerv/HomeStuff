package com.univpm.homestuff.repositories;

import com.google.firebase.firestore.DocumentSnapshot;
import com.univpm.homestuff.callbacks.DataCallBack;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.interfaces.IRepository;
import com.univpm.homestuff.services.FirebaseContext;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IRepository<User> {
    private static FirebaseContext dbContext=new FirebaseContext();
    private ArrayList<User> data ;
    private String collection="users";

    public UserRepository()
    {
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

}
