package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class allcomplains extends AppCompatActivity {

    RVAdapter adapter;
    ComplainClass cc;
    String complainlink,userlink;
    private ProgressDialog pDialog;
    private List<ComplainClass> compclass;
    private RecyclerView rv;
    List<String> complist= new ArrayList<String>();
    SharedPreferences editors;
    String campusid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allcomplains);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        compclass = new ArrayList<>();
        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        rv=(RecyclerView)findViewById(R.id.rvall);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child("role").getValue().toString().equals("parent")==true)
                {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/");
                    //Query qref = ref.orderByChild("state").equalTo("assigntoschool");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            compclass.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                cc = ds.getValue(ComplainClass.class);
                                //if (cc.assignto.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    compclass.add(new ComplainClass(cc.catagories, cc.childid, cc.subject, cc.complainlink, cc.subjects, cc.parentid, cc.status, cc.state, cc.assignto,cc.datetime));
                            }
                            adapter = new RVAdapter(compclass);
                            rv.setAdapter(adapter);
                            pDialog.dismiss();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else
                {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/complains/");
                    //Query qref = ref.orderByChild("state").equalTo("assigntoparent");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            complist.clear();
                            compclass.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                complainlink = String.valueOf(ds.getKey());
                                cc = ds.getValue(ComplainClass.class);
                                compclass.add(new ComplainClass(cc.catagories,cc.childid,cc.subject,cc.complainlink,cc.subjects,cc.parentid,cc.status,cc.state,cc.assignto,cc.datetime));
                            }
                            adapter = new RVAdapter(compclass);
                            rv.setAdapter(adapter);
                            pDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
