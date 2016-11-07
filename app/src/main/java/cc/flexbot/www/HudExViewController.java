package cc.flexbot.www;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lewei.lib.LeweiLib;
import com.lewei.media.MySurfaceView;

import cc.flexbot.www.HexMiniApplication.AppStage;
import cc.flexbot.www.gestures.EnhancedGestureDetector;
import cc.flexbot.www.modal.ApplicationSettings;
import cc.flexbot.www.modal.Channel;
import cc.flexbot.www.modal.OSDCommon;
import cc.flexbot.www.modal.Transmitter;
import cc.flexbot.www.sensors.DeviceOrientationChangeDelegate;
import cc.flexbot.www.sensors.DeviceOrientationManager;
import cc.flexbot.www.sensors.DeviceSensorManagerWrapper;
import cc.flexbot.www.ui.Image;
import cc.flexbot.www.ui.Image.SizeParams;
import cc.flexbot.www.ui.Indicator;
import cc.flexbot.www.ui.Sprite;
import cc.flexbot.www.ui.Sprite.Align;
import cc.flexbot.www.ui.Text;
import cc.flexbot.www.ui.UIRenderer;
import cc.flexbot.www.ui.joystick.AcceleratorJoystick;
import cc.flexbot.www.ui.joystick.AnalogueJoystick;
import cc.flexbot.www.ui.joystick.JoystickBase;
import cc.flexbot.www.ui.joystick.JoystickFactory;
import cc.flexbot.www.ui.joystick.JoystickFactory.JoystickType;
import cc.flexbot.www.ui.joystick.JoystickListener;
import cc.flexbot.www.ui.mButton;

