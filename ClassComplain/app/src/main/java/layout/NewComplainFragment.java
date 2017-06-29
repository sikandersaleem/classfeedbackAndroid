package layout;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.classcomplain.ComplainClass;
import com.example.classcomplain.R;
import com.example.classcomplain.RVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewComplainFragment extends Fragment {

    RVAdapter adapter;
    ComplainClass cc;
    String complainlink,userlink;
    private ProgressDialog pDialog;
    private List<ComplainClass> compclass;
    private RecyclerView rv;
    List<String> complist= new ArrayList<String>();
    SharedPreferences editors;
    String campusid;

    public NewComplainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_new_complain, container, false);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        compclass = new ArrayList<>();
        editors = getActivity().getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        rv=(RecyclerView)view.findViewById(R.id.rvnew);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        String str= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (str.contains("Admin:")==true)
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/");
            Query qref = ref.orderByChild("status").equalTo("new");
            qref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    complist.clear();
                    compclass.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        cc = ds.getValue(ComplainClass.class);
                        //Toast.makeText(getContext(),ds.getValue().toString(),Toast.LENGTH_SHORT).show();
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
        else
        {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/complains/");
            Query qref = ref.orderByChild("status").equalTo("new");
            qref.addValueEventListener(new ValueEventListener() {
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

        return view;
    }

}
