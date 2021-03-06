package com.example.kmcp;

import static com.example.kmcp.MainActivity.First;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity2 extends AppCompatActivity implements LocationListener {

    public static LocationManager locationManager;
    public static Location mLastlocation = null;
    public static double speed;

    Button btn_infoContract,btn_infoContract2,btn_Auto2,btn_Manual2,btn_Begin2,btn_Armat2,btn_Pro2,
            btn_TrimLeftUp,btn_TrimLeftDown,btn_TrimCenterUp,btn_TrimCenterDown,btn_TrimRightUp,btn_TrimRightDown,
            btn_BalastFrontUp,btn_BalastLeftUp,btn_BalastRightUp,btn_BalastFrontDown,btn_BalastLeftDown,btn_BalastRightDown;

    ImageView iv_TrimLeftBackg,iv_TrimCenterBackg,iv_TrimRightBackg,
            iv_BalastGaugeFrontBackg,iv_BalastGaugeLeftBackg,iv_BalastGaugeRightBackg,
            iv_TrimTabBarLeft,iv_TrimTabBarCenter,iv_TrimTabBarRight;

    ProgressBar progressBar2_RPM1,progressBar2_RPM2,progressBar2_KNOT1,progressBar2_KNOT2,LeftBalastValue,RightBalastValue,progressBar_Fuel2;

    TextView Enable_GPS,tv_Hour2, tv_Minute2, tv_Second2, tv_Main, tv_Connect2, tv_Disconnect2, tv_LeftBalastValue, tv_RightBalastValue, tv_Trimtab;

    EditText et_RPM2,et_KNOT2;

    String wakesystem = "0";
    String wakelevel = "0";
    String RPM = "0";
    String KNOT = "0";
    String TrimLeftValue = "412.0";
    String TrimCenterValue = "412.0";
    String TrimRightValue = "412.0";
    //???????????? ?????? ????????? ?????? ????????? 1???, ?????????2???, ?????????2????????? ??????????????????.
    String FrontBalVal = "0";
    String FrontBalVal2 = "0";
    String LeftBalVal = "0";
    String LeftBalVal2 = "0";
    String RightBalVal = "0";
    String RightBalVal2 = "0";

    String BTConnect = "1";  // 0 : ?????????, 1 : ???????????????
    String sendBalastMessage = "$KMCP_PUMP,0,0,0,0,0,0*21$R$L";


    boolean TrimLeftThreadFlag = false;
    boolean TrimCenterThreadFlag = false;
    boolean TrimRightThreadFlag = false;
    boolean BalastFrontThreadFlag = false;
    boolean BalastLeftThreadFlag = false;
    boolean BalastRightThreadFlag = false;

    //????????? ?????? ?????????
    LUHandler luhandler = new LUHandler();
    LDHandler ldhandler = new LDHandler();
    CUHandler cuhandler = new CUHandler();
    CDHandler cdhandler = new CDHandler();
    RUHandler ruhandler = new RUHandler();
    RDHandler rdhandler = new RDHandler();
    //???????????? ?????? ?????????
    LUBHandler lubhandler = new LUBHandler();
    LDBHandler ldbhandler = new LDBHandler();
    RUBHandler rubhandler = new RUBHandler();
    RDBHandler rdbhandler = new RDBHandler();

    float Rpm2;
    int Fuel2;
    int Ctemp2;
    int Opress2;
    int Otemp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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

        Intent intent = getIntent();
        wakesystem = intent.getStringExtra("wakesystem");
        wakelevel = intent.getStringExtra("wakelevel");
        RPM = intent.getStringExtra("RPM");
        KNOT = intent.getStringExtra("KNOT");
        TrimLeftValue = intent.getStringExtra("TrimLeftValue");
        TrimCenterValue = intent.getStringExtra("TrimCenterValue");
        TrimRightValue = intent.getStringExtra("TrimRightValue");
        LeftBalVal = intent.getStringExtra("LeftBalVal");
        RightBalVal = intent.getStringExtra("RightBalVal");
        BTConnect = intent.getStringExtra("BTConnect");
        sendBalastMessage = intent.getStringExtra("sendBalastMessage");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_infoContract = findViewById(R.id.btn_infoContract);
        btn_infoContract2 = findViewById(R.id.btn_infoContract2);
        btn_Auto2 = findViewById(R.id.btn_Auto2);
        btn_Manual2 = findViewById(R.id.btn_Manual2);
        btn_Begin2 = findViewById(R.id.btn_Begin2);
        btn_Armat2 = findViewById(R.id.btn_Armat2);
        btn_Pro2 = findViewById(R.id.btn_Pro2);
        btn_TrimLeftUp = findViewById(R.id.btn_TrimLeftUp);
        btn_TrimLeftDown = findViewById(R.id.btn_TrimLeftDown);
        btn_TrimCenterUp = findViewById(R.id.btn_TrimCenterUp);
        btn_TrimCenterDown = findViewById(R.id.btn_TrimCenterDown);
        btn_TrimRightUp = findViewById(R.id.btn_TrimRightUp);
        btn_TrimRightDown = findViewById(R.id.btn_TrimRightDown);
        btn_BalastFrontUp = findViewById(R.id.btn_BalastFrontUp);
        btn_BalastLeftUp = findViewById(R.id.btn_BalastLeftUp);
        btn_BalastRightUp = findViewById(R.id.btn_BalastRightUp);
        btn_BalastFrontDown = findViewById(R.id.btn_BalastFrontDown);
        btn_BalastLeftDown = findViewById(R.id.btn_BalastLeftDown);
        btn_BalastRightDown = findViewById(R.id.btn_BalastRightDown);

        iv_TrimLeftBackg = findViewById(R.id.iv_TrimLeftBackg);
        iv_TrimCenterBackg = findViewById(R.id.iv_TrimCenterBackg);
        iv_TrimRightBackg = findViewById(R.id.iv_TrimRightBackg);
        iv_BalastGaugeFrontBackg = findViewById(R.id.iv_BalastGaugeFrontBackg);
        iv_BalastGaugeLeftBackg = findViewById(R.id.iv_BalastGaugeLeftBackg);
        iv_BalastGaugeRightBackg = findViewById(R.id.iv_BalastGaugeRightBackg);
        iv_TrimTabBarLeft = findViewById(R.id.iv_TrimTabBarLeft);
        iv_TrimTabBarCenter = findViewById(R.id.iv_TrimTabBarCenter);
        iv_TrimTabBarRight  = findViewById(R.id.iv_TrimTabBarRight);

        LeftBalastValue = findViewById(R.id.LeftBalastValue);
        RightBalastValue = findViewById(R.id.RightBalastValue);
        progressBar_Fuel2 = findViewById(R.id.progressBar_Fuel2);
        progressBar2_RPM1 = findViewById(R.id.progressBar2_RPM1);
        progressBar2_RPM2 = findViewById(R.id.progressBar2_RPM2);
        progressBar2_KNOT1 = findViewById(R.id.progressBar2_KNOT1);
        progressBar2_KNOT2 = findViewById(R.id.progressBar2_KNOT2);

        tv_Main = findViewById(R.id.tv_Main);
        tv_Hour2 = findViewById(R.id.tv_Hour2);
        tv_Minute2 = findViewById(R.id.tv_Minute2);
        tv_Second2 = findViewById(R.id.tv_Second2);
        tv_Connect2 = findViewById(R.id.tv_Connect2);
        tv_Disconnect2 = findViewById(R.id.tv_Disconnect2);
        tv_LeftBalastValue = findViewById(R.id.tv_LeftBalastValue);
        tv_RightBalastValue = findViewById(R.id.tv_RightBalastValue);
        Enable_GPS = findViewById(R.id.textView22);
        tv_Trimtab = findViewById(R.id.tv_Trimtab);

        et_RPM2 = findViewById(R.id.et_RPM2);
        et_KNOT2 = findViewById(R.id.et_KNOT2);

        LeftBalastValue.setProgress(Integer.parseInt(LeftBalVal));
        RightBalastValue.setProgress(Integer.parseInt(RightBalVal));


        et_RPM2.setText(RPM);
        et_KNOT2.setText(KNOT);

        RPM = et_RPM2.getText().toString();
        KNOT = et_KNOT2.getText().toString();


        //????????? ??????????????? ???????????? 360??? ???????????? ?????? ?????????????????? -360??? ?????????. ??????????????? ??????????????? ???????????? ?????? ???????????? ??????.
        iv_TrimTabBarLeft.setY(Float.parseFloat(TrimLeftValue)-360);
        iv_TrimTabBarCenter.setY(Float.parseFloat(TrimCenterValue)-360);
        iv_TrimTabBarRight.setY(Float.parseFloat(TrimRightValue)-360);


        //????????? ???????????? ???????????? ?????? ????????? ??????????????????.
        if(First.equals("1")){

            //-48.0		312.0   ???????????? ???????????? ???????????? Y???
            //2.0		362.0   ???????????? ??????????????? ???????????? Y???
            //52.0		412.0   ???????????? ???????????? ???????????? Y???

            iv_TrimTabBarLeft.setY(2);
            iv_TrimTabBarCenter.setY(2);
            iv_TrimTabBarRight.setY(2);

            First="0";
        }
        //Toast.makeText(getApplicationContext(),String.valueOf(iv_TrimTabBarLeft.getY()),Toast.LENGTH_SHORT).show();


        //???????????? ??????????????? ????????????
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String time = simple.format(date);
        String[] splitTime = time.split(":");
        tv_Hour2.setText(splitTime[0]);
        tv_Minute2.setText(splitTime[1]);
        tv_Second2.setText(splitTime[2]);
        ShowTimeMethod();


        SendMessageThread SMT = new SendMessageThread();
        SMT.start();

        //???????????? ???????????? ???????????? ?????? ??? ????????? ???????????? ?????????
        //????????? ????????? ????????? ?????? ??????.
        BTS.getInstance().mBluetoothHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                ArrayQueue.getInstance().datanumb();
                if(msg.what == BTS.getInstance().BT_MESSAGE_READ) {
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
                                    Log.d("$KMCP_HEART",e.toString());
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
                                    if((n[0].length() - 1) == len) {
                                        mm = m[6].split("\\*");

                                        Rpm2 = Float.parseFloat(m[1]);
                                        if(Rpm2>1000){
                                            progressBar2_RPM1.setProgress(1000);
                                            progressBar2_RPM2.setProgress(((int)Rpm2-1000));
                                        } else {
                                            progressBar2_RPM1.setProgress((int)Rpm2);
                                            progressBar2_RPM2.setProgress(0);
                                        }
                                        Rpm2 = (float) (Rpm2 / 1000.0);
                                        m[1] = String.format("%.1f", Rpm2);
                                        et_RPM2.setText(m[1]);

                                        KNOT = m[2];
                                        et_KNOT2.setText(KNOT);

                                        Fuel2 = Integer.parseInt(m[3]);
                                        progressBar_Fuel2.setProgress(Fuel2);

                                        Ctemp2 = Integer.parseInt(m[4]);

                                        Opress2 = Integer.parseInt(m[5]);

                                    }

                                }catch(Exception e) {
                                    Log.d("$KMCP_ENGINE",e.toString());
                                }



                            }else if(m[0].equals("$KMCP_LEVEL")) {
                                //???????????? ?????? ??????
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
                                    if((n[0].length() - 1) == len){
                                        mm = m[8].split("\\*");

                                        int lbv, lbv2, rbv, rbv2, fbv;

                                        LeftBalVal = m[1];
                                        lbv = Integer.parseInt(m[1]);
                                        LeftBalastValue.setProgress(lbv);

                                        LeftBalVal2 = m[2];

                                        RightBalVal = m[3];
                                        rbv = Integer.parseInt(m[3]);
                                        RightBalastValue.setProgress(rbv);

                                        RightBalVal2 = m[4];

                                        FrontBalVal = m[5];

                                        FrontBalVal2 = m[6];

                                        int left = Integer.parseInt(tv_LeftBalastValue.getText().toString());
                                        int right = Integer.parseInt(tv_RightBalastValue.getText().toString());

                                        if(LeftBalastValue.getProgress()==left){ // Left ?????? Off
                                            StringBuilder newstring = new StringBuilder(sendBalastMessage);
                                            newstring.setCharAt(13,'0');
                                            sendBalastMessage = newstring.toString();
                                        }
                                        if(LeftBalastValue.getProgress()>left){ // Left ?????? Empty, ?????? On
                                            StringBuilder newstring = new StringBuilder(sendBalastMessage);
                                            newstring.setCharAt(11,'0');
                                            newstring.setCharAt(13,'1');
                                            sendBalastMessage = newstring.toString();
                                        }
                                        if(LeftBalastValue.getProgress()<left){ // left ?????? Fill, ?????? On
                                            StringBuilder newstring = new StringBuilder(sendBalastMessage);
                                            newstring.setCharAt(11,'1');
                                            newstring.setCharAt(13,'1');
                                            sendBalastMessage = newstring.toString();
                                        }
                                        if(RightBalastValue.getProgress()==right){ // Right ?????? Off
                                            StringBuilder newstring = new StringBuilder(sendBalastMessage);
                                            newstring.setCharAt(17,'0');
                                            sendBalastMessage = newstring.toString();
                                        }
                                        if(RightBalastValue.getProgress()>right){ // Right ?????? Empty, ?????? On
                                            StringBuilder newstring = new StringBuilder(sendBalastMessage);
                                            newstring.setCharAt(15,'0');
                                            newstring.setCharAt(17,'1');
                                            sendBalastMessage = newstring.toString();
                                        }
                                        if(RightBalastValue.getProgress()<right){ // Right ?????? Fill, ?????? On
                                            StringBuilder newstring = new StringBuilder(sendBalastMessage);
                                            newstring.setCharAt(15,'1');
                                            newstring.setCharAt(17,'1');
                                            sendBalastMessage = newstring.toString();
                                        }
                                        //BTS.getInstance().write(sendBalastMessage);
                                        ArrayQueue.getInstance().enqueue(sendBalastMessage);


                                        //tv_Trimtab.setText(sendBalastMessage); //???????????? ??????????????? ????????? ?????????

                                    }

                                }catch(Exception e) {
                                    Log.d("$KMCP_LEVEL",e.toString());
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

                                //-48.0		312.0   ???????????? ???????????? ???????????? Y???
                                //2.0		362.0   ???????????? ??????????????? ???????????? Y???
                                //52.0		412.0   ???????????? ???????????? ???????????? Y???

                                try {
                                    n=readMessage.split("\\*");
                                    len = Integer.parseInt(n[1]);
                                    if(n[0].length() == len){
                                        mm = m[6].split("\\*");



                                    }

                                }catch(Exception e) {
                                    Log.d("$KMCP_TRIMTAB",e.toString());
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
                                    Log.d("$KMCP_PUMP",e.toString());
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
                                    Log.d("$KMCP_VALVE",e.toString());
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
        btn_Auto2.setTextColor(Color.parseColor("#000000"));
        btn_Auto2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
        btn_Manual2.setTextColor(Color.parseColor("#000000"));
        btn_Manual2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));

        btn_Begin2.setTextColor(Color.parseColor("#000000"));
        btn_Begin2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
        btn_Armat2.setTextColor(Color.parseColor("#000000"));
        btn_Armat2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
        btn_Pro2.setTextColor(Color.parseColor("#000000"));
        btn_Pro2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));



        //?????? ?????? ?????? ????????? ???????????????

        if(wakesystem.equals("0"))
        {
            btn_Auto2.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Auto2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));

            // AUTO????????? ?????? ??????-???, ????????????-??? ????????? ???????????? ??????.
            btn_TrimLeftUp.setEnabled(false);
            btn_TrimLeftDown.setEnabled(false);
            btn_TrimCenterUp.setEnabled(false);
            btn_TrimCenterDown.setEnabled(false);
            btn_TrimRightUp.setEnabled(false);
            btn_TrimRightDown.setEnabled(false);
            btn_BalastFrontUp.setEnabled(false);
            btn_BalastFrontDown.setEnabled(false);
            btn_BalastLeftUp.setEnabled(false);
            btn_BalastLeftDown.setEnabled(false);
            btn_BalastRightUp.setEnabled(false);
            btn_BalastRightDown.setEnabled(false);

        } else if(wakesystem.equals("1")){
            btn_Manual2.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Manual2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));

            // Manual????????? ?????? ??????-???, ????????????-??? ????????? ???????????? ??????.
            btn_TrimLeftUp.setEnabled(true);
            btn_TrimLeftDown.setEnabled(true);
            btn_TrimCenterUp.setEnabled(true);
            btn_TrimCenterDown.setEnabled(true);
            btn_TrimRightUp.setEnabled(true);
            btn_TrimRightDown.setEnabled(true);
            btn_BalastFrontUp.setEnabled(true);
            btn_BalastFrontDown.setEnabled(true);
            btn_BalastLeftUp.setEnabled(true);
            btn_BalastLeftDown.setEnabled(true);
            btn_BalastRightUp.setEnabled(true);
            btn_BalastRightDown.setEnabled(true);
        }
        if(wakelevel.equals("0"))
        {
            btn_Begin2.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Begin2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
        } else if(wakelevel.equals("1")) {
            btn_Armat2.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Armat2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
        } else if(wakelevel.equals("2")){
            btn_Pro2.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Pro2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
        }
        if(BTConnect.equals("0")) {
            tv_Connect2.setTextColor(Color.parseColor("#01F8E0"));
            tv_Disconnect2.setTextColor(Color.parseColor("#5D5D5D"));
            tv_Connect2.setEnabled(false);
            tv_Disconnect2.setEnabled(true);
        } else if(BTConnect.equals("1")) {
            tv_Connect2.setTextColor(Color.parseColor("#5D5D5D"));
            tv_Disconnect2.setTextColor(Color.parseColor("#01F8E0"));
            tv_Connect2.setEnabled(true);
            tv_Disconnect2.setEnabled(false);
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
        et_RPM2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                RPM = textView.getText().toString();
                //Do Something...
                textView.setCursorVisible(false);
                return true;
            }
        });
        et_RPM2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setCursorVisible(true);
            }
        });
        //EditText ???????????? ???????????????
        et_KNOT2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                KNOT = textView.getText().toString();
                //Do Something...
                textView.setCursorVisible(false);
                return true;
            }
        });
        et_KNOT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setCursorVisible(true);
            }
        });

        btn_infoContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("wakesystem",wakesystem);
                intent.putExtra("wakelevel",wakelevel);
                intent.putExtra("RPM", RPM);
                intent.putExtra("KNOT", KNOT);
                intent.putExtra("LeftBalVal", LeftBalVal);
                intent.putExtra("RightBalVal", RightBalVal);
                intent.putExtra("BTConnect", BTConnect);
                intent.putExtra("sendBalastMessage", sendBalastMessage);

                float left = iv_TrimTabBarLeft.getY();
                TrimLeftValue = String.valueOf(left);
                intent.putExtra("TrimLeftValue",TrimLeftValue);

                float center = iv_TrimTabBarCenter.getY();
                TrimCenterValue = String.valueOf(center);
                intent.putExtra("TrimCenterValue",TrimCenterValue);

                float right = iv_TrimTabBarRight.getY();
                TrimRightValue = String.valueOf(right);
                intent.putExtra("TrimRightValue",TrimRightValue);

                startActivity(intent);
            }
        });

        btn_infoContract2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_infoContract.callOnClick();
            }
        });

        tv_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_infoContract.callOnClick();
            }
        });

        tv_Disconnect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BTS.getInstance().mBluetoothSocket.isConnected())
                {
                    tv_Connect2.setEnabled(false);
                    tv_Disconnect2.setEnabled(false);

                    BTS.getInstance().cancel();
                    BTS.getInstance().mPairedDevices = null;
                    BTS.getInstance().mBluetoothAdapter = null;
                    try {
                        BTS.getInstance().mBluetoothSocket.close();

                        Toast.makeText(getApplicationContext(), "???????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();

                        BTConnect = "0";

                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "???????????? ????????? ?????? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }


                    btn_infoContract.callOnClick();

                }
            }
        });

        btn_Auto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Auto2.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Auto2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));
                btn_Manual2.setTextColor(Color.parseColor("#000000"));
                btn_Manual2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                wakesystem = "0";

                // AUTO????????? ?????? ??????-???, ????????????-??? ????????? ???????????? ??????.
                btn_TrimLeftUp.setEnabled(false);
                btn_TrimLeftDown.setEnabled(false);
                btn_TrimCenterUp.setEnabled(false);
                btn_TrimCenterDown.setEnabled(false);
                btn_TrimRightUp.setEnabled(false);
                btn_TrimRightDown.setEnabled(false);
                btn_BalastFrontUp.setEnabled(false);
                btn_BalastFrontDown.setEnabled(false);
                btn_BalastLeftUp.setEnabled(false);
                btn_BalastLeftDown.setEnabled(false);
                btn_BalastRightUp.setEnabled(false);
                btn_BalastRightDown.setEnabled(false);
            }
        });

        btn_Manual2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Auto2.setTextColor(Color.parseColor("#000000"));
                btn_Auto2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Manual2.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Manual2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));
                wakesystem = "1";

                // Manual????????? ?????? ??????-???, ????????????-??? ????????? ???????????? ??????.
                btn_TrimLeftUp.setEnabled(true);
                btn_TrimLeftDown.setEnabled(true);
                btn_TrimCenterUp.setEnabled(true);
                btn_TrimCenterDown.setEnabled(true);
                btn_TrimRightUp.setEnabled(true);
                btn_TrimRightDown.setEnabled(true);
                btn_BalastFrontUp.setEnabled(true);
                btn_BalastFrontDown.setEnabled(true);
                btn_BalastLeftUp.setEnabled(true);
                btn_BalastLeftDown.setEnabled(true);
                btn_BalastRightUp.setEnabled(true);
                btn_BalastRightDown.setEnabled(true);
            }
        });

        btn_Begin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Begin2.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Begin2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
                btn_Armat2.setTextColor(Color.parseColor("#000000"));
                btn_Armat2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Pro2.setTextColor(Color.parseColor("#000000"));
                btn_Pro2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                wakelevel = "0";
            }
        });

        btn_Armat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Begin2.setTextColor(Color.parseColor("#000000"));
                btn_Begin2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Armat2.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Armat2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
                btn_Pro2.setTextColor(Color.parseColor("#000000"));
                btn_Pro2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                wakelevel = "1";
            }
        });

        btn_Pro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Begin2.setTextColor(Color.parseColor("#000000"));
                btn_Begin2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Armat2.setTextColor(Color.parseColor("#000000"));
                btn_Armat2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D3D3")));
                btn_Pro2.setTextColor(Color.parseColor("#FFFFFF"));
                btn_Pro2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32AAA8")));
                wakelevel = "2";
            }
        });



        btn_TrimLeftUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //????????? ?????? ??? ????????? ?????? ??? ???????????? ???????????? ?????? ????????? ?????? ??????????????????.
                        btn_TrimLeftUp.setBackground(btn_TrimLeftUp.getResources().getDrawable(R.drawable.trimtabbuttonuptrans));
                        iv_TrimLeftBackg.setImageResource(R.drawable.trimtabbackgroundtrans);
                        float oldY = iv_TrimTabBarLeft.getY();
                        if(oldY > 316) {
                            iv_TrimTabBarLeft.setY(oldY - 5);
                            //????????? 4?????? ????????????
                            //iv_TrimTabBarLeft.getY ?????? 312????????? 100%, 362????????? 50%, 412????????? 0% ????????? ???????????? ???????????? ?????????.
                            //int bala = (int)(iv_TrimTabBarLeft.getY() - 412)*(-1);
                            //Toast.makeText(getApplicationContext(),String.valueOf(bala),Toast.LENGTH_SHORT).show();
                            //LeftBalastValue.setProgress(bala);
                            //BTS.getInstance().write("$KMCP_TRIMTAB,1,1,0,0,0,0*24$R$L");
                            ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,1,1,0,0,0,0*24$R$L");

                        }
                        iv_TrimTabBarLeft.setImageResource(R.drawable.trimtabbartrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_TrimLeftUp.setBackground(btn_TrimLeftUp.getResources().getDrawable(R.drawable.trimtabbuttonup));
                        iv_TrimLeftBackg.setImageResource(R.drawable.trimtabbackgroundnormal);
                        iv_TrimTabBarLeft.setImageResource(R.drawable.trimtabbarnormal);
                        TrimLeftThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_TrimLeftUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //???????????? ???????????? ????????? ???????????? ??????????????? ????????? ?????? ????????? ???????????? ?????? ????????????.
                LUTrimThread lu = new LUTrimThread();
                lu.start();
                return false;   //true ??? ???????????? OnClick ???????????? ???????????? ?????????.
            }
        });
        btn_TrimLeftUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BTS.getInstance().write("$KMCP_TRIMTAB,1,0,0,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,1,0,0,0,0,0*24$R$L");
                TrimLeftThreadFlag = false;
            }
        });


        btn_TrimLeftDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_TrimLeftDown.setBackground(btn_TrimLeftDown.getResources().getDrawable(R.drawable.trimtabbuttondowntrans));
                        iv_TrimLeftBackg.setImageResource(R.drawable.trimtabbackgroundtrans);
                        float oldY = iv_TrimTabBarLeft.getY();
                        if(oldY < 410) {
                            iv_TrimTabBarLeft.setY(oldY + 5);
                            //BTS.getInstance().write("$KMCP_TRIMTAB,0,1,0,0,0,0*24$R$L");
                            ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,1,0,0,0,0*24$R$L");
                        }
                        //Toast myToast = Toast.makeText(getApplicationContext(),String.valueOf(oldY+4), Toast.LENGTH_SHORT);
                        //myToast.show();
                        iv_TrimTabBarLeft.setImageResource(R.drawable.trimtabbartrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_TrimLeftDown.setBackground(btn_TrimLeftDown.getResources().getDrawable(R.drawable.trimtabbutton2));
                        iv_TrimLeftBackg.setImageResource(R.drawable.trimtabbackgroundnormal);
                        iv_TrimTabBarLeft.setImageResource(R.drawable.trimtabbarnormal);
                        TrimLeftThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_TrimLeftDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LDTrimThread ld = new LDTrimThread();
                ld.start();
                return false;
            }
        });
        btn_TrimLeftDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,0,0*24$R$L");
                TrimLeftThreadFlag = false;
            }
        });

        btn_TrimCenterUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_TrimCenterUp.setBackground(btn_TrimCenterUp.getResources().getDrawable(R.drawable.trimtabbuttonuptrans));
                        iv_TrimCenterBackg.setImageResource(R.drawable.trimtabbackgroundtrans);
                        float oldY = iv_TrimTabBarCenter.getY();
                        if(oldY > 316) {
                            iv_TrimTabBarCenter.setY(oldY - 5);
                            //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,1,1*24$R$L");
                            ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,1,1*24$R$L");
                        }
                        iv_TrimTabBarCenter.setImageResource(R.drawable.trimtabbartrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_TrimCenterUp.setBackground(btn_TrimCenterUp.getResources().getDrawable(R.drawable.trimtabbuttonup));
                        iv_TrimCenterBackg.setImageResource(R.drawable.trimtabbackgroundnormal);
                        iv_TrimTabBarCenter.setImageResource(R.drawable.trimtabbarnormal);
                        TrimCenterThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_TrimCenterUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CUTrimThread cu = new CUTrimThread();
                cu.start();
                return false;
            }
        });
        btn_TrimCenterUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,1,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,1,0*24$R$L");
                TrimCenterThreadFlag = false;
            }
        });

        btn_TrimCenterDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_TrimCenterDown.setBackground(btn_TrimCenterDown.getResources().getDrawable(R.drawable.trimtabbuttondowntrans));
                        iv_TrimCenterBackg.setImageResource(R.drawable.trimtabbackgroundtrans);
                        float oldY = iv_TrimTabBarCenter.getY();
                        if(oldY < 410) {
                            iv_TrimTabBarCenter.setY(oldY + 5);
                            //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,0,1,*24$R$L");
                            ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,0,1,*24$R$L");
                        }
                        iv_TrimTabBarCenter.setImageResource(R.drawable.trimtabbartrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_TrimCenterDown.setBackground(btn_TrimCenterDown.getResources().getDrawable(R.drawable.trimtabbutton2));
                        iv_TrimCenterBackg.setImageResource(R.drawable.trimtabbackgroundnormal);
                        iv_TrimTabBarCenter.setImageResource(R.drawable.trimtabbarnormal);
                        TrimCenterThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_TrimCenterDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CDTrimThread cd = new CDTrimThread();
                cd.start();
                return false;
            }
        });
        btn_TrimCenterDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,0,0*24$R$L");
                TrimCenterThreadFlag = false;
            }
        });

        btn_TrimRightUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_TrimRightUp.setBackground(btn_TrimRightUp.getResources().getDrawable(R.drawable.trimtabbuttonuptrans));
                        iv_TrimRightBackg.setImageResource(R.drawable.trimtabbackgroundtrans);
                        float oldY = iv_TrimTabBarRight.getY();
                        if(oldY > 316) {
                            iv_TrimTabBarRight.setY(oldY - 5);
                            //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,1,1,0,0,*24$R$L");
                            ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,1,1,0,0,*24$R$L");
                        }
                        iv_TrimTabBarRight.setImageResource(R.drawable.trimtabbartrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_TrimRightUp.setBackground(btn_TrimRightUp.getResources().getDrawable(R.drawable.trimtabbuttonup));
                        iv_TrimRightBackg.setImageResource(R.drawable.trimtabbackgroundnormal);
                        iv_TrimTabBarRight.setImageResource(R.drawable.trimtabbarnormal);
                        TrimRightThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_TrimRightUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                RUTrimThread ru = new RUTrimThread();
                ru.start();
                return false;
            }
        });
        btn_TrimRightUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,1,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,1,0,0,0*24$R$L");
                TrimRightThreadFlag = false;
            }
        });

        btn_TrimRightDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_TrimRightDown.setBackground(btn_TrimRightDown.getResources().getDrawable(R.drawable.trimtabbuttondowntrans));
                        iv_TrimRightBackg.setImageResource(R.drawable.trimtabbackgroundtrans);
                        float oldY = iv_TrimTabBarRight.getY();
                        if(oldY < 410) {
                            iv_TrimTabBarRight.setY(oldY + 5);
                            //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,1,0,0,*24$R$L");
                            ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,1,0,0,*24$R$L");
                        }
                        iv_TrimTabBarRight.setImageResource(R.drawable.trimtabbartrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_TrimRightDown.setBackground(btn_TrimRightDown.getResources().getDrawable(R.drawable.trimtabbutton2));
                        iv_TrimRightBackg.setImageResource(R.drawable.trimtabbackgroundnormal);
                        iv_TrimTabBarRight.setImageResource(R.drawable.trimtabbarnormal);
                        TrimRightThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_TrimRightDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                RDTrimThread rd = new RDTrimThread();
                rd.start();
                return false;
            }
        });
        btn_TrimRightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,0,0*24$R$L");
                TrimRightThreadFlag = false;
            }
        });

        btn_BalastFrontUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_BalastFrontUp.setBackground(btn_BalastFrontUp.getResources().getDrawable(R.drawable.balastupbuttontrarns));
                        iv_BalastGaugeFrontBackg.setImageResource(R.drawable.balastgaugetrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_BalastFrontUp.setBackground(btn_BalastFrontUp.getResources().getDrawable(R.drawable.balastupbuttonnormal));
                        iv_BalastGaugeFrontBackg.setImageResource(R.drawable.balastgaugenormal);
                        break;
                }
                return false;
            }
        });

        btn_BalastFrontDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_BalastFrontDown.setBackground(btn_BalastFrontDown.getResources().getDrawable(R.drawable.balastdownbuttontrans));
                        iv_BalastGaugeFrontBackg.setImageResource(R.drawable.balastgaugetrans);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_BalastFrontDown.setBackground(btn_BalastFrontDown.getResources().getDrawable(R.drawable.balastdownbuttonnormal));
                        iv_BalastGaugeFrontBackg.setImageResource(R.drawable.balastgaugenormal);
                        break;
                }
                return false;
            }
        });

        btn_BalastLeftUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_BalastLeftUp.setBackground(btn_BalastLeftUp.getResources().getDrawable(R.drawable.balastupbuttontrarns));
                        iv_BalastGaugeLeftBackg.setImageResource(R.drawable.balastgaugetrans);
                        LeftBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar_trans));

                        try{
                            int temp = Integer.parseInt(tv_LeftBalastValue.getText().toString());
                            if(temp<100) {
                                temp += 10;
                            }
                            tv_LeftBalastValue.setText(String.valueOf(temp));
                            LeftBalVal = String.valueOf(temp);
                        }catch(Exception e) { }

                        break;
                    case MotionEvent.ACTION_UP:
                        btn_BalastLeftUp.setBackground(btn_BalastLeftUp.getResources().getDrawable(R.drawable.balastupbuttonnormal));
                        iv_BalastGaugeLeftBackg.setImageResource(R.drawable.balastgaugenormal);
                        LeftBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar));
                        BalastLeftThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_BalastLeftUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LUBalastThread lub = new LUBalastThread();
                lub.start();
                return false;
            }
        });
        btn_BalastLeftUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalastLeftThreadFlag = false;
            }
        });

        btn_BalastLeftDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_BalastLeftDown.setBackground(btn_BalastLeftDown.getResources().getDrawable(R.drawable.balastdownbuttontrans));
                        iv_BalastGaugeLeftBackg.setImageResource(R.drawable.balastgaugetrans);
                        LeftBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar_trans));

                        try{
                            int temp = Integer.parseInt(tv_LeftBalastValue.getText().toString());
                            if(temp>0) {
                                temp -= 10;
                            }
                            tv_LeftBalastValue.setText(String.valueOf(temp));
                            LeftBalVal = String.valueOf(temp);
                        }catch(Exception e) { }

                        break;
                    case MotionEvent.ACTION_UP:
                        btn_BalastLeftDown.setBackground(btn_BalastLeftDown.getResources().getDrawable(R.drawable.balastdownbuttonnormal));
                        iv_BalastGaugeLeftBackg.setImageResource(R.drawable.balastgaugenormal);
                        LeftBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar));
                        BalastLeftThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_BalastLeftDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LDBalstThread ldb = new LDBalstThread();
                ldb.start();
                return false;
            }
        });
        btn_BalastLeftDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalastLeftThreadFlag = false;
            }
        });

        btn_BalastRightUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_BalastRightUp.setBackground(btn_BalastRightUp.getResources().getDrawable(R.drawable.balastupbuttontrarns));
                        iv_BalastGaugeRightBackg.setImageResource(R.drawable.balastgaugetrans);
                        RightBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar_trans));

                        try{
                            int temp = Integer.parseInt(tv_RightBalastValue.getText().toString());
                            if(temp<100) {
                                temp += 10;
                            }
                            tv_RightBalastValue.setText(String.valueOf(temp));
                            RightBalVal = String.valueOf(temp);
                        }catch(Exception e) { }
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_BalastRightUp.setBackground(btn_BalastRightUp.getResources().getDrawable(R.drawable.balastupbuttonnormal));
                        iv_BalastGaugeRightBackg.setImageResource(R.drawable.balastgaugenormal);
                        RightBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar));
                        BalastRightThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_BalastRightUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RUBalastThread rub = new RUBalastThread();
                rub.start();
                return false;
            }
        });
        btn_BalastRightUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalastRightThreadFlag = false;
            }
        });

        btn_BalastRightDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btn_BalastRightDown.setBackground(btn_BalastRightDown.getResources().getDrawable(R.drawable.balastdownbuttontrans));
                        iv_BalastGaugeRightBackg.setImageResource(R.drawable.balastgaugetrans);
                        RightBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar_trans));

                        try{
                            int temp = Integer.parseInt(tv_RightBalastValue.getText().toString());
                            if(temp>0) {
                                temp -= 10;
                            }
                            tv_RightBalastValue.setText(String.valueOf(temp));
                            RightBalVal = String.valueOf(temp);
                        }catch(Exception e) { }
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_BalastRightDown.setBackground(btn_BalastRightDown.getResources().getDrawable(R.drawable.balastdownbuttonnormal));
                        iv_BalastGaugeRightBackg.setImageResource(R.drawable.balastgaugenormal);
                        RightBalastValue.setProgressDrawable(getResources().getDrawable(R.drawable.balast_bar));
                        BalastRightThreadFlag = false;
                        break;
                }
                return false;
            }
        });
        btn_BalastRightDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RDBalstThread rdb = new RDBalstThread();
                rdb.start();
                return false;
            }
        });
        btn_BalastRightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalastRightThreadFlag = false;
            }
        });


        //Toast myToast = Toast.makeText(this.getApplicationContext(),wakesystem + wakelevel, Toast.LENGTH_SHORT);
        //myToast.show();
    } //onCreate-----------------------------------------------------------------------------------------------------------------------------------

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
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:   // ???????????? ?????? ?????????
                    Toast.makeText(getApplicationContext(),"???????????? ???????????? ????????? ??????????????????.",Toast.LENGTH_SHORT).show();

                    BTS.getInstance().cancel();

                    tv_Connect2.setTextColor(Color.parseColor("#5D5D5D"));
                    tv_Disconnect2.setTextColor(Color.parseColor("#01F8E0"));
                    tv_Connect2.setEnabled(true);
                    tv_Disconnect2.setEnabled(false);
                    BTConnect="1";
                    btn_infoContract.callOnClick();
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



    // ?????? ?????? ????????? ======================================================================================================
    class LUTrimThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            TrimLeftThreadFlag = true;
            while(TrimLeftThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value", value);

                Message msg = luhandler.obtainMessage();
                msg.setData(bundle);
                luhandler.sendMessage(msg);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class LUHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            float oldY = iv_TrimTabBarLeft.getY();
            if(oldY > 316) {
                iv_TrimTabBarLeft.setY(oldY - 5);
                //BTS.getInstance().write("$KMCP_TRIMTAB,1,1,0,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,1,1,0,0,0,0*24$R$L");
            }

        }
    }

    class LDTrimThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            TrimLeftThreadFlag = true;
            while(TrimLeftThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value", value);

                Message msg = ldhandler.obtainMessage();
                msg.setData(bundle);
                ldhandler.sendMessage(msg);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class LDHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            float oldY = iv_TrimTabBarLeft.getY();
            if(oldY < 410) {
                iv_TrimTabBarLeft.setY(oldY + 5);
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,1,0,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,1,0,0,0,0*24$R$L");
            }
        }
    }

    class CUTrimThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            TrimCenterThreadFlag = true;
            while(TrimCenterThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value", value);

                Message msg = cuhandler.obtainMessage();
                msg.setData(bundle);
                cuhandler.sendMessage(msg);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class CUHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            float oldY = iv_TrimTabBarCenter.getY();
            if(oldY > 316) {
                iv_TrimTabBarCenter.setY(oldY - 5);
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,1,1*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,1,1*24$R$L");
            }

        }
    }

    class CDTrimThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            TrimCenterThreadFlag = true;
            while(TrimCenterThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value", value);

                Message msg = cdhandler.obtainMessage();
                msg.setData(bundle);
                cdhandler.sendMessage(msg);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class CDHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            float oldY = iv_TrimTabBarCenter.getY();
            if(oldY < 410) {
                iv_TrimTabBarCenter.setY(oldY + 5);
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,0,0,1,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,0,0,1,0*24$R$L");
            }
        }
    }

    class RUTrimThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            TrimRightThreadFlag = true;
            while(TrimRightThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value", value);

                Message msg = ruhandler.obtainMessage();
                msg.setData(bundle);
                ruhandler.sendMessage(msg);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class RUHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            float oldY = iv_TrimTabBarRight.getY();
            if(oldY > 316) {
                iv_TrimTabBarRight.setY(oldY - 5);
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,1,1,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,1,1,0,0*24$R$L");
            }

        }
    }

    class RDTrimThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            TrimRightThreadFlag = true;
            while(TrimRightThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value", value);

                Message msg = rdhandler.obtainMessage();
                msg.setData(bundle);
                rdhandler.sendMessage(msg);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class RDHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            float oldY = iv_TrimTabBarRight.getY();
            if(oldY < 410) {
                iv_TrimTabBarRight.setY(oldY + 5);
                //BTS.getInstance().write("$KMCP_TRIMTAB,0,0,1,0,0,0*24$R$L");
                ArrayQueue.getInstance().enqueue("$KMCP_TRIMTAB,0,0,1,0,0,0*24$R$L");
            }
        }
    }
