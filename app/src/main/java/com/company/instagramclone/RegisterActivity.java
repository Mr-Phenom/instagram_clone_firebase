package com.company.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName,name,email,password;
    private Button register;
    private TextView loginUser;
    private ProgressDialog progressDialog;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName=findViewById(R.id.userName);
        name=findViewById(R.id.fullName);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        loginUser=findViewById(R.id.login_user);
        progressDialog= new ProgressDialog(this);

        ref = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = userName.getText().toString();
                String txtPassword = password.getText().toString();
                String txtEmail = email.getText().toString();
                String txtName = name.getText().toString();

                if(txtName.equals("") || txtUsername.equals("")|| txtEmail.equals("") || txtPassword.equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "You can not leave any field empty", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length()<6) {
                    Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    registerUser(txtName, txtUsername,txtEmail,txtPassword);
                }
            }
        });
    }

    private void registerUser(String name, String username, String email, String password)
    {

        progressDialog.setMessage("Please wait");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String,Object> map = new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                map.put("userName",username);
                map.put("id",auth.getCurrentUser().getUid());
                map.put("bio","");
                map.put("imageUrl","default");


                ref.child("User").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            Toast.makeText(RegisterActivity.this, "Update your profile from settings", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}