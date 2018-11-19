package android.trithe.real.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.trithe.real.R;
import android.trithe.real.helper.AlarmReceiver;
import android.trithe.real.helper.LocalData;
import android.trithe.real.helper.NotificationScheduler;
import android.trithe.real.helper.RecyclerItemTouchHelper;
import android.trithe.real.adapter.PlanssAdapter;
import android.trithe.real.database.PetDAO;
import android.trithe.real.database.PlanssDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Pet;
import android.trithe.real.model.Planss;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import static android.content.Context.CLIPBOARD_SERVICE;


public class PlanFragment extends Fragment implements DatePickerDialog.OnDateSetListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private ConstraintLayout constraintLayout;
    private Spinner sppet;
    private Spinner spaction;
    private static EditText eddate;
    private EditText edtime;
    private Button btnsave;
    private Button btncancel;
    private PlanssDAO planssDAO;
    private PetDAO petDAO;
    private String name, idpet;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private List<Planss> listplanss = new ArrayList<>();
    private List<Pet> listpet = new ArrayList<>();
    private final List<String> action = new ArrayList<>();
    private PlanssAdapter planssAdapter;
    private ImageView imageDate, imageTime;
    private LocalData localData;
    private CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.plan_fragment, container, false);
        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ///
        ClipboardManager myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        localData = new LocalData(getActivity());
        //
        coordinatorLayout=view.findViewById(R.id.coor);
//
        final CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.coll);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
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
        Glide.with(getContext()).load(R.drawable.pet).into((ImageView) view.findViewById(R.id.backdrop));


//        NotificationScheduler.setReminder(getActivity(), AlarmReceiver.class, localData.get_hour(), localData.get_min());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_viewass);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        constraintLayout = view.findViewById(R.id.ll);
        planssDAO = new PlanssDAO(getActivity());
        petDAO = new PetDAO(getActivity());
        listplanss = planssDAO.getAllPlanssAsc();
        if (planssDAO.getAllPlanss().size() == 0) {
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.GONE);
        }
        planssAdapter = new PlanssAdapter(getActivity(), listplanss, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {
                final LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View view = inflater.inflate(R.layout.dialog_plan, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Edit Plan");
                builder.setView(view);
                final AlertDialog dialog = builder.show();
                eddate = view.findViewById(R.id.eddate);
                edtime = view.findViewById(R.id.edtime);
                sppet = view.findViewById(R.id.sppet);
                spaction = view.findViewById(R.id.spaction);
                btnsave = view.findViewById(R.id.btnsave);
                btncancel = view.findViewById(R.id.btncancel);
                imageDate = view.findViewById(R.id.imgdate);
                imageDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerFragment fragment = new DatePickerFragment();
                        fragment.show(getActivity().getSupportFragmentManager(), "date");
                    }
                });
                imageTime = view.findViewById(R.id.imgtime);
                imageTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.get(Calendar.HOUR);
                        calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                                edtime.setText(hourOfDay + ":" + minutes);
                                /////
                                localData.set_hour(hourOfDay);
                                localData.set_min(minutes);
                                NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, localData.get_hour(), localData.get_min());
                            }
                        }, 0, 0, false);
                        timePickerDialog.show();
                    }
                });




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
                                    Toast.makeText(getActivity(), getString(R.string.alertsuccessfully), Toast.LENGTH_SHORT).show();
                                    listplanss.clear();
                                    listplanss = planssDAO.getAllPlanssAsc();
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
                planssDAO.deletePlanssByID(listplanss.get(position).getId());
                listplanss.clear();
                listplanss = planssDAO.getAllPlanssAsc();
                planssAdapter.changeDataset(listplanss);
            }
        });
        recyclerView.setAdapter(planssAdapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(manager);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);


        return view;
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

//    public void sendChanels() {
//        Notification notificationCompat = new NotificationCompat.Builder(getActivity(), CHANNEL_ID).setSmallIcon(R.drawable.logo).setContentTitle(getString(R.string.app_name2)).setContentText(getString(R.string.event)).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
//        notificationManager.notify(1, notificationCompat);
//    }

//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_add_plan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_plan:
                final LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View view = inflater.inflate(R.layout.dialog_plan, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Plan");
                builder.setView(view);
                final AlertDialog dialog = builder.show();
                sppet = view.findViewById(R.id.sppet);
                spaction = view.findViewById(R.id.spaction);
                eddate = view.findViewById(R.id.eddate);
                edtime = view.findViewById(R.id.edtime);
                btnsave = view.findViewById(R.id.btnsave);
                btncancel = view.findViewById(R.id.btncancel);
                imageDate = view.findViewById(R.id.imgdate);
                imageDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerFragment fragment = new DatePickerFragment();
                        fragment.show(getActivity().getSupportFragmentManager(), "date");

                    }
                });
                imageTime = view.findViewById(R.id.imgtime);
                imageTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.get(Calendar.HOUR);
                        calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                                edtime.setText(hourOfDay + ":" + minutes);
                                localData.set_hour(hourOfDay);
                                localData.set_min(minutes);
                                NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, localData.get_hour(), localData.get_min());
                            }
                        }, 0, 0, false);
                        timePickerDialog.show();
                    }
                });
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
                                Planss planss;
                                planss = new Planss(id, name, idpet, sdf.parse(eddate.getText().toString()), edtime.getText().toString());
                                if (planssDAO.insertPlanss(planss) > 0) {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.alertsuccessfully), Toast.LENGTH_SHORT).show();
                                    listplanss.clear();
                                    listplanss = planssDAO.getAllPlanssAsc();
//                                    sendChanels();
                                    planssAdapter.changeDataset(listplanss);
                                    constraintLayout.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.save_error), Toast.LENGTH_SHORT).show();
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
                break;
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
  if(action.size() ==0) {
      action.add("Đi bộ");
      action.add("Cho ăn");
      action.add("Tắm rửa");
      action.add("Đi khám thú y");
      action.add("Mua thức ăn, phụ kiện");
      action.add("Khác");
  }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, action);
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
        petDAO = new PetDAO(getActivity());
        listpet = petDAO.getAllPet();
        ArrayAdapter<Pet> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listpet);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sppet.setAdapter(dataAdapter);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
    }


    public static class DatePickerFragment extends android.support.v4.app.DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
                    eddate.setText(sdf.format(cal.getTime()));

//                        sendChanels();
                }
            }, year, month, day);
        }

    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        getActivity().invalidateOptionsMenu();
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof PlanssAdapter.MyViewHolder) {
            final Planss deletedItem = listplanss.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            planssAdapter.removeItem(viewHolder.getAdapterPosition());
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, " Removed from item!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    planssAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
