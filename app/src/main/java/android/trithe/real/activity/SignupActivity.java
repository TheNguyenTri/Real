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
import android.trithe.real.LoginActivity;
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

public class SignupActivity extends AppCompatActivity {
    private ImageView imagesignup;
    private ImageView file;
    private EditText signupuser;
    private EditText signupname;
    private EditText signupass;
    private EditText signupconfirm;
    private EditText signupphone;
    private final int SELECT_PHOTO = 101;
    final private int REQUEST_CODE_WRITE_STORAGE = 1;
    Uri uri;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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

//                        InputStream inputStream = getBaseContext().getContentResolver().openInputStream(uri);
//                        Bitmap bm = BitmapFactory.decodeStream(inputStream);
//                        imageAvatar.setImageBitmap(bm);

//                        final Uri imageUri = imageReturnedIntent.getData();
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
        signupuser = (EditText) findViewById(R.id.signupuser);
        signupname = (EditText) findViewById(R.id.signupname);
        signupass = (EditText) findViewById(R.id.signupass);
        signupconfirm = (EditText) findViewById(R.id.signupconfirm);
        signupphone = (EditText) findViewById(R.id.signupphone);

    }

    public void signin(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void signup(View view) {
        User user = new User(signupuser.getText().toString(), signupass.getText().toString(), signupname.getText().toString(), ImageViewChange(imagesignup), signupphone.getText().toString());
        if (userDAO.inserUser(user) > 0) {
            Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    private int validateForm() {
        int check = 1;
        if (signupuser.getText().toString().length() == 0) {
            signupuser.setError(getString(R.string.error_empty));
            check = -1;
        } else if (signupuser.getText().toString().length() > 50) {
            signupuser.setError(getString(R.string.length50));
            check = -1;
        } else if (signupass.getText().toString().length() == 0) {
            signupass.setError(getString(R.string.error_emptyps));
            check = -1;
        } else if ((signupass.getText().toString()).length() < 6 || (signupass.getText().toString()).length() > 50) {
            signupphone.setError(getString(R.string.error_passlength));
            check = -1;
        } else if (signupconfirm.getText().toString().equals("")) {
            signupconfirm.setError(getString(R.string.empty_confirmpassword));
            check = -1;
        } else if (!(signupconfirm.getText().toString()).equals(signupass.getText().toString())) {
            signupconfirm.setError(getString(R.string.error_likepw));
            check = -1;
        } else if (signupname.getText().toString().length() == 0) {
            signupname.setError(getString(R.string.error_emptyname));
            check = -1;
        } else if (signupname.getText().toString().length() > 20) {
            signupname.setError(getString(R.string.length20));
            check = -1;
        } else if (signupphone.getText().toString().length() == 0) {
            signupphone.setError(getString(R.string.error_emptyphone));
            check = -1;
        } else if ((signupphone.getText().toString()).length() < 10 || (signupphone.getText().toString()).length() > 11) {
            signupphone.setError(getString(R.string.error_emptyphonelength));
            check = -1;
        }
        return check;
    }
}
