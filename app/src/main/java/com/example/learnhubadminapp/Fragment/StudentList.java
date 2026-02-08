package com.example.learnhubadminapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnhubadminapp.Adapter.RecyclerViewAdapterUser;
import com.example.learnhubadminapp.R;
import com.example.learnhubadminapp.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentList extends Fragment {
    RecyclerView studentList;
    RecyclerViewAdapterUser userAdapter;
    List<UserModel> userModelList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public StudentList() {
        // Required empty public constructor
    }


    public static StudentList newInstance(String param1, String param2) {
        StudentList fragment = new StudentList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_student_list, container, false);

        userModelList=new ArrayList<>();
        studentList = view.findViewById(R.id.studentList);
        studentList.setHasFixedSize(true);
        studentList.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new RecyclerViewAdapterUser(getContext(),userModelList);
        studentList.setAdapter(userAdapter);
        fetchUser();
        return view;
    }
    private void fetchUser() {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("User").child("Students");
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.hasChildren()) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            userModelList.add(new UserModel(userModel.getName(), userModel.getEmail(), userModel.getPassword(), userModel.getUsertype(), userModel.getSelectedClass(),userModel.getSelectedSubject()));
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}