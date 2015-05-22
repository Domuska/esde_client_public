# esde21

Tomi Lämsä, 2185538, lamsatom@gmail.com
Juhana Pikkarainen, 1957819, juhana.pikkarainen@gmail.com


Instructions:

The application starts in EntryActivity, where a new central unit is asked from ConnectionManager. By clicking the "ENTER" button in the middle of the screen you're taken into the itemListActivity where the device data should be represented. This data is queried from the server and might take a moment to show up.

By clicking the items on the list you will be taken into either a new ItemListActivity (if you click on a container item), or DeviceActivity (if you click a device).

In the DeviceActivity you see the item's "path" in the central unit hierarchy and some other information. If the item is an actuator you can click the widgets at the bottom of the screen. If the device is a sensor the widgets are disabled. After starting a DeviceActivity, 10 seconds will pass and you will get a notification in the Android notification tab that states "Something has happened to this device..." and the device's name. This information is not tied to server information in any way, it is purely a cosmetic thing. The enable and disable monitoring buttons in DeviceAcitivity's actionbar are also purely cosmetic, the value is not stored in shared preferences or in any other place where the value would stay saved.

When set new values in actuator device's activity, the value will change immediately and a request for the server will be sent to change the value, but there are no quarantees that the value will actually be changed on the server. You can only be sure that the value was changed by the server by going back from the DeviceActivity screen and re-entering it (a new value should have been received from server and the UI will be drawn according to the new values).

Also when you are in a DeviceActivity, the values will not be updated if new data is received from the server unless you go out of the activity and re-enter it (which results in re-initializing the whole activity and views inside it with the new values that are stored in the application).

If you enter a DeviceActivity for a device that is an actuator, you can try shaking your phone around and waiting for a moment to get feedback. If succesful, a toast should appear in the lower part of the screen and the application should generate a new random boolean or integer depending if the device is binary or decimal device. Note that this sensor requires API level 18, and is disabled if phone's API level is lower than that. These sensors can be toggled on or off from either DeviceActivity's menu or from EntryActivity's settings "Sensor status". This sensor can be a little bit tricky to get to activate at a times and might need some vigorous shakes or the user standing up or sitting down and waiting for a couple of seconds for it to activate.

Setting a new URL for the server will happen solely through the action bar's Settings item in the EntryActivity screen (which will lead to the SettingsActivity, containing a SettingsFragment). The placement's by you most likely wanting to set a URL before accessing any devices. The URL, user name and password that you enter in the settings are used to connect to the server. The URL you enter will be checked so that it is a valid URL, but no other checks for it will be made (since it doesn't make sense to do so, user is responsible for entering a correct URL for the server he's connecting to).