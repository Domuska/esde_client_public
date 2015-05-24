# esde21

Tomi L�ms�, 2185538, lamsatom@gmail.com
Juhana Pikkarainen, 1957819, juhana.pikkarainen@gmail.com

Build instructions:

There are errors reported in DeviceActivity about too low minimum API levels, but these shouldn't be a problem, the code should compile and function properly anyway.

Usage instructions:

The application starts in EntryActivity, where a new central unit is asked from ConnectionManager. By clicking the "ENTER" button in the middle of the screen you're taken into the itemListActivity where the device data should be represented. This data is queried from the server and might take a moment to show up, assuming the server being connected to is functioning properly.

By clicking the items on the list you will be taken into either a new ItemListActivity (if you click on a container item), or DeviceActivity (if you click a device).

In the DeviceActivity you see the item's "path" in the central unit hierarchy and some other information. If the item is an actuator you can click the widgets at the bottom of the screen. If the device is a sensor the widgets are disabled. After starting a DeviceActivity, 10 seconds will pass and you will get a notification in the Android notification tab that states "Something has happened to this device..." and the device's name. This information is not tied to server information in any way, it is purely a cosmetic thing. The enable and disable monitoring buttons in DeviceAcitivity's actionbar are also purely cosmetic, the value is not stored in shared preferences or in any other place where the value would stay saved.

When set new values in actuator device's activity, the value will change immediately and a request for the server will be sent to change the value, but there are no quarantees that the value will actually be changed on the server. You can only be sure that the value was changed by the server by going back from the DeviceActivity screen and re-entering it (a new value should have been received from server and the UI will be drawn according to the new values).

Also when you are in a DeviceActivity, the values will not be updated if new data is received from the server unless you go out of the activity and re-enter it (which results in re-initializing the whole activity and views inside it with the new values that are stored in the application).

If you enter a DeviceActivity for a device that is an actuator, you can try shaking your phone around and waiting for a moment to get feedback. If successful, a toast should appear in the lower part of the screen and the application should generate a new random boolean or integer depending if the device is binary or decimal device. Note that if using the Significant Motion sensor, this function requires API level 18, and is (should be at least) disabled if phone's API level is lower than that. This sensor can be a little bit tricky to get to activate at a times and might need some vigorous shakes or the user standing up or sitting down and waiting for a couple of seconds for it to activate. The way shaking detection works can be changed from the ItemActivity's menu by selecting Toggle Sensors. This will switch between the "modes" of sensors being turned off altogether, setting the sensor as Significant Motion, or Accelerometer. Toasts are shown accordingly when modes are changed.

Setting a new URL for the server will happen solely through the action bar's Settings item in the EntryActivity screen (which will lead to the SettingsActivity, containing a SettingsFragment). The placement's by you most likely wanting to set a URL before accessing any devices. The URL, user name and password that you enter in the settings are used to connect to the server. The URL you enter will be checked so that it is a valid URL, but no other checks for it will be made (since it doesn't make sense to do so, user is responsible for entering a correct URL for the server he's connecting to). Settings menu also contains the possibility to change the sensors for detecting shaking, instead of having to use the DeviceActivity's menu.


Distribution of work:

Tomi implemented the user interface of the application excluding the Settings activity and fragments, where he only added some elements. He also took care of the network communication, DeviceActivity logic, ItemList logic, CentralUnitConnection logic and ConnectionManager implementation. He also wrote most of this document and added a nifty icon for the application.

Juhana implemented the Settings activity and fragments, initial Preference-saving functionalities, and alongside Tomi added shaking detection to the application and added togglable shaking sensor modes.

Results of testing:

Data is received and shown in the UI of the application. Changing login information in the shared preferences will result in failure to log in into the server (shown by log messages and the lack of elements appearing into the UI).

Changing a a value in the DeviceActivity, moving out of the activity and re-returning into it seems to change the value of the device. Log messages also confirm that a new message arrives from the server after being sent from the DeviceActivity widgets.

Shaking the device in DeviceActivity seems to be sending a message to the server to change the value.

When a connection is not established with the server (be it from network not being available or incorrect login information, the application will stop attempting to connect after 20 seconds. If network connection is lost when the application is running, it seems to lose all network connectivity. The application needs to be completely restarted for it to work correctly again.

Listing a big amount of devices and containers in the ItemList was tested and seemed to function well, but since this data is not available from server it cannot be tested now.

After electing not to receive notifications from actuator changes, the app still seems insistent on showing them.

Significant Motion sensor seems to be slightly more finicky about detecting shaking than the Accelerometer mode.

