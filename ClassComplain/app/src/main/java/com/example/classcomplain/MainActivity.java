package com.example.classcomplain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView username,useremail;
    String[] data,campuslist,campusnolist;
    String a,b,campusid,role;
    ComplainClass cc;
    int i=0;
    SharedPreferences editors;
    private FirebaseAuth mAuth;
    AutoCompleteTextView asd;
    int t;
    int fall=0,fnew=0,fipm=0,fips=0,fc=0;
    PieChart pieChart;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;
    //ArrayAdapter<String> adapter;

    List<String> campusnamelist= new ArrayList<String>();
    List<Integer> chartlist= new ArrayList<Integer>();
    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<small>Feedback Management System</small>"));

        //editors = getSharedPreferences("campus", MODE_PRIVATE);
        //campusid = editors.getString("campus","");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting Ready For You...");
        pDialog.setCancelable(false);
        pDialog.show();
        mAuth = FirebaseAuth.getInstance();
        editor = getSharedPreferences("campus", MODE_PRIVATE).edit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        username=(TextView)header.findViewById(R.id.username);
        useremail=(TextView)header.findViewById(R.id.useremail);

        pieChart = (PieChart) findViewById(R.id.chart);

        DatabaseReference cam= FirebaseDatabase.getInstance().getReference("allcampus/");
        cam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    campusnamelist.add(ds.child("name").getValue().toString());
                }
                //campusnamelist.add(dataSnapshot.child("name").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference user= FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
        user.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             username.setText(dataSnapshot.child("name").getValue().toString());
             useremail.setText(dataSnapshot.child("emailid").getValue().toString());
             pDialog.dismiss();
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
         }
        });
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                campusid = dataSnapshot.child("campus").getValue().toString();
                role = dataSnapshot.child("role").getValue().toString();

                if (campusid.contains(","))
                {

                    campuslist =new String[campusid.split(",").length];
                    campusnolist =new String[campusnamelist.size()];
                    campusnolist = campusid.split(",");
                    for (int i=0;i<campusid.split(",").length;i++)
                    {
                        campuslist[i] = campusnamelist.get(i);
                    }
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Choose Campus");
                    builder.setSingleChoiceItems(campuslist, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            t=which;
                            //Toast.makeText(getApplicationContext(),campuslist[t],Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                         //Toast.makeText(getApplicationContext(),"dqrvga "+t,Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getApplicationContext(),campusnolist[t],Toast.LENGTH_SHORT).show();
                            editor.putString("campus", campusnolist[t]);
                            editor.putString("role", role);
                            editor.commit();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    editor.putString("campus", campusid);
                    editor.putString("role", role);
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");
        role = editors.getString("role","");

        if (role.equals("parent")==true)
        {
            DatabaseReference count = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/complains/");
            count.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fnew=fipm=fips=fc=0;
                    for (DataSnapshot ds: dataSnapshot.getChildren())
                    {
                        cc = ds.getValue(ComplainClass.class);

                        if (cc.status.equals("new"))
                        {
                            fnew++;
                        }
                        else if (cc.status.equals("inprogress") && cc.state.equals("assigntoparent"))
                        {
                            fipm++;
                        }
                        else if (cc.status.equals("inprogress") && cc.state.equals("assigntoschool"))
                        {
                            fips++;
                        }
                        else if (cc.status.equals("closed"))
                        {
                            fc++;
                        }

                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList<String> labels = new ArrayList<String>();
                        //Toast.makeText(getApplicationContext(),""+fnew+fipm+fips+fc,Toast.LENGTH_LONG).show();
                        if (fnew>0)
                        {
                            entries.add(new Entry(fnew, 0));
                            labels.add("New");
                        }
                        if (fipm>0)
                        {
                            entries.add(new Entry(fipm, 1));
                            labels.add("Inprogress (Assign to me)");
                        }
                        if (fips>0)
                        {
                            entries.add(new Entry(fips, 2));
                            labels.add("Inprogress (Assign to school)");
                        }
                        if (fc>0)
                        {
                            entries.add(new Entry(fc, 3));
                            labels.add("Closed");
                        }

                        PieDataSet dataset = new PieDataSet(entries, "Feedbacks");
                        dataset.setSliceSpace(3);
                        dataset.setSelectionShift(5);
                        PieData data = new PieData(labels, dataset);
                        data.setValueTextSize(12);
                        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                        pieChart.setDescription("All Feedbacks.");
                        pieChart.setDrawHoleEnabled(true);
                        pieChart.setHoleColorTransparent(true);
                        pieChart.setHoleRadius(25);

                        pieChart.setTransparentCircleRadius(10);
                        // enable rotation of the chart by touch
                        //pieChart.setRotationAngle(0);
                        pieChart.setRotationEnabled(true);
                        pieChart.setData(data);
                        Legend l = pieChart.getLegend();
                        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
                        l.setXEntrySpace(20);
                        l.setYEntrySpace(0);
                        pieChart.invalidate();
                        pieChart.animateY(5000);
                        //Toast.makeText(getApplicationContext(),""+chartlist.get(0)+chartlist.get(1)+chartlist.get(2)+chartlist.get(3),Toast.LENGTH_LONG).show();
                    }
                    pDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.uploaddata) {

            //csvdata();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.childrens) {
            String str=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if (str.contains("Admin:")==true)
            {
                startActivity(new Intent(MainActivity.this, ChildrenViewByAdminActivity.class));
            }
            else
            {

                startActivity(new Intent(MainActivity.this, ItemListActivity.class).putExtra("classname",""));
            }

        } else if (id == R.id.complain) {

            startActivity(new Intent(MainActivity.this, ComplainTabbedViewActivity.class));
        }  else if (id == R.id.allcomplain) {

            startActivity(new Intent(MainActivity.this, allcomplains.class));

        } else if (id == R.id.notifications) {

            startActivity(new Intent(MainActivity.this, NotificationsActivity.class));

        }  else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            editor = getSharedPreferences("campus", MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
            MainActivity.this.finish();
            startActivity(new Intent(MainActivity.this, Login.class)); //Go back to home page
        } else if (id == R.id.exit) {
            finish();
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void csvdata()
    {
        DatabaseReference ref_data = FirebaseDatabase.getInstance().getReference("users/");
        pDialog.setMessage("Uploading CSV...");
        pDialog.setCancelable(false);

        //pDialog.show();
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(getAssets()
                    .open("allparents.csv"));

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {

                data = line.split(",");
                 a = data[0].toString().replaceAll("\"", "");
                a = a.replaceAll("\n", "");

                b = data[1].toString().replaceAll("\"", "");
                b = b.replaceAll("\n", "");


                /*new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        mAuth.createUserWithEmailAndPassword(a+"@email.com",a )
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (!task.isSuccessful()) {

                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), a+"@email.com, "+a,
                                                    Toast.LENGTH_SHORT).show();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(b).build();
                                            //FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/accounttype").setValue("admin");
                                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                                            DatabaseReference usersref = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
                                            usersref.child("name").setValue(b);
                                            usersref.child("phoneno").setValue(a);
                                            usersref.child("canresolvecomplain").setValue("false");
                                            usersref.child("emailid").setValue(a+"@email.com");
                                            usersref.child("role").setValue("parent");
                                            //FirebaseAuth.getInstance().signOut();
                                        }
                                    }
                                });
                    }
                }, 0, 2500);*/

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
