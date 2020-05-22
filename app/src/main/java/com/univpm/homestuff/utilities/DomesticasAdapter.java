package com.univpm.homestuff.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.dialogs.AddDomesticaDialog;
import com.univpm.homestuff.entities.Domesticas;
import com.univpm.homestuff.entities.Family;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.repositories.FamilyRepository;
import com.univpm.homestuff.repositories.UserRepository;
import com.univpm.homestuff.services.AlertService;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class DomesticasAdapter extends RecyclerView.Adapter<DomesticasAdapter.CViewHolder> {

    private UserRepository userRepository;
    private FamilyRepository familyRepository;

    private FragmentManager sender;
    private Context ct;

    private Family family;

    private AlertService as;
    private ArrayList<User> cache;


    class CViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textMemberName, textDate;
        ImageView profilePic;
        ImageButton done, edit;


        CViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_domestica_name);
            textMemberName = itemView.findViewById(R.id.text_member_domestica_name);
            profilePic = itemView.findViewById(R.id.profile_pic_domestica);
            done = itemView.findViewById(R.id.domestica_done);
            edit = itemView.findViewById(R.id.domestica_edit);
            cache=new ArrayList<User>();
        }
    }

    private ArrayList<Domesticas> struttura;

    public DomesticasAdapter(Family family,ArrayList<Domesticas> struttura, FragmentManager sender, Context ct) {
        this.struttura = struttura;
        this.sender = sender;
        this.family=family;
        this.ct=ct;
        as=new AlertService(ct);

    }

    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.domestica_row, parent, false);
        userRepository = new UserRepository(ct);
        familyRepository = new FamilyRepository();
        return new CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull CViewHolder holder, final int position) {
        holder.textName.setText(struttura.get(position).getName());
        User find=null;
        for (User u:cache)
            if (u.getUID().equals(struttura.get(position).getUser()))
                find=u;

            if (find!=null) {
                holder.textMemberName.setText(find.getFirstName() + " " + find.getLastName());
                StorageReference childStorage = FirebaseStorage.getInstance().getReference().child("profileImages/" + find.getUID() + ".png");
                if (find.getPhotoURL() != null) {
                    try {
                        final File localFile = File.createTempFile(find.getUID(), ".png");
                        childStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap my_image;
                                my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                Glide.with(holder.profilePic.getContext())
                                        .load(my_image)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(holder.profilePic);
                            }
                        });

                    } catch (Exception e) {
                    }
                } else {//loading default profile image
                    Glide.with(holder.profilePic.getContext())
                            .load(R.drawable.placeholder_profile)
                            .centerCrop()
                            .into(holder.profilePic);
                }
            }
            else //if it is the first time that user's data is needded, it is added on the cache
                userRepository.getSingleData(struttura.get(position).getUser(), new RepositoryCallBack<User>() {
                    @Override
                    public void onCallback(ArrayList<User> value) {
                        cache.add(value.get(0));
                        holder.textMemberName.setText(value.get(0).getFirstName()+ " " + value.get(0).getLastName());
                        StorageReference childStorage = FirebaseStorage.getInstance().getReference().child("profileImages/" + value.get(0).getUID() + ".png");
                        if (value.get(0).getPhotoURL() != null) {
                            try {
                                final File localFile = File.createTempFile(value.get(0).getUID(), ".png");
                                childStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap my_image;
                                        my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        Glide.with(holder.profilePic.getContext())
                                                .load(my_image)
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(holder.profilePic);
                                    }
                                });

                            } catch (Exception e) {
                            }
                        } else {//loading default profile image
                            Glide.with(holder.profilePic.getContext())
                                    .load(R.drawable.placeholder_profile)
                                    .centerCrop()
                                    .into(holder.profilePic);
                        }
                    }
                });




        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(struttura.get(position).getUser())) {
                    //Retriving the full name of the family's members
                    final HashMap<String, String> names = new HashMap<String, String>();
                    final ArrayList<String> values = new ArrayList<String>();

                            for (final String uid : family.getMembers()) {
                                userRepository.getSingleData(uid, new RepositoryCallBack<User>() {
                                    @Override
                                    public void onCallback(ArrayList<User> value) {
                                        names.put(value.get(0).getFirstName() + " " + value.get(0).getLastName(), uid);
                                        values.add(value.get(0).getFirstName() + " " + value.get(0).getLastName());
                                        if (names.size() == family.getMembers().size()) {
                                            AddDomesticaDialog myDialog = new AddDomesticaDialog(ct,values, names, family, true, struttura.get(position));
                                            myDialog.show(sender, "" );
                                            sender.executePendingTransactions();
                                            myDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialogInterface) {
                                                   notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    }
                                });
                            }

                } else {
                    printErrorMessage();
                }

            }
        });


        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(struttura.get(position).getUser())) {

                    as.questionAlert(R.string.servizioCompletatoTitolo, R.string.servizioCompletato, new ResponseCallBack() {
                        @Override
                        public void onCallback(boolean value) {
                            if (value) //positive answer from the user
                            {
                                family.getToDoList().remove(struttura.get(position));
                                familyRepository.addData(family, new ResponseCallBack() {
                                    @Override
                                    public void onCallback(boolean value) {
                                        if (value) {
                                            as.successAlert(R.string.servizioCompletatoTitolo, R.string.servizioCompletatoOk);
                                            notifyDataSetChanged();
                                        }
                                        else
                                            as.defaultErrorData();
                                    }
                                });
                            }
                        }
                    });

                } else {
                    printErrorMessage();
                }
            }
        });

    }


    public void printErrorMessage()
    {
        as.errorAlert(R.string.errore,R.string.noAccessoServizio);
    }


    public void addItem(Domesticas item)
    {
        struttura.add(item);
    }

    public void removeItem(Domesticas item)
    {
        for (int i=0;i<struttura.size();i++)
        {
            if (struttura.get(i).getDomesticaID().equals(item.getDomesticaID()))
                struttura.remove(i);
        }

    }

    public ArrayList<Domesticas> get()
    {
        return this.struttura;
    }

    public void clearItem()
    {

        if (struttura!=null)
            struttura.clear();
    }

    @Override
    public int getItemCount() {
        return struttura.size();
    }
}