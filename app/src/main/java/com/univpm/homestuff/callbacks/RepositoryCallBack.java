package com.univpm.homestuff.callbacks;


import java.util.ArrayList;

public interface RepositoryCallBack<T> {
    void onCallback(ArrayList<T> value);
}
