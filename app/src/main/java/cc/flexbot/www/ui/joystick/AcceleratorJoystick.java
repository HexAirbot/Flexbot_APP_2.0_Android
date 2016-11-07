/*
 * AcceleroJoystick
 *
 *  Created on: May 26, 2011
 *      Author: Dmytro Baryskyy
 */

package cc.flexbot.www.ui.joystick;
import cc.flexbot.www.R;
import android.content.Context;


public class AcceleratorJoystick 
	extends JoystickBase
{
	public AcceleratorJoystick(Context context, Align align, boolean isRollPitchJoystick, boolean yStickIsBounced) 
	{
		super(context, align, isRollPitchJoystick, yStickIsBounced);
	}
	
	
	@Override
	protected int getThumbRollPitchDrawableId() {
		
		return R.drawable.joystick_roll_pitch_normal;
		

	}
	
	@Override
	protected int getBackgroundDrawableId() 
	{
		return R.drawable.joystick_bg2;
	}


	@Override
	protected int getTumbDrawableId() 
	{
		return R.drawable.joystick_roll_pitch_new;
	}

	
	@Override
	protected void onActionMove(float x, float y) 
	{
	}
}
