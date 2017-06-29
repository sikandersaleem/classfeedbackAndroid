package com.example.classcomplain;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationsActivity extends AppCompatActivity {

    private FirebaseListAdapter<notificationreceived> adapter;
    ListView notificationlist;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        notificationlist = (ListView) findViewById(R.id.notifylist);
        notificationlist.setFocusable(true);
        notificationlist.requestFocus();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications/");
        adapter = new FirebaseListAdapter<notificationreceived>(this, notificationreceived.class,
                R.layout.notifications, ref) {
            @Override
            protected void populateView(View v, notificationreceived model, int position) {
                TextView messageText = (TextView)v.findViewById(R.id.notificationmessage);
                TextView messageTime = (TextView)v.findViewById(R.id.notificationtime);

                messageText.setText(model.Message);
                messageTime.setText(model.Date);
            }
        };
        adapter.notifyDataSetChanged();
        notificationlist.setAdapter(adapter);
        pDialog.dismiss();
    }

    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
