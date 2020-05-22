package com.univpm.homestuff.services;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.univpm.homestuff.callbacks.DataCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public void readQueriedData(String collectionName, String fieldQuery,Object valueQuery,final DataCallBack myCallBack){
            try {
                context.collection(collectionName)
                        .whereEqualTo(fieldQuery,valueQuery)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    List<DocumentSnapshot> value=task.getResult().getDocuments();
                                    myCallBack.onCallback(value);
                                }
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public void readDoubleQueriedData(String collectionName, String fieldQuery,Object valueQuery,String fieldQuery2,Object valueQuery2,final DataCallBack myCallBack){
        try {
            context.collection(collectionName)
                    .whereEqualTo(fieldQuery,valueQuery)
                    .whereEqualTo(fieldQuery2,valueQuery2)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                List<DocumentSnapshot> value=task.getResult().getDocuments();
                                myCallBack.onCallback(value);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeData(String collectionName,Object data,final ResponseCallBack myCallBack)
    {
        try {
            context.collection(collectionName).add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
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

    public void writeData(String collectionName,String documentName,String nameData,Object data,final ResponseCallBack myCallBack)
    {
        try {
            context.collection(collectionName).document(documentName).collection(nameData).add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful())
                        myCallBack.onCallback(true);
                    else
                        myCallBack.onCallback(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            myCallBack.onCallback(false);
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

    public void updateData(String collectionName,String nameData,String field,Object data,final ResponseCallBack myCallBack)
    {
        Map<String,Object> editMap=new HashMap<>();

        editMap.put(field,data);
        try {
            context.collection(collectionName).document(nameData).update(editMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
