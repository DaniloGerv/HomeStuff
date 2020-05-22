package com.univpm.homestuff.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.dialogs.AddDomesticaDialog;
import com.univpm.homestuff.entities.Domesticas;
import com.univpm.homestuff.entities.Family;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.repositories.FamilyRepository;
import com.univpm.homestuff.repositories.UserRepository;
import com.univpm.homestuff.services.AlertService;
import com.univpm.homestuff.utilities.DomesticasAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private UserRepository userRepository=new UserRepository(getContext());
    private FamilyRepository familyRepository=new FamilyRepository();
    private User user;
    private Family family;
    private ArrayList<Domesticas> services;

    private TabLayout tabs;
    private ImageButton addDomestica;
    private LinearLayout yesDomestica,noDomestica;
    private RecyclerView recyclerViewServices;


    private DomesticasAdapter domesticasAdapter;

    private AlertService as;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
        yesDomestica=view.findViewById(R.id.yes_services);
        noDomestica=view.findViewById(R.id.no_services);
        yesDomestica.setVisibility(View.INVISIBLE);
        noDomestica.setVisibility(View.INVISIBLE);
        addDomestica=view.findViewById(R.id.add_domestica);
        addDomestica.setOnClickListener(this);
        recyclerViewServices=view.findViewById(R.id.list_family_services);
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(getContext()));
        as=new AlertService(getContext());
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
                       as.errorAlert(R.string.errore,R.string.completaProfilo);
                       tabs.getTabAt(2).select();

                   }else {

                               if (user.getFamilyID() == null) {  //User has to have a family first

                                   as.errorAlert(R.string.errore,R.string.scegliFamiglia);
                                   tabs.getTabAt(1).select();

                               }else
                               {
                                    familyRepository.getSingleData(user.getFamilyID(), new RepositoryCallBack<Family>() {
                                        @Override
                                        public void onCallback(ArrayList<Family> value) {
                                            family=value.get(0);
                                            checkFamilyToDoList();
                                        }
                                    });
                               }

                   }
               }
           });

        }
    }

    public void checkFamilyToDoList()
    {
        if (family.getToDoList()==null || family.getToDoList().size()==0)
        {
            services=new ArrayList<Domesticas>();
            yesDomestica.setVisibility(View.INVISIBLE);
            noDomestica.setVisibility(View.VISIBLE);
        }else
        {
            services=family.getToDoList();
            yesDomestica.setVisibility(View.VISIBLE);
            noDomestica.setVisibility(View.INVISIBLE);
            domesticasAdapter=new DomesticasAdapter(family,services,getFragmentManager(),getContext());
            recyclerViewServices.setAdapter(domesticasAdapter);


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_domestica:

                //Retriving the full name of the family's members
                final HashMap<String,String> names = new HashMap<String,String>();
                final ArrayList<String> values=new ArrayList<String>();
                names.put(user.getFirstName()+" "+user.getLastName(),user.getUID());
                values.add(user.getFirstName()+" "+user.getLastName());
                for (final String uid:family.getMembers())
                {
                    if (!uid.equals(user.getUID()))
                        userRepository.getSingleData(uid, new RepositoryCallBack<User>() {
                            @Override
                            public void onCallback(ArrayList<User> value) {
                                names.put(value.get(0).getFirstName()+" "+value.get(0).getLastName(),uid);
                                values.add(value.get(0).getFirstName()+" "+value.get(0).getLastName());
                                if (names.size()==family.getMembers().size())
                                {
                                    AddDomesticaDialog myDialog = new AddDomesticaDialog(getContext(),values,names,family,false,null);
                                    FragmentManager fm=getFragmentManager();
                                    myDialog.show(fm, "" );
                                    fm.executePendingTransactions();
                                    myDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            familyRepository.getSingleData(family.getFID(), new RepositoryCallBack<Family>() {
                                                @Override
                                                public void onCallback(ArrayList<Family> value) {
                                                    family=value.get(0);
                                                    checkFamilyToDoList();
                                                }
                                            });
                                        }
                                    });

                                }
                            }
                        });
                }

                break;
        }
    }

}
