package com.example.classcomplain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    children child;
    String qwet="hfghftfhf";
    public List<String> data= new ArrayList<String>(10);
    Button submit;
    String campusid;
    SharedPreferences editors;
    StringBuilder builder;
    String refer;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         builder = new StringBuilder();

        editors = getActivity().getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {

            String str=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if (str.contains("Admin:")==true)
            {
                refer="allcampus/"+campusid+"/"+mItem.details+"/";
            }
            else
            {
                refer="allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/childrens/"+mItem.details+"/";
            }
            final DatabaseReference reff= FirebaseDatabase.getInstance().getReference(refer);
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    child=dataSnapshot.getValue(children.class);
                    //builder.append(child.name+ " is a student of "+child.classname+ " \""+child.subjects+"\" \nwith Roll Number "+child.rollno);
                    builder.append("Full Name: "+child.name);
                    builder.append("\nClass & Section: "+child.classname+" \""+child.section+"\"");
                    builder.append("\nAdmission ID: " +child.admissionid);
                    builder.append("\nRoll no: " +child.rollno);
                    builder.append("\nSubjects: "+child.subjects);
                    data.add(dataSnapshot.getKey().toString());
                    //data.add(builder.toString());
                   // Toast.makeText(getContext(),data.get(0),Toast.LENGTH_SHORT).show();
                    ((TextView) rootView.findViewById(R.id.item_detail)).setText(builder.toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //((TextView) rootView.findViewById(R.id.item_detail)).setText(data.get(0).toString());
            submit= (Button)rootView.findViewById(R.id.addcomplain);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent wer=new Intent(getActivity(), ComplainActivity.class);
                    wer.putExtra("stuid",mItem.details);
                    wer.putExtra("refer",refer);
                    startActivity(wer);
                    //Toast.makeText(getContext(),refer,Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(),mItem.details,Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getActivity(), ComplainActivity.class).putExtra("stuid","xyz"));
                }
            });

        }

        return rootView;
    }
}
