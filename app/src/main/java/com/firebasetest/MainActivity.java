package com.firebasetest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference database;
    private EditText edtId, edtName, edtAddress;
    private Button btnSave, btnUpdate, btnDelete;
    private TextView strDatat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance().getReference();

        init();
        getAllData();

    }

    private void init() {
        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);

        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        strDatat = findViewById(R.id.strDatat);

        btnSave.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {
            hideKeyboard();
            String id = edtId.getText().toString();
            String nm = edtName.getText().toString();
            String address = edtAddress.getText().toString();
            checkId(id);
            database.child(id).setValue(new Student(nm, address));
            getAllData();
        }
        if (v.getId() == R.id.btnUpdate) {
            hideKeyboard();
            String id = edtId.getText().toString();
            String nm = edtName.getText().toString();
            String address = edtAddress.getText().toString();
            checkId(id);
            checkValueIsAvailable(id, new Student(nm, address));
            getAllData();

        }
        if (v.getId() == R.id.btnDelete) {
            hideKeyboard();
            String id = edtId.getText().toString();
            checkId(id);
            database.child(id).removeValue();
            getAllData();
        }
    }

    private void hideKeyboard() {
        try {
            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllData() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("!_!_!", dataSnapshot + "");
                StringBuilder sb = new StringBuilder();
                for (DataSnapshot imageSnapshot : dataSnapshot.getChildren()) {
                    sb.append(
                            imageSnapshot.getKey()
                                    + " " +
                                    imageSnapshot.child("name").getValue(String.class)
                                    + " " +
                                    imageSnapshot.child("address").getValue(String.class)
                                    + "\n"
                    );
                }
                Log.e("!_@_@", sb.toString());
                strDatat.setText(sb.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkValueIsAvailable(final String id, final Student student) {
        database.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("!_+@_@+", dataSnapshot.getChildrenCount() + "----");
                if (dataSnapshot.getChildrenCount() > 0) {
                    HashMap hmData = new HashMap();
                    hmData.put("name", student.name);
                    hmData.put("address", student.address);

                    HashMap hmUsrAdd = new HashMap();
                    hmUsrAdd.put(id, hmData);

                    database.updateChildren(hmUsrAdd);
                } else {
                    showMSG("No Value found for update..");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("!_+@_@+", databaseError.getMessage() + "----");
            }
        });
    }

    public void checkId(String id) {
        database.child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                showMSG("Value added ");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                showMSG("Value changed ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                showMSG("Value removed ");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                showMSG("Value moved ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showMSG("cancelled ");
            }
        });
    }

    public void showMSG(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
