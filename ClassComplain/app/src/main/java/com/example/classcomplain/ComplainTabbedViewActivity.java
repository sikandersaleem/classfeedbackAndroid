package com.example.classcomplain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
   
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Objects;

import layout.NewComplainFragment;

public class ComplainTabbedViewActivity extends AppCompatActivity {

    String check,str1;
    SharedPreferences editors;
    String campusid,role;
    String value,link;
    String[] childrenslist;
    int t;
    HashMap<String,String> childlist = new HashMap<String, String>();
    List<String> childrens= new ArrayList<String>();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_tabbed_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");
        role = editors.getString("role","");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        str1= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (str1.contains("Admin:"))
        {
            check= "Assigned to parent";
        }
        else
        {
            check= "Assigned to School";
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/childrens/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childlist.clear();
                childrens.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    childrens.add(ds.child("name").getValue().toString());
                    childlist.put(ds.child("name").getValue().toString(),ds.getKey());
                }
                childrenslist = new String[childrens.size()];

                for (int i=0;i<childrens.size();i++)
                {
                    childrenslist[i]=childrens.get(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(role.equals("parent"))
        getMenuInflater().inflate(R.menu.addfeedbackintabbedactivity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addfeedback) {
            {

                final AlertDialog.Builder builder = new AlertDialog.Builder(ComplainTabbedViewActivity.this);
                builder.setTitle("Choose Child");
                int checkedItem = 0;

        builder.setSingleChoiceItems(childrenslist, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                t=which;
                            }
            });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        value = childlist.get(childrenslist[t]);
                        link = "allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/childrens/"+value+"/";
                        startActivity(new Intent(ComplainTabbedViewActivity.this,ComplainActivity.class).putExtra("stuid","").putExtra("refer",link));
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag =null;
            switch (position) {
                case 0:
                    frag = new NewComplainFragment();
                    break;
                case 1:
                    frag = new AssignToMeFragment();
                    break;
                case 2:
                    frag = new AssigntoSchoolFragment();
                    break;
                case 3:
                    frag = new Closed();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "New";
                case 1:
                    return "Assigned to me";
                case 2:
                    return check;
                case 3:
                    return "Closed";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_complain_tabbed_view, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            return rootView;
        }
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
