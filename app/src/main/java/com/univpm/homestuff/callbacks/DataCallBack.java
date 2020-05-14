package com.univpm.homestuff.callbacks;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface DataCallBack {
        void onCallback(List<DocumentSnapshot> value);
}
