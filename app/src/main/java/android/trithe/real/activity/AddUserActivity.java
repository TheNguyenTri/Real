package android.trithe.real.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.trithe.real.database.UserDAO;
import android.trithe.real.model.User;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddUserActivity extends AppCompatActivity {
    private ImageView imagesignup;
    private ImageView file;
    private EditText username;
    private EditText name;
    private EditText age;
    private EditText gmail;
    private EditText phone;
    private final int SELECT_PHOTO = 101;
    final private int REQUEST_CODE_WRITE_STORAGE = 1;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        initView();
        userDAO = new UserDAO(getApplicationContext());
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasWriteStoragePermission = 0;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hasWriteStoragePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_WRITE_STORAGE);
                    }
                    //return;
                }

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
//                        InputStream inputStream = getContentResolver().openInputStream(uri);
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        imageAvatar.setImageBitmap(bitmap);

//                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                        imageAvatar.setImageBitmap(selectedImage);

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imagesignup.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public byte[] ImageViewChange(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void initView() {
        imagesignup = (ImageView) findViewById(R.id.imagesignup);
        file = (ImageView) findViewById(R.id.file);
        name = (EditText) findViewById(R.id.name);
        username=findViewById(R.id.username);
        age = findViewById(R.id.age);
        gmail = findViewById(R.id.gmail);
        phone = (EditText) findViewById(R.id.phone);


    }


//    public void signup(View view) {
//        User user = new User(signupuser.getText().toString(), signupass.getText().toString(), signupname.getText().toString(), ImageViewChange(imagesignup), signupphone.getText().toString());
//        if (userDAO.inserUser(user) > 0) {
//            Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void adduser(View view) {
        if (validateForm() > 0) {
            User user = new User(username.getText().toString(), name.getText().toString(), Integer.parseInt(age.getText().toString()), phone.getText().toString(), gmail.getText().toString(), ImageViewChange(imagesignup));
            if (userDAO.inserUser(user) > 0) {
                Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private int validateForm() {
        int check = 1;
        if (name.getText().toString().length() == 0) {
            name.setError(getString(R.string.error_emptyname));
            check = -1;
        } else if (name.getText().toString().length() > 20) {
            name.setError(getString(R.string.length20));
            check = -1;
        } else if (age.getText().toString().length() == 0) {
            age.setError(getString(R.string.error_emptyage));
            check = -1;
        } else if (Integer.parseInt(age.getText().toString()) > 100) {
            age.setError(getString(R.string.length10));
            check = -1;
        } else if (phone.getText().toString().length() == 0) {
            phone.setError(getString(R.string.error_emptyphone));
            check = -1;
        } else if ((phone.getText().toString()).length() < 10 || (phone.getText().toString()).length() > 11) {
            phone.setError(getString(R.string.error_emptyphonelength));
            check = -1;
        } else if (gmail.getText().toString().length() == 0) {
            gmail.setError(getString(R.string.error_emptygmail));
            check = -1;
        }

        return check;
    }

    public void back(View view) {
        onBackPressed();
    }

}
