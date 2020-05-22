package com.univpm.homestuff.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.entities.Domesticas;
import com.univpm.homestuff.entities.Family;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.repositories.FamilyRepository;
import com.univpm.homestuff.repositories.UserRepository;
import com.univpm.homestuff.services.AlertService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class AddDomesticaDialog extends DialogFragment implements  View.OnClickListener {

    private UserRepository userRepository;
    private FamilyRepository familyRepository;

    private HashMap<String,String> names;
    private ArrayList<String> values;
    private boolean onEdit;

    private Domesticas domesticas;
    private String uid;
    private Family family;

    private TextView textName;
    private Button buttonAdd,buttonCancel;

    private AlertService as;

    private Context ct;


    public AddDomesticaDialog(Context ct, ArrayList<String> values, HashMap<String,String> names, Family family, boolean onEdit, @Nullable Domesticas domesticas)
    {
        this.names=names;
        this.values=values;
        this.onEdit=onEdit;
        this.family=family;
        if (onEdit)
            this.domesticas=domesticas;
        else
            this.domesticas=new Domesticas();
        this.ct=ct;
        this.as=new AlertService(ct);
    }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.add_domestica_dialog,container,false);

            userRepository=new UserRepository(ct);
            familyRepository=new FamilyRepository();

            textName=view.findViewById(R.id.name_domestica_add);
            if (onEdit)
                textName.setText(this.domesticas.getName());

            //Different button strings based on if we are on edit or not


            buttonAdd=view.findViewById(R.id.add_domestica_dialog);
            buttonAdd.setOnClickListener(this);
            if (onEdit)
                buttonAdd.setText(R.string.modificaServizio);
            else
                buttonAdd.setText(R.string.creaServizio);
            buttonCancel=view.findViewById(R.id.cancel_domestica_dialog);
            buttonCancel.setOnClickListener(this);

            final ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            getContext(),
                            R.layout.dropdown_menu_pop_item,
                            values);

           final AutoCompleteTextView editTextFilledExposedDropdown =
                    view.findViewById(R.id.dropdown_user_domestica);
            if (onEdit) {
                userRepository.getSingleData(this.domesticas.getUser(), new RepositoryCallBack<User>() {
                    @Override
                    public void onCallback(ArrayList<User> value) {
                        editTextFilledExposedDropdown.setText(value.get(0).getFirstName()+" "+value.get(0).getLastName(),false);
                        uid=names.get(value.get(0).getFirstName()+" "+value.get(0).getLastName());
                    }
                });

            }
            editTextFilledExposedDropdown.setAdapter(adapter);
            editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   uid=names.get(adapter.getItem(position));
                }
            });

            return view;
        }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_domestica_dialog:

                if (uid!=null & uid!="" && textName.getText()!="")
                {
                        domesticas.setName(textName.getText().toString());
                        userRepository.getSingleData(uid, new RepositoryCallBack<User>() {
                            @Override
                            public void onCallback(ArrayList<User> value) {
                                domesticas.setUser(value.get(0).getUID());

                                //If we are adding the domestica I have to set its ID on firebase
                                if (!onEdit) {
                                    domesticas.setFamilyID(family.getFID());
                                    DocumentReference ref = FirebaseFirestore.getInstance().collection("families").document(domesticas.getFamilyID()).collection("toDoList").document();
                                    domesticas.setDomesticaID(ref.getId());
                                }

                                if (family.getToDoList()==null)
                                    family.setToDoList(new ArrayList<Domesticas>());

                                //If we are on editing it is needded to remove old domestica data from the to do list of the family
                                if (!onEdit)
                                    family.addDomesticas(domesticas);
                                else
                                {
                                   for (Domesticas dom: family.getToDoList())
                                   {
                                       if (dom.getDomesticaID().equals(domesticas.getDomesticaID()))
                                       {
                                           dom.setName(domesticas.getName());
                                           dom.setUser(domesticas.getUser());
                                       }
                                   }
                                }



                                     familyRepository.addData(family, new ResponseCallBack() {
                                    @Override
                                    public void onCallback(boolean value) {
                                        if (!onEdit)
                                            as.successAlert(R.string.servizioCompletatoTitolo,R.string.servizioAggiunto);
                                        else
                                            as.successAlert(R.string.servizioCompletatoTitolo,R.string.servizioModificato);

                                        getDialog().dismiss();
                                    }
                                });
                            }
                        });
                }else
                {
                    as.errorAlert(R.string.errore,R.string.compilareDialogDomestica);
                }
                break;
            case R.id.cancel_domestica_dialog:
                getDialog().dismiss();
                break;
        }
    }

}
