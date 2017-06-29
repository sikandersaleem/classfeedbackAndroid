package com.example.classcomplain;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.R.attr.color;
import static android.R.attr.value;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    public List<ComplainClass> compclass;

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        RVAdapter rva=new RVAdapter();
        public CardView cv;
        TextView subject;
        TextView from;
        TextView date;
        List<ComplainClass> compclass1;
        PersonViewHolder(final View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            subject = (TextView)itemView.findViewById(R.id.subject);
            subject.setSingleLine(true);
            date = (TextView)itemView.findViewById(R.id.date);
            from = (TextView)itemView.findViewById(R.id.from);
            this.compclass1 = PersonViewHolder.this.rva.compclass;
        }

    }
    public RVAdapter(List<ComplainClass> persons){
        this.compclass = persons;
    }
    RVAdapter(){

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {

        personViewHolder.subject.setText(compclass.get(i).subject);
        //personViewHolder.from.setText(compclass.get(i).childid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+compclass.get(i).parentid+"/name/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                personViewHolder.from.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        personViewHolder.date.setText(compclass.get(i).datetime);

        if(compclass.get(i).status.equals("new"))
            personViewHolder.cv.setCardBackgroundColor(Color.parseColor("#004236"));

        else if(compclass.get(i).status.equals("inprogress"))
            personViewHolder.cv.setCardBackgroundColor(Color.parseColor("#003066"));

        else if(compclass.get(i).status.equals("closed"))
            personViewHolder.cv.setCardBackgroundColor(Color.parseColor("#892034"));

        personViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent geoshow=new Intent(v.getContext(),ComplainDetailAcitvity.class);
                geoshow.putExtra("uservalue",compclass.get(personViewHolder.getPosition()).parentid);
                geoshow.putExtra("compvalue",compclass.get(personViewHolder.getPosition()).complainlink);
                v.getContext().startActivity(geoshow);
                //Toast.makeText(v.getContext(),compclass.get(personViewHolder.getPosition()).subject,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return compclass.size();
    }
}
