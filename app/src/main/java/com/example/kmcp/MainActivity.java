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
    String BTConnect = "1";  // 0 : 커넥트, 1 : 디스커넥트

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


        //현재시간 실시간으로 가져오기
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String time = simple.format(date);
        String[] splitTime = time.split(":");
        if (SystemRunningTime.equals("0")) {
            //static 변수에 초기에 한번만 초기실행시간을 담는다.
            SystemRunningTime = time;
        }
        tv_Hour.setText(splitTime[0]);
        tv_Minute.setText(splitTime[1]);
        tv_Second.setText(splitTime[2]);
        ShowTimeMethod();


        //블루투스 핸들러로 블루트스 연결 뒤 수신된 데이터를 읽어옴
        //여기서 데이터 값들에 대한 처리.
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
                        m = readMessage.split(",");


                        // 지워야 하는 구 프로토콜 시작점------------------------------------------------------------

                        // 구 버전 프로토콜
                        // m[0] = $KMCP_HEART or $KMCP_ENGINE or $KMCP_LEVEL or $KMCP_TRIMTAB
                        // m[1] = RPM(0~7000)    m[2] = FuelLevel(0~100%)    m[3] = CoolantTemp(0~240ºF)
                        // m[4] = OilPressure(0~80 PSI)    m[5] = OilTemp(0~280ºF)    m[6] = EngineRunTime(0~999hour)
                        // m[7] = SystemRunTime(0~999Min)    m[8] = CommunicationState(Connection/Disconnection)
                        // m[9] = LeftBallastTankLevel    m[10] = RightBallastTankLevel
                        // m[11] = LeftTrim-tab    m[12] = CenterTrim-tab    m[13] = RightTrim-tab

                        try {
                            Log.i("Length:", String.valueOf(readMessage.length()));

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
                            //Log.d("RPM:",m[1]);

                            if ((Rpm * 1000) >= 4000) {
                                iv_RpmGradation.setImageResource(R.drawable.rpm_redzone);
                            } else {
                                iv_RpmGradation.setImageResource(R.drawable.rpm_normal);
                            }

                            String sendMessage = et_KNOT.getText().toString();
                            BTS.getInstance().write(sendMessage);

                            Fuel = Integer.parseInt(m[2]);
                            progressBar_Fuel.setProgress(Fuel);
                            //Log.d("FuelLevel:",m[2]);

                            Ctemp = Integer.parseInt(m[3]);
                            progressBar_CoolantTemp.setProgress(Ctemp);
                            tv_CoolantTempValue.setText(m[3] + "ºF");
                            //Log.d("CoolantTemp:",m[3]);

                            Opress = Integer.parseInt(m[4]);
                            progressBar_OilPress.setProgress(Opress);
                            tv_OilPressValue.setText(m[4] + " PSI");
                            //Log.d("OilPressure:",m[4]);

                            Otemp = Integer.parseInt(m[5]);
                            progressBar_OilTemp.setProgress(Otemp);
                            tv_OilTempValue.setText(m[5] + "ºF");
                            //Log.d("OilTempValue:",m[5]);


                            //Toast.makeText(getApplicationContext(),m[4]+"    "+String.valueOf(Opress),Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                        // 지워야 하는 구 프로토콜 끝점------------------------------------------------------------


                        //Toast.makeText(getApplicationContext(),readMessage,Toast.LENGTH_SHORT).show();
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

                                    Fuel = Integer.parseInt(m[2]);
                                    progressBar_Fuel.setProgress(Fuel);

                                    Ctemp = Integer.parseInt(m[3]);
                                    progressBar_CoolantTemp.setProgress(Ctemp);
                                    tv_CoolantTempValue.setText(m[3] + "ºF");

                                    Opress = Integer.parseInt(m[4]);
                                    progressBar_OilPress.setProgress(Opress);
                                    tv_OilPressValue.setText(m[4] + " PSI");

                                    Otemp = Integer.parseInt(m[5]);
                                    progressBar_OilTemp.setProgress(Otemp);
                                    tv_OilTempValue.setText(m[5] + "ºF");

                                    tv_EngineRunningTime.setText(mm[0] + "Hhr");
                                }

                            }catch(Exception e) {
                                Log.d("$KMCP_ENGINE",e.toString());
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
                            // m[8]을 "*"로 스플릿 해줍니다.
                            // mm[0] = Fuel Tank(0 ~ 100)
                            // mm[1] = Length($ 부터 * 까지 Length)
                            try {
                                n=readMessage.split("\\*");
                                len = Integer.parseInt(n[1]);
                                if(n[0].length() == len){
                                    mm = m[8].split("\\*");

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


        //전달 받은 값을 토대로 버튼활성화
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
        //EditText 설정이후 커서없애기
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

                Toast.makeText(getApplicationContext(),"블루투스 연결을 시도합니다.",Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(getApplicationContext(),"블루투스 연결을 해제합니다.",Toast.LENGTH_SHORT).show();
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
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"블루투스 연결을 확인하세요.",Toast.LENGTH_SHORT).show();
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

                    BTConnect = "0";
                    tv_Connect.setTextColor(Color.parseColor("#01F8E0"));
                    tv_Disconnect.setTextColor(Color.parseColor("#5D5D5D"));
                    tv_Connect.setEnabled(false);
                    tv_Disconnect.setEnabled(true);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:   // 블루투스 기기 끊어짐
                    Toast.makeText(getApplicationContext(),"블루투스 기기와의 연결이 끊어졌습니다.",Toast.LENGTH_SHORT).show();

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
        //프로그레스다이얼로그에서 백그라운드 쓰레드로 들어오는 블루투스 접속함수인데 내부에 토스트메시지 삽입시 널포인터입셉션발생하며 팅겨버린다.
        BTS.getInstance();
        BTS.getInstance().mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 연결하고자하는 블루투스 장치는 페어링이 이루어져 있다고 가정하였으므로 장치를 선택하기 위해서는 먼저 페어링된 장치의 목록을 얻어와야한다.

        if (BTS.getInstance().mBluetoothAdapter.isEnabled()) {
            BTS.getInstance().mPairedDevices = BTS.getInstance().mBluetoothAdapter.getBondedDevices();
            if(BTS.getInstance().mPairedDevices.size() > 0) {
                //페어링된 장비가 존재한다면...
                BTS.getInstance().mListPairedDevices = new ArrayList<>();
                for (BluetoothDevice device : BTS.getInstance().mPairedDevices) {
                    BTS.getInstance().mListPairedDevices.add(device.getName());
                    if(device.getName().equals("SD1000v2.0.8-77FC4B")) {    // "SD1000v2.0.8-77FC4B" "SD1000v2.0.8-77FBF5"

                        //Toast.makeText(getApplicationContext(),"연결을 시도합니다.",Toast.LENGTH_SHORT).show();

                        for(BluetoothDevice tempDevice : BTS.getInstance().mPairedDevices) {    // 페어링된 항목중 내가 정한 장비이름과 비교하여 매칭되면 블루투스 장비 변수에 그 장비명을 넣어주는 작업.
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

                            //수정필
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
                            //Toast.makeText(getApplicationContext(), "연결에 실패하여 재시도합니다.(2)" , Toast.LENGTH_SHORT).show();
                            tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                            tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                            tv_Connect.setEnabled(true);
                            tv_Disconnect.setEnabled(false);
                        }

                        if(!connectFlag){   // 2차 시도
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //수정필
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
                                Toast.makeText(getApplicationContext(), "연결에 실패하여 재시도합니다.(2)" , Toast.LENGTH_SHORT).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                        if(!connectFlag){   // 3차 시도
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //수정필
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
                                //Toast.makeText(getApplicationContext(), "연결에 실패하여 재시도합니다.(3)" , Toast.LENGTH_SHORT).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                        if(!connectFlag){   // 4차 시도
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //수정필
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
                                //Toast.makeText(getApplicationContext(), "연결에 실패하여 재시도합니다.(4)" , Toast.LENGTH_SHORT).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                        if(!connectFlag){   // 5차 시도
                            try {
                                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                                BTS.getInstance().mBluetoothSocket.connect();
                                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                                //수정필
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
                                //Toast.makeText(super.getApplicationContext(), "블루트스 연결에 실패하였습니다. 장비상태를 확인해 주세요" , Toast.LENGTH_LONG).show();
                                tv_Connect.setTextColor(Color.parseColor("#5D5D5D"));
                                tv_Disconnect.setTextColor(Color.parseColor("#01F8E0"));
                                tv_Connect.setEnabled(true);
                                tv_Disconnect.setEnabled(false);
                            }
                        }

                    }
                }
            } else {
                //페어링 기기가 없을 경우 검색을 수행한다.
                Toast.makeText(getApplicationContext(),"검색된 블루투스 장비 없음",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어있습니다. 설정에서 활성화 해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    //블루투스 connect 버튼 클릭시 발생하는 프로그래스 다이얼로그
    public class ShowProgressDialog extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            asyncDialog.setCancelable(false);

            asyncDialog.show();
            super.onPreExecute();
        }

        // 백그라운드에서 실행
        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                connectSelectedDevice("SD1000v2.0.8-77FC4B");   // "SD1000v2.0.8-77FC4B" "SD1000v2.0.8-77FBF5"
            }catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // 백그라운드가 모드 끝난 후 실행
        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            //Toast.makeText(MainActivity.this,String.valueOf(tv_Connect.getCurrentTextColor()), Toast.LENGTH_LONG).show();   //-10658467
            if(tv_Connect.getCurrentTextColor()==(-10658467)){
                Toast.makeText(getApplicationContext(),"연결에 실패하였습니다. 장비상태를 확인해주세요.", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }

    //블루투스 disconnect 버튼 클릭시 발생하는 프로그래스 다이얼로그
    public class ShowProgressDialog2 extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            asyncDialog.setCancelable(false);

            asyncDialog.show();
            super.onPreExecute();
        }

        // 백그라운드에서 실행
        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                BTS.getInstance().cancel();
            }catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // 백그라운드가 모드 끝난 후 실행
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "소켓 해제중...", Toast.LENGTH_SHORT).show();
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
            et_KNOT.setText(String.valueOf(getspeed));
            KNOT = et_KNOT.getText().toString();
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
                et_KNOT.setText(String.valueOf(calspeed));
                KNOT = et_KNOT.getText().toString();
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