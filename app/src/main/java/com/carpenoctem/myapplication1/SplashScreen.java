package com.carpenoctem.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lakshay Singhla on 25-Oct-17.
 */

public class SplashScreen extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private int index = 1;
    private String check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

       /* mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(index = 1;index<3;index++){
                    check = dataSnapshot.child("879").child("bus" + index).child("check").getValue(String.class);

                    if(check.equals("true")){
                        mDatabase.child("879").child("bus" + index).child("check").setValue("false");
                        Intent intent = new Intent(SplashScreen.this , MapsActivity.class);
                        intent.putExtra("index",index);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
       mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for(index = 1;index<3;index++){
                   check = dataSnapshot.child("879").child("bus" + index).child("check").getValue(String.class);

                   if(check.equals("true")){
                       mDatabase.child("879").child("bus" + index).child("check").setValue("false");
                       Intent intent = new Intent(SplashScreen.this , MapsActivity.class);
                       intent.putExtra("index",index);
                       startActivity(intent);
                       finish();
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


    }
}
