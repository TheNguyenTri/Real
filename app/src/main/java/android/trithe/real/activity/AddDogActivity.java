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
import android.trithe.real.database.DogDAO;
import android.trithe.real.database.UserDAO;
import android.trithe.real.model.Dog;
import android.trithe.real.model.User;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddDogActivity extends AppCompatActivity {
    private ImageView imageaddog;
    private ImageView fileaddog;
    private EditText idaddog;
    private EditText nameaddog;
    private EditText ageaddog;
    private EditText priceaddog;
    private final int SELECT_PHOTO = 101;
    final private int REQUEST_CODE_WRITE_STORAGE = 1;
    private DogDAO dogDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);
        initView();
        dogDAO = new DogDAO(getApplicationContext());
        fileaddog.setOnClickListener(new View.OnClickListener() {
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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageaddog.setImageBitmap(bitmap);
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
        imageaddog = (ImageView) findViewById(R.id.imageaddog);
        fileaddog = (ImageView) findViewById(R.id.fileaddog);
        idaddog = (EditText) findViewById(R.id.idaddog);
        nameaddog = (EditText) findViewById(R.id.nameaddog);
        ageaddog = (EditText) findViewById(R.id.ageaddog);
        priceaddog = (EditText) findViewById(R.id.priceaddog);
    }

    public void addog(View view) {
        Dog dog = new Dog(idaddog.getText().toString(), nameaddog.getText().toString(), Integer.parseInt(ageaddog.getText().toString()), Float.parseFloat(priceaddog.getText().toString()), ImageViewChange(imageaddog));
        if (dogDAO.inserDog(dog) > 0) {
            Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    public void outadd(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
