package com.univpm.homestuff.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.univpm.homestuff.callbacks.DataCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;

import java.util.List;

public class FirebaseContext {

    private FirebaseFirestore context;

    public FirebaseContext()
    {
        context = FirebaseFirestore.getInstance();
    }



    public void readData(String collectionName, final DataCallBack myCallBack)
    {
        try {
            context.collection(collectionName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                List<DocumentSnapshot> value=task.getResult().getDocuments();
                                myCallBack.onCallback(value);
                            }else
                            {
                                //error handler
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeData(String collectionName,String nameData,Object data,final ResponseCallBack myCallBack)
    {
        try {
        context.collection(collectionName).document(nameData).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    myCallBack.onCallback(true);
                }else
                {
                    myCallBack.onCallback((false));
                    //error
                }
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
            myCallBack.onCallback(false);
        }

    }




}
