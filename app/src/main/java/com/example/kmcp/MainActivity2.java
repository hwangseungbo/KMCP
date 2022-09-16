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
import android.graphics.drawable.Drawable;
import android.location.Location;
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

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


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


    TextView Enable_GPS,tv_Hour2, tv_Minute2, tv_Second2, tv_Main, tv_Connect2, tv_Disconnect2, tv_LeftBalastValue, tv_RightBalastValue;

    EditText et_RPM2,et_KNOT2;

    String wakesystem = "0";
    String wakelevel = "0";
    String RPM = "0";
    String KNOT = "0";
    String TrimLeftValue = "412.0";
    String TrimCenterValue = "412.0";
    String TrimRightValue = "412.0";
    //발라스트 값은 확장을 위해 프론트 1개, 레프트2개, 라이트2개까지 이루어져있다.
    String FrontBalVal = "0";
    String FrontBalVal2 = "0";
    String LeftBalVal = "0";
    String LeftBalVal2 = "0";
    String RightBalVal = "0";
    String RightBalVal2 = "0";

    String BTConnect = "1";  // 0 : 커넥트, 1 : 디스커넥트

    boolean TrimLeftThreadFlag = false;
    boolean TrimCenterThreadFlag = false;
    boolean TrimRightThreadFlag = false;
    boolean BalastFrontThreadFlag = false;
    boolean BalastLeftThreadFlag = false;
    boolean BalastRightThreadFlag = false;

    //트림탭 관련 핸들러
    LUHandler luhandler = new LUHandler();
    LDHandler ldhandler = new LDHandler();
    CUHandler cuhandler = new CUHandler();
    CDHandler cdhandler = new CDHandler();
    RUHandler ruhandler = new RUHandler();
    RDHandler rdhandler = new RDHandler();
    //발라스트 관련 핸들러
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

        //블루투스 브로드캐스트 리시버 등록
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);    //기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //기기 검색 종료
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

        et_RPM2 = findViewById(R.id.et_RPM2);
        et_KNOT2 = findViewById(R.id.et_KNOT2);

        LeftBalastValue.setProgress(Integer.parseInt(LeftBalVal));
        try{
            tv_LeftBalastValue.setText(String.valueOf(LeftBalastValue.getProgress())+"%");
        }catch(Exception e){}
        RightBalastValue.setProgress(Integer.parseInt(RightBalVal));
        try{
            tv_RightBalastValue.setText(String.valueOf(RightBalastValue.getProgress())+"%");
        }catch(Exception e){}

        et_RPM2.setText(RPM);
        et_KNOT2.setText(KNOT);

        RPM = et_RPM2.getText().toString();
        KNOT = et_KNOT2.getText().toString();


        //이유를 모르겠는데 위치값이 360씩 커지므로 이를 보정하기위해 -360을 하였다. 절대좌표와 상대좌표의 차이에서 오는 문제인듯 하다.
        iv_TrimTabBarLeft.setY(Float.parseFloat(TrimLeftValue)-360);
        iv_TrimTabBarCenter.setY(Float.parseFloat(TrimCenterValue)-360);
        iv_TrimTabBarRight.setY(Float.parseFloat(TrimRightValue)-360);




        //트림탭 중앙으로 맞춰주기 처음 한번만 동작해야한다.
        if(First.equals("1")){

            //-48.0		312.0   트림탭이 최고위에 있을경우 Y값
            //2.0		362.0   트림탭이 중간위치에 있을경우 Y값
            //52.0		412.0   트림탭이 맨아래에 있을경우 Y값

            iv_TrimTabBarLeft.setY(2);
            iv_TrimTabBarCenter.setY(2);
            iv_TrimTabBarRight.setY(2);

            First="0";
        }

        //Toast.makeText(getApplicationContext(),String.valueOf(iv_TrimTabBarLeft.getY()),Toast.LENGTH_SHORT).show();




        //현재시간 실시간으로 가져오기
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String time = simple.format(date);
        String[] splitTime = time.split(":");
        tv_Hour2.setText(splitTime[0]);
        tv_Minute2.setText(splitTime[1]);
        tv_Second2.setText(splitTime[2]);
        ShowTimeMethod();


        //블루투스 핸들러로 블루트스 연결 뒤 수신된 데이터를 읽어옴
        //여기서 데이터 값들에 대한 처리.
        BTS.getInstance().mBluetoothHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(msg.what == BTS.getInstance().BT_MESSAGE_READ) {
                    String readMessage = null;
                    String[] m = null;
                    String[] mm = null;
                    String[] n = null;
                    int len = 0;

                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        m = readMessage.split(",");

                        // 지워야 하는 구 프로토콜 시작점------------------------------------------------------------

                        // m[0] = $KMCP    m[1] = RPM(0~7000)    m[2] = FuelLevel(0~100%)    m[3] = CoolantTemp(0~240ºF)
                        // m[4] = OilPressure(0~80 PSI)    m[5] = OilTemp(0~280ºF)    m[6] = EngineRunTime(0~999hour)
                        // m[7] = SystemRunTime(0~999Min)    m[8] = CommunicationState(Connection/Disconnection)
                        // m[9] = LeftBallastTankLevel    m[10] = RightBallastTankLevel
                        // m[11] = LeftTrim-tab    m[12] = CenterTrim-tab    m[13] = RightTrim-tab

                        try {
                            Rpm2 = Float.parseFloat(m[1]);
                            if(Rpm2>1000){
                                progressBar2_RPM1.setProgress(1000);
                                progressBar2_RPM2.setProgress(((int)Rpm2-1000));
                            } else {
                                progressBar2_RPM1.setProgress((int)Rpm2);
                                progressBar2_RPM2.setProgress(0);
                            }
                            Rpm2 = (float)(Rpm2/1000.0);
                            m[1] = String.format("%.1f",Rpm2);
                            et_RPM2.setText(m[1]);

                            //임시추가 노트값 plc로 전송
                            String sendMessage = et_KNOT2.getText().toString();
                            BTS.getInstance().write(sendMessage);

                            Fuel2 = Integer.parseInt(m[2]);
                            progressBar_Fuel2.setProgress(Fuel2);

                            Ctemp2 = Integer.parseInt(m[3]);

                            Opress2 = Integer.parseInt(m[4]);

                            Otemp2 = Integer.parseInt(m[5]);

                            //Toast.makeText(getApplicationContext(),m[4]+"    "+String.valueOf(Opress),Toast.LENGTH_SHORT).show();
                        }catch (Exception e) {
                        }
                        // 지워야 하는 구 프로토콜 끝점------------------------------------------------------------





                        //변경된 프로토콜
                        if(m[0].equals("$KMCP_HEART")) {
                            // 1) $KMCP_HEART, Counter*Length$R$L
                            // m[0] = $KMCP_HEART
                            // m[1]을 "*"로 스플릿 해줍니다.
                            // mm[0] = Counter(0 ~ 1,800,000)
                            // mm[1] = Length($ 부터 * 까지 Length)
                            try {
                                n = readMessage.split("\\*");   // 특수문자를 이용해 문자열을 스플릿 할 경우 "*" 만 적어주면 Dangling metacharacter 에러가 발생하기 때문에 "\\*"로 적어준다.
                                len = Integer.parseInt(n[1]);
                                if(n[0].length() == len) {  // 지정한 문자열의 길이가 맞다면 즉 받은메세지에 문제가 없다면 파싱하여 데이터로서 이용한다.
                                    mm = m[1].split("\\*");

                                    //하트 카운터를 어떻게 써야할지에 대한 논의가 되어있지 않습니다.
                                }

                            }catch(Exception e) {
                                Log.d("$KMCP_HEART",e.toString());
                            }




                        }else if(m[0].equals("$KMCP_ENGINE")) {
                            // 2) $KMCP_ENGINE, RPM, Fuel Level, Coolant Temp, Oil Pressure, Oil Temp, Running Time*Length$R$L
                            // m[0] = $KMCP_ENGINE
                            // m[1] = RPM(0 ~ 7000)
                            // m[2] = Fuel Level(0 ~ 100)
                            // m[3] = Coolant Temp(0 ~ 240ºF)
                            // m[4] = Oil Pressure(0 ~ 80 PSI)
                            // m[5] = Oil Temp(0 ~ 280ºF)
                            // m[6]을 "*"로 스플릿 해줍니다.
                            // mm[0] = Running Time(0 ~ 999hour)
                            // mm[1] = Length($ 부터 * 까지 Length)
                            try {
                                n=readMessage.split("\\*");
                                len = Integer.parseInt(n[1]);
                                if(n[0].length() == len) {
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

                                    Fuel2 = Integer.parseInt(m[2]);
                                    progressBar_Fuel2.setProgress(Fuel2);

                                    Ctemp2 = Integer.parseInt(m[3]);

                                    Opress2 = Integer.parseInt(m[4]);

                                    Otemp2 = Integer.parseInt(m[5]);


                                }

                            }catch(Exception e) {
                                Log.d("$KMCP_ENGINE",e.toString());
                            }



                        }else if(m[0].equals("$KMCP_LEVEL")) {
                            //발라스트 탱크 레벨
                            // 3) $KMCP_LEVEL, Left Tank 1, Left Tank 2, Right Tank 1, Right Tank 2, Front Tank 1, Front Tank2, Fuel Tank, Rudder Angle*Length$R$L
                            // m[0] = $KMCP_LEVEL
                            // m[1] = Left Tank 1(0 ~ 100)
                            // m[2] = Left Tank 2(0 ~ 100)
                            // m[3] = Right Tank 1(0 ~ 100)
                            // m[4] = Right Tank 2(0 ~ 100)
                            // m[5] = Front Tank 1(0 ~ 100)
                            // m[6] = Front Tank 2(0 ~ 100)
                            // m[7] = Fuel Tank (0 ~ 100)
                            // m[8]을 "*"로 스플릿 해줍니다.
                            // mm[0] = Fuel Tank(0 ~ 100)
                            // mm[1] = Length($ 부터 * 까지 Length)
                            try {
                                n=readMessage.split("\\*");
                                len = Integer.parseInt(n[1]);
                                if(n[0].length() == len){
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
                            // m[6]을 "*"로 스플릿 해줍니다.
                            // mm[0] = Center Trim-tab On/Off : 0(Off) or 1(On)
                            // mm[1] = Length($ 부터 * 까지 Length)

                            //-48.0		312.0   트림탭이 최고위에 있을경우 Y값
                            //2.0		362.0   트림탭이 중간위치에 있을경우 Y값
                            //52.0		412.0   트림탭이 맨아래에 있을경우 Y값

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
                            // m[6]을 "*"로 스플릿 해줍니다.
                            // mm[0] = Center Pump On/Off
                            // mm[1] = Length($ 부터 * 까지 Length)
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
                            // m[6]을 "*"로 스플릿 해줍니다.
                            // mm[0] = Center Valve On/Off
                            // mm[1] = Length($ 부터 * 까지 length)
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


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });


        //버튼선택초기화
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



        //전달 받은 값을 토대로 버튼활성화
        if(wakesystem.equals("0"))
        {
            btn_Auto2.setTextColor(Color.parseColor("#FFFFFF"));
            btn_Auto2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5248")));

            // AUTO버튼일 경우 트림-탭, 발라스트-탭 버튼을 조작할수 없다.
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

            // Manual버튼일 경우 트림-탭, 발라스트-탭 버튼을 조작할수 있다.
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


        // GPS관련 권한체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // GPS 사용 가능 여부 확인
        boolean isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isEnable){
            Enable_GPS.setTextColor(Color.parseColor("#FF0000"));   // GPS사용이 불가할 경우 KNOT글자색이 붉은색으로 변한다.
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);


        //EditText 설정이후 커서없애기
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
        //EditText 설정이후 커서없애기
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

                        Toast.makeText(getApplicationContext(), "블루투스 연결을 해제합니다.", Toast.LENGTH_SHORT).show();

                        BTConnect = "0";

                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "블루투스 소켓을 닫는 도중 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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

                // AUTO버튼일 경우 트림-탭, 발라스트-탭 버튼을 조작할수 없다.
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

                // Manual버튼일 경우 트림-탭, 발라스트-탭 버튼을 조작할수 있다.
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
                        //트림탭 좌측 위 버튼을 누를 시 발생하는 이벤트로 탭의 위치가 위로 이동하게된다.
                        btn_TrimLeftUp.setBackground(btn_TrimLeftUp.getResources().getDrawable(R.drawable.trimtabbuttonuptrans));
                        iv_TrimLeftBackg.setImageResource(R.drawable.trimtabbackgroundtrans);
                        float oldY = iv_TrimTabBarLeft.getY();
                        if(oldY > 316) {
                            iv_TrimTabBarLeft.setY(oldY - 5);
                            //아래의 4줄은 테스트용
                            //iv_TrimTabBarLeft.getY 값이 312일때가 100%, 362일때가 50%, 412일때가 0% 이므로 이런식의 계산식이 나온다.
                            //int bala = (int)(iv_TrimTabBarLeft.getY() - 412)*(-1);
                            //Toast.makeText(getApplicationContext(),String.valueOf(bala),Toast.LENGTH_SHORT).show();
                            //LeftBalastValue.setProgress(bala);

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
                //쓰레드를 이용하여 롱클릭 이벤트가 발생하도록 하였고 이는 쓰래드 플래그를 통해 제어된다.
                LUTrimThread lu = new LUTrimThread();
                lu.start();
                return false;   //true 로 바꿀경우 OnClick 이벤트가 발생하지 않는다.
            }
        });
        btn_TrimLeftUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        int temp = LeftBalastValue.getProgress();
                        LeftBalastValue.setProgress(temp + 10);
                        try{
                            tv_LeftBalastValue.setText(String.valueOf(LeftBalastValue.getProgress())+"%");
                        }catch(Exception e) {}
                        LeftBalVal = String.valueOf(temp + 10);
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
                        int temp = LeftBalastValue.getProgress();
                        LeftBalastValue.setProgress(temp - 10);
                        try{
                            tv_LeftBalastValue.setText(String.valueOf(LeftBalastValue.getProgress())+"%");
                        }catch(Exception e) {}
                        LeftBalVal = String.valueOf(temp - 10);
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
                        int temp = RightBalastValue.getProgress();
                        RightBalastValue.setProgress(temp + 10);
                        try{
                            tv_RightBalastValue.setText(String.valueOf(RightBalastValue.getProgress())+"%");
                        }catch(Exception e) {}
                        RightBalVal = String.valueOf(temp + 10);
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
                        int temp = RightBalastValue.getProgress();
                        RightBalastValue.setProgress(temp - 10);
                        try{
                            tv_RightBalastValue.setText(String.valueOf(RightBalastValue.getProgress())+"%");
                        }catch(Exception e) {}
                        RightBalVal = String.valueOf(temp - 10);
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

    //리시버 블루투스 상태변화 BroadcastReceiver
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();   //입력된 action
            Log.d("Bluetooth action", action);
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = null;
            if(device != null) {
                name = device.getName();    //broadcast를 보낸 기기의 이름을 가져온다.
            }
            // 입력된 action에 따라서 함수를 처리한다.
            switch (action) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch(state) {
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(getApplicationContext(),"블루투스가 비활성화 되었습니다.",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(getApplicationContext(),"블루투스가 활성화 되었습니다.",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:  // 블루투스 기기연결
                    Toast.makeText(getApplicationContext(),"블루투스 기기와 연결되었습니다.",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:   // 블루투스 기기 끊어짐
                    Toast.makeText(getApplicationContext(),"블루투스 기기와의 연결이 끊어졌습니다.",Toast.LENGTH_SHORT).show();

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



    // 이하 트림 스레드 ======================================================================================================
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
            }
        }
    }
//=============================================================================================================


    //이하 발라스트관련 쓰레드
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

            int value = LeftBalastValue.getProgress();

            if(value < 100) {
                LeftBalastValue.setProgress(value+10);
                try {
                    tv_LeftBalastValue.setText(String.valueOf(LeftBalastValue.getProgress()) + "%");
                }catch (Exception e) {}

            }
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

            int value = LeftBalastValue.getProgress();

            if(value > 0) {
                LeftBalastValue.setProgress(value-10);
                try{
                    tv_LeftBalastValue.setText(String.valueOf(LeftBalastValue.getProgress())+"%");
                }catch(Exception e) {}
            }
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

            int value = RightBalastValue.getProgress();

            if(value < 100) {
                RightBalastValue.setProgress(value+10);
                try{
                    tv_RightBalastValue.setText(String.valueOf(RightBalastValue.getProgress())+"%");
                }catch(Exception e) {}
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

            int value = RightBalastValue.getProgress();

            if(value > 0) {
                RightBalastValue.setProgress(value-10);
                try{
                    tv_RightBalastValue.setText(String.valueOf(RightBalastValue.getProgress())+"%");
                }catch(Exception e) {}
            }
        }
    }



    //현재시간을 표시해주는 함수
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
                    handler.sendEmptyMessage(1); //핸들러 호출 = 시간 갱신
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }



    // GPS 관련 함수 오버라이드
    @Override
    public void onLocationChanged(Location location) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        double deltaTime = 0;

        try{
            // getSpeed() 함수를 이용하여 속도 계산
            double getSpeed = Double.parseDouble((String.format("%f", location.getSpeed())));
            // m/s 를 km/h로 바꾸려면 3.6을 곱해주면된다.
            getSpeed = (getSpeed * 3.6)/1.852;
            int getspeed = (int) getSpeed;
            et_KNOT2.setText(String.valueOf(getspeed));
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

        // 위치변경이 두번째로 변경된 경우 계산에 의해 속도 계산
        if (mLastlocation != null) {
            deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000;
            // 속도 계산
            speed = mLastlocation.distanceTo(location) / deltaTime;
            String formatLastDate = sdf.format(new Date(mLastlocation.getTime()));

            try {
                double calSpeed = Double.parseDouble(String.format("%f, speed"));
                // m/s 를 km/h로 바꾸려면 3.6을 곱해주면된다.
                calSpeed = (calSpeed * 3.6)/1.852;
                int calspeed = (int) calSpeed;
                et_KNOT2.setText(String.valueOf(calspeed));
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
        //현재위치를 지난위치로 변경
        mLastlocation = location;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }
    @Override
    public void onProviderEnabled(String provider) {
        // 권한체크
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        // 위치정보 업데이트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, this);
    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        //권한체크
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //위치정보 업데이트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0,this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 위치정보 가져오기 제거
        locationManager.removeUpdates(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재용청 확인
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
                return;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기버튼 비활성화
    }

}