//=============================================================================================================


    //?????? ?????????????????? ?????????
    class LUBalastThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            BalastLeftThreadFlag = true;
            while(BalastLeftThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value",value);

                Message msg = lubhandler.obtainMessage();
                msg.setData(bundle);
                lubhandler.sendMessage(msg);

                try{
                    Thread.sleep(500);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class LUBHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            try{
                int value = Integer.parseInt(tv_LeftBalastValue.getText().toString());
                if(value < 100) {
                    value += 10;
                    tv_LeftBalastValue.setText(String.valueOf(value));
                }
            }catch(Exception e){ }

        }
    }

    class LDBalstThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            BalastLeftThreadFlag = true;
            while(BalastLeftThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value",value);

                Message msg = ldbhandler.obtainMessage();
                msg.setData(bundle);
                ldbhandler.sendMessage(msg);

                try{
                    Thread.sleep(500);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class LDBHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            try{
                int value = Integer.parseInt(tv_LeftBalastValue.getText().toString());
                if(value > 0) {
                    value -= 10;
                    tv_LeftBalastValue.setText(String.valueOf(value));
                }
            }catch(Exception e){ }
        }
    }

    class RUBalastThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            BalastRightThreadFlag = true;
            while(BalastRightThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value",value);

                Message msg = rubhandler.obtainMessage();
                msg.setData(bundle);
                rubhandler.sendMessage(msg);

                try{
                    Thread.sleep(500);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class RUBHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            int value = Integer.parseInt(tv_RightBalastValue.getText().toString());
            if(value < 100) {
                value += 10;
                tv_RightBalastValue.setText(String.valueOf(value));
            }

        }
    }

    class RDBalstThread extends Thread {
        int value = 0;

        @Override
        public void run() {
            BalastRightThreadFlag = true;
            while(BalastRightThreadFlag){
                value += 1;

                Bundle bundle = new Bundle();
                bundle.putInt("value",value);

                Message msg = rdbhandler.obtainMessage();
                msg.setData(bundle);
                rdbhandler.sendMessage(msg);

                try{
                    Thread.sleep(500);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class RDBHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            int value = Integer.parseInt(tv_RightBalastValue.getText().toString());
            if(value > 0) {
                value -= 10;
                tv_RightBalastValue.setText(String.valueOf(value));
            }
        }
    }



    //??????????????? ??????????????? ??????
    public void ShowTimeMethod() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                String time = simple.format(date);
                String[] splitTime = time.split(":");
                tv_Hour2.setText(splitTime[0]);
                tv_Minute2.setText(splitTime[1]);
                tv_Second2.setText(splitTime[2]);
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
            //et_KNOT2.setText(String.valueOf(getspeed));
            KNOT = et_KNOT2.getText().toString();
            if(getspeed > 10){
                progressBar2_KNOT1.setProgress(10);
                progressBar2_KNOT2.setProgress(getspeed-10);
            }else {
                progressBar2_KNOT1.setProgress(getspeed);
                progressBar2_KNOT2.setProgress(0);
            }
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
                //et_KNOT2.setText(String.valueOf(calspeed));
                KNOT = et_KNOT2.getText().toString();
                if(calspeed > 10){
                    progressBar2_KNOT1.setProgress(10);
                    progressBar2_KNOT2.setProgress(calspeed-10);
                }else {
                    progressBar2_KNOT1.setProgress(calspeed);
                    progressBar2_KNOT2.setProgress(0);
                }
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
                    Thread.sleep(200);
                } catch(Exception e) { }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // ?????????????????? ????????????
    }

}
