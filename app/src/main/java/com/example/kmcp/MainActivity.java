package com.example.kmcp;

import static com.example.kmcp.BTS.bts;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements LocationListener {

    public static LocationManager locationManager;
    public static Location mLastlocation = null;
    public static double speed;

    public static String First = "1";

    Button btn_infoExpand, btn_infoExpand2, btn_Auto, btn_Manual, btn_Begin, btn_Armat, btn_Pro;
    TextView tv_Hour, tv_Minute, tv_Second, tv_Sub, tv_SystemRunningTime, tv_Connect, tv_Disconnect,
            tv_CoolantTempValue, tv_OilTempValue, tv_OilPressValue, Enable_GPS, tv_EngineRunningTime;
    ImageView iv_RpmGradation;
    ProgressBar progressBar_RPM1, progressBar_RPM2, progressBar_Fuel, progressBar_CoolantTemp, progressBar_OilTemp, progressBar_OilPress;
    EditText et_RPM, et_KNOT;
    String wakesystem = "0";
    String wakelevel = "0";
    String RPM = "0";
    String KNOT = "0";
    String TrimLeftValue = "412.0";
    String TrimCenterValue = "412.0";
    String TrimRightValue = "412.0";
    String LeftBalVal = "0";
    String RightBalVal = "0";
    String BTConnect = "1";  // 0 : ?????????, 1 : ???????????????
    String sendBalastMessage = null;

    static String SystemRunningTime = "0";
    double RPMvalue = 0;

    float Rpm;
    int Fuel;
    int Ctemp;
    int Opress;
    int Otemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //???????????? ?????????????????? ????????? ??????
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : ???????????? ???????????? ??????
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //?????? ??????
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //?????? ?????? ??????
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);    //?????? ?????????
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //?????? ?????? ??????
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //?????? ?????? ??????
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        registerReceiver(mBluetoothStateReceiver, stateFilter);

        if(sendBalastMessage==null){
            sendBalastMessage = "$KMCP_PUMP,0,0,0,0,0,0*21$R$L";
        }

        Intent intent = getIntent();
        if (intent.getStringExtra("wakesystem") != null) {
            wakesystem = intent.getStringExtra("wakesystem");
        }
        if (intent.getStringExtra("wakelevel") != null) {
            wakelevel = intent.getStringExtra("wakelevel");
        }
        if (intent.getStringExtra("TrimLeftValue") != null) {
            TrimLeftValue = intent.getStringExtra("TrimLeftValue");
            //Toast myToast = Toast.makeText(getApplicationContext(),TrimLeftValue, Toast.LENGTH_SHORT);
            //myToast.show();
        }
        if (intent.getStringExtra("TrimCenterValue") != null) {
            TrimCenterValue = intent.getStringExtra("TrimCenterValue");
        }
        if (intent.getStringExtra("TrimRightValue") != null) {
            TrimRightValue = intent.getStringExtra("TrimRightValue");
        }
        if (intent.getStringExtra("LeftBalVal") != null) {
            LeftBalVal = intent.getStringExtra("LeftBalVal");
        }
        if (intent.getStringExtra("RightBalVal") != null) {
            RightBalVal = intent.getStringExtra("RightBalVal");
        }
        if (intent.getStringExtra("BTConnect") != null) {
            BTConnect = intent.getStringExtra("BTConnect");
        }
        if (intent.getStringExtra("sendBalastMessage") != null) {
            sendBalastMessage = intent.getStringExtra("sendBalastMessage");
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        btn_infoExpand = findViewById(R.id.btn_infoExpand);
        btn_infoExpand2 = findViewById(R.id.btn_infoExpand2);
        btn_Auto = findViewById(R.id.btn_Auto);
        btn_Manual = findViewById(R.id.btn_Manual);
        btn_Begin = findViewById(R.id.btn_Begin);
        btn_Armat = findViewById(R.id.btn_Armat);
        btn_Pro = findViewById(R.id.btn_Pro);

        tv_Connect = findViewById(R.id.tv_Connect);
        tv_Disconnect = findViewById(R.id.tv_Disconnect);
        tv_Sub = findViewById(R.id.tv_Sub);
        tv_Hour = findViewById(R.id.tv_Hour);
        tv_Minute = findViewById(R.id.tv_Minute);
        tv_Second = findViewById(R.id.tv_Second);
        tv_SystemRunningTime = findViewById(R.id.tv_SystemRunningTime);
        tv_CoolantTempValue = findViewById(R.id.tv_CoolantTempValue);
        tv_OilTempValue = findViewById(R.id.tv_OilTempValue);
        tv_OilPressValue = findViewById(R.id.tv_OilPressValue);
        tv_EngineRunningTime = findViewById(R.id.tv_EngineRunningTime);
        Enable_GPS = findViewById(R.id.textView20);

        iv_RpmGradation = findViewById(R.id.iv_RpmGradation);

        et_RPM = findViewById(R.id.et_RPM);
        et_KNOT = findViewById(R.id.et_KNOT);

        progressBar_RPM1 = findViewById(R.id.progressBar_RPM1);
        progressBar_RPM2 = findViewById(R.id.progressBar_RPM2);
        progressBar_Fuel = findViewById(R.id.progressBar_Fuel);
        progressBar_CoolantTemp = findViewById(R.id.progressBar_CoolantTemp);
        progressBar_OilTemp = findViewById(R.id.progressBar_OilTemp);
        progressBar_OilPress = findViewById(R.id.progressBar_OilPress);


        if (intent.getStringExtra("RPM") != null) {
            RPM = intent.getStringExtra("RPM");
            et_RPM.setText(RPM);
            RPM = et_RPM.getText().toString();
            RPMvalue = Double.parseDouble(RPM) * 1000;
            if (RPMvalue >= 4000) {
                iv_RpmGradation.setImageResource(R.drawable.rpm_redzone);
            } else {
                iv_RpmGradation.setImageResource(R.drawable.rpm_normal);
            }
            //Toast myToast = Toast.makeText(this.getApplicationContext(),RPM, Toast.LENGTH_SHORT);
            //myToast.show();
        }
        if (intent.getStringExtra("KNOT") != null) {
            KNOT = intent.getStringExtra("KNOT");
            et_KNOT.setText(KNOT);
            //Toast myToast = Toast.makeText(this.getApplicationContext(),KNOT, Toast.LENGTH_SHORT);
            //myToast.show();
        }


        //???????????? ??????????????? ????????????
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String time = simple.format(date);
        String[] splitTime = time.split(":");
        if (SystemRunningTime.equals("0")) {
            //static ????????? ????????? ????????? ????????????????????? ?????????.
            SystemRunningTime = time;
        }
        tv_Hour.setText(splitTime[0]);
        tv_Minute.setText(splitTime[1]);
        tv_Second.setText(splitTime[2]);
        ShowTimeMethod();

        SendMessageThread SMT = new SendMessageThread();
        SMT.start();


        //???????????? ???????????? ???????????? ?????? ??? ????????? ???????????? ?????????
        //????????? ????????? ????????? ?????? ??????.
        BTS.getInstance().mBluetoothHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == BTS.getInstance().BT_MESSAGE_READ) {
                    String readMessage = null;
                    String[] m = null;
                    String[] mm = null;
                    String[] n = null;
                    int len = 0;

                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        if(readMessage.charAt(0)!='$'){
                            readMessage = null;
                        }

                        if(readMessage!=null){

                            int index = readMessage.indexOf('*');
                            readMessage = readMessage.substring(0,index+3);
                            m = readMessage.split(",");
                            Log.d("readMessage",readMessage);


                            //????????? ????????????
                            if(m[0].equals("$KMCP_HEART")) {
                                // 1) $KMCP_HEART, Counter*Length$R$L
                                // m[0] = $KMCP_HEART
                                // m[1]??? "*"??? ????????? ????????????.
                                // mm[0] = Counter(0 ~ 1,800,000)
                                // mm[1] = Length($ ?????? * ?????? Length)
                                try {
                                    n = readMessage.split("\\*");   // ??????????????? ????????? ???????????? ????????? ??? ?????? "*" ??? ???????????? Dangling metacharacter ????????? ???????????? ????????? "\\*"??? ????????????.
                                    len = Integer.parseInt(n[1]);
                                    if((n[0].length() - 1) == len) {  // ????????? ???????????? ????????? ????????? ??? ?????????????????? ????????? ????????? ???????????? ??????????????? ????????????.
                                        mm = m[1].split("\\*");

                                        //?????? ???????????? ????????? ??????????????? ?????? ????????? ???????????? ????????????.
                                    }

                                }catch(Exception e) {
                                    //Log.d("$KMCP_HEART",e.toString());
                                }


                            }else if(m[0].equals("$KMCP_ENGINE")) {
                                // 2) $KMCP_ENGINE, RPM, Fuel Level, Coolant Temp, Oil Pressure, Oil Temp, Running Time*Length$R$L
                                // m[0] = $KMCP_ENGINE
                                // m[1] = RPM(0 ~ 7000)
                                // m[2] = KNOT(0 ~ 100)
                                // m[3] = Fuel Level(0 ~ 100)
                                // m[4] = Coolant Temp(0 ~ 240)
                                // m[5] = Oil Pressure(0 ~ 80)
                                // m[6]??? "*"??? ????????? ????????????.
                                // mm[0] = Running Time(0 ~ 999hour)
                                // mm[1] = Length($ ?????? * ?????? Length)
                                try {
                                    n=readMessage.split("\\*");
                                    len = Integer.parseInt(n[1]);
                                    //Toast.makeText(MainActivity.this,n[0],Toast.LENGTH_SHORT).show();
                                    if((n[0].length() - 1) == len) {    // $KMCP_ENGINE,RPM,Fuel Level,Coolant Temp,Oil Pressure,Oil Temp,Running Time ????????? n[0]????????? $???????????? ????????? ?????? n[0].length()?????? -1??? ?????????.
                                        mm = m[6].split("\\*");

                                        Rpm = Float.parseFloat(m[1]);
                                        if(Rpm>1000){
                                            progressBar_RPM1.setProgress(1000);
                                            progressBar_RPM2.setProgress(((int)Rpm-1000));
                                        } else {
                                            progressBar_RPM1.setProgress((int)Rpm);
                                            progressBar_RPM2.setProgress(0);
                                        }
                                        Rpm = (float) (Rpm / 1000.0);
                                        m[1] = String.format("%.1f", Rpm);
                                        et_RPM.setText(m[1]);

                                        if ((Rpm * 1000) >= 4000) {
                                            iv_RpmGradation.setImageResource(R.drawable.rpm_redzone);
                                        } else {
                                            iv_RpmGradation.setImageResource(R.drawable.rpm_normal);
                                        }

                                        KNOT = m[2];
                                        et_KNOT.setText(KNOT);

                                        Fuel = Integer.parseInt(m[3]);
                                        progressBar_Fuel.setProgress(Fuel);

                                        Ctemp = Integer.parseInt(m[4]);
                                        progressBar_CoolantTemp.setProgress(Ctemp);
                                        tv_CoolantTempValue.setText(m[4] + "??F");

                                        Opress = Integer.parseInt(m[5]);
                                        progressBar_OilPress.setProgress(Opress);
                                        tv_OilPressValue.setText(m[5] + " PSI");

                                        tv_EngineRunningTime.setText(mm[0] + "Hhr");
                                    }

                                }catch(Exception e) {
                                    //Log.d("$KMCP_ENGINE",e.toString());
                                }



                            }else if(m[0].equals("$KMCP_LEVEL")) {
                                // 3) $KMCP_LEVEL, Left Tank 1, Left Tank 2, Right Tank 1, Right Tank 2, Front Tank 1, Front Tank2, Fuel Tank, Rudder Angle*Length$R$L
                                // m[0] = $KMCP_LEVEL
                                // m[1] = Left Tank 1(0 ~ 100)
                                // m[2] = Left Tank 2(0 ~ 100)
                                // m[3] = Right Tank 1(0 ~ 100)
                                // m[4] = Right Tank 2(0 ~ 100)
                                // m[5] = Front Tank 1(0 ~ 100)
                                // m[6] = Front Tank 2(0 ~ 100)
                                // m[7] = Fuel Tank (0 ~ 100)
                                // m[8]??? "*"??? ????????? ????????????.
                                // mm[0] = Fuel Tank(0 ~ 100)
                                // mm[1] = Length($ ?????? * ?????? Length)
                                try {
                                    n=readMessage.split("\\*");
                                    len = Integer.parseInt(n[1]);
                                    if(n[0].length() == len){
                                        mm = m[8].split("\\*");

                                    }

                                }catch(Exception e) {
                                    //Log.d("$KMCP_LEVEL",e.toString());
                                }



                            }else if(m[0].equals("$KMCP_TRIMTAB")) {
                                // 4) $KMCP_TRIMTAB, Left Trim-tab UP/DOWN, Left Trim-tab On/Off, Right Trim-tab UP/Down, Right Trim-tab On/Off, Center Trim-tab UP/Down, Center Trim-tab On/Off*Length$R$L
                                // m[0] = $KMCP_TRIMTAB
                                // m[1] = Left Trim-tab UP/DOWN : 0(DOWN) or 1(UP)
                                // m[2] = Left Trim-tab On/Off : 0(Off) or 1(On)
                                // m[3] = Right Trim-tab UP/DOWN : 0(DOWN) or 1(UP)
                                // m[4] = Right Trim-tab On/Off : 0(Off) or 1(On)
                                // m[5] = Center Trim-tab UP/DOWN : 0(DOWN) or 1(UP)
                                // m[6]??? "*"??? ????????? ????????????.
                                // mm[0] = Center Trim-tab On/Off : 0(Off) or 1(On)
                                // mm[1] = Length($ ?????? * ?????? Length)
                                try {
                                    n=readMessage.split("\\*");
                                    len = Integer.parseInt(n[1]);
                                    if(n[0].length() == len){
                                        mm = m[6].split("\\*");

                                    }

                                }catch(Exception e) {
                                    //Log.d("$KMCP_TRIMTAB",e.toString());
                                }


                            }else if(m[0].equals("$KMCP_PUMP")) {
                                // 5) $KMCP_PUMP, Left Pump Fill/Empty, Left Pump On/Off, Right Pump Fill/Empty, Right Pump On/Off, Center Pump Fill/Empty, Center Pump On/Off*Length$R$L
                                // m[0] = $KMCP_PUMP
                                // m[1] = Left Pump Fill/Empty
                                // m[2] = Left Pump On/Off
                                // m[3] = Right Pump Fill/Empty
                                // m[4] = Right Pump On/Off
                                // m[5] = Center Pump Fill/Empty
                                // m[6]??? "*"??? ????????? ????????????.
                                // mm[0] = Center Pump On/Off
                                // mm[1] = Length($ ?????? * ?????? Length)
                                try{
                                    n=readMessage.split("\\*");
                                    len = Integer.parseInt(n[1]);
                                    if(n[0].length() == len){
                                        mm = m[6].split("\\*");



                                    }
                                }catch(Exception e) {
                                    //Log.d("$KMCP_PUMP",e.toString());
                                }


                            }else if(m[0].equals("$KMCP_VALVE")) {
                                // 6) $KMCP_VALVE, Left Valve Close/Open, Left Valve On/Off, Right Valve Close/Open, Right Valve On/Off, Center Valve Close/Open, Center Valve On/Off*Length$R$L
                                // m[0] = $KMCP_VALVE
                                // m[1] = Left Vave Close/Open
                                // m[2] = Left Valve On/Off
                                // m[3] = Right Valve Close/Open
                                // m[4] = Right Valve On/Off
                                // m[5] = Center Valve Close/Open
                                // m[6]??? "*"??? ????????? ????????????.
                                // mm[0] = Center Valve On/Off
                                // mm[1] = Length($ ?????? * ?????? length)
                                try{
                                    n=readMessage.split("\\*");
                                    len = Integer.parseInt(n[1]);
                                    if(n[0].length() == len){
                                        mm = m[6].split("\\*");


                                    }
                                }catch(Exception e) {
                                    //Log.d("$KMCP_VALVE",e.toString());
                                }
                            }
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        //?????????????????????
        btn_Auto.setTextColor(Color.parseColor("#000000"));
        btn_Auto.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
        btn_Manual.setTextColor(Color.parseColor("#000000"));
        btn_Manual.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));

        btn_Begin.setTextColor(Color.parseColor("#000000"));
        btn_Begin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
        btn_Armat.setTextColor(Color.parseColor("#000000"));
        btn_Armat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
        btn_Pro.setTextColor(Color.parseColor("#000000"));
        btn_Pro.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));


        //?????? ?????? ?????? ????????? ???????????????
        if (wakesystem.equals("0")) {
            btn_Auto.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Auto.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));
        } else if (wakesystem.equals("1")) {
            btn_Manual.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Manual.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));
        }
        if (wakelevel.equals("0")) {
            btn_Begin.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Begin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
        } else if (wakelevel.equals("1")) {
            btn_Armat.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Armat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
        } else if (wakelevel.equals("2")) {
            btn_Pro.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Pro.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
        }
        if (BTConnect.equals("0")) {
            tv_Connect.setTextColor(Color.parseColor("#01F8E0"));
            tv_Disconnect.setTextColor(Color.parseColor("#5D5D5D"));
            tv_Connect.setEnabled(false);
            tv_Disconnect.setEnabled(true);
        } else if (BTConnect.equals("1")) {
            tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
            tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
            tv_Connect.setEnabled(true);
            tv_Disconnect.setEnabled(false);
        }

        // GPS?????? ????????????
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // GPS ?????? ?????? ?????? ??????
        boolean isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isEnable){
            Enable_GPS.setTextColor(Color.parseColor("#FF0000"));   // GPS????????? ????????? ?????? KNOT???????????? ??????????????? ?????????.
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);





        //EditText ???????????? ???????????????
        et_RPM.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();

                RPM = textView.getText().toString();
                RPMvalue = Double.parseDouble(RPM)*1000;
                if(RPMvalue >= 4000){
                    iv_RpmGradation.setImageResource(R.drawable.rpm_redzone);
                } else {
                    iv_RpmGradation.setImageResource(R.drawable.rpm_normal);
                }
                //Do Something...
                textView.setCursorVisible(false);
                return true;
            }
        });
        et_RPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setCursorVisible(true);
            }
        });
        //EditText ???????????? ???????????????
        et_KNOT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                KNOT = textView.getText().toString();
                //Do Something...
                textView.setCursorVisible(false);
                return true;
            }
        });
        et_KNOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setCursorVisible(true);
            }
        });

        tv_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv_Connect.setEnabled(false);
                tv_Disconnect.setEnabled(false);

                Toast.makeText(getApplicationContext(),"???????????? ????????? ???????????????.",Toast.LENGTH_SHORT).show();
                ShowProgressDialog spd = new ShowProgressDialog();
                spd.execute();

                //connectSelectedDevice("SD1000v2.0.8-77FBF5");   // "SD1000v2.0.8-77FC4B"

            }
        });

        tv_Disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(BTS.getInstance().mBluetoothSocket.isConnected())
                {
                    tv_Connect.setEnabled(false);
                    tv_Disconnect.setEnabled(false);

                    Toast.makeText(getApplicationContext(),"???????????? ????????? ???????????????.",Toast.LENGTH_SHORT).show();
                    ShowProgressDialog2 spd2 = new ShowProgressDialog2();
                    spd2.execute();
                }

            }
        });

        btn_infoExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(BTConnect.equals("0")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("wakesystem", wakesystem);
                    intent.putExtra("wakelevel", wakelevel);
                    intent.putExtra("RPM", RPM);
                    intent.putExtra("KNOT", KNOT);
                    intent.putExtra("TrimLeftValue", TrimLeftValue);
                    intent.putExtra("TrimCenterValue", TrimCenterValue);
                    intent.putExtra("TrimRightValue", TrimRightValue);
                    intent.putExtra("LeftBalVal", LeftBalVal);
                    intent.putExtra("RightBalVal", RightBalVal);
                    intent.putExtra("BTConnect", BTConnect);
                    intent.putExtra("sendBalastMessage", sendBalastMessage);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"???????????? ????????? ???????????????.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_infoExpand2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_infoExpand.callOnClick();
            }
        });

        tv_Sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_infoExpand.callOnClick();
            }
        });

        btn_Auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               btn_Auto.setTextColor(Color.parseColor("#FFFFFF"));
               btn_Auto.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));
               btn_Manual.setTextColor(Color.parseColor("#000000"));
               btn_Manual.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
               wakesystem = "0";
            }
        });

        btn_Manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Auto.setTextColor(Color.parseColor("#000000"));
                btn_Auto.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Manual.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Manual.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));
                wakesystem = "1";
            }
        });

        btn_Begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Begin.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Begin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
                btn_Armat.setTextColor(Color.parseColor("#000000"));
                btn_Armat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Pro.setTextColor(Color.parseColor("#000000"));
                btn_Pro.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                wakelevel = "0";
            }
        });

        btn_Armat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Begin.setTextColor(Color.parseColor("#000000"));
                btn_Begin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Armat.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Armat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
                btn_Pro.setTextColor(Color.parseColor("#000000"));
                btn_Pro.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                wakelevel = "1";
            }
        });

        btn_Pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Begin.setTextColor(Color.parseColor("#000000"));
                btn_Begin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Armat.setTextColor(Color.parseColor("#000000"));
                btn_Armat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Pro.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Pro.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
                wakelevel = "2";
            }
        });

    }//onCreate().................................................................................................................................................................


    //????????? ???????????? ???????????? BroadcastReceiver
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();   //????????? action
            Log.d("Bluetooth action", action);
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = null;
            if(device != null) {
                name = device.getName();    //broadcast??? ?????? ????????? ????????? ????????????.
            }
            // ????????? action??? ????????? ????????? ????????????.
            switch (action) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch(state) {
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(getApplicationContext(),"??????????????? ???????????? ???????????????.",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(getApplicationContext(),"??????????????? ????????? ???????????????.",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:  // ???????????? ????????????
                    Toast.makeText(getApplicationContext(),"???????????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();

                    BTConnect = "0";
                    tv_Connect.setTextColor(Color.parseColor("#01F8E0"));
                    tv_Disconnect.setTextColor(Color.parseColor("#5D5D5D"));
                    tv_Connect.setEnabled(false);
                    tv_Disconnect.setEnabled(true);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:   // ???????????? ?????? ?????????
                    Toast.makeText(getApplicationContext(),"???????????? ???????????? ????????? ??????????????????.",Toast.LENGTH_SHORT).show();

                    BTS.getInstance().cancel();

                    BTConnect="1";
                    tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                    tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                    tv_Connect.setEnabled(true);
                    tv_Disconnect.setEnabled(false);

                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
                    break;
            }
        }
    };



    @Override
    protected void onDestroy() {
        unregisterReceiver(mBluetoothStateReceiver);
        super.onDestroy();
    }

    void connectSelectedDevice(String selectedDeviceName) {
        //???????????????????????????????????? ??????????????? ???????????? ???????????? ???????????? ?????????????????? ????????? ?????????????????? ????????? ????????????????????????????????? ???????????????.
        BTS.getInstance();
        BTS.getInstance().mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // ????????????????????? ???????????? ????????? ???????????? ???????????? ????????? ????????????????????? ????????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ??????????????????.

        if (BTS.getInstance().mBluetoothAdapter.isEnabled()) {
            BTS.getInstance().mPairedDevices = BTS.getInstance().mBluetoothAdapter.getBondedDevices();
            if(BTS.getInstance().mPairedDevices.size() > 0) {
                //???????????? ????????? ???????????????...
                BTS.getInstance().mListPairedDevices = new ArrayList<>();
                for (BluetoothDevice device : BTS.getInstance().mPairedDevices) {
                    BTS.getInstance().mListPairedDevices.add(device.getName());
                    if(device.getName().equals("SD1000v2.0.8-77FBF5")) {    // "SD1000v2.0.8-77FC4B"

                        //Toast.makeText(getApplicationContext(),"????????? ???????????????.",Toast.LENGTH_SHORT).show();

                        for(BluetoothDevice tempDevice : BTS.getInstance().mPairedDevices) {    // ???????????? ????????? ?????? ?????? ??????????????? ???????????? ???????????? ???????????? ?????? ????????? ??? ???????????? ???????????? ??????.
                            if(selectedDeviceName.equals(tempDevice.getName())) {
                                BTS.getInstance().mBluetoothDevice = tempDevice;
                                break;
                            }
                        }

                        boolean connectFlag = false;

                        try {
                            BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                            BTS.getInstance().mBluetoothSocket.connect();
                            BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                            //?????????
                             //if(BTS.getInstance().getState() == Thread.State.NEW){
                               // BTS.getInstance().start();
                            //}
                            if(BTS.getInstance().isAlive()) {
                                BTS.getInstance().interrupt();
                            }
                            bts = new BTS();
                            BTS.getInstance().start();

                            connectFlag = true;

                        } catch (IOException e1) {
                            //Toast.makeText(getApplicationContext(), "????????? ???????????? ??????????????????.(2)" , Toast.LENGTH_SHORT).show();
                            tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                            tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                            tv_Connect.setEnabled(true);
                            tv_Disconnect.setEnabled(false);
                        }

                        if(!connectFlag){   // 2??? ??????
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //?????????
                                //if(BTS.getInstance().getState() == Thread.State.NEW){
                                // BTS.getInstance().start();
                                //}
                                if(BTS.getInstance().isAlive()) {
                                    BTS.getInstance().interrupt();
                                }
                                bts = new BTS();
                                BTS.getInstance().start();

                                connectFlag = true;

                            } catch (IOException e1) {
                                Toast.makeText(getApplicationContext(), "????????? ???????????? ??????????????????.(2)" , Toast.LENGTH_SHORT).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                        if(!connectFlag){   // 3??? ??????
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //?????????
                                //if(BTS.getInstance().getState() == Thread.State.NEW){
                                // BTS.getInstance().start();
                                //}
                                if(BTS.getInstance().isAlive()) {
                                    BTS.getInstance().interrupt();
                                }
                                bts = new BTS();
                                BTS.getInstance().start();

                                connectFlag = true;

                            } catch (IOException e1) {
                                //Toast.makeText(getApplicationContext(), "????????? ???????????? ??????????????????.(3)" , Toast.LENGTH_SHORT).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                        if(!connectFlag){   // 4??? ??????
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //?????????
                                //if(BTS.getInstance().getState() == Thread.State.NEW){
                                // BTS.getInstance().start();
                                //}
                                if(BTS.getInstance().isAlive()) {
                                    BTS.getInstance().interrupt();
                                }
                                bts = new BTS();
                                BTS.getInstance().start();

                                connectFlag = true;

                            } catch (IOException e1) {
                                //Toast.makeText(getApplicationContext(), "????????? ???????????? ??????????????????.(4)" , Toast.LENGTH_SHORT).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                        if(!connectFlag){   // 5??? ??????
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //?????????
                                //if(BTS.getInstance().getState() == Thread.State.NEW){
                                // BTS.getInstance().start();
                                //}
                                if(BTS.getInstance().isAlive()) {
                                    BTS.getInstance().interrupt();
                                }
                                bts = new BTS();
                                BTS.getInstance().start();

                                connectFlag = true;

                            } catch (IOException e1) {
                                //Toast.makeText(super.getApplicationContext(), "???????????? ????????? ?????????????????????. ??????????????? ????????? ?????????" , Toast.LENGTH_LONG).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                    }
                }
            } else {
                //????????? ????????? ?????? ?????? ????????? ????????????.
                Toast.makeText(getApplicationContext(),"????????? ???????????? ?????? ??????",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "??????????????? ???????????? ??????????????????. ???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    //???????????? connect ?????? ????????? ???????????? ??????????????? ???????????????
    public class ShowProgressDialog extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            asyncDialog.setCancelable(false);

            asyncDialog.show();
            super.onPreExecute();
        }

        // ????????????????????? ??????
        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                connectSelectedDevice("SD1000v2.0.8-77FBF5");   // "SD1000v2.0.8-77FC4B"
            }catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // ?????????????????? ?????? ?????? ??? ??????
        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            //Toast.makeText(MainActivity.this,String.valueOf(tv_Connect.getCurrentTextColor()), Toast.LENGTH_LONG).show();   //-10658467
            if(tv_Connect.getCurrentTextColor()==(-10658467)){
                Toast.makeText(getApplicationContext(),"????????? ?????????????????????. ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }

    //???????????? disconnect ?????? ????????? ???????????? ??????????????? ???????????????
    public class ShowProgressDialog2 extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            asyncDialog.setCancelable(false);

            asyncDialog.show();
            super.onPreExecute();
        }

        // ????????????????????? ??????
        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                BTS.getInstance().cancel();
            }catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // ?????????????????? ?????? ?????? ??? ??????
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "?????? ?????????...", Toast.LENGTH_SHORT).show();
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }


    public void ShowTimeMethod() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                String time = simple.format(date);
                String[] splitTime = time.split(":");
                tv_Hour.setText(splitTime[0]);
                tv_Minute.setText(splitTime[1]);
                tv_Second.setText(splitTime[2]);

                try {
                    Date AppStartTime = simple.parse(SystemRunningTime);
                    Date CurrentTime = simple.parse(time);
                    long minute = (CurrentTime.getTime()-AppStartTime.getTime()) / (60 * 1000);
                    tv_SystemRunningTime.setText(String.valueOf(minute) + " " + "Min");

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){}
                    handler.sendEmptyMessage(1); //????????? ?????? = ?????? ??????
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    // GPS ?????? ?????? ???????????????
    @Override
    public void onLocationChanged(Location location) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        double deltaTime = 0;

        try{
            // getSpeed() ????????? ???????????? ?????? ??????
            double getSpeed = Double.parseDouble((String.format("%f", location.getSpeed())));
            // m/s ??? km/h??? ???????????? 3.6??? ??????????????????.
            getSpeed = (getSpeed * 3.6)/1.852;
            int getspeed = (int) getSpeed;
            //et_KNOT.setText(String.valueOf(getspeed));
            KNOT = et_KNOT.getText().toString();
        } catch (Exception e) { }


        String formatDate = sdf.format(new Date(location.getTime()));

        // ??????????????? ???????????? ????????? ?????? ????????? ?????? ?????? ??????
        if (mLastlocation != null) {
            deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000;
            // ?????? ??????
            speed = mLastlocation.distanceTo(location) / deltaTime;
            String formatLastDate = sdf.format(new Date(mLastlocation.getTime()));

            try {
                double calSpeed = Double.parseDouble(String.format("%f, speed"));
                // m/s ??? km/h??? ???????????? 3.6??? ??????????????????.
                calSpeed = (calSpeed * 3.6)/1.852;
                int calspeed = (int) calSpeed;
                //et_KNOT.setText(String.valueOf(calspeed));
                KNOT = et_KNOT.getText().toString();
            }catch (Exception e) { }
        }
        //??????????????? ??????????????? ??????
        mLastlocation = location;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }
    @Override
    public void onProviderEnabled(String provider) {
        // ????????????
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        // ???????????? ????????????
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, this);
    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        //????????????
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //???????????? ????????????
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0,this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // ???????????? ???????????? ??????
        locationManager.removeUpdates(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
             && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //????????? ?????? ?????? ?????? ?????? ?????? ?????? ???????????? ?????? ????????? ??????
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // ?????? ?????????
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
                return;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
                return;
            }
        }
    }

    //???????????? ???????????? ??????????????? ???????????? ?????????
    class SendMessageThread extends Thread {
        public void run() {

            while(true){
                try {
                    if(!ArrayQueue.getInstance().isEmpty()){
                        String data = ArrayQueue.getInstance().dequeue();
                        BTS.getInstance().write(data);
                        Log.d("sendgingMessage : ",data);
                    }
                    Thread.sleep(500);
                } catch(Exception e) { }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // ?????????????????? ????????????
    }

}