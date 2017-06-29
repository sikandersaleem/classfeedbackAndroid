package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classcomplain.dummy.DummyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    children child;
    int check=0;
    String classname;
    private ProgressDialog pDialog;
    private boolean mTwoPane;
    Object value;
    String str;
    View recyclerView;
    SharedPreferences editors;
    String campusid;
    private SharedPreferences.Editor loginPrefsEditor;
    HashMap<String,String> hmforgeo;
    ArrayList<children> arrchild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        hmforgeo = new HashMap<String, String>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        arrchild = new ArrayList<>(100);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        Bundle bun= getIntent().getExtras();
        classname = bun.getString("classname");

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        DummyContent.ITEMS.clear();
        //DummyContent.addItem(new DummyContent.DummyItem("12","hello","this is it"));


        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
       str = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (str.contains("Admin:") == true) {

            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/childrens/"+classname+"/");
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DummyContent.ITEMS.clear();
                    DummyContent.ITEM_MAP.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        check++;
                        DummyContent.addItem(new DummyContent.DummyItem(check + "", ds.child("name").getValue().toString(), ds.child("parent").getValue().toString()+"/childrens/"+ds.getKey().toString()));
                    }
                    pDialog.dismiss();
                    setupRecyclerView((RecyclerView) recyclerView);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        }

        else {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/childrens/");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DummyContent.ITEMS.clear();
                DummyContent.ITEM_MAP.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    child = ds.getValue(children.class);
                    //hmforgeo.put(child.name,String.valueOf(ds.getKey()));
                    //arrchild.add(check,new children(child.name,child.classname,child.rollno,child.section,child.subjects));
                    check++;
                    DummyContent.addItem(new DummyContent.DummyItem(check + "", child.name, String.valueOf(ds.getKey())));
                    //Toast.makeText(getApplicationContext(),ds.getValue().toString(),Toast.LENGTH_SHORT).show();
                }
                pDialog.dismiss();
                setupRecyclerView((RecyclerView) recyclerView);
                ////Toast.makeText(getApplicationContext(),DummyContent.ITEMS.toString(),Toast.LENGTH_SHORT).show();
                // Toast.makeText(getApplicationContext(),DummyContent.ITEM_MAP.toString(),Toast.LENGTH_SHORT).show();
                //adddodummy();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
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
                startActivity(new Intent(ItemListActivity.this, ParentViewActivity.class));
            }
            else
            {
                startActivity(new Intent(ItemListActivity.this, AddChildrenActivity.class).putExtra("parentuid",FirebaseAuth.getInstance().getCurrentUser().getUid().toString()));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
