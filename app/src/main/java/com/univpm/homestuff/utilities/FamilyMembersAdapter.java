package com.univpm.homestuff.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.univpm.homestuff.R;
import com.univpm.homestuff.entities.User;

import java.io.File;
import java.util.ArrayList;

public class FamilyMembersAdapter extends RecyclerView.Adapter<FamilyMembersAdapter.CViewHolder> {

    class CViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        ImageView profilePic;



        CViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_member_family_name);
            profilePic=itemView.findViewById(R.id.profile_pic);
        }
    }

    private ArrayList<User> struttura;

    public FamilyMembersAdapter(ArrayList<User> struttura) {
        this.struttura = struttura;
    }

    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_family_row, parent, false);
        return new CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull CViewHolder holder, int position) {
        holder.textName.setText(struttura.get(position).getFirstName()+" "+struttura.get(position).getLastName());
        StorageReference childStorage = FirebaseStorage.getInstance().getReference().child("profileImages/"+struttura.get(position).getUID()+".png");
        if(struttura.get(position).getPhotoURL() != null ) {
            try {
                    final File localFile = File.createTempFile(struttura.get(position).getUID(), ".png");
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

                }
            catch (Exception e) {
            }
        }
        else {//loading default profile image
            Glide.with(holder.profilePic.getContext())
                    .load(R.drawable.placeholder_profile)
                    .centerCrop()
                    .into(holder.profilePic);
        }
    }

    public void addItem(User item)
    {
        struttura.add(item);
    }

    public void removeItem(User item)
    {
        for (int i=0;i<struttura.size();i++)
        {
            if (struttura.get(i).getUID().equals(item.getUID()))
                struttura.remove(i);
        }

    }

    public ArrayList<User> get()
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