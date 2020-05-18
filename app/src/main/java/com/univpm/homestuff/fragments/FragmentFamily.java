package com.univpm.homestuff.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.entities.Family;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.repositories.FamilyRepository;
import com.univpm.homestuff.repositories.UserRepository;
import com.univpm.homestuff.services.NotificationSender;
import com.univpm.homestuff.utilities.FamilyMembersAdapter;

import java.util.ArrayList;

public class FragmentFamily extends Fragment implements View.OnClickListener {

    private TextView familyName,textFamilyName,textFamilyNameShow;
    private RecyclerView recyclerViewMembersFamilyList,recyclerViewMembersFamilyListShow;
    private LinearLayout newFamily,myFamily;
    private ImageButton addFamilyMember,addFamilyMemberShow;
    private Button addFamily,editFamily,leaveFamily;
    private TabLayout tabs;

    private FamilyMembersAdapter familyMembersAdapter;

    private Family family;
    private User user;

    private FamilyRepository familyRepository;
    private UserRepository userRepository;

    private NotificationSender notificationSender;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family, container, false);

        notificationSender=new NotificationSender();

        tabs = (TabLayout) getActivity().findViewById(R.id.tabs);

        familyName=view.findViewById(R.id.family);
        textFamilyName=view.findViewById(R.id.text_family_name);
        textFamilyNameShow=view.findViewById(R.id.text_family_name_show);
        addFamilyMemberShow=view.findViewById(R.id.add_family_member_show);
        addFamilyMemberShow.setOnClickListener(this);
        addFamilyMember=view.findViewById(R.id.add_family_member);
        addFamilyMember.setOnClickListener(this);
        editFamily=view.findViewById(R.id.edit_family);
        editFamily.setOnClickListener(this);
        leaveFamily=view.findViewById(R.id.leave_family);
        leaveFamily.setOnClickListener(this);
        addFamily=view.findViewById(R.id.add_family);
        addFamily.setOnClickListener(this);
        recyclerViewMembersFamilyList=view.findViewById(R.id.list_family_members);
        recyclerViewMembersFamilyListShow=view.findViewById(R.id.list_family_members_show);
        recyclerViewMembersFamilyList.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMembersFamilyListShow.setLayoutManager(new LinearLayoutManager(getContext()));

        newFamily=view.findViewById(R.id.new_family);
        myFamily=view.findViewById(R.id.my_family);
        newFamily.setVisibility(View.INVISIBLE);
        myFamily.setVisibility(View.INVISIBLE);


        familyRepository=new FamilyRepository();
        userRepository=new UserRepository();

        return view;
    }


    //Called when the tab is selected
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
           {
               userRepository.getSingleData(FirebaseAuth.getInstance().getUid(), new RepositoryCallBack<User>() {
                   @Override
                   public void onCallback(ArrayList<User> value) {
                       user = value.get(0);
                       if (user.getPlace()==null || user.getLastName()==null || user.getFirstName()==null)  //User has to locate himself first
                       {
                           new MaterialAlertDialogBuilder(getContext())
                                   .setTitle(R.string.errore)
                                   .setMessage(R.string.completaProfilo)
                                   .setPositiveButton(R.string.ok,null)
                                   .show();
                           tabs.getTabAt(2).select();

                       } else {
                           //Cheking if the user has a family or not
                           if (user.getFamilyID() == null) {
                               familyName.setText(R.string.noFamiglia);


                               newFamily.setVisibility(View.VISIBLE);
                               familyMembersAdapter = new FamilyMembersAdapter(new ArrayList<User>());
                               familyMembersAdapter.addItem(user);
                               recyclerViewMembersFamilyList.setAdapter(familyMembersAdapter);
                               family = new Family();


                           } else  //The user has a family, retriving family's data
                           {
                               myFamily.setVisibility(View.VISIBLE);
                               newFamily.setVisibility(View.INVISIBLE);
                               familyRepository.getSingleData(user.getFamilyID(), new RepositoryCallBack<Family>() {
                                   @Override
                                   public void onCallback(ArrayList<Family> value) {
                                       family = value.get(0);
                                       textFamilyNameShow.setText(family.getFamilyName());
                                       familyMembersAdapter = new FamilyMembersAdapter(new ArrayList<User>());

                                       for (String usID : family.getMembers())
                                           userRepository.getSingleData(usID, new RepositoryCallBack<User>() {
                                               @Override
                                               public void onCallback(ArrayList<User> value) {
                                                   familyMembersAdapter.addItem(value.get(0));
                                                   recyclerViewMembersFamilyListShow.setAdapter(familyMembersAdapter);
                                               }
                                           });

                                   }
                               });
                           }
                       }
                   }
               });

           }
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.add_family_member:
            case R.id.add_family_member_show:
               ArrayList<Boolean> checkedItems = new ArrayList<>();
               userRepository.getDataFromPlace(user.getPlace(), new RepositoryCallBack<User>() {
                   @Override
                   public void onCallback(ArrayList<User> value) {
                       //These vectors may have empty element, so their element will be insert in a vector inside the realUsers variable
                       final String[] names=new String[value.size()];
                       final boolean[] checkedItems=new boolean[value.size()];

                       final ArrayList realUsers=new ArrayList<Object>();

                       realUsers.add(new ArrayList<User>()); //Contains users objects

                       int index=0;
                       for (User us:value)
                       {
                           if (us!=null && us.getFirstName()!=null && us.getLastName()!=null && !us.getUID().equals(user.getUID())) {
                               names[index] = us.getFirstName() + " " + us.getLastName();
                               if (family.getMembers()==null || !family.getMembers().contains(us.getUID()))
                               checkedItems[index] = false;
                               else
                               checkedItems[index]=true;
                               index++;
                               ((ArrayList<User>)realUsers.get(0)).add(us);
                           }
                       }

                       realUsers.add(new String[index]); //Contains users names


                       final boolean[] realCheckedItems=new boolean[index];

                       for (int i=0;i<index;i++)
                       {
                           ((String[])realUsers.get(1))[i]=names[i];
                           realCheckedItems[i]=checkedItems[i];
                       }

                       final ArrayList<User> toInsert=new ArrayList<User>();
                       if (index>0) {       //There are users with first name and last name set located in the same place
                           new MaterialAlertDialogBuilder(getContext())
                                   .setTitle(R.string.aggiungiMembroFamigliaTitolo)
                                   .setMultiChoiceItems((String[])realUsers.get(1), realCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                                           if (isChecked)
                                               toInsert.add(((ArrayList<User>) realUsers.get(0)).get(index));
                                           else {
                                               toInsert.remove(((ArrayList<User>) realUsers.get(0)).get(index));
                                                if (family.getMembers()!=null) {
                                                    family.getMembers().remove(((ArrayList<User>) realUsers.get(0)).get(index).getUID());
                                                    familyMembersAdapter.removeItem(((ArrayList<User>) realUsers.get(0)).get(index));
                                                }

                                           }
                                       }
                                   })
                                   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           ArrayList<String> oldMembers=new ArrayList<String>();
                                           ArrayList<User> oldMembersObj= new ArrayList<User>();
                                           if (family.getMembers()!=null) {
                                               oldMembers.addAll(family.getMembers());
                                               family.getMembers().clear();
                                           }else
                                               oldMembers.add(user.getUID());

                                           oldMembersObj.addAll(familyMembersAdapter.get());
                                           familyMembersAdapter.clearItem();
                                           for(User u:toInsert) {
                                               family.addMember(u.getUID());
                                               familyMembersAdapter.addItem(u);
                                           }
                                           for (User u:oldMembersObj)
                                           familyMembersAdapter.addItem(u);
                                           for (String s:oldMembers)
                                           family.addMember(s);
                                           if (v.getId()==R.id.add_family_member)
                                               recyclerViewMembersFamilyList.setAdapter(familyMembersAdapter);
                                           else
                                               recyclerViewMembersFamilyListShow.setAdapter(familyMembersAdapter);


                                       }
                                   })
                                   .setNegativeButton(R.string.cancella, null)
                                   .show();
                       }else //There aren't users located in the same place without a family
                       {
                           new MaterialAlertDialogBuilder(getContext())
                                   .setTitle(R.string.aggiungiMembroFamigliaTitolo)
                                   .setMessage(R.string.noAggiungiMembroFamiglia)
                                   .setPositiveButton(R.string.ok,null)
                                   .show();
                       }
                   }
               });
                break;
            case R.id.add_family:
                DocumentReference ref = FirebaseFirestore.getInstance().collection("families").document();
                family.setFID(ref.getId());
                user.setFamilyID(family.getFID());
                family.setFamilyName(textFamilyName.getText().toString());
                family.setPlace(user.getPlace());
                if (family.getMembers()==null)
                {
                    family.setMembers(new ArrayList<String>());
                    family.addMember(user.getUID());

                }

                familyRepository.addData(family, new ResponseCallBack() {
                    @Override
                    public void onCallback(boolean value) {
                        if (value)
                        {
                            Toast.makeText(getContext(),R.string.famigliaAggiunta,Toast.LENGTH_LONG).show();
                            newFamily.setVisibility(View.INVISIBLE);
                            myFamily.setVisibility(View.VISIBLE);
                            textFamilyNameShow.setText(family.getFamilyName());
                            familyName.setText(R.string.famigliaTitolo);
                            for (String uid:family.getMembers()) {
                                userRepository.editFamilyId(uid, family.getFID(), null);
                            }
                            familyMembersAdapter=new FamilyMembersAdapter(new ArrayList<User>());

                            for(String usID:family.getMembers())
                                userRepository.getSingleData(usID, new RepositoryCallBack<User>() {
                                    @Override
                                    public void onCallback(ArrayList<User> value) {
                                        familyMembersAdapter.addItem(value.get(0));
                                        recyclerViewMembersFamilyListShow.setAdapter(familyMembersAdapter);
                                    }
                                });


                        }else
                        {
                            //error
                        }
                    }
                });
                break;
            case R.id.edit_family:
                family.setFamilyName(textFamilyNameShow.getText().toString());
                familyRepository.addData(family, new ResponseCallBack() {
                    @Override
                    public void onCallback(boolean value) {
                        if (value)
                        {
                            Toast.makeText(getContext(),R.string.famigliaModificata,Toast.LENGTH_LONG).show();
                            for (String uid: family.getMembers())
                            {
                                userRepository.editFamilyId(uid,family.getFID(),null);
                            }
                        }else
                        {
                            //error
                        }
                    }
                });

                break;

            case R.id.leave_family:
                family.removeMember(user.getUID());
                familyRepository.leaveUserFromFamily(family.getFID(),family.getMembers() , new ResponseCallBack() {
                    @Override
                    public void onCallback(boolean value) {
                        if (value)
                        {
                            userRepository.editFamilyId(user.getUID(), null, new ResponseCallBack() {
                                @Override
                                public void onCallback(boolean value) {
                                    if (value)
                                    {
                                        Toast.makeText(getContext(),R.string.esciFamigliaOk,Toast.LENGTH_LONG).show();

                                        //Setting the GUI for creating a new family
                                        user.setFamilyID(null);
                                        myFamily.setVisibility(View.INVISIBLE);
                                        newFamily.setVisibility(View.VISIBLE);
                                        familyName.setText(R.string.noFamiglia);
                                        familyMembersAdapter.clearItem();
                                        familyMembersAdapter.addItem(user);
                                        recyclerViewMembersFamilyList.setAdapter(familyMembersAdapter);
                                        family.reset();
                                        textFamilyName.setText("");
                                    }
                                    else
                                    {
                                        //error
                                    }
                                }
                            });
                        }else
                        {
                            //error
                        }
                    }
                });

                break;

        }
    }
}
