package com.example.learnhubadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.learnhubadminapp.Fragment.FacultyList;
import com.example.learnhubadminapp.Fragment.ParentList;
import com.example.learnhubadminapp.Fragment.StudentList;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPanel extends AppCompatActivity {
    Toolbar toolbar;
    ImageView img,img2,img1;
    TextView totalStudent ,totalFaculty,totalParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Admin Panel");
        toolbar.getOverflowIcon().setTint(getResources().getColor(R.color.white));
        toolbar.setTitleTextColor(getResources().getColor(R.color.BrightYellow));
        setSupportActionBar(toolbar);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        totalFaculty = findViewById(R.id.totalFaculty);
        totalStudent = findViewById(R.id.totalStudent);
        totalParent= findViewById(R.id.totalParent);
        img = findViewById(R.id.img);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img.setOnClickListener(v -> startActivity(new Intent(AdminPanel.this, AddUser.class)));
        img1.setOnClickListener(v -> startActivity(new Intent(AdminPanel.this, AddUser.class)));
        img2.setOnClickListener(v -> startActivity(new Intent(AdminPanel.this, AddUser.class)));
        fetchTotalStudentAndFaculty();
        replaceFragment(new FacultyList());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        replaceFragment(new FacultyList());
                        break;
                    case 1:
                        replaceFragment(new StudentList());
                        break;
                    case 2:
                        replaceFragment(new ParentList());
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.adduser);
        LinearLayout menuItemLayout = (LinearLayout) menuItem.getActionView();
        TextView textView = menuItemLayout.findViewById(R.id.menu_item_title);
        menuItemLayout.setOnClickListener(v -> onOptionsItemSelected(menuItem));
        textView.setText("Add User");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("Ids","item id "+item.getItemId());
        Log.d("Ids","user id "+R.id.adduser);
        if ( (item.getItemId())==R.id.adduser ){
            Toast.makeText(this, "Add user selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminPanel.this, AddUser.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack for navigation
        fragmentTransaction.commit();
    }

    private void fetchTotalStudentAndFaculty(){

            DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("User").child("Faculty");
            facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                  int  total = snapshot.child("total").getValue(Integer.class);
                   totalFaculty.setText(String.valueOf(total));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("User").child("Students");
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int  total = snapshot.child("total").getValue(Integer.class);
                totalStudent.setText(String.valueOf(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference("Parent");
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int  total = snapshot.child("total").getValue(Integer.class);
                totalParent.setText(String.valueOf(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void refreshTotal() {
        fetchTotalStudentAndFaculty();
    }
    @Override
    public void onResume() {

        super.onResume();
        refreshTotal();
    }
}
