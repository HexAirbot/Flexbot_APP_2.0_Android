package cc.flexbot.www;

import cc.flexbot.www.SettingsDialog;

public interface SettingsDialogDelegate
{
    public void prepareDialog(SettingsDialog dialog);
    public void onDismissed(SettingsDialog settingsDialog);
}
