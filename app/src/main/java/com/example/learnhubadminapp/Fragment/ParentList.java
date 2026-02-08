package com.example.learnhubadminapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnhubadminapp.Adapter.RecyclerViewAdapterParent;
import com.example.learnhubadminapp.R;
import com.example.learnhubadminapp.model.ParentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParentList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParentList extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ParentList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParentList.
     */
    // TODO: Rename and change types and number of parameters

    RecyclerView parentList;
    RecyclerViewAdapterParent adapterParent;
    List<ParentModel> parentModelList;
    public static ParentList newInstance(String param1, String param2) {
        ParentList fragment = new ParentList();
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
        View view= inflater.inflate(R.layout.fragment_parent_list, container, false);
    parentModelList=new ArrayList<>();
        parentList= view.findViewById(R.id.parentList);
        parentList.setHasFixedSize(true);
        parentList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterParent = new RecyclerViewAdapterParent(getContext(),parentModelList);
        parentList.setAdapter(adapterParent);
        fetchParent();
    return view;
    }
    private void fetchParentData() {
        // Test with hardcoded data
        parentModelList.clear();
        parentModelList.add(new ParentModel("John Doe", "password", "Alice", "alice@example.com", "Class 10"));
        parentModelList.add(new ParentModel("Jane Smith", "password123", "Bob", "bob@example.com", "Class 12"));

        adapterParent.notifyDataSetChanged();
    }

    private void fetchParent() {
        parentModelList.clear();  // Clear the existing list to avoid duplicates

        // Reference to the Parent node
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference("Parent");

        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Loop through the data snapshot to fetch all fields
                for (DataSnapshot uidSnapshot : snapshot.getChildren()) {
                    String parentID = uidSnapshot.getKey();
                    String parentName = uidSnapshot.child("parentName").getValue(String.class);
                    String parentPassword = uidSnapshot.child("parentPassword").getValue(String.class);
                    String stdClass = uidSnapshot.child("stdClass").getValue(String.class);
                    String stdEmail = uidSnapshot.child("stdEmail").getValue(String.class);
                    String stdName = uidSnapshot.child("stdName").getValue(String.class);

                    // Check if the parentName field is not null
                    if (parentName != null) {
                        // Create a new ParentModel with all the fields and add it to the list
                        ParentModel parentModel = new ParentModel(parentName, parentPassword, stdName, stdEmail, stdClass,parentID);
                        parentModelList.add(parentModel);
                    }
                }

                // Notify the adapter that the data has changed
                adapterParent.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
                Log.e("ParentData", "Failed to read parent data", error.toException());
            }
        });
    }

}

