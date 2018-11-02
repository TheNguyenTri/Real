package android.trithe.real.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.trithe.real.database.TypeDAO;
import android.trithe.real.model.TypePet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditTypeActivity extends AppCompatActivity {
    private ImageView imageedit;
    private ImageView fileedit;
    private EditText nameedit;
    private Button btnsaveedit;
    private Button btncanceledit;
    String id;
    private final int SELECT_PHOTO = 101;
    private TypeDAO typeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initView();
        typeDAO = new TypeDAO(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("ID");
        nameedit.setText(bundle.getString("NAME"));
        Glide.with(this).load(bundle.getByteArray("IMAGE")).into(imageedit);
        fileedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
        btnsaveedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TypePet type = new TypePet(id, nameedit.getText().toString(), ImageViewChange(imageedit));
                if (typeDAO.updateType(id, nameedit.getText().toString(), ImageViewChange(imageedit)) > 0) {
                    Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                    finish();
//                    Log.d("nhan", type.toString());
                }
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
                        imageedit.setImageBitmap(bitmap);
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
        imageedit = (ImageView) findViewById(R.id.imageedit);
        fileedit = (ImageView) findViewById(R.id.fileedit);
        nameedit = (EditText) findViewById(R.id.nameedit);
        btnsaveedit = (Button) findViewById(R.id.btnsaveedit);
        btncanceledit = (Button) findViewById(R.id.btncanceledit);
    }
}