public class HudExViewController extends ViewController
        implements OnTouchListener, OnGestureListener, SettingsViewControllerDelegate, DeviceOrientationChangeDelegate {
    private static final String TAG = "HudExViewController";

    public final static String ACTION_RESTART_PREVIEW = "action_restart_preview";

    private static final int JOY_ID_LEFT = 1;
    private static final int JOY_ID_RIGHT = 2;
    private static final int MIDLLE_BG_ID = 3;
    private static final int TAKE_OFF_BTN_ID = 6;
    public static final int STOP_BTN_ID = 7;
    private static final int SETTINGS_BTN_ID = 8;
    private static final int DEVICE_BATTERY_INDICATOR = 9;
    private static final int BLE_INDICATOR = 10;
    // private static final int DEBUG_TEXT_VIEW = 25;
    private static final int BACK_BTN_ID = 14;
    private static final int RECORD_BTN = 19;
    private static final int CAPTURE_BTN = 20;

    private static final int ATTITUDE_BTN_ID = 11;
    private static final int CONNECT_BTN_ID = 12;
    private static final int ATTITUDE_BG = 13;

    private final float BEGINNER_ELEVATOR_CHANNEL_RATIO = 0.5f;
    private final float BEGINNER_AILERON_CHANNEL_RATIO = 0.5f;
    private final float BEGINNER_RUDDER_CHANNEL_RATIO = 0.0f;
    private final float BEGINNER_THROTTLE_CHANNEL_RATIO = 0.8f;

    private mButton stopBtn;
    private mButton takeOffBtn;
    private mButton connectBtn;
    private mButton settingsBtn;
    private mButton recordBtn;
    private mButton captureBtn;
    private mButton[] buttons;

    private mButton attitudeBtn;
    private mButton backBtn;

    private boolean isAccMode;
    private boolean isLeftHanded;

    private Indicator deviceBatteryIndicator;
    private Indicator bleIndicator;
    private GLSurfaceView glView;

    private JoystickBase[] joysticks; // [0]roll and pitch, [1]rudder and
    // throttle
    private float joypadOpacity;
    private GestureDetector gestureDetector;
    private UIRenderer renderer;
    private HudViewControllerDelegate delegate;

    private JoystickListener rollPitchListener;
    private JoystickListener rudderThrottleListener;
    private MySurfaceView mySurfaceView;

    private ApplicationSettings settings;

    private Channel aileronChannel;
    private Channel elevatorChannel;
    private Channel rudderChannel;
    private Channel throttleChannel;


    private SoundPool mSoundPool;
    private int camera_click_sound;
    private int video_record_sound;


    private DeviceOrientationManager deviceOrientationManager;

    private static final float ACCELERO_TRESHOLD = (float) Math.PI / 180.0f * 2.0f;
    private static final int PITCH = 1;
    private static final int ROLL = 2;
    private float pitchBase;
    private float rollBase;
    private boolean rollAndPitchJoystickPressed;
    private Image middleBg;
    private Image attitudeBg;

    private boolean isattitudeBg = true;
    private boolean isRecording = true;


    private Text debugTextView;

    public HudExViewController(Activity context, HudViewControllerDelegate delegate) {
        this.delegate = delegate;
        this.context = context;

        settings = ((HexMiniApplication) context.getApplication()).getAppSettings();

        joypadOpacity = settings.getInterfaceOpacity();
        isLeftHanded = settings.isLeftHanded();

        gestureDetector = new EnhancedGestureDetector(context, this);

        joysticks = new JoystickBase[2];

        context.setContentView(R.layout.hud_view_controller_framelayout);
        FrameLayout mainFrameLayout = (FrameLayout) context.findViewById(R.id.mainFrameLaytout);

        glView = new GLSurfaceView(context);
        glView.setEGLContextClientVersion(2);
        glView.setZOrderOnTop(true);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        mainFrameLayout.addView(glView);

        mySurfaceView = (MySurfaceView) context.findViewById(R.id.mySurfaceView);
        mySurfaceView.startMySurface();

        renderer = new UIRenderer(context, null);

        initGLSurfaceView();

        Resources res = context.getResources();

        middleBg = new Image(res, R.drawable.main_background, Align.CENTER);
        if (LeweiLib.LW93SendChangeRecPlan(0) > 0) {
            middleBg.setVisible(false);
        } else {
            middleBg.setVisible(true);
        }
        middleBg.setSizeParams(SizeParams.FILL_SCREEN, SizeParams.FILL_SCREEN);

        connectBtn = new mButton(res, R.drawable.btn_connect_normal, R.drawable.btn_connect_press, Align.BOTTOM_CENTER);
        connectBtn.setAlphaEnabled(false);
        connectBtn.setEnabled(false);
        connectBtn.setVisible(false);

        takeOffBtn = new mButton(res, R.drawable.btn_unlock_normal, R.drawable.btn_unlock_press, Align.BOTTOM_CENTER);
        takeOffBtn.setAlphaEnabled(false);

        stopBtn = new mButton(res, R.drawable.btn_lock_press, R.drawable.btn_lock_normal, Align.TOP_CENTER);
        stopBtn.setAlphaEnabled(false);

        backBtn = new mButton(res, R.drawable.btn_back_normal, R.drawable.btn_back_press, Align.TOP_LEFT);
        backBtn.setMargin((int) res.getDimension(R.dimen.main_btn_back_margin_top), 0, 0,
                (int) res.getDimension(R.dimen.main_btn_back_margin_left));
        backBtn.setAlphaEnabled(false);
        backBtn.setEnabled(true);


        captureBtn = new mButton(res, R.drawable.btn_capture_normal, R.drawable.btn_capture_press, Align.TOP_LEFT);
        captureBtn.setMargin((int) res.getDimension(R.dimen.main_btn_capture_margin_top), 0, 0,
                (int) res.getDimension(R.dimen.main_btn_capture_margin_left));

        recordBtn = new mButton(res, R.drawable.btn_record_video_normal, R.drawable.btn_record_video_press,
                Align.TOP_LEFT);
        recordBtn.setMargin((int) res.getDimension(R.dimen.main_btn_record_margin_top), 0, 0,
                (int) res.getDimension(R.dimen.main_btn_record_margin_left));


        settingsBtn = new mButton(res, R.drawable.btn_settings_normal1, R.drawable.btn_settings_normal1_press,
                Align.TOP_RIGHT);
        settingsBtn.setMargin((int) res.getDimension(R.dimen.main_btn_settings_margin_top),
                (int) res.getDimension(R.dimen.main_btn_settings_margin_right), 0, 0);
        settingsBtn.setAlphaEnabled(false);

        attitudeBtn = new mButton(res, R.drawable.btn_attitude_normal, R.drawable.btn_attutide_press, Align.TOP_RIGHT);
        attitudeBtn.setMargin((int) res.getDimension(R.dimen.main_btn_attitude_margin_top),
                (int) res.getDimension(R.dimen.main_btn_attitude_margin_right), 0, 0);
        attitudeBtn.setEnabled(false);
        attitudeBtn.setAlphaEnabled(false);
        attitudeBtn.setImages(res, R.drawable.btn_attutide_press, 1);


        attitudeBg = new Image(res, R.drawable.attitudebackground, Align.TOP_CENTER);
        attitudeBg.setMargin((int) res.getDimension(R.dimen.main_attitudeBg_margin_top), 0, 0, 0);
        attitudeBg.setVisible(false);
        attitudeBg.setAlphaEnabled(false);


        int bleIndicatorRes[] = {R.drawable.ble_indicator_opened, R.drawable.ble_indicator_closed};
        bleIndicator = new Indicator(res, bleIndicatorRes, Align.TOP_RIGHT);
        bleIndicator.setMargin((int) res.getDimension(R.dimen.main_ble_margin_top),
                (int) res.getDimension(R.dimen.main_ble_margin_right), 0, 0);
        bleIndicator.setVisible(false);
        bleIndicator.setValue(1);
        // 飞机电池电量
        int deviceBatteryIndicatorRes[] = {R.drawable.btn_battery_0, R.drawable.btn_battery_1,
                R.drawable.btn_battery_2, R.drawable.btn_battery_3, R.drawable.btn_battery_4,
                R.drawable.btn_battery_5};
        deviceBatteryIndicator = new Indicator(res, deviceBatteryIndicatorRes, Align.TOP_RIGHT);
        deviceBatteryIndicator.setMargin((int) res.getDimension(R.dimen.main_ble_margin_top),
                (int) res.getDimension(R.dimen.main_ble_margin_right), 0, 0);

        buttons = new mButton[8];
        buttons[0] = settingsBtn;
        buttons[1] = takeOffBtn;
        buttons[2] = connectBtn;
        buttons[3] = stopBtn;
        buttons[4] = attitudeBtn;
        buttons[5] = backBtn;
        buttons[6] = recordBtn;
        buttons[7] = captureBtn;


        // String debugStr = "000, 000, 000, 0.0";
        //debugTextView = new Text(context, debugStr, Align.TOP_LEFT);
        //debugTextView.setMargin((int) res.getDimension(R.dimen.main_state_text_margin_top) * 2, 0, 0, 0);
        // debugTextView.setTextColor(Color.WHITE);
        //debugTextView.setTypeface(FontUtils.TYPEFACE.Helvetica(context));
        //debugTextView.setTextSize(res.getDimensionPixelSize(R.dimen.main_state_text_size) * 2 / 3);

        // HexMiniApplication.sharedApplicaion().setDebugTextView(debugTextView);
        HexMiniApplication.sharedApplicaion().setBatteryIndicator(deviceBatteryIndicator);

        renderer.addSprite(MIDLLE_BG_ID, middleBg);
        renderer.addSprite(DEVICE_BATTERY_INDICATOR, deviceBatteryIndicator);
        renderer.addSprite(TAKE_OFF_BTN_ID, takeOffBtn);
        renderer.addSprite(CONNECT_BTN_ID, connectBtn);
        renderer.addSprite(STOP_BTN_ID, stopBtn);
        renderer.addSprite(SETTINGS_BTN_ID, settingsBtn);
        renderer.addSprite(BLE_INDICATOR, bleIndicator);
        renderer.addSprite(CAPTURE_BTN, captureBtn);
        renderer.addSprite(RECORD_BTN, recordBtn);
        // renderer.addSprite(DEBUG_TEXT_VIEW, debugTextView);
        renderer.addSprite(ATTITUDE_BTN_ID, attitudeBtn);
        renderer.addSprite(ATTITUDE_BG, attitudeBg);
        renderer.addSprite(BACK_BTN_ID, backBtn);


        isAccMode = settings.isAccMode();
        deviceOrientationManager = new DeviceOrientationManager(new DeviceSensorManagerWrapper(this.context), this);
        deviceOrientationManager.onCreate();

        initJoystickListeners();

        if (isAccMode) {
            initJoysticks(JoystickType.ACCELERO);
        } else {
            initJoysticks(JoystickType.ANALOGUE);
        }

        initListeners();

        initChannels();


        if (settings.isBeginnerMode()) {
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.dialog_title_info)
                    .setMessage(R.string.beginner_mode_info)
                    .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }

        initSound();
    }


    private void initChannels() {
        aileronChannel = settings.getChannel(Channel.CHANNEL_NAME_AILERON);
        elevatorChannel = settings.getChannel(Channel.CHANNEL_NAME_ELEVATOR);
        rudderChannel = settings.getChannel(Channel.CHANNEL_NAME_RUDDER);
        throttleChannel = settings.getChannel(Channel.CHANNEL_NAME_THROTTLE);

        aileronChannel.setValue(0.0f);
        elevatorChannel.setValue(0.0f);
        rudderChannel.setValue(0.0f);
        throttleChannel.setValue(-1);
    }

    private void initJoystickListeners() {
        rollPitchListener = new JoystickListener() {
            public void onChanged(JoystickBase joy, float x, float y) {
                if (HexMiniApplication.sharedApplicaion().getAppStage() == AppStage.SETTINGS
                        || HexMiniApplication.sharedApplicaion().getAppStage() == AppStage.UNKNOWN) {
                    return;
                }
                if (isAccMode == false && rollAndPitchJoystickPressed == true) {
                    if (settings.isBeginnerMode()) {
                        aileronChannel.setValue(x * BEGINNER_AILERON_CHANNEL_RATIO);
                        elevatorChannel.setValue(y * BEGINNER_ELEVATOR_CHANNEL_RATIO);
                    } else {
                        aileronChannel.setValue(x);
                        elevatorChannel.setValue(y);
                    }
                }
            }

            @Override
            public void onPressed(JoystickBase joy) {
                rollAndPitchJoystickPressed = true;
            }

            @Override
            public void onReleased(JoystickBase joy) {
                rollAndPitchJoystickPressed = false;

                aileronChannel.setValue(0.0f);
                elevatorChannel.setValue(0.0f);
            }
        };

        rudderThrottleListener = new JoystickListener() {
            public void onChanged(JoystickBase joy, float x, float y) {
                if (HexMiniApplication.sharedApplicaion().getAppStage() == AppStage.SETTINGS) {
                    return;
                }

                if (settings.isBeginnerMode()) {
                    rudderChannel.setValue(x * BEGINNER_RUDDER_CHANNEL_RATIO);
                    throttleChannel.setValue((BEGINNER_THROTTLE_CHANNEL_RATIO - 1) + y * BEGINNER_THROTTLE_CHANNEL_RATIO);

                } else {
                    rudderChannel.setValue(x);
                    throttleChannel.setValue(y);
                }
            }

            @Override
            public void onPressed(JoystickBase joy) {

            }

            @Override
            public void onReleased(JoystickBase joy) {
                rudderChannel.setValue(0.0f);
                throttleChannel.setValue(joy.getYValue());

            }
        };
    }

    private void initListeners() {
        settingsBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (delegate != null) {
                    delegate.settingsBtnDidClick(arg0);
                }

            }
        });


        attitudeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isattitudeBg) {
                    attitudeBg.setVisible(true);
                    isattitudeBg = false;
                } else {
                    attitudeBg.setVisible(false);
                    isattitudeBg = true;
                }
            }
        });

        connectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("connectBtn", "****** onPress");
                if (!Transmitter.sharedTransmitter().getBleConnectionManager().isConnected()) {
                    if (delegate != null) {
                        delegate.backBtnLaunchActivity(arg0);
                    }
                    if (mSoundPool != null) {
                        mSoundPool.release();
                    }
                }
            }
        });


        takeOffBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Transmitter.sharedTransmitter().getBleConnectionManager().isConnected()) {
                    throttleChannel.setValue(-1);
                    getRudderAndThrottleJoystick().setYValue(-1);
                    Transmitter.sharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_ARM);
                    Log.i("**********", " " + Transmitter.sharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_ARM));
                    settingsBtn.setEnabled(false);
                    settingsBtn.setImages(context.getResources(), R.drawable.btn_settings_normal1_press, 1);
                    takeOffBtn.setImages(context.getResources(), R.drawable.btn_unlock_press, 1);
                    stopBtn.setImages(context.getResources(), R.drawable.btn_lock_normal, 1);
                    backBtn.setEnabled(false);
                    backBtn.setImages(context.getResources(), R.drawable.btn_back_press, 1);
                }
            }
        });


        stopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Transmitter.sharedTransmitter().getBleConnectionManager().isConnected()) {
                    Transmitter.sharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_DISARM);
                    settingsBtn.setEnabled(true);
                    settingsBtn.setImages(context.getResources(), R.drawable.btn_settings_normal1, 1);
                    stopBtn.setImages(context.getResources(), R.drawable.btn_lock_press, 1);
                    takeOffBtn.setImages(context.getResources(), R.drawable.btn_unlock_normal, 1);
                    backBtn.setEnabled(true);
                    backBtn.setImages(context.getResources(), R.drawable.btn_back_normal, 1);

                }
            }
        });


        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mySurfaceView.stop();
                if (delegate != null) {
                    delegate.backBtnLaunchActivity(arg0);
                }
                if (mSoundPool != null) {
                    mSoundPool.release();
                }
            }
        });

        recordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySurfaceView.takeRecord();
                if (LeweiLib.getSdcardStatus() > 0) {
                    playSound(video_record_sound);
                    if (isRecording) {
                        LeweiLib.LW93SendChangeRecPlan(1);
                        recordBtn.setImages(context.getResources(), R.drawable.recording_status, 1);
                        Log.i("**********", "start video sdcard");
                        Toast.makeText(context.getApplicationContext(), "开始录像", Toast.LENGTH_SHORT).show();
                        isRecording = false;
                    } else {
                        LeweiLib.LW93SendChangeRecPlan(0);
                        recordBtn.setImages(context.getResources(), R.drawable.main_takevideo_state, 1);
                        Toast.makeText(context.getApplicationContext(), "录像结束", Toast.LENGTH_SHORT).show();
                        Log.i("**********", "stop video sdcard");
                        isRecording = true;
                    }

                }
            }
        });

        captureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (LeweiLib.LW93SendChangeRecPlan(0) > 0) {
                    playSound(camera_click_sound);
                    mySurfaceView.takePhoto();
                } else {
                    Toast toast = Toast.makeText(context.getApplicationContext(), "请连接WiFi摄像头", Toast.LENGTH_LONG);
                    toast.show();
                }
                Log.i(TAG, "*********takephoto");
            }
        });

    }

    private void initGLSurfaceView() {
        if (glView != null) {
            glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            glView.setRenderer(renderer);
            glView.setOnTouchListener(this);
        }
    }

    private void initJoysticks(JoystickType rollAndPitchType) {
        JoystickBase rollAndPitchJoystick = getRollAndPitchJoystick();
        JoystickBase rudderAndThrottleJoystick = getRudderAndThrottleJoystick();

        if (rollAndPitchType == JoystickType.ANALOGUE) {
            if (rollAndPitchJoystick == null || !(rollAndPitchJoystick instanceof AnalogueJoystick)) {
                rollAndPitchJoystick = JoystickFactory.createAnalogueJoystick(this.getContext(), true,
                        rollPitchListener, true);
                rollAndPitchJoystick.setXDeadBand(settings.getAileronDeadBand());
                rollAndPitchJoystick.setYDeadBand(settings.getElevatorDeadBand());
            } else {
                rollAndPitchJoystick.setOnAnalogueChangedListener(rollPitchListener);
            }
        } else if (rollAndPitchType == JoystickType.ACCELERO) {
            if (rollAndPitchJoystick == null || !(rollAndPitchJoystick instanceof AcceleratorJoystick)) {
                rollAndPitchJoystick = JoystickFactory.createAcceleroJoystick(this.getContext(), true,
                        rollPitchListener, true);

            } else {
                rollAndPitchJoystick.setOnAnalogueChangedListener(rollPitchListener);
            }
        }

        if (rudderAndThrottleJoystick == null || !(rudderAndThrottleJoystick instanceof AnalogueJoystick)) {
            rudderAndThrottleJoystick = JoystickFactory.createAnalogueJoystick(this.getContext(), false,
                    rudderThrottleListener, false);
            rudderAndThrottleJoystick.setXDeadBand(settings.getRudderDeadBand());
        } else {
            rudderAndThrottleJoystick.setOnAnalogueChangedListener(rudderThrottleListener);
        }

        rollAndPitchJoystick.setIsRollPitchJoystick(true);
        rudderAndThrottleJoystick.setIsRollPitchJoystick(false);

        joysticks[0] = rollAndPitchJoystick;
        joysticks[1] = rudderAndThrottleJoystick;

        setJoysticks();

        getRudderAndThrottleJoystick().setYValue(-1);
    }

    public void setJoysticks() {
        JoystickBase rollAndPitchJoystick = joysticks[0];
        JoystickBase rudderAndThrottleJoystick = joysticks[1];

        if (rollAndPitchJoystick != null) {
            if (isLeftHanded) {
                joysticks[0].setAlign(Align.BOTTOM_RIGHT);
                joysticks[0].setAlpha(joypadOpacity);
            } else {
                joysticks[0].setAlign(Align.BOTTOM_LEFT);
                joysticks[0].setAlpha(joypadOpacity);
            }

            rollAndPitchJoystick.setNeedsUpdate();
        }

        if (rudderAndThrottleJoystick != null) {
            if (isLeftHanded) {
                joysticks[1].setAlign(Align.BOTTOM_LEFT);
                joysticks[1].setAlpha(joypadOpacity);
            } else {
                joysticks[1].setAlign(Align.BOTTOM_RIGHT);
                joysticks[1].setAlpha(joypadOpacity);
            }

            rudderAndThrottleJoystick.setNeedsUpdate();
        }

        for (int i = 0; i < joysticks.length; ++i) {
            JoystickBase joystick = joysticks[i];

            if (joystick != null) {
                joystick.setInverseYWhenDraw(true);

                int margin = context.getResources().getDimensionPixelSize(R.dimen.main_joy_margin);

                joystick.setMargin(0, margin, 48 + margin, margin);
            }
        }

        renderer.removeSprite(JOY_ID_LEFT);
        renderer.removeSprite(JOY_ID_RIGHT);

        if (rollAndPitchJoystick != null) {
            if (isLeftHanded) {
                renderer.addSprite(JOY_ID_RIGHT, rollAndPitchJoystick);
            } else {
                renderer.addSprite(JOY_ID_LEFT, rollAndPitchJoystick);
            }
        }

        if (rudderAndThrottleJoystick != null) {
            if (isLeftHanded) {
                renderer.addSprite(JOY_ID_LEFT, rudderAndThrottleJoystick);
            } else {
                renderer.addSprite(JOY_ID_RIGHT, rudderAndThrottleJoystick);
            }
        }
    }

    public JoystickBase getRollAndPitchJoystick() {
        return joysticks[0];
    }

    public JoystickBase getRudderAndThrottleJoystick() {
        return joysticks[1];
    }

    public void setInterfaceOpacity(float opacity) {
        if (opacity < 0 || opacity > 100.0f) {
            Log.w(TAG, "Can't set interface opacity. Invalid value: " + opacity);
            return;
        }

        joypadOpacity = opacity / 100f;

        Sprite joystick = renderer.getSprite(JOY_ID_LEFT);
        joystick.setAlpha(joypadOpacity);

        joystick = renderer.getSprite(JOY_ID_RIGHT);
        joystick.setAlpha(joypadOpacity);
    }

    public void setSettingsButtonEnabled(boolean enabled) {
        settingsBtn.setEnabled(enabled);
    }

    public void onPause() {
        if (glView != null) {
            glView.onPause();
        }

        deviceOrientationManager.pause();
    }

    public void onResume() {
        if (glView != null) {
            glView.onResume();
        }

        deviceOrientationManager.resume();

    }

    // glView onTouch Event handler
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = false;

        for (int i = 0; i < buttons.length; ++i) {
            if (buttons[i].processTouch(v, event)) {
                result = true;
                break;
            }
        }

        if (result != true) {
            gestureDetector.onTouchEvent(event);

            for (int i = 0; i < joysticks.length; ++i) {
                JoystickBase joy = joysticks[i];
                if (joy != null) {
                    if (joy.processTouch(v, event)) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    public void onDestroy() {
        renderer.clearSprites();
        deviceOrientationManager.destroy();

    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
        // Left unimplemented
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
        // Left unimplemented
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public View getRootView() {
        if (glView != null) {
            return glView;
        }

        Log.w(TAG, "Can't find root view");
        return null;
    }

    @Override
    public void leftHandedValueDidChange(boolean isLeftHanded) {
        this.isLeftHanded = isLeftHanded;

        setJoysticks();

        Log.e(TAG, "THRO:" + throttleChannel.getValue());

        getRudderAndThrottleJoystick().setYValue(throttleChannel.getValue());
    }

    @Override
    public void accModeValueDidChange(boolean isAccMode) {
        this.isAccMode = isAccMode;

        initJoystickListeners();

        if (isAccMode) {
            initJoysticks(JoystickType.ACCELERO);
        } else {
            initJoysticks(JoystickType.ANALOGUE);
        }
    }

    @Override
    public void onDeviceOrientationChanged(float[] orientation, float magneticHeading, int magnetoAccuracy) {
        if (rollAndPitchJoystickPressed == false) {
            pitchBase = orientation[PITCH];
            rollBase = orientation[ROLL];
            aileronChannel.setValue(0.0f);
            elevatorChannel.setValue(0.0f);
        } else {
            float x = (orientation[PITCH] - pitchBase);
            float y = (orientation[ROLL] - rollBase);

            if (isAccMode) {
                Log.d(TAG, "ROLL:" + (-x) + ",PITCH:" + y);

                if (Math.abs(x) > ACCELERO_TRESHOLD || Math.abs(y) > ACCELERO_TRESHOLD) {
                    if (settings.isBeginnerMode()) {
                        aileronChannel.setValue(-x * BEGINNER_AILERON_CHANNEL_RATIO);
                        elevatorChannel.setValue(y * BEGINNER_ELEVATOR_CHANNEL_RATIO);
                    } else {
                        aileronChannel.setValue(-x);
                        elevatorChannel.setValue(y);
                    }
                }
            }
        }
    }

    @Override
    public void didConnect() {
        takeOffBtn.setImages(context.getResources(), R.drawable.btn_unlock_normal, 1);
        takeOffBtn.setEnabled(true);
        takeOffBtn.setVisible(true);
        stopBtn.setEnabled(true);
        connectBtn.setEnabled(false);
        connectBtn.setVisible(false);
        attitudeBtn.setEnabled(true);
        attitudeBtn.setImages(context.getResources(), R.drawable.btn_attitude_normal, 1);
        bleIndicator.setValue(0);

    }

    @Override
    public void didDisconnect() {
        if (stopBtn.isEnabled()) {
            stopBtn.setImages(context.getResources(), R.drawable.btn_lock_press, 1);
            stopBtn.setEnabled(false);
        }
        if (takeOffBtn.isEnabled()) {
            takeOffBtn.setImages(context.getResources(), R.drawable.btn_unlock_normal, 1);
            takeOffBtn.setEnabled(false);
            takeOffBtn.setVisible(false);
        }
        connectBtn.setEnabled(true);
        connectBtn.setVisible(true);
        if (!(settingsBtn.isEnabled())) {
            settingsBtn.setEnabled(true);
            settingsBtn.setImages(context.getResources(), R.drawable.btn_settings_normal1, 1);
        }
        if (!(backBtn.isEnabled())) {
            backBtn.setEnabled(true);
            backBtn.setImages(context.getResources(), R.drawable.btn_back_normal, 1);
        }

        deviceBatteryIndicator.setValue(5);
        if (attitudeBtn.isEnabled()) {
            attitudeBtn.setEnabled(false);
            attitudeBtn.setImages(context.getResources(), R.drawable.btn_attutide_press, 1);
            attitudeBg.setVisible(false);
        }
        bleIndicator.setValue(1);
    }

    @Override
    public void didFailToConnect() {

        bleIndicator.setValue(1);
    }

    public void checkConnectBle() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    if (!(Transmitter.sharedTransmitter().getBleConnectionManager().isConnected())) {
                        didDisconnect();
                        return;
                    } else {
                        msleep(1000);
                    }
                }

            }
        }).start();
    }

    private void msleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beginnerModeValueDidChange(boolean isBeginnerMode) {

    }


    private void initSound() {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 0);
        }
        camera_click_sound = mSoundPool.load(context, R.raw.camera_click, 1);
        video_record_sound = mSoundPool.load(context, R.raw.video_record, 1);
    }

    private void playSound(int soundId) {
        if (mSoundPool != null)
            mSoundPool.play(soundId, 1, 1, 0, 0, 1);
    }


    @Override
    public void tryingToConnect(String target) {
        ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();

        if (target.equals("FlexBLE")) {

            if (settings.getFlexbotVersion().equals("1.5.0") == false) {
                settings.setFlexbotVersion("1.5.0");
                settings.save();
            }

            HexMiniApplication.sharedApplicaion().setFullDuplex(true);
        } else {
            settings.getFlexbotVersion().equals("1.0.0");
            settings.save();
            HexMiniApplication.sharedApplicaion().setFullDuplex(false);

        }
    }
}
