package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static android.R.id.list;

public class ComplainViewActivity extends AppCompatActivity  {

    private List<ComplainClass> compclass;
    private RecyclerView rv;


    ListView complainlist;
    String complainlink,userlink;
    ComplainClass cc;
    RVAdapter adapter;
    Object value="",value1="";
    HashMap<String,String> mapforuser,mapforcomplain;
    private ProgressDialog pDialog;
    List<String> complist= new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.RIGHT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(ComplainViewActivity.this); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rv.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                            clearView(rv, viewHolder);
                            return;
                        }
                    }).show();
            }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        mapforuser = new HashMap<String, String>();
        mapforcomplain = new HashMap<String, String>();
        compclass = new ArrayList<>();

        /*complainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (mapforuser.containsKey((String) ((TextView)view).getText())) {
                    value = mapforuser.get((String) ((TextView)view).getText());
                }
                if (mapforcomplain.containsKey((String) ((TextView)view).getText())) {
                    value1 = mapforcomplain.get((String) ((TextView)view).getText());
                }
                //Toast.makeText(getApplicationContext(),"userid "+value.toString(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"complain "+value1.toString(),Toast.LENGTH_SHORT).show();

                Intent geoshow=new Intent(ComplainViewActivity.this,ComplainDetailAcitvity.class);
                geoshow.putExtra("uservalue",value.toString());
                geoshow.putExtra("compvalue",value1.toString());
                startActivity(geoshow);

            }
        });*/
        String str=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (str.contains("Admin:")==true)
        {
            //Toast.makeText(getApplicationContext(),"Admin",Toast.LENGTH_SHORT).show();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admin/complains/");
            //contactList = ref.getKey();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    complist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //ds.child("subject").getValue();
                        userlink = String.valueOf(ds.getKey());
                        //Toast.makeText(getApplicationContext(),ds.getValue().toString(),Toast.LENGTH_SHORT).show();

                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("admin/complains/"+userlink+"/");
                        //contactList = ref.getKey();
                        ref1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                complist.clear();
                                compclass.clear();
                                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                    //ds.child("subject").getValue();
                                    complainlink = String.valueOf(ds1.getKey());

                                    //Toast.makeText(getApplicationContext(),ds.getValue().toString(),Toast.LENGTH_SHORT).show();
                                    cc = ds1.getValue(ComplainClass.class);
                                    //compclass.add(new ComplainClass(cc.complaindetail,cc.complaintype,cc.childid,cc.subject,cc.complainlink,cc.subjects,cc.parentid));
                                    mapforuser.put(cc.subject, userlink);
                                    mapforcomplain.put(cc.subject, complainlink);
                                    complist.add(cc.subject);
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
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
        else
            {
                //Toast.makeText(getApplicationContext(),"User",Toast.LENGTH_SHORT).show();
            // Toast.makeText(getApplicationContext(),amg,Toast.LENGTH_SHORT).show();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/complains/");
            //contactList = ref.getKey();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    complist.clear();
                    compclass.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        complainlink = String.valueOf(ds.getKey());

                        //Toast.makeText(getApplicationContext(),ds.getValue().toString(),Toast.LENGTH_SHORT).show();
                        cc = ds.getValue(ComplainClass.class);
                        //compclass.add(new ComplainClass(cc.complaindetail,cc.complaintype,cc.childid,cc.subject,cc.complainlink,cc.subjects,cc.parentid));
                        mapforuser.put(cc.subject, userlink);
                        mapforcomplain.put(cc.subject, complainlink);
                        complist.add(cc.subject);
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

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.viewdetail: {
                //Toast.makeText(getApplicationContext(), "sxvghv", Toast.LENGTH_SHORT).show();
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/complains/"+mapforuser.get(complist.get(info.position).toString()));
                //contactList = ref.getKey();
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            //ds.child("subject").getValue();
                            cc = dataSnapshot.getValue(ComplainClass.class);
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ComplainViewActivity.this);
                        builder1.setTitle(cc.subject);
                        builder1.setMessage("Complain Catagories: "+cc.catagories+System.getProperty("line.separator")+"Subjects: "+cc.subjects);
                        builder1.setCancelable(false);
                        builder1.setNeutralButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        if(!isFinishing())
                        {
                            alert11.show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
                // add stuff here
                return true;
            case R.id.delete:{
                //complainlist.getSelectedItem().toString();
                value = mapforuser.get(complist.get(info.position));
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/complains/"+value.toString()).removeValue();
                Toast.makeText(getApplicationContext(),"Deleted.",Toast.LENGTH_SHORT).show();
            }
                // add stuff here
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }*/
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (pDialog.isShowing()==true)
        {
            pDialog.dismiss();
        }
        else
        {
            finish();
        }
    }


}
