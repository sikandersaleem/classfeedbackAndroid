package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddChildrenActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText childname,childrollno,childadmissionid;
    Spinner childclass,childsection;
    private ProgressDialog pDialog;
    Button addchild;
    String parentuid;
    SharedPreferences editors;
    String campusid;
    String childnameget,childrollnoget, childclassget,childsectionget,childadmission;

    List<String> classlist= new ArrayList<String>();
    List<String> sectionlist= new ArrayList<String>();

    ArrayAdapter<String> classlistAdapter, sectionlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        Bundle bun= getIntent().getExtras();
        parentuid = bun.getString("parentuid");

        childname = (EditText) findViewById(R.id.childname);
        childrollno = (EditText) findViewById(R.id.childrollno);
        childadmissionid = (EditText) findViewById(R.id.admissionid);

        childclass = (Spinner) findViewById(R.id.childclass);
        childsection = (Spinner) findViewById(R.id.childsection);

        addchild = (Button) findViewById(R.id.addchild);

        addchild.setOnClickListener(this);
        childclass.setOnItemSelectedListener(this);
        childsection.setOnItemSelectedListener(this);

        sectionlist.add("Select Section");
        sectionlist.add("A");
        sectionlist.add("B");
        sectionlist.add("C");

        classlistAdapter = new ArrayAdapter<String>(AddChildrenActivity.this,android.R.layout.simple_spinner_item, classlist);
        classlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childclass.setAdapter(classlistAdapter);

        sectionlistAdapter = new ArrayAdapter<String>(AddChildrenActivity.this,android.R.layout.simple_spinner_item, sectionlist);
        sectionlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childsection.setAdapter(sectionlistAdapter);

        DatabaseReference refclasses = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/classes/");
        refclasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classlist.clear();
                classlist.add("Select Class");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    classlist.add(String.valueOf(ds.getKey()));
                }
                classlistAdapter = new ArrayAdapter<String>(AddChildrenActivity.this,android.R.layout.simple_spinner_item, classlist);
                classlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                childclass.setAdapter(classlistAdapter);
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

    @Override
    public void onClick(View v) {

        if(!childname.getText().toString().equals("") && !childrollno.getText().toString().equals("") && !childclassget.equals("Select Class") && !childsectionget.equals("Select Section") && !childadmissionid.getText().toString().equals(""))
        {
            childnameget = childname.getText().toString();
            childrollnoget = childrollno.getText().toString();
            childadmission = childadmissionid.getText().toString();

            final DatabaseReference ref_notification = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+parentuid+"/childrens/");
            final DatabaseReference newref = ref_notification.push();

            DatabaseReference ref_admin = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/childrens/"+childclassget+"/");
            //DatabaseReference childref=newref.push();
            ref_notification.child(newref.getKey()).child("classname").setValue(childclassget);

            ref_admin.child(newref.getKey()).child("name").setValue(childnameget);
            ref_admin.child(newref.getKey()).child("parent").setValue(parentuid);

            ref_notification.child(newref.getKey()).child("name").setValue(childnameget);
            ref_notification.child(newref.getKey()).child("admissionid").setValue(childadmission);
            ref_notification.child(newref.getKey()).child("rollno").setValue(childrollnoget);
            ref_notification.child(newref.getKey()).child("section").setValue(childsectionget);

            DatabaseReference reff= FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/classes/"+childclassget+"/subjects/");
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ref_notification.child(newref.getKey()).child("subjects").setValue(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Check input values.",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.childclass)
        {
            childclassget = parent.getItemAtPosition(position).toString();
        }
        else if(spinner.getId() == R.id.childsection)
        {
            childsectionget = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
