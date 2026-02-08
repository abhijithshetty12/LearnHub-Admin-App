package com.example.learnhubadminapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhubadminapp.AdminPanel;
import com.example.learnhubadminapp.R;
import com.example.learnhubadminapp.model.ParentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerViewAdapterParent extends RecyclerView.Adapter<RecyclerViewAdapterParent.ViewHolder> {
    Context context;
    List<ParentModel> parentModelList;

    public RecyclerViewAdapterParent(Context context, List<ParentModel> parentModelList) {
        this.context = context;
        this.parentModelList = parentModelList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterParent.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.showparent,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterParent.ViewHolder holder, int position) {
        ParentModel parentModel = parentModelList.get(position);
        holder.parentName.setText(parentModel.getParentName());
        holder.studentName.setText(parentModel.getStdName());
        holder.studentClass.setText(parentModel.getStdClass());
        String parentid  =parentModel.getParentId();
        holder.deleteuser.setOnClickListener(v -> deleteUser(position,parentid));
    }

    private void deleteUser(int position, String parentId) {
        // Create an AlertDialog for confirmation
        new AlertDialog.Builder(context)
                .setTitle("Delete Parent")
                .setMessage("Are you sure you want to delete this parent?")
                .setCancelable(false) // Prevent dismissing by tapping outside the dialog
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with the deletion
                    DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference("Parent");

                    // Remove the parent data using the parent ID (unique key)
                    parentRef.child(parentId).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Notify the user that the deletion was successful
                                    Toast.makeText(context, "Parent deleted successfully", Toast.LENGTH_SHORT).show();
                                    updateTotalStudentCount();
                                    // Remove the item from the list and notify the adapter
                                    parentModelList.remove(position);
                                    notifyItemRemoved(position);
                                } else {
                                    // Show an error message if deletion fails
                                    Toast.makeText(context, "Failed to delete parent", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog if the user chooses 'No'
                    dialog.dismiss();
                })
                .show();
    }
    private void updateTotalStudentCount() {
        DatabaseReference adminPanelRef = FirebaseDatabase.getInstance().getReference("Parent");
        adminPanelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentCount = snapshot.child("total").getValue(Integer.class);
                if (currentCount != null && currentCount > 0) {
                    adminPanelRef.child("total").setValue(currentCount - 1);
                    AdminPanel adminPanel = (AdminPanel) context;
                    adminPanel.refreshTotal();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to update total students: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView parentName, studentName, studentClass;
        ImageButton deleteuser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentName = itemView.findViewById(R.id.pusername);
            studentName = itemView.findViewById(R.id.pstdusername);
            studentClass = itemView.findViewById(R.id.pstduserclass);
            deleteuser = itemView.findViewById(R.id.puserDeletebtn);

        }
    }
}
