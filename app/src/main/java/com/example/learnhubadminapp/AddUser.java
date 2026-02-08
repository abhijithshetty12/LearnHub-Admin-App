package com.example.learnhubadminapp;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.learnhubadminapp.model.ParentModel;
import com.example.learnhubadminapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddUser extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText selectClass,input_name,input_email,input_password,input_Subjects, stdname,stdemail;
    TextInputLayout stdnametext,stdemailtext;
    ProgressBar progressBar;
    Spinner spinner_usertype;
    private ArrayList<Integer> selectedIndices = new ArrayList<>();
    private boolean[] selectedOptions;
    String name , email,password,usertype;
    List<String> selectedClass , selectedSubject;
    Button submitBtn;
    private String[] options = {"Class 8", "Class 9", "Class 10", "Class 11 Commerce","Class 11 Science","Class 12 Commerce","Class 12 Science"};
    private String[] class8 = {"English","Hindi","Marathi","Maths","Science","History-Civics","Geography"};
    private String[] class9 = {"English","Hindi","Marathi","Maths-I","Maths-II","Science","History-Civics","Geography"};
    private String[] class10 = {"English","Hindi","Marathi","Maths-I","Maths-II","Science-I","Science-II","History-Civics","Geography"};
    private String[] class11Science = {"English","Hindi","Marathi","Maths","Physics","Chemistry","Biology"};
    private String[] class11Commerce = {"English","Hindi","BK","Maths","SP","Economics","OCM"};
    private String[] class12Commerce = {"English","Hindi","BK","Maths","SP","Economics","OCM"};
    private String[] class12Science = {"English","Hindi","Maths","Physics","Chemistry","Biology"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar = findViewById(R.id.adduserToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable upArrow = getResources().getDrawable(R.drawable.backbtn); // Default back icon for AppCompat
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        selectClass = findViewById(R.id.input_standard);
        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        spinner_usertype = findViewById(R.id.input_usertype);
        input_Subjects = findViewById(R.id.input_subject);
        progressBar = findViewById(R.id.progressbar);
        submitBtn = findViewById(R.id.btn_submit);

        stdname  =findViewById(R.id.input_stdname);
        stdemail  =findViewById(R.id.input_stdemail);
        stdnametext  =findViewById(R.id.stdnametext);
        stdemailtext  =findViewById(R.id.stdemailtext);

        selectedClass= new ArrayList<>();
        selectedSubject= new ArrayList<>();
        selectedOptions = new boolean[options.length];
        selectClass.setOnClickListener(v -> chooseClass());
        input_Subjects.setOnClickListener(v -> chooseSubject());
        submitBtn.setOnClickListener(v -> addUser());
        input_name.setOnClickListener(v ->   input_password.setText(input_name.getText()+("@123")));

        spinner_usertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinner_usertype.getSelectedItem().toString();

                if (selectedItem.equals("Parent")) {
                    stdname.setVisibility(View.VISIBLE);
                    stdemail.setVisibility(View.VISIBLE);
                    stdnametext.setVisibility(View.VISIBLE);
                    stdemailtext.setVisibility(View.VISIBLE);
                    input_Subjects.setVisibility(View.GONE);
                    input_email.setVisibility(View.GONE);
                } else {
                    stdname.setVisibility(View.GONE);
                    stdemail.setVisibility(View.GONE);
                    stdnametext.setVisibility(View.GONE);
                    stdemailtext.setVisibility(View.GONE);
                    input_Subjects.setVisibility(View.VISIBLE);
                    input_email.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no item is selected (optional)
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void addUser() {
        changeprogress(true);
        name = input_name.getText().toString().trim();
        email = input_email.getText().toString().trim();
        password = input_password.getText().toString().trim();
        usertype = spinner_usertype.getSelectedItem().toString().trim();
        if (usertype.equals("Parent")){
            addParent();
            return;
        }
        if (name.isEmpty() || email.isEmpty()|| password.isEmpty() || selectedClass.isEmpty()||selectedSubject.isEmpty()) {
            Toast.makeText(AddUser.this, "All fields are required", Toast.LENGTH_SHORT).show();
            changeprogress(false);
            return;
        }
        if (usertype.equals("Students")&&selectedClass.size()>1){
            Toast.makeText(AddUser.this, "Multiple class selected", Toast.LENGTH_SHORT).show();
            selectClass.setText("");
            selectedClass.clear();
            selectedSubject.clear();
            input_Subjects.setText("");
            changeprogress(false);
            return;
        }
        if (isValidEmail(email)) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(usertype);
            UserModel user = new UserModel(name, email, password, usertype, selectedClass,selectedSubject);
            String uid = userRef.push().getKey();
            userRef.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    storedInClassDb(uid,user);
                    addTotalcount();
                    Toast.makeText(AddUser.this, "User added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                    changeprogress(false);
                }
            });
        }else {
            Toast.makeText(AddUser.this, "Email is invalid", Toast.LENGTH_SHORT).show();
            changeprogress(false);
        }

    }

    private void addParent() {
        changeprogress(true);
        name = input_name.getText().toString().trim();
        String  stdEmail = stdemail.getText().toString().trim();
        password = input_password.getText().toString().trim();
        String stdName = stdname.getText().toString().trim();
        if (name.isEmpty() || stdEmail.isEmpty()|| password.isEmpty() ||stdName.isEmpty()|| selectedClass.isEmpty()) {
            Toast.makeText(AddUser.this, "All fields are required", Toast.LENGTH_SHORT).show();
            changeprogress(false);
            return;
        }
        if (usertype.equals("Parent")&&selectedClass.size()>1){
            Toast.makeText(AddUser.this, "Multiple class selected", Toast.LENGTH_SHORT).show();
            selectClass.setText("");
            selectedClass.clear();
            changeprogress(false);
            return;
        }
        if (isValidEmail(stdEmail)) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Parent");
            ParentModel parentModel = new ParentModel(name,password,stdName,stdEmail,selectedClass.get(0));
            String uid = userRef.push().getKey();
            userRef.child(uid).setValue(parentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    addTotalcount();
                    Toast.makeText(AddUser.this, "Parent added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                    changeprogress(false);
                }
            });
        }else {
            Toast.makeText(AddUser.this, "Email is invalid", Toast.LENGTH_SHORT).show();
            changeprogress(false);
        }
    }

    private void storedInClassDb(String uid, UserModel user) {
        changeprogress(true);
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("Classes");

        for (String cls : user.getSelectedClass()) {
            String[] subjectsForClass = getSubjectsForClass(cls);

            for (String sub : user.getSelectedSubject()) {
                if (isSubjectForClass(sub, subjectsForClass)) {
                    DatabaseReference subjectRef = classRef.child(cls).child(sub);
                    subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String classcode;
                            if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                classcode = snapshot.getChildren().iterator().next().getKey();
                            } else {
                                classcode = generateClasscode();
                            }
                            UserModel userModel = new UserModel(user.getName(), user.getEmail(), user.getPassword(), user.getUsertype());
                            subjectRef.child(classcode).child(user.getUsertype()).child(uid).setValue(userModel)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AddUser.this, "Added in Classes", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseError", "Error fetching data: " + error.getMessage());
                            changeprogress(false);
                        }
                    });
                }
            }
        }
        changeprogress(false);
    }


    private String generateClasscode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(10);
        for(int i=0;i<8;i++){
            int randomindex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomindex));
        }
        return  code.toString();
    }

    private String[] getSubjectsForClass(String cls) {
        switch (cls) {
            case "Class 8":
                return class8;
            case "Class 9":
                return class9;
            case "Class 10":
                return class10;
            case "Class 11 Science":
                return class11Science;
            case "Class 11 Commerce":
                return class11Commerce;
            case "Class 12 Science":
                return class12Science;
            case "Class 12 Commerce":
                return class12Commerce;
            default:
                return new String[0];
        }
    }
    private boolean isSubjectForClass(String subject, String[] subjectsForClass) {
        for (String s : subjectsForClass) {
            if (s.equals(subject)) {
                return true;
            }
        }
        return false;
    }
    private void addTotalcount(){
        if (usertype.equals("Parent")){
            DatabaseReference totalRef = FirebaseDatabase.getInstance().getReference("Parent");
            totalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer total = snapshot.child("total").getValue(Integer.class);
                    if (total == null) {
                        totalRef.child("total").setValue(1);
                    } else {
                        totalRef.child("total").setValue(total + 1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            DatabaseReference totalRef = FirebaseDatabase.getInstance().getReference("User").child(usertype);
            totalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer total = snapshot.child("total").getValue(Integer.class);
                    if (total == null) {
                        totalRef.child("total").setValue(1);
                    } else {
                        totalRef.child("total").setValue(total + 1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void chooseClass() {
        selectedClass.clear();

        AlertDialog.Builder builder = new AlertDialog.Builder(AddUser.this);
        builder.setTitle("Select Class");
        builder.setMultiChoiceItems(options, selectedOptions, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (!selectedIndices.contains(which)) {
                    selectedIndices.add(which);
                }
            } else {
                selectedIndices.remove((Integer) which);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder selectedText = new StringBuilder();
            for (int i = 0; i < selectedIndices.size(); i++) {
                selectedText.append(options[selectedIndices.get(i)]);
                selectedClass.add(options[selectedIndices.get(i)]);
                if (i != selectedIndices.size() - 1) {
                    selectedText.append(", ");
                }
            }
            if (selectedIndices.size() > 0) {
                selectClass.setText(selectedText.toString()+" ");
            } else {
                selectClass.setText("Select Options");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    private void chooseSubject() {
        selectedSubject.clear();
        selectedIndices.clear();
        if (selectedClass.isEmpty()) {
            Toast.makeText(AddUser.this, "Please select a class first", Toast.LENGTH_SHORT).show();
            return;
        }// Mapping classes to their respective subjects
        HashMap<String, String[]> classSubjectMap = new HashMap<>();
        classSubjectMap.put("Class 8", class8);
        classSubjectMap.put("Class 9", class9);
        classSubjectMap.put("Class 10", class10);
        classSubjectMap.put("Class 11 Science", class11Science);
        classSubjectMap.put("Class 11 Commerce", class11Commerce);
        classSubjectMap.put("Class 12 Science", class12Science);
        classSubjectMap.put("Class 12 Commerce", class12Commerce);
        // Combine subjects for all selected classes
        List<String> subjects = new ArrayList<>();
        for (String cls : selectedClass) {
            if (classSubjectMap.containsKey(cls)) {
                for (String subject : classSubjectMap.get(cls)) {
                    if (!subjects.contains(subject)) {
                        subjects.add(subject);
                    }
                }
            }
        }

        String[] subjectArray = subjects.toArray(new String[0]);
        boolean[] selectedSubjects = new boolean[subjectArray.length];

        // Create an AlertDialog for subject selection
        AlertDialog.Builder builder = new AlertDialog.Builder(AddUser.this);
        builder.setTitle("Select Subjects");
        builder.setMultiChoiceItems(subjectArray, selectedSubjects, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (!selectedIndices.contains(which)) {
                    selectedIndices.add(which);
                }
            } else {
                selectedIndices.remove((Integer) which);
            }
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder selectedText = new StringBuilder();
            for (int i = 0; i < selectedIndices.size(); i++) {
                selectedText.append(subjectArray[selectedIndices.get(i)]);
                selectedSubject.add(subjectArray[selectedIndices.get(i)]);
                if (i != selectedIndices.size() - 1) {
                    selectedText.append(", ");
                }
            }
            if (selectedIndices.size() > 0) {
                input_Subjects.setText(selectedText.toString());
                Log.d("Subjects", selectedSubject.toString());
            } else {
                input_Subjects.setText("No Subjects Selected");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void clearFields() {
        input_name.setText("");
        input_email.setText("");
        input_password.setText("");
        selectClass.setText("");
        spinner_usertype.setSelection(0);
        selectedClass.clear();
        selectedSubject.clear();
        input_Subjects.setText("");
        stdemail.setText("");
        stdname.setText("");
    }
    private void changeprogress(boolean progress){
        if (progress){
            progressBar.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            submitBtn.setVisibility(View.VISIBLE);
        }
    }
    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}