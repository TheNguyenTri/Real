package android.trithe.real.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.database.PetDAO;
import android.trithe.real.database.TypeDAO;
import android.trithe.real.model.Pet;
import android.trithe.real.model.TypePet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PetsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imgpet;
    private EditText edName;
    private Spinner sploaipet;
    private EditText edage;
    private Spinner sphealth;
    private EditText edweight;
    private Button btnSave;
    private final int SELECT_PHOTO = 101;
    private PetDAO petDAO;
    private TypeDAO typeDAO;
    private List<TypePet> listtype = new ArrayList<>();
    private final List<String> heal = new ArrayList<>();
    private String id;
    private String magiongloai;
    private String health;
    private String gender;
    private final int CAPTURE_PHOTO = 102;
    private RadioGroup rg;
    private String idupdate;
    //    String value;
    private RadioButton radioButton, radioButtonfe;
    private final List<Pet> list = new ArrayList<>();
//    final private int REQUEST_CODE_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        initView();
        setSupportActionBar(toolbar);
        // radio
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioButton.isChecked()) {
                    gender = "Male";
                } else if (radioButtonfe.isChecked()) {
                    gender = "Female";
                }
            }
        });
        if (radioButton.isChecked()) {
            gender = "Male";
        } else if (radioButtonfe.isChecked()) {
            gender = "Female";
        }

        sploaipet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                magiongloai = listtype.get(sploaipet.getSelectedItemPosition()).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getType();


        //health

        sphealth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                health = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //
        heal.add("Strong");
        heal.add("Normal");
        heal.add("Weak");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, heal);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sphealth.setAdapter(dataAdapter);
        //
        imgpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LayoutInflater inflater = LayoutInflater.from(PetsActivity.this);
                final View view = inflater.inflate(R.layout.dialog_image, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(PetsActivity.this);
                builder.setTitle("Choose");
                builder.setView(view);
                LinearLayout iconcamera = view.findViewById(R.id.iconcamera);
                LinearLayout iconimage = view.findViewById(R.id.iconimage);
                final AlertDialog dialog1 = builder.show();
                iconimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                        dialog1.dismiss();
                    }
                });
                iconcamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(i, CAPTURE_PHOTO);
                        dialog1.dismiss();
                    }
                });
            }
        });

//botom
        getintent();
    }

    private void getintent() {
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {
            idupdate = bundle.getString("ID");
            edName.setText(bundle.getString("NAME"));
            String loaiupdate = bundle.getString("LOAI");
            edage.setText(bundle.getString("AGE"));
            edweight.setText(bundle.getString("WEIGHT"));
            final String genders = bundle.getString("GENDER");
            if (genders.equals("Male")) {
                radioButton.setChecked(true);
            } else {
                radioButtonfe.setChecked(true);
            }

            sploaipet.setSelection(checkPositionType(loaiupdate));
            sphealth.setSelection(checkPositionHealth(bundle.getString("HEALTH")));
            Glide.with(PetsActivity.this).load(bundle.getByteArray("IMAGE")).into(imgpet);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validate() > 0) {
//                        Pet pet = new Pet(idupdate, edName.getText().toString(), magiongloai, Integer.parseInt(edage.getText().toString()), health, Float.parseFloat(edweight.getText().toString()), gender, ImageViewChange(imgpet));
                        if (petDAO.updatePet(idupdate, edName.getText().toString(), magiongloai, Integer.parseInt(edage.getText().toString()), Float.parseFloat(edweight.getText().toString()), health, gender, ImageViewChange(imgpet)) > 0) {

                            Toast.makeText(getApplicationContext(), getString(R.string.alertsuccessfully), Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                }
            });

        } else {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Random random = new Random();
                    id = String.valueOf(random.nextInt());
                    if (validate() > 0) {
                        Pet pet = new Pet(id, edName.getText().toString(), magiongloai, Integer.parseInt(edage.getText().toString()), health, Float.parseFloat(edweight.getText().toString()), gender, ImageViewChange(imgpet));
                        if (petDAO.insertPet(pet) > 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.alertsuccessfully) + " " + gender, Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Log.d("a", list.toString());
                        }
                    }
                }
            });
        }
    }

    private int checkPositionType(String strTheLoai) {
        for (int i = 0; i < listtype.size(); i++) {
            if (strTheLoai.equals(listtype.get(i).getName())) {
                return i;
            }
        }
        return 0;
    }

    private int checkPositionHealth(String health) {
        for (int i = 0; i < heal.size(); i++) {
            if (health.equals(heal.get(i))) {
                return i;
            }
        }
        return 0;
    }

    private void getType() {
        typeDAO = new TypeDAO(this);
        listtype = typeDAO.getAllType();
        ArrayAdapter<TypePet> dataAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, listtype);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sploaipet.setAdapter(dataAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imgpet.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CAPTURE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bmp = data.getExtras().getParcelable("data");
                    imgpet.setImageBitmap(bmp);
                }
                break;
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        imgpet = findViewById(R.id.imgpet);
        edName = findViewById(R.id.edName);
        sploaipet = findViewById(R.id.sploaipet);
        edage = findViewById(R.id.edage);
        sphealth = findViewById(R.id.sphealth);
        edweight = findViewById(R.id.edweight);
        btnSave = findViewById(R.id.btnSave);
        rg = findViewById(R.id.rdgroup);
        petDAO = new PetDAO(getApplicationContext());
        typeDAO = new TypeDAO(getApplicationContext());
        radioButton = findViewById(R.id.rdMale);
        radioButtonfe = findViewById(R.id.rdFemale);
    }

    private int validate() {
        int check = -1;
        if (edName.getText().toString().equals("")) {
            edName.setError(getString(R.string.empty));
            return check;
        } else if (edName.getText().toString().length() > 20) {
            edName.setError(getString(R.string.maximum20));
            return check;
        } else if (edage.getText().toString().equals("")) {
            edage.setError(getString(R.string.empty));
            return check;
        } else if (Integer.parseInt(edage.getText().toString()) > 10) {
            edage.setError(getString(R.string.agemaximum));
            return check;
        } else if (edweight.getText().toString().equals("")) {
            edweight.setError(getString(R.string.empty));
            return check;
        } else if (Float.parseFloat(edweight.getText().toString()) < 1) {
            edweight.setError(getString(R.string.min1));
            return check;
        }
        return 1;
    }


    private byte[] ImageViewChange(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    public void back(View view) {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
