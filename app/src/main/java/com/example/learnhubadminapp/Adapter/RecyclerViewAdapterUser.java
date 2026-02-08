package com.example.learnhubadminapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnhubadminapp.AdminPanel;
import com.example.learnhubadminapp.R;
import com.example.learnhubadminapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewAdapterUser.ViewHolder> {
    Context context;
    List<UserModel> userModelList;

    public RecyclerViewAdapterUser(Context context, List<UserModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_show_user,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterUser.ViewHolder holder, int position) {
      UserModel userModel = userModelList.get(position);
      holder.username.setText(userModel.getName());
      holder.userClass.setText(String.join(",",userModel.getSelectedClass()));

        DatabaseReference stdref = FirebaseDatabase.getInstance().getReference((userModel.getUsertype()));
        stdref.child(userModel.getEmail().replace(".", ",")).child("imageUrl").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String imageUrl = task.getResult().getValue(String.class);
                if (imageUrl != null) {
                    Glide.with(context).
                            load(imageUrl)
                            .fitCenter()
                            .placeholder(R.drawable.profileimg)
                            .into(holder.imageView);
                } else {
                    holder.imageView.setImageResource(R.drawable.profileimg);
                }
            }
        });

        holder.deleteUser.setOnClickListener(v -> deleleUser(position));
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    private void deleleUser(int position){
        UserModel userModel = userModelList.get(position);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userModel.getUsertype());
         userRef.orderByChild("email").equalTo(userModel.getEmail()).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound =false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userid = dataSnapshot.getKey();
                    String name = dataSnapshot.child("name").getValue(String.class);
                    if (name.equals(userModel.getName())) {
                        userFound=true;
                        new AlertDialog.Builder(context)
                                .setTitle("Delete User")
                                .setMessage("Are you sure you want to delete this user?")
                                .setPositiveButton("Yes", ((dialog, which) -> {
                                    deleteFromClassDb(userid,userModel);
                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            userModelList.remove(position);
                                            notifyItemRemoved(position);
                                            updateTotalStudentCount(userModel.getUsertype());
                                            Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                })).setNegativeButton("No", null)
                                .show();
                        break;
                    }
                }
                if (!userFound) {
                    Toast.makeText(context, "User not found or mismatch in data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void deleteFromClassDb(String userid, UserModel userModel) {
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("Classes");

        // Iterate through selected classes
        for (String cls : userModel.getSelectedClass()) {
            classRef.child(cls).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot classSnapshot = task.getResult();

                    // Iterate through selected subjects
                    for (String subject : userModel.getSelectedSubject()) {
                        DataSnapshot subjectSnapshot = classSnapshot.child(subject);

                        if (subjectSnapshot.exists()) {
                            for (DataSnapshot classcodeSnapshot : subjectSnapshot.getChildren()) {
                                DataSnapshot userTypeSnapshot = classcodeSnapshot.child(userModel.getUsertype());

                                // Check if the user exists and delete it
                                if (userTypeSnapshot.exists() && userTypeSnapshot.hasChild(userid)) {
                                    DatabaseReference userRef = userTypeSnapshot.child(userid).getRef();
                                    userRef.removeValue().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(context, "User removed from " + subject, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to remove user from " + subject, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "User not found in " + subject, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(context, "Subject " + subject + " does not exist in " + cls, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch class " + cls, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void updateTotalStudentCount(String usertype) {
        DatabaseReference adminPanelRef = FirebaseDatabase.getInstance().getReference("User").child(usertype);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView username , userClass;
        public ImageButton deleteUser;
        public CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            username=itemView.findViewById(R.id.username);
            userClass=itemView.findViewById(R.id.userclass);
            deleteUser=itemView.findViewById(R.id.userDeletebtn);
            imageView=itemView.findViewById(R.id.profileimg);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
