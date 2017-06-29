package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParentViewActivity extends AppCompatActivity {

    ListView parentslist;
    private ProgressDialog pDialog;
    HashMap<String,String> parents;
    Object value;
    SharedPreferences editors;
    String campusid;
    List<String> parentlist= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        parents = new HashMap<String, String>();

        parentslist = (ListView) findViewById(R.id.parentslist);
        parentslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (parents.containsKey((String) ((TextView)view).getText())) {
                    value = parents.get((String) ((TextView)view).getText());
                }

                Intent geoshow=new Intent(ParentViewActivity.this,AddChildrenActivity.class);
                geoshow.putExtra("parentuid",value.toString());
                startActivity(geoshow);

            }
        });

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("users/");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parentlist.clear();
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    //Toast.makeText(getApplicationContext(),ds1.getValue().toString(),Toast.LENGTH_SHORT).show();
                    if(ds1.child("role").getValue().toString().equals("parent")==true)
                    {
                        if(ds1.child("campus").getValue().toString().equals(campusid)==true)
                        {
                            String temp = ds1.child("name").getValue().toString()+" ("+ds1.child("emailid").getValue().toString()+")";
                            parents.put(temp,ds1.getKey().toString());
                            parentlist.add(temp);
                        }
                    }
                }
                parentslist.setAdapter(new ArrayAdapter<String>(ParentViewActivity.this,
                        android.R.layout.simple_list_item_1, parentlist));
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
