package com.univpm.homestuff.callbacks;

import android.location.Location;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface DataCallBack {
        void onCallback(List<DocumentSnapshot> value);
}
