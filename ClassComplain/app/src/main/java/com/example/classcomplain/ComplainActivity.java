package com.example.classcomplain;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComplainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText message;
    String allsubjects,allcatagories;
    Button submit;
    String parentid,childid;
    CheckBox[] checkBox,checkBox1;
    LinearLayout linearMain,linearcata;
    String sbuilder="",cbuilder="";
    SharedPreferences editors;
    String campusid;
    List<String> detaillist= new ArrayList<String>();
    List<String> typelist= new ArrayList<String>();
    List<String> subjects=new ArrayList<String>(10);
    List<String> catagories=new ArrayList<String>(10);
    String etsubject, etmessage, spcomplaindetail, spcomplaictype, spsubjects;
    String stuid,refer,cut;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        linearMain = (LinearLayout) findViewById(R.id.linearMain);
        linearcata = (LinearLayout) findViewById(R.id.linearcata);

        Bundle bun= getIntent().getExtras();
        stuid = bun.getString("stuid");
        refer = bun.getString("refer");
        cut = bun.getString("refer");

        cut = cut.replace("allcampus/"+campusid+"/","");

        parentid = cut.substring(0,cut.indexOf("/childrens/"));
        childid = cut.substring(cut.indexOf("/childrens/"),cut.length());
        //Toast.makeText(getApplicationContext(),parentid,Toast.LENGTH_SHORT).show();
        childid = childid.replace("/childrens/","");
        childid = childid.replace("/","");

        message = (EditText) findViewById(R.id.details);
        message.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        message.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(this);

        detaillist.add("Complain Detail:");
        detaillist.add("Not Attend");
        detaillist.add("Teacher on Leave");

        typelist.add("Complain Type:");
        typelist.add("Behaviour");
        typelist.add("Imbehaviour");


        final DatabaseReference reff= FirebaseDatabase.getInstance().getReference(refer);
        reff.child("subjects").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                subjects.clear();
                allsubjects= dataSnapshot.getValue().toString();
               subjects =Arrays.asList(allsubjects.split(","));
                //Toast.makeText(getApplicationContext(),subjects.toString(),Toast.LENGTH_SHORT).show();
                checkBox =new CheckBox[subjects.size()];
                for (Integer t=0;t<subjects.size();t++) {
                    //Toast.makeText(getApplicationContext(),subjects.get(t),Toast.LENGTH_SHORT).show();
                    checkBox[t] = new CheckBox(ComplainActivity.this);
                    checkBox[t].setTextSize(14);
                    checkBox[t].setId(Integer.parseInt(t.toString()));
                    checkBox[t].setText(subjects.get(t));
                    //checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
                    final Integer finalT = t;
                    checkBox[t].setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(checkBox[finalT].isChecked()){
                                if (sbuilder.equals(""))
                                sbuilder = ""+checkBox[finalT].getText().toString();
                                else
                                    sbuilder = sbuilder +","+checkBox[finalT].getText().toString();
                                //Toast.makeText(getApplicationContext(),sbuilder,Toast.LENGTH_SHORT).show();
                            }else{

                                sbuilder = sbuilder.replace(checkBox[finalT].getText().toString(),"");
                                sbuilder = sbuilder.replace(",","");
                                //Toast.makeText(getApplicationContext(),sbuilder,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    linearMain.addView(checkBox[t]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        final DatabaseReference reff1= FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+"admin/complaincatagories/");
        reff1.child("catagories").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                catagories.clear();
                allcatagories= dataSnapshot.getValue().toString();
                catagories =Arrays.asList(allcatagories.split(","));
                //Toast.makeText(getApplicationContext(),subjects.toString(),Toast.LENGTH_SHORT).show();
                checkBox1 =new CheckBox[catagories.size()];
                for (Integer p=0;p<catagories.size();p++) {
                    //Toast.makeText(getApplicationContext(),subjects.get(t),Toast.LENGTH_SHORT).show();
                    checkBox1[p] = new CheckBox(ComplainActivity.this);
                    checkBox1[p].setTextSize(14);
                    checkBox1[p].setId(Integer.parseInt(p.toString()));
                    checkBox1[p].setText(catagories.get(p));
                    //checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
                    final Integer finalp = p;
                    checkBox1[p].setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(checkBox1[finalp].isChecked()){
                                if (cbuilder.equals(""))
                                    cbuilder = ""+checkBox1[finalp].getText().toString();
                                else
                                    cbuilder = cbuilder +","+checkBox1[finalp].getText().toString();
                                //Toast.makeText(getApplicationContext(),sbuilder,Toast.LENGTH_SHORT).show();
                            }else{
                                cbuilder = cbuilder.replace(checkBox1[finalp].getText().toString(),"");
                                cbuilder = cbuilder.replace(",","");
                            }
                        }
                    });
                    linearcata.addView(checkBox1[p]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onClick(View v) {
        etmessage=message.getText().toString();

            if(etmessage.length()>=20)
            etsubject= etmessage.substring(0,19);
            else
            etsubject = etmessage;

            if(cbuilder.equals("")==true)
            {
                cbuilder="Not Mentioned.";
            }
            if(sbuilder.equals("")==true)
            {
                 sbuilder="Not Mentioned.";
            }

            DatabaseReference ref_notification = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+parentid+"/complains/");
            DatabaseReference newref = ref_notification.push();

            DatabaseReference ref_admin = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/");

            ref_notification.child(newref.getKey()).child("childid").setValue(childid);
            ref_admin.child(newref.getKey()).child("childid").setValue(childid);
            ref_notification.child(newref.getKey()).child("datetime").setValue(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            ref_admin.child(newref.getKey()).child("datetime").setValue(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            ref_notification.child(newref.getKey()).child("assignto").setValue("Currently not set");
            ref_admin.child(newref.getKey()).child("assignto").setValue("Currently not set");
            ref_notification.child(newref.getKey()).child("subjects").setValue(sbuilder);
            ref_admin.child(newref.getKey()).child("subjects").setValue(sbuilder);
            ref_notification.child(newref.getKey()).child("catagories").setValue(cbuilder);
            ref_admin.child(newref.getKey()).child("catagories").setValue(cbuilder);
            String str = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if (str.contains("Admin:")==true)
            {
                ref_notification.child(newref.getKey()).child("status").setValue("inprogress");
                ref_admin.child(newref.getKey()).child("status").setValue("inprogress");
                ref_notification.child(newref.getKey()).child("state").setValue("assigntoparent");
                ref_admin.child(newref.getKey()).child("state").setValue("assigntoparent");
            }
            else
            {
                ref_notification.child(newref.getKey()).child("status").setValue("new");
                ref_admin.child(newref.getKey()).child("status").setValue("new");
                ref_notification.child(newref.getKey()).child("state").setValue("new");
                ref_admin.child(newref.getKey()).child("state").setValue("new");
            }
            ref_notification.child(newref.getKey()).child("parentid").setValue(parentid);
            ref_admin.child(newref.getKey()).child("parentid").setValue(parentid);
            ref_notification.child(newref.getKey()).child("subject").setValue(etsubject);
            ref_admin.child(newref.getKey()).child("subject").setValue(etsubject);
            ref_notification.child(newref.getKey()).child("complainlink").setValue(newref.getKey());
            ref_admin.child(newref.getKey()).child("complainlink").setValue(newref.getKey());
            DatabaseReference msgref=newref.push();
            ref_notification.child(newref.getKey()).child("messages").child(msgref.getKey()).child("message").setValue(etmessage);
            ref_notification.child(newref.getKey()).child("messages").child(msgref.getKey()).child("time").setValue(currentDate());
            ref_notification.child(newref.getKey()).child("messages").child(msgref.getKey()).child("type").setValue("public");
        ref_notification.child(newref.getKey()).child("messages").child(msgref.getKey()).child("image").setValue("");
        ref_notification.child(newref.getKey()).child("messages").child(msgref.getKey()).child("messagelink").setValue(msgref.getKey());
            ref_notification.child(newref.getKey()).child("messages").child(msgref.getKey()).child("sender").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            Toast.makeText(getApplicationContext(),"Your feedback is acknowledged, we shall respond shortly.",Toast.LENGTH_SHORT).show();
            this.finish();


    }
    public static String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy (hh:mm:ss)");
        // get current date time with Date()
        Date date = new Date();
        return dateFormat.format(date);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}