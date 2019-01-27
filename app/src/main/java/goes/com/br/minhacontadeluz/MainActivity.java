package goes.com.br.minhacontadeluz;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.preference.PreferenceFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //GLOBAL VARIABLES
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private static NavigationView nvDrawer;
    static String[] device_suggestions;
    static InterstitialAd mInterstitialAd;

    //FRAMENTS ID
    static final int FRAGMENT_BILL = 0;

    static final int FRAGMENT_DEVICES = 1;
    static final int FRAGMENT_VIEW_DEVICE = 11;
    static final int FRAGMENT_NEW_DEVICE = 12;
    static final int FRAGMENT_UPDATE_DEVICE = 13;

    static final int FRAGMENT_ROOMS = 2;
    static final int FRAGMENT_VIEW_ROOMS = 21;

    static final int FRAGMENT_TIPS = 4;
    static final int FRAGMENT_MEASURER = 3;
    static final int FRAGMENT_SETTINGS = 7;
    static final int FRAGMENT_HELP = 5;
    static final int FRAGMENT_ABOUT = 6;

    //PREFERENCES KEYS
    static final String CHECKBOX_ADDITIONAL_BILL="pref_checkbox_additional_bill";
    static final String CHECKBOX_RED_FLAG="pref_checkbox_red_flag";
    static final String CHECKBOX_PUBLIC_ILLUMINATION = "pref_checkbox_public_illumination";
    static final String VALUE_KWH="value_kwh";
    static final String VALUE_PUBLIC_ILLUMINATION = "value_public_illumination";
    static final String DAY_PICKER = "day";
    static final String VOLTS ="volts";
    static final String NOTIFICATIONS_CHECKBOX="set_notifications";

    static float KWH_value = 0.49231f;

    static ArrayList<Integer> isw_resources;
    static ArrayList<Room> roomsList;
    static ArrayList<Device> devices;
    static ArrayList<String> chartDevicesConsumptionData_xAxis;
    static ArrayList<BarEntry> chartDevicesConsumptionData_yAxis;

    static private CharSequence mTitle;
    static Database_Acesso db_acesso;
    static AlarmReceiver alarm;

    static ImageSwitcher imageSwitcher, updateDeviceImageSwitcher;
    static Spinner newDevice_Spinner, updateDevice_Spinner, newDevice_powerUnitSpinner, update_powerUnitSpinner;
    static ActionBar actionBar;
    static SharedPreferences sharedPreferences;
    static BarChart deviceConsumptionChart;
    static LineChart consumptionByMonthChart;
    static PieChart consumptionByRoomsChart;

    static AutoCompleteTextView editText_device_name;
    static EditText editText_device_pot;
    static EditText editText_device_time ;
    static EditText editText_device_period ;
    static EditText editText_device_number;

    public SharedPreferences.OnSharedPreferenceChangeListener mListener;


    static Device deviceForUpdate;

    static boolean timeIsHours = true;
    protected static int device_id=0, room_id=0;
    private final int SELECT_PHOTO = 1;

    static int imageSwitcherPosition=0;
    static int updateDevice_imageSwitcherPosition=0;
    static int roomID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDrawablesResources();
        db_acesso = new Database_Acesso(this);
        db_acesso.initializeRooms();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8231621972196832/9549778704");

        device_id = db_acesso.getMaxDeviceId();
        room_id = db_acesso.getMaxRoomId();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set the menu icon instead of the launcher icon.
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);
        nvDrawer.getMenu().performIdentifierAction(R.id.nav_first_fragment, 0);

        alarm = new AlarmReceiver();
        alarm.setAlarm(this);

        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                alarm.cancelAlarm(getApplicationContext());
                alarm.setAlarm(getApplicationContext());
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(mListener);

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                calculateBill();
                AdRequest adRequest2 = new AdRequest.Builder()
                        .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                        .build();
                mInterstitialAd.loadAd(adRequest2);

                fragment = PlaceholderFragment.newInstance(FRAGMENT_BILL);
                mInterstitialAd.show();

                break;
            case R.id.nav_second_fragment:
                fragment = PlaceholderFragment.newInstance(FRAGMENT_DEVICES);
                break;
            case R.id.nav_third_fragment:
                fragment = PlaceholderFragment.newInstance(FRAGMENT_ROOMS);
                break;
            case R.id.nav_fourth_fragment:
                adRequest2 = new AdRequest.Builder()
                        .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                        .build();
                mInterstitialAd.loadAd(adRequest2);
                fragment = PlaceholderFragment.newInstance(FRAGMENT_MEASURER);
                mInterstitialAd.show();
                break;
            case R.id.nav_fifth_fragment:
                adRequest2 = new AdRequest.Builder()
                        .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                        .build();
                mInterstitialAd.loadAd(adRequest2);
                fragment = PlaceholderFragment.newInstance(FRAGMENT_TIPS);
                mInterstitialAd.show();

                break;
            case R.id.nav_sixth_fragment:
                adRequest2 = new AdRequest.Builder()
                        .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                        .build();
                mInterstitialAd.loadAd(adRequest2);

                fragment = PlaceholderFragment.newInstance(FRAGMENT_HELP);
                mInterstitialAd.show();

                break;
            case R.id.nav_seventh_fragment:
                adRequest2 = new AdRequest.Builder()
                        .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                        .build();
                mInterstitialAd.loadAd(adRequest2);
                fragment = PlaceholderFragment.newInstance(FRAGMENT_ABOUT);
                mInterstitialAd.show();

                break;
            case R.id.nav_eighth_fragment:
                adRequest2 = new AdRequest.Builder()
                        .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                        .build();
                mInterstitialAd.loadAd(adRequest2);
                fragment = new FragmentSettings();
                mInterstitialAd.show();
                break;
            default:
                fragment = PlaceholderFragment.newInstance(FRAGMENT_BILL);
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out)
                .replace(R.id.container, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        actionBar.setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("Receiving intent", "Name: " + intent.getAction());
        if (intent.getAction().equals("New_bill")) {
            calculateNewBill();
        }
    }

    public void calculateBill(){
        ArrayList<Bill> bills = db_acesso.getBills();
        if (bills.size()!=0) {
            for (Bill bill : bills) {
                ArrayList<Device> bill_devices = db_acesso.getDevicesByID(db_acesso.getDevicesIDFromDevice_Bill(bill.getMonth(), bill.getYear()));
                boolean hasAdditionalBill = false, hasRedFlag = false;
                KWH_value = Float.parseFloat(sharedPreferences.getString(VALUE_KWH, "0.49231"));
                if (sharedPreferences.getBoolean(CHECKBOX_ADDITIONAL_BILL, false)) {
                    hasAdditionalBill = true;
                    if (sharedPreferences.getBoolean(CHECKBOX_RED_FLAG, false)) {
                        hasRedFlag = true;
                    } else {
                        hasRedFlag = false;
                    }
                } else {
                    hasAdditionalBill = false;
                }
                double total = 0.0;
                for (int i = 0; i < bill_devices.size(); i++) {
                    total += ((((bill_devices.get(i).getTime() * bill_devices.get(i).getPower() * bill_devices.get(i).getPeriod()) / 1000) * KWH_value) * bill_devices.get(i).getNumber());
                    if (hasAdditionalBill) {
                        if (hasRedFlag) {
                            total += 0.055;
                        } else {
                            total += 0.025;
                        }
                    }
                }

                if (sharedPreferences.getBoolean(CHECKBOX_PUBLIC_ILLUMINATION, false)) {
                    total += Float.parseFloat(sharedPreferences.getString(VALUE_PUBLIC_ILLUMINATION, "10"));
                }

                db_acesso.update_bill(total, bill.getMonth(), bill.getYear());


            }
        }else{
            calculateNewBill();
        }
    }

    public void calculateNewBill(){
        boolean hasAdditionalBill = false, hasRedFlag = false;
        if (db_acesso != null) {
            devices = new ArrayList<>();
            devices = db_acesso.getDevices();
            if (devices.size() > 0) {
                Calendar c = Calendar.getInstance();
                devices = db_acesso.getDevices();
                KWH_value = Float.parseFloat(sharedPreferences.getString(VALUE_KWH, "0.49231"));
                if (sharedPreferences.getBoolean(CHECKBOX_ADDITIONAL_BILL, false)) {
                    hasAdditionalBill = true;
                    if (sharedPreferences.getBoolean(CHECKBOX_RED_FLAG, false)) {
                        hasRedFlag = true;
                    } else {
                        hasRedFlag = false;
                    }
                } else {
                    hasAdditionalBill = false;
                }
                double total = 0.0;
                for (int i = 0; i < devices.size(); i++) {
                    total += ((((devices.get(i).getTime() * devices.get(i).getPower() * devices.get(i).getPeriod()) / 1000) * KWH_value) * devices.get(i).getNumber());
                    if (hasAdditionalBill) {
                        if (hasRedFlag) {
                            total += 0.055;
                        } else {
                            total += 0.025;
                        }
                    }
                }

                if (sharedPreferences.getBoolean(CHECKBOX_PUBLIC_ILLUMINATION, false)){
                    total += Float.parseFloat(sharedPreferences.getString(VALUE_PUBLIC_ILLUMINATION, "10"));
                }

                total = (double)Math.round(total * 100) / 100;
                Log.i("Alarm", "Month: " + (c.get(Calendar.MONTH) + 1) + " - year" + c.get(Calendar.YEAR));
                db_acesso.insert_bill(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, total);
                db_acesso.insert_bill_device(devices, c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentSettings fragmentSettings = new FragmentSettings();
                actionBar.setTitle(mTitle);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out)
                        .replace(R.id.container, PlaceholderFragment.newInstance(FRAGMENT_BILL))
                        .commit();
            } else {
                Log.i("ALARM RECEIVER", "No devices");
            }
        } else {
            Log.i("ALARM RECEIVER", "DB ACESSO == NULL");
        }
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_horas:
                if (checked) {
                    timeIsHours = true;
                }
                break;
            case R.id.radio_minutos:
                if (checked) {
                    timeIsHours = false;
                }
                break;
            case R.id.updateDevice_radio_horas:
                if (checked) {
                    timeIsHours = true;
                }
                break;
            case R.id.updateDevice_radio_minutos:
                if (checked) {
                    timeIsHours = false;
                }
                break;
        }
    }

    public void searchInWeb(View view){
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.inmetro.gov.br/consumidor/tabelas.asp"));
        startActivity(i);
    }

    public void actionPrevious(View view){
        Button btn_next, btn_previous;

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_previous = (Button) findViewById(R.id.btn_previous);

        if(imageSwitcherPosition>0){
            imageSwitcherPosition--;
            imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
            if (!btn_next.isEnabled()) {
                btn_next.setEnabled(true);
            }
            if(imageSwitcherPosition==0){
                btn_previous.setEnabled(false);
            }
        }else {
            imageSwitcher.setImageResource(isw_resources.get(0));
            Toast.makeText(this, "Fim", Toast.LENGTH_SHORT).show();
        }

    }

    public void updateDevice_actionPrevious(View view){
        Button update_btn_next, update_btn_previous;

        update_btn_next = (Button) findViewById(R.id.updateDevice_btn_next);
        update_btn_previous = (Button) findViewById(R.id.updateDevice_btn_previous);

        if(updateDevice_imageSwitcherPosition>0){
            updateDevice_imageSwitcherPosition--;
            updateDeviceImageSwitcher.setImageResource(isw_resources.get(updateDevice_imageSwitcherPosition));
        }else {
            updateDeviceImageSwitcher.setImageResource(isw_resources.get(0));
            Toast.makeText(this, "Fim", Toast.LENGTH_SHORT).show();
        }
    }

    public void actionNext(View view){
        Button btn_next, btn_previous;
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_previous = (Button) findViewById(R.id.btn_previous);
        if(imageSwitcherPosition<isw_resources.size()-1){
            imageSwitcherPosition++;
            imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
            if (!btn_previous.isEnabled()) {
                btn_previous.setEnabled(true);
            }
            if(imageSwitcherPosition==isw_resources.size()-1){
                btn_next.setEnabled(false);
            }
        }else{
            imageSwitcher.setImageResource(isw_resources.get(isw_resources.size()-1));
            Toast.makeText(this, "Fim", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateDevice_actionNext(View view) {
        Button update_btn_next, update_btn_previous;

        update_btn_next = (Button) findViewById(R.id.updateDevice_btn_next);
        update_btn_previous = (Button) findViewById(R.id.updateDevice_btn_previous);
        if(updateDevice_imageSwitcherPosition<isw_resources.size()-1){
            updateDevice_imageSwitcherPosition++;
            updateDeviceImageSwitcher.setImageResource(isw_resources.get(updateDevice_imageSwitcherPosition));
        }else{
            updateDeviceImageSwitcher.setImageResource(isw_resources.get(isw_resources.size() - 1));
            Toast.makeText(this, "Fim", Toast.LENGTH_SHORT).show();
        }
    }

    public void onMeasurerCalculationClick(View view){
        EditText initial_value = (EditText) findViewById(R.id.initial_measuare);
        EditText final_value = (EditText) findViewById(R.id.final_measuare);
        TextView total_measurer = (TextView) findViewById(R.id.measurer_total);
        TextView inf_measurer = (TextView) findViewById(R.id.measurer_inf);

        if (!initial_value.getText().toString().isEmpty() && !final_value.getText().toString().isEmpty()){
            KWH_value = Float.parseFloat(sharedPreferences.getString(VALUE_KWH, "0.49231"));
            double additional = 0;
            double initial, finalVal;
            initial = Double.parseDouble(initial_value.getText().toString());
            finalVal = Double.parseDouble(final_value.getText().toString());
            if (sharedPreferences.getBoolean(CHECKBOX_ADDITIONAL_BILL, false)) {
                if (sharedPreferences.getBoolean(CHECKBOX_RED_FLAG, false)) {
                    additional = ((finalVal-initial)/100)*5.5;
                } else {
                    additional = ((finalVal-initial)/100)*2.50;
                }
            }
            total_measurer.setText("R$ " + ((float) Math.round((((finalVal - initial)*KWH_value)+additional) * 100) / 100));
            total_measurer.setAlpha(1);
            inf_measurer.setAlpha(1);
        }else{
            Toast.makeText(this, "Fim", Toast.LENGTH_SHORT).show();
        }
    }

    public void onShareButtonClick(View view){
        String shareBody = "Recomendo o aplicativo Minha Conta de Luz (https://goo.gl/MuVJwI)";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Saiba quanto cada eletrodoméstico está consumindo");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Compartilhar em: "));
    }

    public void onAvaliationClick(View view){
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=goes.com.br.minhacontadeluz"));
        startActivity(i);
    }

    public void verNaLoja(View view){
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=goes.com.br.nonodigito"));
        startActivity(i);
    }

    public void onAddDeviceButtonClick(View view) {
        double time;
        if (timeIsHours) {
            time = Double.parseDouble(editText_device_time.getText().toString());
        } else {
            time = (Double.parseDouble(editText_device_time.getText().toString())) / 60;
        }

        if (newDevice_Spinner.getChildCount() > 0) {
            if ((!editText_device_name.getText().toString().isEmpty())
                    && (!editText_device_pot.getText().toString().isEmpty())
                    && (!editText_device_period.getText().toString().isEmpty())
                    && (!editText_device_number.getText().toString().isEmpty())) {

                double power = 0;
                switch (newDevice_powerUnitSpinner.getSelectedItemPosition()){
                    case 0:
                        power = Double.parseDouble(editText_device_pot.getText().toString());
                        break;
                    case 1:
                        power = Double.parseDouble(editText_device_pot.getText().toString())/1000;
                        break;
                    case 2:
                        power = 1000 * (Double.parseDouble(editText_device_pot.getText().toString()) / time);
                        break;
                    case 3:
                        power = Double.parseDouble(editText_device_pot.getText().toString()) * Integer.parseInt(sharedPreferences.getString(VOLTS, "110"));
                        break;
                    case 4:
                        power = (Double.parseDouble(editText_device_pot.getText().toString()) * Integer.parseInt(sharedPreferences.getString(VOLTS, "110")))/1000;
                        break;
                }

                Device d = new Device(++device_id, editText_device_name.getText().toString(),
                        power,
                        time, Double.parseDouble(editText_device_period.getText().toString()),
                        Integer.parseInt(editText_device_number.getText().toString())
                        , isw_resources.get(imageSwitcherPosition),
                        roomsList.get(newDevice_Spinner.getSelectedItemPosition()).getId());
                try {
                    db_acesso.insert_device(d);
                    Toast.makeText(this,"Dispostivo inserido com sucesso!" , Toast.LENGTH_SHORT).show();

                } catch (android.database.sqlite.SQLiteException e) {
                    Toast.makeText(this,"Erro ao inserir dispostivo." , Toast.LENGTH_SHORT).show();

                }
                imageSwitcherPosition = 0;
                imageSwitcher.setImageResource(isw_resources.get(0));
                ((Button) findViewById(R.id.btn_previous)).setEnabled(false);
                ((Button) findViewById(R.id.btn_next)).setEnabled(true);
                editText_device_name.setText("");
                editText_device_period.setText("30");
                editText_device_pot.setText("");
                editText_device_time.setText("24");
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Adicione cômodos primeiro", Toast.LENGTH_SHORT).show();

        }
    }

    public void newDevice(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_NEW_DEVICE))
                .commit();
    }

    public void updateDevice(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_UPDATE_DEVICE))
                .commit();
    }

    public void loadDrawablesResources (){
        isw_resources = new ArrayList<Integer>();

        //lights
        isw_resources.add(R.drawable.bulb); //0
        isw_resources.add(R.drawable.lamp);//1
        isw_resources.add(R.drawable.lights);//2

        //utilities
        isw_resources.add(R.drawable.iron);//3
        isw_resources.add(R.drawable.fan);//4
        isw_resources.add(R.drawable.air_conditioner);//5
        isw_resources.add(R.drawable.vacuum_cleaner);//6
        isw_resources.add(R.drawable.washing_machine);//7
        isw_resources.add(R.drawable.hair_dryer);//8

        //kitchen
        isw_resources.add(R.drawable.coffe_maker);//9
        isw_resources.add(R.drawable.blender);//10
        isw_resources.add(R.drawable.cooker);//11
        isw_resources.add(R.drawable.cooker_hood);//12
        isw_resources.add(R.drawable.fridge);//13
        isw_resources.add(R.drawable.microwave);//14
        isw_resources.add(R.drawable.mixer);//15
        isw_resources.add(R.drawable.toaster);//16

        //BATHROOM
        isw_resources.add(R.drawable.shower);//17
        isw_resources.add(R.drawable.shower_and_tub);//18

        //audio, video and games
        isw_resources.add(R.drawable.av_receiver);//19
        isw_resources.add(R.drawable.dvd);//20
        isw_resources.add(R.drawable.blu_ray);//21
        isw_resources.add(R.drawable.video_projector);//22
        isw_resources.add(R.drawable.radio);//23
        isw_resources.add(R.drawable.boombox);//24
        isw_resources.add(R.drawable.loudspeaker);//25
        isw_resources.add(R.drawable.controller);//26

        //Computer, tv
        isw_resources.add(R.drawable.workstation);//27
        isw_resources.add(R.drawable.notebook);//28
        isw_resources.add(R.drawable.windows);//29
        isw_resources.add(R.drawable.mac);//30
        isw_resources.add(R.drawable.linux);//31
        isw_resources.add(R.drawable.print);//32
        isw_resources.add(R.drawable.scanner);//33
        isw_resources.add(R.drawable.tv);//34
        isw_resources.add(R.drawable.hdtv);//35
        isw_resources.add(R.drawable.widescreen_tv);//36


        //Mobile
        isw_resources.add(R.drawable.camera);//37
        isw_resources.add(R.drawable.cell_phone);
        isw_resources.add(R.drawable.android_phone);//39
        isw_resources.add(R.drawable.iphone);
        isw_resources.add(R.drawable.smartphone);//41
        isw_resources.add(R.drawable.multiple_smartphones);
        isw_resources.add(R.drawable.android_tablet);//43
        isw_resources.add(R.drawable.ipad);
        isw_resources.add(R.drawable.phone);//45

        //OTHERS
        isw_resources.add(R.drawable.heating);//46
        isw_resources.add(R.drawable.battery);//47
        isw_resources.add(R.drawable.electrical_plug);//48
        isw_resources.add(R.drawable.wall_socket);//49
        isw_resources.add(R.drawable.security_camera);//50

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else if (item.getItemId() == R.id.action_calc_bill){
            Log.i("Calc bill menu", "pressed");
            calculateNewBill();
            nvDrawer.getMenu().performIdentifierAction(R.id.nav_first_fragment, 0);
            return true;
        } else if (item.getItemId() == R.id.action_settings){
            Log.i("FRAGMENT_SETTINGS", "" + FRAGMENT_SETTINGS);
            nvDrawer.getMenu().performIdentifierAction(R.id.nav_eighth_fragment, 0);
            return true;
        }

        /*int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (id){
            case  R.id.action_calc_bill:
                Log.i("Calc bill menu", "pressed");
                calculateBill();
                return true;
            case R.id.action_settings:
                Log.i("FRAGMENT_SETTINGS", "" + FRAGMENT_SETTINGS);
                onSectionAttached(FRAGMENT_SETTINGS);
                actionBar.setTitle(mTitle);
                FragmentSettings fragmentSettings = new FragmentSettings();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out)
                        .replace(R.id.container, fragmentSettings)
                        .commit();
                return true;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }*/
        /*if (item.getItemId() == R.id.action_calc_bill){
            Log.i("Calc bill menu", "pressed");
            calculateBill();
            return true;
        }else if(item.getItemId() == R.id.action_settings){
            Log.i("FRAGMENT_SETTINGS", "" + FRAGMENT_SETTINGS);
            onSectionAttached(FRAGMENT_SETTINGS);
            actionBar.setTitle(mTitle);
            FragmentSettings fragmentSettings = new FragmentSettings();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out)
                    .replace(R.id.container, fragmentSettings)
                    .commit();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static Context context;
        static Spinner billSpinner;
        static View header, footer;
        static ListView bill_list_view;
        ArrayList<Bill> billList;
        static String powerUnits[] = new String[]{"W", "kW" ,"kWh", "A", "mA"};
        ContentResolver cr;
        static ListView update_list_view, rooms_list_view;
        static AutoCompleteTextView editText_newRoomName;
        static Button btn_newRoom;
        static int actualMonth=0, actualYear=0;

        /*
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            context = getActivity().getApplicationContext();
            cr = getActivity().getContentResolver();
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Animation in = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.slide_out_right);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNumber) {
                case FRAGMENT_DEVICES: //VIEW DEVICES
                    Log.i("SELECTED", "FRAGMENT DEVICES");
                    rootView = inflater.inflate(R.layout.device_list, container, false);
                    update_list_view = (ListView) rootView.findViewById(R.id.update_device_listView);
                    devices = db_acesso.getDevices();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            mInterstitialAd.show();
                        }
                    });
                    break;
                case FRAGMENT_UPDATE_DEVICE://UPDATE DEVICE
                    Log.i("SELECTED", "FRAGMENT UPDATE DEVICE");
                    rootView = inflater.inflate(R.layout.update_device, container, false);
                    MainActivity.actionBar.setTitle("Alterar");
                    updateDeviceImageSwitcher = (ImageSwitcher) rootView.findViewById(R.id.updateDevice_imageSwitcher);
                    updateDeviceImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                        public View makeView() {
                            ImageView myView = new ImageView(getActivity().getApplicationContext());
                            myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            myView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            return myView;
                        }
                    });
                    updateDeviceImageSwitcher.setInAnimation(in);
                    updateDeviceImageSwitcher.setOutAnimation(out);
                    updateDeviceImageSwitcher.setImageResource(isw_resources.get(0));

                    Button btn_edit_device = (Button) rootView.findViewById(R.id.btn_edit_device);
                    final RadioButton radio_minutos = (RadioButton) rootView.findViewById(R.id.updateDevice_radio_minutos);

                    final TextView updateDevice_TextView_device_name = (TextView) rootView.findViewById(R.id.updateDevice_device_name_textView);
                    updateDevice_TextView_device_name.setText(deviceForUpdate.getName());

                    final EditText updateDevice_editText_device_pot = (EditText) rootView.findViewById(R.id.updateDevice_device_pot);
                    updateDevice_editText_device_pot.setText(String.valueOf(deviceForUpdate.getPower()));

                    final EditText update_device_number  = (EditText) rootView.findViewById(R.id.updateDevice_number);
                    update_device_number.setText(String.valueOf(deviceForUpdate.getNumber()));

                    final EditText updateDevice_editText_device_time = (EditText) rootView.findViewById(R.id.updateDevice_device_time);
                    final Spinner updateDevice_unitPowerSpinner = (Spinner) rootView.findViewById(R.id.updateDevice_powerUnit_spinner);

                    if (deviceForUpdate.getTime() < 1){
                        deviceForUpdate.setTime(deviceForUpdate.getTime() * 60);
                        radio_minutos.setChecked(true);
                        timeIsHours=false;
                    }else{
                        timeIsHours = true;
                    }

                    updateDevice_editText_device_time.setText(String.valueOf(deviceForUpdate.getTime()));
                    final EditText updateDevice_editText_device_period = (EditText) rootView.findViewById(R.id.updateDevice_device_period);
                    updateDevice_editText_device_period.setText(String.valueOf(deviceForUpdate.getPeriod()));
                    final EditText updateDevice_editText_device_number = (EditText) rootView.findViewById(R.id.updateDevice_number);
                    updateDevice_editText_device_number.setText(String.valueOf(deviceForUpdate.getNumber()));

                    updateDevice_Spinner = (Spinner) rootView.findViewById(R.id.updateDevice_spinnerRooms);
                    roomsList = db_acesso.getRooms();
                    int spinner_selected_id = 0;
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < roomsList.size(); i++) {
                        list.add(roomsList.get(i).getName());
                        if(roomsList.get(i).getId() == deviceForUpdate.getId()){
                            spinner_selected_id=i;
                        }
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                            R.layout.spinner_item, list);
                    dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    updateDevice_Spinner.setAdapter(dataAdapter);
                    updateDevice_Spinner.setSelection(spinner_selected_id);

                    dataAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                            R.layout.spinner_item, powerUnits);
                    dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    updateDevice_unitPowerSpinner.setAdapter(dataAdapter);
                    updateDevice_Spinner.setSelection(0);

                    updateDeviceImageSwitcher.setImageResource(deviceForUpdate.getResource_image());
                    updateDevice_imageSwitcherPosition = isw_resources.lastIndexOf(deviceForUpdate.getResource_image());
                    btn_edit_device.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!updateDevice_editText_device_period.getText().toString().isEmpty()
                                    && !updateDevice_editText_device_pot.getText().toString().isEmpty()
                                    && !updateDevice_editText_device_time.getText().toString().isEmpty()
                                    && !updateDevice_editText_device_number.getText().toString().isEmpty()
                                    && updateDevice_Spinner.getChildCount()>0) {
                                double time = 0;
                                if (timeIsHours) {
                                    time = Double.parseDouble(updateDevice_editText_device_time.getText().toString());
                                } else {
                                    time = (Double.parseDouble(updateDevice_editText_device_time.getText().toString())) / 60;
                                }

                                double power = 0;
                                switch (updateDevice_unitPowerSpinner.getSelectedItemPosition()){
                                    case 0:
                                        power = Double.parseDouble(updateDevice_editText_device_pot.getText().toString());
                                        break;
                                    case 1:
                                        power = Double.parseDouble(updateDevice_editText_device_pot.getText().toString())/1000;
                                        break;
                                    case 2:
                                        power = 1000 * (Double.parseDouble(updateDevice_editText_device_pot.getText().toString()) / time);
                                        break;
                                    case 3:
                                        power = Double.parseDouble(updateDevice_editText_device_pot.getText().toString()) * Integer.parseInt(sharedPreferences.getString(VOLTS, "110"));
                                        break;
                                    case 4:
                                        power = (Double.parseDouble(updateDevice_editText_device_pot.getText().toString())) * Integer.parseInt(sharedPreferences.getString(VOLTS, "110"))/1000;
                                        break;
                                }

                                Device d = new Device(
                                        deviceForUpdate.getId(),
                                        deviceForUpdate.getName(),
                                        power, time,
                                        Double.parseDouble(updateDevice_editText_device_period.getText().toString()),
                                        Integer.parseInt(updateDevice_editText_device_number.getText().toString()),
                                        isw_resources.get(updateDevice_imageSwitcherPosition),
                                        roomsList.get(updateDevice_Spinner.getSelectedItemPosition()).getId()
                                );

                                try {
                                    db_acesso.update_device(d);
                                    Toast.makeText(getActivity().getApplicationContext(), "Dispostivo alterado com sucesso!", Toast.LENGTH_SHORT).show();
                                }catch (android.database.sqlite.SQLiteException e) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Erro ao alterar dispostivo.", Toast.LENGTH_SHORT).show();
                                }

                                updateDevice_imageSwitcherPosition = 0;
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                                        .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_DEVICES))
                                        .commit();
                            } else {
                               Toast.makeText(getActivity().getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    break;
                case FRAGMENT_VIEW_DEVICE:
                    Log.i("SELECTED", "FRAGMENT VIEW DEVICE");
                    rootView = inflater.inflate(R.layout.device_view, container, false);
                    MainActivity.actionBar.setTitle("Visualização");
                    ((TextView) rootView.findViewById(R.id.device_view_name)).setText(deviceForUpdate.getName());
                    ((TextView) rootView.findViewById(R.id.device_view_power)).setText("Potência: " + deviceForUpdate.getPower() + " Watts");
                    if(deviceForUpdate.getTime()<1){
                        ((TextView) rootView.findViewById(R.id.device_view_time)).setText("Ligado por " +deviceForUpdate.getTime()*60 + " minutos");
                    }else{
                        ((TextView) rootView.findViewById(R.id.device_view_time)).setText("Ligado por " + deviceForUpdate.getTime()+ " horas");
                    }
                    ((TextView) rootView.findViewById(R.id.device_view_number)).setText("Quantidade: " + deviceForUpdate.getNumber());
                    ((TextView) rootView.findViewById(R.id.device_view_period)).setText("Período: " +deviceForUpdate.getPeriod() + " dias");
                    ((TextView) rootView.findViewById(R.id.device_view_room))
                            .setText("Cômodo: " + (db_acesso.getRoomByID(deviceForUpdate.getRoomID())).getName());
                    ((ImageView) rootView.findViewById(R.id.device_view_imageView)).setImageResource(deviceForUpdate.getResource_image());

                    break;
                case FRAGMENT_NEW_DEVICE:
                    Log.i("SELECTED", "FRAGMENT NEW DEVICE");
                    rootView = inflater.inflate(R.layout.add_device, container, false);
                    MainActivity.actionBar.setTitle("Novo");
                    //imageButton = (ImageButton) rootView.findViewById(R.id.device_image);
                    Button btn_add_device = (Button) rootView.findViewById(R.id.btn_add_device);
                    editText_device_name = (AutoCompleteTextView) rootView.findViewById(R.id.device_name);
                    editText_device_pot = (EditText) rootView.findViewById(R.id.device_pot);
                    editText_device_time = (EditText) rootView.findViewById(R.id.device_time);
                    editText_device_period = (EditText) rootView.findViewById(R.id.device_period);
                    editText_device_number = (EditText) rootView.findViewById(R.id.device_number);
                    imageSwitcher = (ImageSwitcher) rootView.findViewById(R.id.imageSwitcher);
                    newDevice_Spinner = (Spinner) rootView.findViewById(R.id.spinnerRooms);
                    newDevice_powerUnitSpinner = (Spinner) rootView.findViewById(R.id.addDevice_power_unit_spinner);
                    device_suggestions = getResources().getStringArray(R.array.suggestions_array);

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    R.layout.simple_dropdown_item, device_suggestions);
                    editText_device_name.setAdapter(adapter);
                    editText_device_name.setThreshold(1);
                    editText_device_name.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    editText_device_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (!b) {
                                deviceNameListener(editText_device_name.getText().toString().toLowerCase());
                            } else {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Lista de dispotivos cadastrados: ")
                                        .setItems(device_suggestions, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (i != (device_suggestions.length - 1)) {
                                                    editText_device_name.setText(device_suggestions[i]);
                                                    editText_device_number.requestFocus();
                                                } else {
                                                    editText_device_name.setText(null);
                                                }
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                    editText_device_name.requestFocus();
                    roomsList = db_acesso.getRooms();
                    list = new ArrayList<String>();
                    for (int i = 0; i < roomsList.size(); i++) {
                        list.add(roomsList.get(i).getName());
                    }

                    dataAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                            R.layout.spinner_item, list);
                    dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    newDevice_Spinner.setAdapter(dataAdapter);
                    if (newDevice_Spinner.getChildCount() <= 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "Adicione cômodos", Toast.LENGTH_SHORT).show();
                    }else{
                    }
                    imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                        public View makeView() {
                            ImageView myView = new ImageView(getActivity().getApplicationContext());
                            myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            myView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            return myView;
                        }
                    });

                    dataAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                            R.layout.spinner_item, powerUnits);
                    dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    newDevice_powerUnitSpinner.setAdapter(dataAdapter);

                    imageSwitcher.setInAnimation(in);
                    imageSwitcher.setOutAnimation(out);
                    imageSwitcher.setImageResource(isw_resources.get(0));
                    break;
                case FRAGMENT_ROOMS:
                    Log.i("SELECTED", "FRAGMENT ROOMS");
                    rootView = inflater.inflate(R.layout.rooms_list, container, false);
                    rooms_list_view = (ListView) rootView.findViewById(R.id.rooms_listView);
                    btn_newRoom = (Button) rootView.findViewById(R.id.btn_newRoom);
                    editText_newRoomName = (AutoCompleteTextView) rootView.findViewById(R.id.newRoom_editText);
                    device_suggestions = getResources().getStringArray(R.array.suggestions_array_rooms);
                    ArrayAdapter<String> adapter_rooms_autocomplete =
                            new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    R.layout.simple_dropdown_item, device_suggestions);
                    editText_newRoomName.setAdapter(adapter_rooms_autocomplete);
                    roomsList = db_acesso.getRooms();
                    break;
                case FRAGMENT_VIEW_ROOMS:
                    rootView = inflater.inflate(R.layout.rooms_view, container, false);
                    MainActivity.actionBar.setTitle("Dispositivos no cômodo");
                    final ListView roomsDevices_list_view = (ListView) rootView.findViewById(R.id.roomsViewList_registered_devices);
                    final ArrayList<Device> rooms_devices = db_acesso.getDevicesByRoomID(roomID);
                    devices = db_acesso.getDevices();
                    final UpdateDeviceListAdapter rooms_listAdapter =
                            new UpdateDeviceListAdapter(getActivity().getApplicationContext(), rooms_devices, db_acesso);
                    roomsDevices_list_view.setAdapter(rooms_listAdapter);
                    roomsDevices_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            deviceForUpdate = new Device(devices.get(i));
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                                    .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_VIEW_DEVICE))
                                    .commit();
                        }
                    });
                    roomsDevices_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("O que deseja fazer com o dispositivo " + devices.get(position).getName() + "?")
                                    .setPositiveButton("Alterar",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deviceForUpdate = new Device(devices.get(position));
                                                    Log.i("Device for update", "id =" + deviceForUpdate.getId());
                                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                    fragmentManager.beginTransaction()
                                                            .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                                                            .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_UPDATE_DEVICE))
                                                            .commit();
                                                }
                                            })
                                    .setNeutralButton("Remover", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            db_acesso.remove_device(devices.get(position).getId());
                                            devices.remove(position);
                                            UpdateDeviceListAdapter listAdapter =
                                                    new UpdateDeviceListAdapter(getActivity().getApplicationContext(), devices, db_acesso);
                                            roomsDevices_list_view.setAdapter(listAdapter);
                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                            return true;
                        }
                    });
                    break;
                case FRAGMENT_HELP:
                    rootView = inflater.inflate(R.layout.fragment_help, container, false);
                    setResizedImage(R.drawable.procel, (ImageView) rootView.findViewById(R.id.selo_procel));
                    setResizedImage(R.drawable.help_amps, (ImageView) rootView.findViewById(R.id.image_amps));
                    break;
                case FRAGMENT_ABOUT:
                    rootView = inflater.inflate(R.layout.fragment_about, container, false);
                    setResizedImage(R.drawable.ic_9digito, (ImageView) rootView.findViewById(R.id.image9digito));
                    break;
                case FRAGMENT_TIPS:
                    rootView = inflater.inflate(R.layout.fragment_tips, container, false);
                    break;
                case FRAGMENT_MEASURER:
                    rootView = inflater.inflate(R.layout.measurer_fragment, container, false);
                    break;
                case FRAGMENT_BILL:
                    billList = db_acesso.getBills();
                    if (billList.size()>0){
                        rootView = inflater.inflate(R.layout.fragment_bill, container, false);
                        bill_list_view = (ListView) rootView.findViewById(R.id.bill_device_listView);
                        header = inflater.inflate(R.layout.fragment_bill_header, null);
                        TextView bill_flag_color = (TextView) header.findViewById(R.id.bill_flag_type);
                        TextView bill_illumination_tax = (TextView) header.findViewById(R.id.bill_public_tax);

                        footer = inflater.inflate(R.layout.fragment_bill_footer, null);
                        final TextView totalBill_textView = (TextView) header.findViewById(R.id.total_bill);
                        final TextView monthBillIndicator_textView = (TextView) header.findViewById(R.id.monthBillIndicator);
                        bill_list_view.addHeaderView(header);
                        bill_list_view.addFooterView(footer);

                        Button add_device_to_bill = (Button) footer.findViewById(R.id.add_device_to_bill);

                        KWH_value = Float.parseFloat(sharedPreferences.getString(VALUE_KWH, "0.49231"));
                        if (sharedPreferences.getBoolean(CHECKBOX_ADDITIONAL_BILL, false)) {
                            bill_flag_color.setAlpha(1);
                            //bill_flag_color.setHeight(LayoutParams.WRAP_CONTENT);
                            if (sharedPreferences.getBoolean(CHECKBOX_RED_FLAG, false)) {
                                KWH_value += 0.055;
                                bill_flag_color.setBackgroundColor(getResources().getColor(R.color.RED));
                                bill_flag_color.setTextColor(getResources().getColor(R.color.WHITE));
                                bill_flag_color.setText("Bandeira Vermelha");
                            } else {
                                bill_flag_color.setBackgroundColor(getResources().getColor(R.color.Yellow));
                                bill_flag_color.setText("Bandeira Amarela");
                                bill_flag_color.setTextColor(getResources().getColor(R.color.BLACK));
                                KWH_value += 0.025;
                            }
                        }else{
                            bill_flag_color.setBackgroundColor(getResources().getColor(R.color.Green));
                            bill_flag_color.setText("Bandeira Verde");
                            bill_flag_color.setTextColor(getResources().getColor(R.color.WHITE));
                        }

                        if(sharedPreferences.getBoolean(CHECKBOX_PUBLIC_ILLUMINATION, false)){
                            bill_illumination_tax.setAlpha(1);
                            //bill_illumination_tax.setHeight(LayoutParams.WRAP_CONTENT);
                        }else{
                            bill_illumination_tax.setAlpha(0);
                            //bill_illumination_tax.setHeight(0);
                        }
                        Log.i("KWH", String.valueOf(KWH_value));

                        ArrayList<String> bills_names_list = new ArrayList<String>();
                        //READ MONTH NAMES
                        for (int i = 0; i < billList.size(); i++) {
                            String mes = "";
                            switch (billList.get(i).getMonth()){
                                case 1:
                                    mes = "Janeiro";
                                    break;
                                case 2:
                                    mes = "Fevereiro";
                                    break;
                                case 3:
                                    mes = "Março";
                                    break;
                                case 4:
                                    mes = "Abril";
                                    break;
                                case 5:
                                    mes = "Maio";
                                    break;
                                case 6:
                                    mes = "Junho";
                                    break;
                                case 7:
                                    mes = "Julho";
                                    break;
                                case 8:
                                    mes = "Agosto";
                                    break;
                                case 9:
                                    mes = "Setembro";
                                    break;
                                case 10:
                                    mes = "Outubro";
                                    break;
                                case 11:
                                    mes = "Novembro";
                                    break;
                                case 12:
                                    mes = "Dezembro";
                                    break;
                            }
                            bills_names_list.add(mes+ "/" + billList.get(i).getYear());
                        }
                        billSpinner = (Spinner) rootView.findViewById(R.id.bill_switcher_spinner);
                        dataAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                R.layout.spinner_item, bills_names_list);
                        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        billSpinner.setAdapter(dataAdapter);
                        billSpinner.setSelection(dataAdapter.getCount() - 1);
                        billSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                actualMonth = billList.get(position).getMonth();
                                actualYear = billList.get(position).getYear();

                                final ArrayList<Device> bill_devices = db_acesso.getDevicesByID(db_acesso.getDevicesIDFromDevice_Bill(actualMonth, actualYear));

                                double maxValue = 0;
                                if (db_acesso.getDevices().size() > 0) {
                                    deviceConsumptionChart = (BarChart) header.findViewById(R.id.device_consumption_chart);
                                    drawDevicesChart(bill_devices, maxValue, totalBill_textView, monthBillIndicator_textView);

                                    consumptionByMonthChart = (LineChart) header.findViewById(R.id.month_consumption_chart);
                                    drawMonthBillChart(db_acesso.getBills());

                                    //consumptionByRoomsChart = (PieChart) footer.findViewById(R.id.rooms_consumption_chart);
                                    //drawRoomsBillChart(db_acesso.getRooms());
                                }

                                final Bill_ListAdapter bill_adapter =
                                        new Bill_ListAdapter(getActivity().getApplicationContext(), bill_devices, db_acesso, KWH_value);
                                bill_list_view.setAdapter(bill_adapter);
                                bill_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                                        if (position != 0) {
                                            final int pos = position - 1;
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("O que deseja fazer com o dispositivo " + bill_devices.get(pos).getName() + "?")
                                                    .setPositiveButton("Alterar",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    deviceForUpdate = new Device(bill_devices.get(pos));
                                                                    Log.i("Device for update", "id =" + deviceForUpdate.getId());
                                                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                    fragmentManager.beginTransaction()
                                                                            .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                                                                            .replace(R.id.container, PlaceholderFragment.newInstance(FRAGMENT_UPDATE_DEVICE))
                                                                            .commit();
                                                                }
                                                            })
                                                    .setNegativeButton("Cancelar", null)
                                                    .setNeutralButton("Remover", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            new AlertDialog.Builder(getActivity())
                                                                    .setTitle("Deseja continuar?")
                                                                    .setMessage("Isso irá excluir este dispositivo da conta atual")
                                                                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            db_acesso.remove_device_bill(bill_devices.get(pos).getId(), actualMonth, actualYear);
                                                                            nvDrawer.getMenu().performIdentifierAction(R.id.nav_first_fragment, 0);
                                                                        }
                                                                    })
                                                                    .setNegativeButton("Cancelar", null)
                                                                    .show();
                                                        }
                                                    })
                                                    .show();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Log.i("SPINNER BILLS", "NOTHING SELECTED");
                            }
                        });

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getActivity(),
                                android.R.layout.select_dialog_singlechoice);
                        devices = db_acesso.getDevices();
                        for (int i = 0; i < devices.size(); i++) {
                            arrayAdapter.add(devices.get(i).getName());
                        }

                        add_device_to_bill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Selecione o dispositivo que deseja inserir")
                                        .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                db_acesso.insert_bill_device(devices.get(which).getId(), actualMonth, actualYear);
                                                nvDrawer.getMenu().performIdentifierAction(R.id.nav_first_fragment, 0);
                                            }
                                        })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                        billSpinner.setSelected(false);
                    }else{
                        rootView = inflater.inflate(R.layout.no_data, container, false);
                    }
                    break;
            }

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNumber) {
                case FRAGMENT_DEVICES:
                    final UpdateDeviceListAdapter listAdapter =
                            new UpdateDeviceListAdapter(getActivity().getApplicationContext(), devices, db_acesso);
                    update_list_view.setAdapter(listAdapter);
                    update_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            deviceForUpdate = new Device(devices.get(i));
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                                    .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_VIEW_DEVICE))
                                    .commit();
                        }
                    });
                    update_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("O que deseja fazer com o dispositivo " + devices.get(position).getName() + "?")
                                    .setPositiveButton("Alterar",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deviceForUpdate = new Device(devices.get(position));
                                                    Log.i("Device for update", "id =" + deviceForUpdate.getId());
                                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                    fragmentManager.beginTransaction()
                                                            .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                                                            .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_UPDATE_DEVICE))
                                                            .commit();
                                                }
                                            })
                                    .setNeutralButton("Remover", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("Deseja continuar?")
                                                    .setMessage("Isso irá excluir este dispositivo de todas as contas")
                                                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            db_acesso.remove_device(devices.get(position).getId());
                                                            devices.remove(position);
                                                            UpdateDeviceListAdapter listAdapter =
                                                                    new UpdateDeviceListAdapter(getActivity().getApplicationContext(), devices, db_acesso);
                                                            update_list_view.setAdapter(listAdapter);
                                                        }
                                                    })
                                                    .setNegativeButton("Cancelar", null)
                                                    .show();
                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                            return true;
                        }
                    });
                    break;
                case FRAGMENT_ROOMS:
                    final Rooms_ListAdapter rooms_adapter =
                            new Rooms_ListAdapter(getActivity().getApplicationContext(), roomsList);
                    rooms_list_view.setAdapter(rooms_adapter);
                    btn_newRoom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db_acesso.insert_room(new Room(++room_id, editText_newRoomName.getText().toString()));
                            roomsList = db_acesso.getRooms();
                            final Rooms_ListAdapter rooms_adapter =
                                    new Rooms_ListAdapter(getActivity().getApplicationContext(), roomsList);
                            rooms_list_view.setAdapter(rooms_adapter);
                            editText_newRoomName.setText("");
                        }
                    });
                    rooms_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.up_from_bottom, R.anim.push_up_out)
                                    .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(FRAGMENT_VIEW_ROOMS))
                                    .commit();
                            roomID = i;
                            Log.i("roomID", "" + i);
                        }
                    });
                    rooms_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Deseja remover o cômodo " + roomsList.get(position).getName() + "?")
                                    .setPositiveButton("Remover",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.i("Remove room", "id =" + roomsList.get(position).getId());
                                                    if (!db_acesso.roomDeletableCheck(position)) {
                                                        new AlertDialog.Builder(getActivity())
                                                                .setTitle("Operação não permitida")
                                                                .setMessage("Não é permitida a exlusão do cômodo " + roomsList.get(position).getName()
                                                                        + " pois existem dispositivos cadastrados no mesmo")
                                                                .show();
                                                    } else {
                                                        db_acesso.remove_room(roomsList.get(position).getId());
                                                        roomsList.remove(position);
                                                        Rooms_ListAdapter listAdapter =
                                                                new Rooms_ListAdapter(getActivity().getApplicationContext(), roomsList);
                                                        rooms_list_view.setAdapter(listAdapter);
                                                    }
                                                }
                                            })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                            return true;
                        }
                    });
                    break;
            }
        }

        public void setResizedImage(int drawable_id, ImageView imageView){
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            int newWidth, newHeight;

            Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), drawable_id);
            if(selectedImage.getWidth() > screenWidth){
                newWidth = screenWidth;
                newHeight = (selectedImage.getHeight() * newWidth)/selectedImage.getWidth();
            }else{
                newHeight = selectedImage.getHeight();
                newWidth = selectedImage.getWidth();
            }
            selectedImage = Bitmap.createScaledBitmap(
                    selectedImage, newWidth, newHeight, false);
            imageView.setImageBitmap(selectedImage);
        }

        public void drawDevicesChart(ArrayList<Device> bill_devices, double maxValue, TextView totalBill_textView, TextView monthBillIndicator_textView){
            deviceConsumptionChart.setLogEnabled(false);
            deviceConsumptionChart.setDescription("Cosumo dos dispostivos:");
            deviceConsumptionChart.setDescriptionPosition(0, 0);
            deviceConsumptionChart.setNoDataTextDescription("Não há dados suficientes.");
            deviceConsumptionChart.setDescriptionColor((getResources().getColor(R.color.AppThemeColor)));
            deviceConsumptionChart.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            deviceConsumptionChart.setHorizontalFadingEdgeEnabled(true);
            deviceConsumptionChart.setHorizontalFadingEdgeEnabled(true);
            deviceConsumptionChart.setDrawGridBackground(true);
            deviceConsumptionChart.setGridBackgroundColor((getResources().getColor(R.color.lists_purple)));
            deviceConsumptionChart.setPinchZoom(true);
            deviceConsumptionChart.setDoubleTapToZoomEnabled(true);
            deviceConsumptionChart.setDrawBarShadow(false);
            deviceConsumptionChart.setDrawValueAboveBar(true);
            deviceConsumptionChart.getLegend().setEnabled(false);

            float totalBillValue=0;
            chartDevicesConsumptionData_xAxis = new ArrayList<>();
            ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
            for(int i=0; i<bill_devices.size(); i++){
                chartDevicesConsumptionData_xAxis.add(bill_devices.get(i).getName());
            }
            chartDevicesConsumptionData_yAxis = new ArrayList<>();
            for (int i = 0; i < bill_devices.size(); i++) {
                float bill_value= (((float)(bill_devices.get(i).getTime()*bill_devices.get(i).getPower()*bill_devices.get(i).getPeriod())/1000)*KWH_value)* bill_devices.get(i).getNumber();
                chartDevicesConsumptionData_yAxis.add(new BarEntry(bill_value, i, bill_devices.get(i).getName()));
                if(bill_value>maxValue){
                    maxValue=bill_value;
                }
                totalBillValue += bill_value;
            }
            totalBillValue = (float)Math.round(totalBillValue * 100) / 100;
            BarDataSet set1 = new BarDataSet(chartDevicesConsumptionData_yAxis, "set");
            set1.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSets.add(set1);

            BarData barData = new BarData();
            barData.setGroupSpace(80f);
            barData = new BarData(chartDevicesConsumptionData_xAxis, dataSets);
            monthBillIndicator_textView.setText("Total da conta " + billSpinner.getSelectedItem().toString());
            totalBill_textView.setText("R$ " + totalBillValue);
            deviceConsumptionChart.setMaxVisibleValueCount((int) maxValue + 10);

            XAxis xl = deviceConsumptionChart.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setDrawAxisLine(true);
            xl.setDrawGridLines(false);
            xl.setGridLineWidth(0.3f);
            xl.setTextSize(7f);

            YAxis yl = deviceConsumptionChart.getAxisLeft();
            yl.setDrawAxisLine(true);
            yl.setDrawGridLines(true);
            yl.setGridLineWidth(0.3f);

            YAxis yr = deviceConsumptionChart.getAxisRight();
            yr.setDrawAxisLine(true);
            yr.setDrawGridLines(true);

            if(!chartDevicesConsumptionData_xAxis.isEmpty()){
                deviceConsumptionChart.setData(barData);
                deviceConsumptionChart.setDescriptionPosition(0,0);
                deviceConsumptionChart.animateY(2500);
                deviceConsumptionChart.invalidate();
            }
        }

        public void drawMonthBillChart(ArrayList<Bill> bills) {
            consumptionByMonthChart.setDescription("");
            consumptionByMonthChart.setNoDataTextDescription("Não há dados suficientes.");
            consumptionByMonthChart.setDescriptionColor((getResources().getColor(R.color.AppThemeColor)));
            consumptionByMonthChart.setLogEnabled(false);
            consumptionByMonthChart.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            consumptionByMonthChart.setDrawGridBackground(true);
            consumptionByMonthChart.setGridBackgroundColor((getResources().getColor(R.color.lists_purple)));
            consumptionByMonthChart.getLegend().setEnabled(false);

            XAxis xl = consumptionByMonthChart.getXAxis();
            xl.setEnabled(true);
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setDrawGridLines(true);


            float maxValue=0, minValue=1000101000;
            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 0; i < bills.size(); i++) {
                if(bills.get(i).getValue()>maxValue)
                    maxValue=(float) bills.get(i).getValue();

                if(bills.get(i).getValue()<minValue)
                    minValue=(float) bills.get(i).getValue();

                xVals.add(bills.get(i).getMonth()+"/"+bills.get(i).getYear());
            }

            ArrayList<Entry> yVals = new ArrayList<Entry>();
            for (int i = 0; i < bills.size(); i++) {
                yVals.add(new Entry( (float) bills.get(i).getValue(), i));
            }

            maxValue+=10;
            minValue-=10;

            YAxis leftAxis = consumptionByMonthChart.getAxisLeft();
            leftAxis.setDrawGridLines(true);
            leftAxis.setDrawAxisLine(true);

            YAxis rightAxis = consumptionByMonthChart.getAxisRight();
            rightAxis.setDrawGridLines(true);
            rightAxis.setDrawAxisLine(true);


            LineDataSet set1 = new LineDataSet(yVals, "Meses");
            set1.enableDashedLine(10f, 5f, 0f);
            set1.setCircleColors(ColorTemplate.JOYFUL_COLORS);
            set1.setLineWidth(1f);
            set1.setCircleSize(7f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setFillAlpha(65);
            set1.setFillColor(Color.RED);

            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set1);
            LineData data = new LineData(xVals, dataSets);

            consumptionByMonthChart.setData(data);
        }

        /*public void drawRoomsBillChart(ArrayList<Room> rooms){
            consumptionByRoomsChart.setUsePercentValues(true);
            consumptionByRoomsChart.setDrawHoleEnabled(true);
            consumptionByRoomsChart.setDescription("");
            consumptionByRoomsChart.setHoleColorTransparent(true);
            consumptionByRoomsChart.setTransparentCircleColor(Color.parseColor("#22306AC1"));
            consumptionByRoomsChart.setHoleRadius(58f);
            consumptionByRoomsChart.setTransparentCircleRadius(61f);
            consumptionByRoomsChart.setDrawCenterText(true);
            consumptionByRoomsChart.setRotationAngle(0);
            consumptionByRoomsChart.setRotationEnabled(true);
            consumptionByRoomsChart.setCenterText("Consumo por\ncômodos");
            consumptionByRoomsChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
            Legend l = consumptionByRoomsChart.getLegend();
            l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

            ArrayList<Entry> yVals1 = new ArrayList<Entry>();;

            ArrayList<String> xVals = new ArrayList<String>();

            for (int i = 0; i < rooms.size(); i++){
                xVals.add(rooms.get(i).getName());
            }

            for (int i=0; i<rooms.size(); i++) {
                ArrayList<Device> devices;
                float roomBill = 0;
                devices = db_acesso.getDevicesByRoomIDAndDate(rooms.get(i).getId(), actualMonth, actualYear);
                for (Device item: devices) {
                    roomBill+=((((item.getTime() * item.getPower() * item.getPeriod())/1000) * KWH_value) * item.getNumber());
                }
                yVals1.add(new Entry(roomBill, i));
            }

            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            ArrayList<Integer> colors = new ArrayList<Integer>();
            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            dataSet.setColors(colors);

            PieData data = new PieData(xVals, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.parseColor("#909393"));
            consumptionByRoomsChart.setData(data);
            // undo all highlights
            consumptionByRoomsChart.highlightValues(null);
            consumptionByRoomsChart.invalidate();
        }
*/
        public void deviceNameListener(String deviceName){
            switch (deviceName) {
                case "ar-condicionado":
                case "ar condicionado":
                    imageSwitcherPosition = 5;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("1400");
                    editText_device_period.setText("30");
                    editText_device_time.setText("8");
                    break;
                case "abajour":
                    editText_device_pot.setText("60");
                    editText_device_period.setText("30");
                    editText_device_time.setText("2");
                    imageSwitcherPosition = 1;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "aspirador de pó":
                case "aspirador de po":
                    editText_device_pot.setText("1000");
                    editText_device_period.setText("30");
                    editText_device_time.setText("0.333");
                    imageSwitcherPosition = 6;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "aquecedor":
                    imageSwitcherPosition = 46;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("1550");
                    editText_device_period.setText("30");
                    editText_device_time.setText("4");
                    break;
                case "banheira":
                    editText_device_pot.setText("3500");
                    editText_device_period.setText("30");
                    editText_device_time.setText("1");
                    imageSwitcherPosition = 18;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "bateria":
                    editText_device_pot.setText("3.5");
                    editText_device_period.setText("5");
                    editText_device_time.setText("3");
                    imageSwitcherPosition = 47;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "batedeira":
                    editText_device_pot.setText("120");
                    editText_device_period.setText("30");
                    editText_device_time.setText("0.5");
                    imageSwitcherPosition = 15;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "blu-ray":
                case "bluray":
                case "blu ray":
                    editText_device_pot.setText("50");
                    editText_device_period.setText("15");
                    editText_device_time.setText("2");
                    imageSwitcherPosition = 21;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "câmera":
                case "camera":
                case "carregador de câmera":
                case "carregador de camera":
                    editText_device_pot.setText("3.5");
                    editText_device_period.setText("5");
                    editText_device_time.setText("3");
                    imageSwitcherPosition = 37;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "câmera de seguranca":
                case "camera de seguranca":
                case "câmera de segurança":
                case "camera de segurança":
                    editText_device_pot.setText("40");
                    editText_device_period.setText("30");
                    editText_device_time.setText("24");
                    imageSwitcherPosition = 50;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "celular":
                case "carregador":
                case "Carregador de celular":
                    editText_device_pot.setText("3.5");
                    editText_device_period.setText("30");
                    editText_device_time.setText("8");
                    imageSwitcherPosition = 38;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "cafeteira":
                    editText_device_pot.setText("100");
                    editText_device_period.setText("30");
                    editText_device_time.setText("1");
                    imageSwitcherPosition = 9;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "chuveiro elétrico":
                case "chuveiro eletrico":
                case "chuveiro":
                    imageSwitcherPosition = 17;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("3500");
                    editText_device_period.setText("10");
                    editText_device_time.setText("0.67");
                    break;
                case "computador":
                case "desktop":
                    imageSwitcherPosition = 27;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("180");
                    editText_device_period.setText("30");
                    editText_device_time.setText("3");
                    break;
                case "datashow":
                case "projetor":
                    imageSwitcherPosition = 22;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("200");
                    editText_device_period.setText("5");
                    editText_device_time.setText("3");
                    break;
                case "dvd":
                    imageSwitcherPosition = 20;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("50");
                    editText_device_period.setText("15");
                    editText_device_time.setText("2");
                    break;
                case "exaustor":
                    imageSwitcherPosition = 12;
                    editText_device_pot.setText("170");
                    editText_device_period.setText("30");
                    editText_device_time.setText("4");
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "ferro":
                case "ferro elétrico":
                    imageSwitcherPosition = 3;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("1000");
                    editText_device_period.setText("5");
                    editText_device_time.setText("1");
                    break;
                case "fogão":
                case "fogao":
                    imageSwitcherPosition = 11;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("60");
                    editText_device_period.setText("30");
                    editText_device_time.setText("5");
                    break;
                case "geladeira":
                    editText_device_pot.setText("200");
                    editText_device_time.setText("10");
                    imageSwitcherPosition = 13;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "freezer":
                    editText_device_pot.setText("400");
                    editText_device_period.setText("30");
                    editText_device_time.setText("10");
                    imageSwitcherPosition = 13;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "impressora":
                    imageSwitcherPosition = 32;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("180");
                    editText_device_period.setText("30");
                    editText_device_time.setText("3");
                    break;
                case "ipad":
                    imageSwitcherPosition = 44;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("3.5");
                    editText_device_period.setText("30");
                    editText_device_time.setText("8");
                    break;
                case "iphone":
                case "ipod":
                    imageSwitcherPosition = 40;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("3.5");
                    editText_device_period.setText("30");
                    editText_device_time.setText("8");
                    break;
                case "lampada incandescente":
                case "lâmpada incandescente":
                    imageSwitcherPosition = 0;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("40");
                    editText_device_period.setText("60");
                    editText_device_time.setText("5");
                    break;
                case "lâmpada de led":
                case "lampada de led":
                    imageSwitcherPosition = 0;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("5");
                    editText_device_period.setText("30");
                    editText_device_time.setText("5");
                    break;
                case "laptop":
                case "notebook":
                    imageSwitcherPosition = 28;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("20");
                    editText_device_period.setText("30");
                    editText_device_time.setText("3");
                    break;
                case "liquidificador":
                    imageSwitcherPosition = 15;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("300");
                    editText_device_period.setText("30");
                    editText_device_time.setText("0.5");
                    break;
                case "luz":
                case "lampada":
                case "lâmpada":
                case "lampada fluorescente":
                case "lâmpada fluorescente":
                    imageSwitcherPosition = 2;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("15");
                    editText_device_period.setText("30");
                    editText_device_time.setText("5");
                    break;
                case "microondas":
                    imageSwitcherPosition = 14;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("1300");
                    editText_device_period.setText("30");
                    editText_device_time.setText("0.34");
                    break;
                case "forno":
                case "forno elétrico":
                    imageSwitcherPosition = 14;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("1000");
                    editText_device_period.setText("12");
                    editText_device_time.setText("1");
                    break;
                case "máquina de lavar roupas":
                case "máquina de lavar":
                case "mqquina de lavar roupas":
                case "maquina de lavar":
                    imageSwitcherPosition = 7;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("1500");
                    editText_device_period.setText("12");
                    editText_device_time.setText("0.5");
                    break;
                case "rádio":
                case "radio":
                    imageSwitcherPosition = 23;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("20");
                    editText_device_period.setText("30");
                    editText_device_time.setText("1");
                    break;
                case "receptor de televisão":
                case "receptor de televisao":
                case "receptor de tv":
                case "receptor":
                    imageSwitcherPosition = 19;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("80");
                    editText_device_period.setText("30");
                    editText_device_time.setText("5");
                    break;
                case "secador de cabelo":
                case "secador":
                    imageSwitcherPosition = 8;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("600");
                    editText_device_period.setText("30");
                    editText_device_time.setText("0.25");
                    break;
                case "smartphone":
                    imageSwitcherPosition = 41;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("3.5");
                    editText_device_period.setText("30");
                    editText_device_time.setText("8");
                    break;
                case "som":
                case "aparelho de som":
                case "aparelho de som pequeno":
                    imageSwitcherPosition = 24;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("20");
                    editText_device_period.setText("30");
                    editText_device_time.setText("1");
                    break;
                case "aparelho de som grande":
                    imageSwitcherPosition = 25;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("80");
                    editText_device_period.setText("30");
                    editText_device_time.setText("1");
                    break;
                case "scanner":
                    imageSwitcherPosition = 33;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("40");
                    break;
                case "tablet":
                    imageSwitcherPosition = 43;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("3.5");
                    editText_device_period.setText("30");
                    editText_device_time.setText("8");
                    break;
                case "torradeira":
                    imageSwitcherPosition = 16;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("800");
                    editText_device_period.setText("30");
                    editText_device_time.setText("0.1667");
                    break;
                case "telefone":
                    imageSwitcherPosition = 6;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("90");
                    editText_device_period.setText("30");
                    editText_device_time.setText("1");
                    break;
                case "televisão":
                case "televisao":
                case "televisor":
                case "tv":
                    editText_device_pot.setText("250");
                    editText_device_period.setText("30");
                    editText_device_time.setText("5");
                    imageSwitcherPosition = 34;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    break;
                case "ventilador":
                    imageSwitcherPosition = 4;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("100");
                    editText_device_period.setText("30");
                    editText_device_time.setText("8");
                    break;
                case "video game":
                case "video-game":
                case "vídeo-game":
                case "vídeo game":
                case "playstation":
                case "xbox":
                    imageSwitcherPosition = 26;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("15");
                    editText_device_period.setText("15");
                    editText_device_time.setText("4");
                    break;
                default:
                    imageSwitcherPosition = 48;
                    imageSwitcher.setImageResource(isw_resources.get(imageSwitcherPosition));
                    editText_device_pot.setText("0");
                    editText_device_period.setText("0");
                    editText_device_time.setText("0");
                    break;
            }

        }

    }

    public static class FragmentSettings extends PreferenceFragment {
        public FragmentSettings() {
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            Preference btnDateFilter = (Preference) findPreference("day");
            btnDateFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDateDialog();
                    return false;
                }
            });
        }

        private void showDateDialog() {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = sharedPreferences.getInt(DAY_PICKER, 1);

            new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String data = String.valueOf(dayOfMonth) + " /" + String.valueOf(monthOfYear + 1) + " /" + String.valueOf(year);
                    sharedPreferences.edit().putInt(DAY_PICKER, (dayOfMonth)).commit();
                }
            }, year, month, day).show();

        }
    }

}
    /*public void onImageButtonClicked(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image*//*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int screenWidth = size.x;
                        int newWidth, newHeight;
                        Uri imageUri = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        if(selectedImage.getWidth() > screenWidth){
                            newWidth = screenWidth;
                            newHeight = (selectedImage.getHeight() * newWidth)/selectedImage.getWidth();
                        }else{
                            newHeight = selectedImage.getHeight();
                            newWidth = selectedImage.getWidth();
                        }
                        selectedImage = Bitmap.createScaledBitmap(
                                selectedImage, newWidth, newHeight, false);
                        imageButton = (ImageButton) findViewById(R.id.device_image);
                        imageButton.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }*/
