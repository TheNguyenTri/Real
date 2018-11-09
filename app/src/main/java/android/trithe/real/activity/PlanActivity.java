package android.trithe.real.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.adapter.PlanssAdapter;
import android.trithe.real.database.PetDAO;
import android.trithe.real.database.PlanssDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Pet;
import android.trithe.real.model.Planss;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import static android.trithe.real.Channels.CHANNEL_ID;

public class PlanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private Spinner sppet;
    private Spinner spaction;
    private EditText eddate, edtime;
    private Button btnsave;
    private Button btncancel;
    private PlanssDAO planssDAO;
    private PetDAO petDAO;
    private String name, idpet;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private List<Planss> listplanss = new ArrayList<>();
    private List<Pet> listpet = new ArrayList<>();
    private final List<String> action = new ArrayList<>();
    private PlanssAdapter planssAdapter;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        initView();
        notificationManager = NotificationManagerCompat.from(this);
        planssDAO = new PlanssDAO(this);
        petDAO = new PetDAO(this);
        listplanss = planssDAO.getAllPlanss();
        planssAdapter = new PlanssAdapter(this, listplanss, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {
                final LayoutInflater inflater = LayoutInflater.from(PlanActivity.this);
                final View view = inflater.inflate(R.layout.dialog_plan, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
                builder.setTitle("Edit Plan");
                builder.setView(view);
                final AlertDialog dialog = builder.show();
                eddate = view.findViewById(R.id.eddate);
                edtime = view.findViewById(R.id.edtime);
                sppet = (Spinner) view.findViewById(R.id.sppet);
                spaction = (Spinner) view.findViewById(R.id.spaction);
                btnsave = (Button) view.findViewById(R.id.btnsave);
                btncancel = (Button) view.findViewById(R.id.btncancel);
                spinner();
                eddate.setText(sdf.format(listplanss.get(position).getDay()));
                edtime.setText(listplanss.get(position).getTime());
                final String idss = listplanss.get(position).getId();
                String namess = listplanss.get(position).getName();
                String namepetss = listplanss.get(position).getIdpet();
                sppet.setSelection(checkPositionPet(namepetss));
                spaction.setSelection(checkPositionName(namess));
                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (eddate.getText().toString().equals("")) {
                            eddate.setError(getString(R.string.empty));
                        } else if (edtime.getText().toString().equals("")) {
                            edtime.setError(getString(R.string.empty));
                        } else {
                            try {
                                if (planssDAO.updatePlanss(idss, name, idpet, sdf.parse(eddate.getText().toString()), edtime.getText().toString()) > 0) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), getString(R.string.alertsuccessfully), Toast.LENGTH_SHORT).show();
                                    listplanss.clear();
                                    listplanss = planssDAO.getAllPlanss();
                                    planssAdapter.changeDataset(listplanss);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
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
        }, new OnClick1() {
            @Override
            public void onItemClickClicked(final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        planssDAO.deletePlanssByID(listplanss.get(position).getId());
                        Toast.makeText(getApplicationContext(), getString(R.string.finish_plan), Toast.LENGTH_SHORT).show();
                        listplanss.clear();
                        listplanss = planssDAO.getAllPlanss();
                        planssAdapter.changeDataset(listplanss);
                    }
                }, 1500);
            }
        });
        setSupportActionBar(toolbar);
        recyclerView.setAdapter(planssAdapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private int checkPositionPet(String strPet) {
        for (int i = 0; i < listpet.size(); i++) {
            if (strPet.equals(listpet.get(i).getName())) {
                return i;
            }
        }
        return 0;
    }

    private int checkPositionName(String name) {
        for (int i = 0; i < action.size(); i++) {
            if (name.equals(action.get(i))) {
                return i;
            }
        }
        return 0;
    }

    public void sendChanels() {
        Notification notificationCompat = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.logo).setContentTitle(getString(R.string.app_name2)).setContentText(getString(R.string.event)).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
        notificationManager.notify(1, notificationCompat);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    break;
                case R.id.browse:
                    startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                    break;
                case R.id.plan:
                    startActivity(new Intent(getApplicationContext(), PlanActivity.class));
                    break;
                case R.id.setting:
                    startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                    break;
            }
            return false;
        }
    };

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.navi);
        recyclerView = findViewById(R.id.recycler_viewass);

    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_plan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_plan:
                final LayoutInflater inflater = LayoutInflater.from(PlanActivity.this);
                final View view = inflater.inflate(R.layout.dialog_plan, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
                builder.setTitle("Add Plan");
                builder.setView(view);
                final AlertDialog dialog = builder.show();
                sppet = (Spinner) view.findViewById(R.id.sppet);
                spaction = (Spinner) view.findViewById(R.id.spaction);
                eddate = (EditText) view.findViewById(R.id.eddate);
                edtime = view.findViewById(R.id.edtime);
                btnsave = (Button) view.findViewById(R.id.btnsave);
                btncancel = (Button) view.findViewById(R.id.btncancel);
                spinner();

                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (eddate.getText().toString().equals("")) {
                            eddate.setError(getString(R.string.empty));
                        } else if (edtime.getText().toString().equals("")) {
                            edtime.setError(getString(R.string.empty));
                        } else {
                            try {
                                Random random = new Random();
                                String id = String.valueOf(random.nextInt());
                                Planss planss = null;
                                planss = new Planss(id, name, idpet, sdf.parse(eddate.getText().toString()), edtime.getText().toString());
                                if (planssDAO.insertPlanss(planss) > 0) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), getString(R.string.alertsuccessfully), Toast.LENGTH_SHORT).show();
                                    listplanss.clear();
                                    listplanss = planssDAO.getAllPlanss();
                                    sendChanels();
                                    planssAdapter.changeDataset(listplanss);
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.save_error), Toast.LENGTH_SHORT).show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

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
        return super.onOptionsItemSelected(item);
    }

    private void spinner() {
        spaction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        action.removeAll(action);
        action.add("Đi bộ");
        action.add("Cho ăn");
        action.add("Tắm rửa");
        action.add("Đi khám thú y");
        action.add("Mua thức ăn, phụ kiện");
        action.add("Khác");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, action);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spaction.setAdapter(dataAdapter);

        sppet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idpet = listpet.get(sppet.getSelectedItemPosition()).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getPet();
    }

    private void getPet() {
        petDAO = new PetDAO(this);
        listpet = petDAO.getAllPet();
        ArrayAdapter<Pet> dataAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, listpet);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sppet.setAdapter(dataAdapter);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    private void setDate(final Calendar calendar) {
        eddate.setText(sdf.format(calendar.getTime()));
    }

    public static class DatePickerFragment extends android.support.v4.app.DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

    public void datePicker(View view) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }

    public void timePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.HOUR);
        calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
////                Toast.makeText(PlanActivity.this, hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
//            }
//        }, hour, minute, true);
//
//        timePickerDialog.show();

        TimePickerDialog timePickerDialog = new TimePickerDialog(PlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                edtime.setText(hourOfDay + ":" + minutes);
            }
        }, 0, 0, false);
        timePickerDialog.show();
    }
}
