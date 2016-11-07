
package cc.flexbot.www;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cc.flexbot.www.adapter.SettingsViewAdapter;
import cc.flexbot.www.modal.ApplicationSettings;
import cc.flexbot.www.modal.OSDCommon;
import cc.flexbot.www.modal.Transmitter;

public class SettingsViewController extends ViewController {

    private static final String TAG = SettingsViewController.class.getSimpleName();

    private SettingsViewControllerDelegate delegate;
    private List<View> settingsViews;

    private ImageView prombtn1;
    private ImageView prombtn2;
    private ImageView propicture1;
    private ImageView propicture2;
    private ViewPager viewPager;
    private Button backBtn;
    private Button magCalibrateBtn;
    private Button resetBtn;
    private CheckBox isLeftHandedCheckBox;
    private CheckBox isAccModeCheckBox;
    private CheckBox isBeginnerModeCheckBox;
    private boolean propictureVisible = false;
    private Resources res;

    LocalBroadcastManager mLocalBroadcastManager;

    public SettingsViewController(Context context, LayoutInflater inflater, ViewGroup container,
                                  SettingsViewControllerDelegate delegate) {

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        res = context.getResources();
        this.context = context;
        this.delegate = delegate;

        backBtn = (Button) container.findViewById(R.id.backBtn);

        int[] pageIds = new int[]{R.layout.settings_page_mode};
        settingsViews = initPages(inflater, pageIds);
        viewPager = (ViewPager) container.findViewById(R.id.viewPager);
        viewPager.setAdapter(new SettingsViewAdapter(settingsViews));

        final int modePageIdx = 0;

        isLeftHandedCheckBox = (CheckBox) settingsViews.get(modePageIdx).findViewById(R.id.isLeftHandedCheckBox);
        isAccModeCheckBox = (CheckBox) settingsViews.get(modePageIdx).findViewById(R.id.isAccModeCheckBox);
        magCalibrateBtn = (Button) settingsViews.get(modePageIdx).findViewById(R.id.magCalibrateBtn);
        resetBtn = (Button) settingsViews.get(modePageIdx).findViewById(R.id.resetBtn);
        isBeginnerModeCheckBox = (CheckBox) settingsViews.get(modePageIdx).findViewById(R.id.isBeginnerModeCheckBox);
        prombtn1 = (ImageView) settingsViews.get(modePageIdx).findViewById(R.id.prombtn1);
        prombtn2 = (ImageView) settingsViews.get(modePageIdx).findViewById(R.id.prombtn2);
        propicture1 = (ImageView) settingsViews.get(modePageIdx).findViewById(R.id.propicture1);
        propicture2 = (ImageView) settingsViews.get(modePageIdx).findViewById(R.id.propicture2);
        propicture1.setVisibility(View.INVISIBLE);
        propicture2.setVisibility(View.INVISIBLE);

        initListeners();

        updateSettingsUI();

        Log.d(TAG, "new settings view controller");
    }


    public void setBackBtnOnClickListner(OnClickListener listener) {
        backBtn.setOnClickListener(listener);
    }

    private void updateSettingsUI() {
        ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();

        isLeftHandedCheckBox.setChecked(settings.isLeftHanded());
        isAccModeCheckBox.setChecked(settings.isAccMode());
        isBeginnerModeCheckBox.setChecked(settings.isBeginnerMode());

    }

    private List<View> initPages(LayoutInflater inflater, int[] pageIds) {

        ArrayList<View> pageList = new ArrayList<View>(pageIds.length);
        //for (int i = 0; i < pageIds.length; i++) {
        View view = inflater.inflate(pageIds[0], null);
        pageList.add(view);
        //}

        return pageList;
    }

    private void initListeners() {

        magCalibrateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(SettingsViewController.this.context).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dialog_title_info).setMessage(R.string.dialog_calibrate_mag)
                        .setPositiveButton(R.string.dialog_btn_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Transmitter.sharedTransmitter()
                                        .transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_MAG_CALIBRATION);
                            }
                        }).setNegativeButton(R.string.dialog_btn_no, null).show();
            }
        });

        resetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsViewController.this.context)
                        .setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.dialog_title_info)
                        .setMessage(R.string.dialog_reset)
                        .setPositiveButton(R.string.dialog_btn_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences preferences = getContext().getSharedPreferences("settings", 0);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();

                                ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
                                settings.resetToDefault();
                                settings.save();
                                SettingsViewController.this.updateSettingsUI();
                                if (delegate != null) {
                                    delegate.leftHandedValueDidChange(settings.isLeftHanded());
                                    delegate.accModeValueDidChange(settings.isAccMode());
                                    delegate.beginnerModeValueDidChange(settings.isBeginnerMode());
                                }
                            }
                        }).setNegativeButton(R.string.dialog_btn_no, null).show();
            }
        });


        isLeftHandedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isLeftHanded) {
                ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
                settings.setLeftHanded(isLeftHanded);
                settings.save();
                if (delegate != null) {
                    delegate.leftHandedValueDidChange(isLeftHanded);
                }

            }
        });

        isAccModeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isAccMode) {
                ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
                settings.setIsAccMode(isAccMode);
                settings.save();
                if (delegate != null) {
                    delegate.accModeValueDidChange(isAccMode);
                }
            }
        });

        isBeginnerModeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isBeginnerMode) {
                ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
                settings.setIsBeginnerMode(isBeginnerMode);
                settings.save();
                if (delegate != null) {
                    delegate.beginnerModeValueDidChange(isBeginnerMode);
                }
            }
        });

        prombtn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                propictureVisible = !propictureVisible;
                propicture2.setVisibility(View.INVISIBLE);
                if (propictureVisible == true) {

                    propicture1.setVisibility(View.VISIBLE);
                } else {
                    propicture1.setVisibility(View.INVISIBLE);
                }
            }
        });

        prombtn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                propictureVisible = !propictureVisible;

                if (propictureVisible == true) {
                    propicture1.setVisibility(View.GONE);
                    propicture2.setVisibility(View.VISIBLE);

                } else {
                    propicture2.setVisibility(View.INVISIBLE);
                }
            }
        });

        settingsViews.get(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                propicture1.setVisibility(View.INVISIBLE);
                propicture2.setVisibility(View.INVISIBLE);

            }
        });
    }


    @Override
    public void viewWillDisappear() {
        // TODO Auto-generated method stub
        super.viewWillDisappear();

    }

}
