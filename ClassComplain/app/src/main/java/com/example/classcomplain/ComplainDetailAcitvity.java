package com.example.classcomplain;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ComplainDetailAcitvity extends AppCompatActivity implements View.OnClickListener {

    String complink,userlink;
    Button converationbtn,resolve,assign;
    ComplainClass cc;
    children child;
    String str;
    SharedPreferences editors;
    String campusid;
    String get;
    int t;
    messages msg;
    DatabaseReference ref1,ref11,ref;
    private ProgressDialog pDialog;
    StringBuilder usersb,compsb,lastconvo;
    AutoCompleteTextView asd;
    List<String> employees= new ArrayList<String>();
    String[] getemp;
    ArrayAdapter<String> adapter;
    LinkedHashMap<String, String > emplist= new LinkedHashMap<String, String>();
    TextView usertv,admintv,lastmsgtv,comptv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_detail_acitvity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        Bundle bundle = getIntent().getExtras();
        complink = bundle.getString("compvalue");
        userlink = bundle.getString("uservalue");

        converationbtn =(Button) findViewById(R.id.conversationbtn);
        converationbtn.setOnClickListener(this);
        resolve = (Button) findViewById(R.id.resolve);
        resolve.setVisibility(View.GONE);
        resolve.setOnClickListener(this);
        assign = (Button) findViewById(R.id.assignrepresenter);
        assign.setVisibility(View.GONE);
        assign.setOnClickListener(this);

        usersb = new StringBuilder();
        compsb = new StringBuilder();
        lastconvo = new StringBuilder();

        usertv = (TextView) findViewById(R.id.usertv);
        admintv = (TextView) findViewById(R.id.admintv);
        comptv = (TextView) findViewById(R.id.comptv);
        lastmsgtv = (TextView) findViewById(R.id.lastmsgtv);

        admintv.setText("Representer: Currently not set.");

        ref = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("canresolvecomplain").getValue().toString().equals("true")==true)
                {
                    resolve.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        DatabaseReference newref= FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/employees/");
        newref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employees.clear();
                emplist.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    employees.add(ds.child("name").getValue().toString()+" ("+ds.child("designation").getValue().toString()+")");
                    emplist.put(ds.child("name").getValue().toString(),ds.getKey().toString());
                }
                getemp =new String[employees.size()];
                for (int i=0;i<employees.size();i++)
                {
                    getemp[i]=employees.get(i);
                }
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        str=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (str.contains("Admin:")==true) {
            ref1 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+userlink + "/complains/" + complink+"/");
            //assign.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(),"Admin: "+ref1,Toast.LENGTH_SHORT).show();
        }
        else
        {
            ref1 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/complains/" + complink+"/");
        }
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                compsb.delete(0,compsb.length());
                cc = dataSnapshot.getValue(ComplainClass.class);
                compsb.append("COMPLAIN INFORMATION");
                compsb.append("\nSubject: "+cc.subject);
                compsb.append("\nCatagories: "+cc.catagories);
                compsb.append("\nSubjects: "+cc.subjects);
                comptv.setText(compsb);

                DatabaseReference db =FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (cc.status.equals("closed")==true)
                        {
                            resolve.setVisibility(View.GONE);
                        }
                        if (cc.assignto.equals("Currently not set") && dataSnapshot.child("canassign").getValue().toString().equals("true")==true)
                        {
                            assign.setVisibility(View.VISIBLE);
                        }
                        else if (cc.assignto.equals("Currently not set")&& !dataSnapshot.child("canassign").getValue().toString().equals("true")==true)
                        {
                            admintv.setText("Representer: "+cc.assignto);
                        }
                        else
                        {
                            DatabaseReference newref= FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/employees/"+cc.assignto+"/");
                            newref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    admintv.setText("Representer: "+dataSnapshot.child("name").getValue().toString());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            assign.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if (str.contains("Admin:")==true) {
                    ref11 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+userlink + "/childrens/"+cc.childid+"/");
                }
                else
                {
                    ref11 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/childrens/"+cc.childid+"/");
                }
                ref11.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usersb.delete(0,usersb.length());
                        child = dataSnapshot.getValue(children.class);
                        //Toast.makeText(getApplicationContext(),child.toString(),Toast.LENGTH_SHORT).show();
                        usersb.append("STUDENT INFORMATION");
                        usersb.append("\nFull Name: "+child.name);
                        usersb.append("\nClass & Section: "+child.classname+" \""+child.section+"\"");
                        usersb.append("\nAdmission ID: " +child.admissionid);
                        usersb.append("\nRoll no: " +child.rollno);
                        usersb.append("\nSubjects: "+child.subjects);
                        usertv.setText(usersb);
                        pDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        Query qref1= ref1.child("messages").orderByChild("type").equalTo("public");
        qref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastconvo.delete(0,lastconvo.length());
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    msg = ds1.getValue(messages.class);
                    //Toast.makeText(getApplicationContext(),msg.toString(),Toast.LENGTH_SHORT).show();
                }
                lastconvo.append("LAST CONVERSATION");
                lastconvo.append("\nFrom: "+msg.sender);
                lastconvo.append("\nMessage: "+msg.message);
                lastconvo.append("\nat "+msg.time);
                lastmsgtv.setText(lastconvo);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.conversationbtn)
        {
            Intent innt =new Intent(ComplainDetailAcitvity.this,ChatActivity.class);
            innt.putExtra("keyvalue",userlink);
            innt.putExtra("keyvalue1",complink);
            startActivity(innt);
        }
    else if(v.getId()==R.id.resolve)
        {
            DatabaseReference resolveref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+userlink+"/complains/"+complink);
            resolveref.child("status").setValue("closed");
            resolveref.child("state").setValue("closed");
            DatabaseReference resolveref1 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/"+complink);
            resolveref1.child("status").setValue("closed");
            resolveref1.child("state").setValue("closed");
            Toast.makeText(getApplicationContext(),"Feedback Closed.",Toast.LENGTH_SHORT).show();
            finish();
        }

    else if(v.getId()==R.id.assignrepresenter)
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(ComplainDetailAcitvity.this);
        builder.setTitle("Choose Representator");
        //employees.add("Choose Representer:");
        int checkedItem = 0;
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View search_View = inflater.inflate(R.layout.search, null);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getemp);
        asd = (AutoCompleteTextView) search_View.findViewById(R.id.movie_name);
        asd.setAdapter(adapter);

        asd.setThreshold(0);
        asd.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                asd.showDropDown();
                //asd.requestFocus();
                return false;
            }

        });
        asd.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                t=position;
                //Toast.makeText(ComplainDetailAcitvity.this,
                        //adapter.getItem(position).toString(),
                        //Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(search_View);
        /*builder.setSingleChoiceItems(getemp, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                get=getemp[which];
                t=which;
                if (!get.equals("Choose Representer:"))
                {
                    admintv.setText("Representer: "+get);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"not selected.",Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                assign.setVisibility(View.GONE);
                //admintv.setText("Representer: "+getemp[t]);
                admintv.setText("Representer: "+asd.getText().toString());
                String value = (new ArrayList<String>(emplist.values())).get(t);
                //emplist.get(getemp[t]).toString();
                //Toast.makeText(getApplicationContext(),value+" () "+asd.getText().toString(),Toast.LENGTH_SHORT).show();

                DatabaseReference setadmin=FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/"+complink+"/");
                setadmin.child("assignto").setValue(value);
                DatabaseReference setparent=FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+userlink+"/complains/"+complink+"/");
                setparent.child("assignto").setValue(value);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+userlink+"/complains/"+complink);
                ref.child("status").setValue("inprogress");
                ref.child("state").setValue("assigntoschool");
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/"+complink);
                ref1.child("status").setValue("inprogress");
                ref1.child("state").setValue("assigntoschool");
                DatabaseReference refforemp = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/employees/"+value+"/complains/");
                DatabaseReference refforcmp = refforemp.push();
                refforcmp.child("complainid").setValue(complink);
                Toast.makeText(getApplicationContext(),"Assigned Successfully.",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //assign.setVisibility(View.VISIBLE);
                admintv.setText("Representer: Currently not set.");
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.copylink, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.copylink) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("complainlink", complink);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(),"Copied to Clipboard.",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}