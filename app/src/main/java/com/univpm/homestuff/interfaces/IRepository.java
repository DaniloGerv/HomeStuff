package com.univpm.homestuff.interfaces;

import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;

public interface IRepository<T> {
    public void getData(final RepositoryCallBack<T> myCallBack);
    public void addData(T data, final ResponseCallBack myCallBack);

}
