package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.firebase.ui.database.FirebaseListAdapter;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
String keyget,keyget1;
    Object value;
    String addr;
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private ProgressDialog pDialog;
    ListView listforchat;
    EditText input;
    String encodedImage="";

    SharedPreferences editors;
    String campusid;
    //ChatMessage chatmessage;
    FloatingActionButton fab;
    ImageButton attachment;
    Query qref;
    Bitmap selectedimage;
    private Uri imageUri;
    DatabaseReference dbref;
    private FirebaseListAdapter<ChatMessage> adapter;
    //List<String> complist= new ArrayList<String>();
    List<messages> msglist;//
    ListAdapter ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        msglist= new ArrayList<messages>();

        editors = getSharedPreferences("campus", MODE_PRIVATE);
        campusid = editors.getString("campus","");

        Bundle bundle = getIntent().getExtras();
        keyget = bundle.getString("keyvalue");
        keyget1 = bundle.getString("keyvalue1");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        input = (EditText) findViewById(R.id.input);

        attachment = (ImageButton) findViewById(R.id.attachment);
        attachment.setOnClickListener(this);

        listforchat = (ListView) findViewById(R.id.list_of_messages);
        listforchat.setFocusable(true);
        listforchat.requestFocus();

        String str=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (str.contains("Admin:")==true)
        {
            registerForContextMenu(listforchat);
            addr ="allcampus/"+campusid+"/"+keyget+"/complains/"+keyget1+"/messages/";
            dbref = FirebaseDatabase.getInstance().getReference(addr);
            qref = dbref;
        }
        else
        {
            addr = "allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/complains/" + keyget1 + "/messages/";
            dbref = FirebaseDatabase.getInstance().getReference(addr);
            qref = dbref.orderByChild("type").equalTo("public");
        }
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(addr);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, qref) {
            @Override
            protected void populateView(View v, ChatMessage model, final int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                ImageView image = (ImageView)v.findViewById(R.id.messge_image);

                //image.setVisibility(View.GONE);

                //msglist.add(new messages(model.message,model.sender,model.time,model.type,model.messagelink));
                if(!model.image.equals(""))
                {
                    byte[] decodedString = Base64.decode(model.image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Bitmap ThumbImage = Bitmap.createScaledBitmap(decodedByte, 64, 64, false);
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(ThumbImage);
                }
                image.setPadding(8,8,8,8);
                image.setBackgroundColor(Color.parseColor("#c0c0c0"));
                // Set their text
                messageText.setText(model.message);
                messageUser.setText(model.sender);
                messageTime.setText(model.time);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(ChatActivity.this,imageViewActivity.class).putExtra("img",msglist.get(position).image));
                    }
                });;
            }
        };
        adapter.notifyDataSetChanged();
        listforchat.setAdapter(adapter);
        DatabaseReference makelist = FirebaseDatabase.getInstance().getReference(addr);
        makelist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                msglist.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    msglist.add(new messages(ds.child("message").getValue().toString(),ds.child("time").getValue().toString(),ds.child("sender").getValue().toString(),ds.child("type").getValue().toString(),ds.child("messagelink").getValue().toString(),ds.child("image").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pDialog.dismiss();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref_notification = FirebaseDatabase.getInstance().getReference(addr);
                DatabaseReference newref = ref_notification.push();

                //ref_notification.child(newref.getKey()).setValue(new ChatMessage(input.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),currentDate(),"public"));
                String str=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (str.contains("Admin:")==true)
                {
                    ref_notification.child(newref.getKey()).setValue(new ChatMessage(input.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),currentDate(),"private", newref.getKey(),encodedImage));
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+keyget+"/complains/"+keyget1);
                    ref.child("status").setValue("inprogress");
                    ref.child("state").setValue("assigntoparent");
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/"+keyget1);
                    ref1.child("status").setValue("inprogress");
                    ref1.child("state").setValue("assigntoparent");
                }
                else
                {
                    ref_notification.child(newref.getKey()).setValue(new ChatMessage(input.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),currentDate(),"public",newref.getKey(),encodedImage));
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/complains/" + keyget1);
                    ref.child("state").setValue("assigntoschool");
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("allcampus/"+campusid+"/admin/complains/"+keyget1);
                    ref1.child("state").setValue("assigntoschool");
                }

                //ad.notify();
                input.setText("");
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list_of_messages) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()) {
            case R.id.type:
            {
                Toast.makeText(getApplicationContext(),msglist.get(info.position).messagelink+"   ,   "+info.position,Toast.LENGTH_SHORT).show();
                DatabaseReference typeref = FirebaseDatabase.getInstance().getReference(addr).child(msglist.get(info.position).messagelink);
                typeref.child("type").setValue("public");

            }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.attachment)
        {
            LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup, null);
            final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);

            ImageButton camera = (ImageButton) popupView.findViewById(R.id.camerap);
            ImageButton gallary = (ImageButton) popupView.findViewById(R.id.galleryp);
            camera.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    popupWindow.dismiss();
                    String filename = System.currentTimeMillis() + ".jpg";
                    checkpermission();
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                }});
            gallary.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    popupWindow.dismiss();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_GALLERY);
                }});

            popupWindow.showAsDropDown(attachment, 50, -30);
        }

    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK ) {

            final Uri imageUri = data.getData();
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                selectedimage = BitmapFactory.decodeStream(imageStream);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedimage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteFormat = stream.toByteArray();
            encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        }

    }
    public boolean checkpermission()
    {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Log.v(TAG,"Permission is granted");
            //File write logic here
            return true;
        }
        return  true;
    }
}
