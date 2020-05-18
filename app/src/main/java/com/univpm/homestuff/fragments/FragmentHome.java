package com.univpm.homestuff.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.entities.Family;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.repositories.FamilyRepository;
import com.univpm.homestuff.repositories.UserRepository;
import com.univpm.homestuff.utilities.FamilyMembersAdapter;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    private UserRepository userRepository=new UserRepository();
    private FamilyRepository familyRepository=new FamilyRepository();
    private User user;
    private Family family;
    private TabLayout tabs;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
        return view;


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
           userRepository.getSingleData(FirebaseAuth.getInstance().getUid(), new RepositoryCallBack<User>() {
               @Override
               public void onCallback(ArrayList<User> value) {
                   user=value.get(0);
                   if (user.getPlace()==null || user.getLastName()==null || user.getFirstName()==null)  //User has to locate himself first
                   {
                       new MaterialAlertDialogBuilder(getContext())
                               .setTitle(R.string.errore)
                               .setMessage(R.string.completaProfilo)
                               .setPositiveButton(R.string.ok,null)
                               .show();
                       tabs.getTabAt(2).select();

                   }else {

                               if (user.getFamilyID() == null) {  //User has to have a family first
                                   new MaterialAlertDialogBuilder(getContext())
                                           .setTitle(R.string.errore)
                                           .setMessage(R.string.scegliFamiglia)
                                           .setPositiveButton(R.string.ok,null)
                                           .show();
                                   tabs.getTabAt(1).select();

                               }else
                               {

                               }

                   }
               }
           });

        }
    }

}
