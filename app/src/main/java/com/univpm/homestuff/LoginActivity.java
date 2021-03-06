package com.univpm.homestuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import com.univpm.homestuff.callbacks.RepositoryCallBack;
import com.univpm.homestuff.callbacks.ResponseCallBack;
import com.univpm.homestuff.entities.User;
import com.univpm.homestuff.repositories.UserRepository;
import com.univpm.homestuff.services.AlertService;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private UserRepository userRepository;
    private boolean userAlreadyOnDb;    //Flag to know if the user data has already been saved on the db

    private TextView txtEmail;
    private TextView txtPassword;

    private AlertService as;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        as=new AlertService(this);
        userAlreadyOnDb = false;
        userRepository = new UserRepository(this);

        findViewById(R.id.btnRegistrati).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        txtEmail=findViewById(R.id.text_email);
        txtPassword=findViewById(R.id.text_password);
        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                signIn();
                break;
            case R.id.btnRegistrati:
                signUp();
                break;
        }
    }


    private void signIn()
    {
        try  {
            final String email=txtEmail.getText().toString();
            final String password=txtPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("LOGIN","User signed in");
                                currentUser= mAuth.getCurrentUser();
                                checkUserOnDb();
                            }
                            else
                                {
                                    as.errorAlert(R.string.errore,R.string.errorSignIn);
                            }
                        }
                    });
        }catch (Exception ex){
            as.errorAlert(R.string.errore,R.string.errorSignIn);
            ex.printStackTrace();
        }    }


    private void signUp()
    {
        try  {
            final String email=txtEmail.getText().toString();
            final String password=txtPassword.getText().toString();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Log.d("LOGIN","User signed up");
                        currentUser=mAuth.getCurrentUser();
                        writeUserToDb();
                    }else if (task.getException().getClass()== FirebaseAuthUserCollisionException.class)
                    {
                        task.getException().printStackTrace();
                        as.errorAlert(R.string.errore,R.string.errorEmailRegister);

                    }else
                    {
                        task.getException().printStackTrace();
                        as.errorAlert(R.string.errore,R.string.errorSignUp);
                    }
                }
            });
        }catch (Exception ex){
            as.errorAlert(R.string.errore,R.string.infoRequiredRegister);
            ex.printStackTrace();
        }
    }

    public void checkUserOnDb() {
        userRepository.getData(new RepositoryCallBack<User>() {
            @Override
            public void onCallback(ArrayList<User> value) {
                for (User u: value) {
                    if (u.getUID().equals(currentUser.getUid())) {
                        userAlreadyOnDb = true;
                        Log.d("LOGIN","User is already on db");
                    }
                }
                if (!userAlreadyOnDb)
                writeUserToDb();
                else
                {
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });
    }


    private void writeUserToDb(){
        User u=new User(currentUser.getEmail());
        u.setUID(currentUser.getUid());
        userRepository.addData(u, new ResponseCallBack() {
            @Override
            public void onCallback(boolean value) {
                //If there is an error, it will be managed into the FirebaseContext class
                if (value) {
                    Log.d("LOGIN", "User data added correctly");
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }
}
