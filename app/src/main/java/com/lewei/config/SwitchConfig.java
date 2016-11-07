package com.lewei.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SwitchConfig
{
	private Context context;
	
	public SwitchConfig(Context context)
	{
		this.context = context;
	}
	
	public void writeSdcardChoose(Boolean sdcardState)
	{
		SharedPreferences sp = context.getSharedPreferences("sdcardchoose", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("sdcardchoose", sdcardState);
		edit.commit();
	}

	public boolean readSdcardChoose()
	{
		SharedPreferences sp = context.getSharedPreferences("sdcardchoose", Context.MODE_PRIVATE);
		return sp.getBoolean("sdcardchoose", false);
	}
	
	public void writeTurnLeftRight(Boolean isTurnLeftRight)
	{
		SharedPreferences sp = context.getSharedPreferences("isTurnLeftRight", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("isTurnLeftRight", isTurnLeftRight);
		edit.commit();
	}

	public boolean readTurnLeftRight()
	{
		SharedPreferences sp = context.getSharedPreferences("isTurnLeftRight", Context.MODE_PRIVATE);
		return sp.getBoolean("isTurnLeftRight", false);
	}
}
