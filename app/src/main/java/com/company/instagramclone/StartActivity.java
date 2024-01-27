package com.company.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {
    Button login,register;
    ImageView imageIcon;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        imageIcon = findViewById(R.id.icon_image);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        linearLayout=findViewById(R.id.linearLayout);

        linearLayout.animate().alpha(0f).setDuration(1);
        linearLayout.setVisibility(View.INVISIBLE);


        AlphaAnimation animation = new AlphaAnimation(0f,1f);
        animation.setDuration(2200);
        animation.setFillAfter(false);

        imageIcon.setAnimation(animation);

        animation.setAnimationListener(new MyAnimationListener());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent);
                //finish();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,LogInActivity.class);
                startActivity(intent);
                //finish();
            }
        });

    }
    private class MyAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            TranslateAnimation animation1 = new TranslateAnimation(0,0,0,-1000);
            animation1.setDuration(1000);
            animation1.setFillAfter(false);
            imageIcon.clearAnimation();
            imageIcon.setAnimation(animation1);
            imageIcon.animate().alpha(0f).setDuration(1000);

            imageIcon.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout.animate().alpha(0f).setDuration(1);
            linearLayout.animate().alpha(1f).setDuration(2000);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent i = new Intent(StartActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}