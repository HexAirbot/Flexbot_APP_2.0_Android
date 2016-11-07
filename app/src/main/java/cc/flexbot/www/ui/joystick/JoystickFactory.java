/*
 * JoystickFactory
 *
 *  Created on: May 26, 2011
 *      Author: Dmytro Baryskyy
 */

package cc.flexbot.www.ui.joystick;


import cc.flexbot.www.ui.Sprite.Align;

import android.content.Context;


public class JoystickFactory 
{
	public enum JoystickType {
		NONE,
		ANALOGUE,
		ACCELERO,
	}
	
	public static JoystickBase createAnalogueJoystick(Context context, boolean isRollPitchJoystick,
															JoystickListener analogueListener,
															boolean yStickIsBounced)
	{
		AnalogueJoystick joy = new AnalogueJoystick(context, Align.NO_ALIGN, isRollPitchJoystick, yStickIsBounced);
		joy.setOnAnalogueChangedListener(analogueListener);
		
		return joy;
	}
	
	
	public static JoystickBase createAcceleroJoystick(Context context, boolean isRollPitchJoystick,
															JoystickListener acceleroListener,
															boolean yStickIsBounced)
	{
		
		AcceleratorJoystick joy = new AcceleratorJoystick(context, Align.NO_ALIGN, isRollPitchJoystick, yStickIsBounced);
		joy.setOnAnalogueChangedListener(acceleroListener);
		
		
		return joy;
	}
}