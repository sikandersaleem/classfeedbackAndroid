package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChildrenViewByAdminActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    ListView childrenslist;
    String str;
    SharedPreferences editors;
    String campusid;

    List<String> childrenlist= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_view_by_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        childrenslist = (ListView) findViewById(R.id.childrenslist);
        childrenslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent classview=new Intent(ChildrenViewByAdminActivity.this,ItemListActivity.class);
                classview.putExtra("classname",(String) ((TextView)view).getText());
                startActivity(classview);

            }
        });
        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");
        str = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/childrens/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childrenlist.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    childrenlist.add(ds.getKey().toString());
                }
                childrenslist.setAdapter(new ArrayAdapter<String>(ChildrenViewByAdminActivity.this,
                        android.R.layout.simple_list_item_1, childrenlist));
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addchildmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addnewchild) {

            if (str.contains("Admin:") == true)
            {
                startActivity(new Intent(ChildrenViewByAdminActivity.this, ParentViewActivity.class));
            }
            else
            {
                startActivity(new Intent(ChildrenViewByAdminActivity.this, AddChildrenActivity.class).putExtra("parentuid",FirebaseAuth.getInstance().getCurrentUser().getUid().toString()));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
