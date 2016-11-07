/*
 * AnalogueJoystick
 *
 *  Created on: May 26, 2011
 *      Author: Dmytro Baryskyy
 */

package cc.flexbot.www.ui.joystick;

import cc.flexbot.www.R;
import android.content.Context;
//import com.hexairbot.hexmini.ui.joystick.JoystickBase;

public class AnalogueJoystick extends JoystickBase {

	public AnalogueJoystick(Context context, Align align, boolean isRollPitchJoystick, boolean yStickIsBounced) {
		super(context, align, isRollPitchJoystick, yStickIsBounced);
	}

	// protected int getBackgroundDrawableThrottleId()
	// {
	// return R.drawable.joystick_bg_throttle;
	// }

	@Override
	protected int getTumbDrawableId() {
		return R.drawable.joystick_rudder_throttle_new;
	}
}
