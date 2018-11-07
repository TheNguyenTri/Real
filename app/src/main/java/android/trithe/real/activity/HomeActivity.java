package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.adapter.TypeAdapter;
import android.trithe.real.database.TypeDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.TypePet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    FloatingActionButton fab1, fab2, fab3;
    Animation fabOpen, fabClose, fabrotate, fabbackrotate;
    boolean isOpen = false;
    private TypeDAO typeDAO;
    private RecyclerView recyclerView;
    private TypeAdapter adapter;
    private List<TypePet> list;
    private Toolbar toolbar;
    private final int SELECT_PHOTO = 101;
    private ImageView image;
    private EditText name;
    private Button btnsave;
    private Button btncancel;
    private TextView txt1, txt2;
    String id;
    private BottomNavigationView bottomNavigationView;
    Uri uri;
    private final int CAPTURE_PHOTO = 102;

    @SuppressLint({"RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        typeDAO = new TypeDAO(getApplicationContext());
        list = new ArrayList<>();
        list = typeDAO.getAllType();
        adapter = new TypeAdapter(this, list, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {
                final LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                final View view = inflater.inflate(R.layout.dialog_pet, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Edit Pet");
                builder.setView(view);
                image = (ImageView) view.findViewById(R.id.imageedit);
                name = (EditText) view.findViewById(R.id.nameedit);
                btnsave = (Button) view.findViewById(R.id.btnsaveedit);
                btncancel = (Button) view.findViewById(R.id.btncanceledit);
                id = list.get(position).getId();
                name.setText(list.get(position).getName());
                final AlertDialog dialog = builder.show();
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                    }
                });
                Glide.with(HomeActivity.this).load(list.get(position).getImage()).into(image);
                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TypePet type = new TypePet(id, name.getText().toString(), ImageViewChange(image));
                        ////lỗi
                        if (typeDAO.updateType(id, name.getText().toString(), ImageViewChange(image)) > 0) {
                            Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                            list.clear();
                            list = typeDAO.getAllType();
                            adapter.changeDataset(list);
                            dialog.dismiss();
                        }
                    }
                });
                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
//
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab1.startAnimation(fabbackrotate);
                fab2.startAnimation(fabClose);
                fab3.startAnimation(fabClose);
                txt1.startAnimation(fabClose);
                txt2.startAnimation(fabClose);
                fab2.setClickable(false);
                fab3.setClickable(false);
                isOpen = false;
//                startActivity(new Intent(getApplicationContext(), AddTypeActivity.class));
                final LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                final View view = inflater.inflate(R.layout.dialog_pet, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Add Pet");
                builder.setView(view);
                image = (ImageView) view.findViewById(R.id.imageedit);
                name = (EditText) view.findViewById(R.id.nameedit);
                btnsave = (Button) view.findViewById(R.id.btnsaveedit);
                btncancel = (Button) view.findViewById(R.id.btncanceledit);
                final AlertDialog dialog = builder.show();
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                        photoPickerIntent.setType("image/*");
//                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                        final LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                        final View view = inflater.inflate(R.layout.dialog_image, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                                //Uri uri  = Uri.parse("file:///sdcard/photo.jpg");
                                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "propic.jpg";
                                uri = Uri.parse(root);
                                //i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(i, CAPTURE_PHOTO);
                                dialog1.dismiss();
                            }
                        });
                    }
                });
                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Random random = new Random();
                        id = String.valueOf(random.nextInt());
                        TypePet type = new TypePet(id, name.getText().toString(), ImageViewChange(image));
                        if (typeDAO.insertType(type) > 0) {
                            Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                            list.clear();
                            list = typeDAO.getAllType();
                            adapter.changeDataset(list);
                            Log.d("nhan", type.toString());
                            dialog.dismiss();
                        }
                    }
                });
                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab1.startAnimation(fabbackrotate);
                fab2.startAnimation(fabClose);
                fab3.startAnimation(fabClose);
                txt1.startAnimation(fabClose);
                txt2.startAnimation(fabClose);
                fab2.setClickable(false);
                fab3.setClickable(false);
                isOpen = false;
                startActivity(new Intent(getApplicationContext(), PetActivity.class));
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.browse:
                        startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                        break;
                    case R.id.setting:
                        startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                        break;
                }
                return false;
            }
        });
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.coll);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fabthemloai);
        fab3 = findViewById(R.id.fabthemchungloai);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabrotate = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        fabbackrotate = AnimationUtils.loadAnimation(this, R.anim.rotate_backforward);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        bottomNavigationView = findViewById(R.id.navi);
    }

    public void addtype(View view) {
        anim();
    }

    private void anim() {
        if (isOpen) {
            fab1.startAnimation(fabbackrotate);
            fab2.startAnimation(fabClose);
            fab3.startAnimation(fabClose);
            txt1.startAnimation(fabClose);
            txt2.startAnimation(fabClose);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isOpen = false;
        } else {
            fab1.startAnimation(fabrotate);
            fab2.startAnimation(fabOpen);
            fab3.startAnimation(fabOpen);
            txt1.startAnimation(fabOpen);
            txt2.startAnimation(fabOpen);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isOpen = true;
        }
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
                        image.setImageBitmap(bitmap);
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
                    image.setImageBitmap(bmp);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        list = typeDAO.getAllType();
        adapter.changeDataset(list);
    }

    public byte[] ImageViewChange(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), PetActivity.class));
        return super.onOptionsItemSelected(item);

    }
}
//             bundle.putByteArray("IMAGE", list.get(position).getImage());
//                    intent.putExtras(bundle);
