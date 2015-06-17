# esde21

Tomi Lämsä, 2185538, lamsatom@gmail.com
Juhana Pikkarainen, 1957819, juhana.pikkarainen@gmail.com

Updated (17.6.2015) usage instructions:

A possibility to "connect" to multiple servers has been added. The functionality works with dummy data (since no real servers are available to be connected to), and some code regarding this was added. In order to get new servers visible one should go to settings and add a new server (through the action bar overflow menu). URL, user name and password can be added but are not necessary since the application doesn't actually try to connect anywhere with that information. After doing this the new server should appear in the list activity (that you can enter through the ENTER button in entry screen).

For now the application works so that if the URL you set for a central unit connection is not http://ohap.opimobi.com:18000/, the server will be considered as "dummy" server and the dummy data will be shown. In the real application if the URL set for the central unit was not a valid an URL that sends an answer, the application should just show an empty list.

The dummy servers all just have name "dummy server" and contain the same 3 devices and one container in them, this data is only to showcase how it would look when connecting to a real server that sends back real data. The URL that is entered for the server only shows in the list where you choose which server you wish to view.

Note: there's a bug in the settings screen, when you enter it again after adding new server(s) you will only see the default OHAP server. If you add additional servers through the overflow menu the servers should appear one by one with the information that you have filled in earlier, this is just a problem with creating the UI of the SettingsFragment, the preferences you have set earlier should nevertheless be saved and be accessible.

Build instructions:

There are errors reported in DeviceActivity about too low minimum API levels, but these shouldn't be a problem, the code should compile and function properly anyway. 

Source files are in 3 folders, opimobi_ohap_files includes files gotten from Henrik at the start of the course, ohap folder includes classes related to central units and network communication and ohap_client has classes related directly to activities in the application.

Usage instructions:

The application starts in EntryActivity, where a new central unit is asked from ConnectionManager. By clicking the "ENTER" button in the middle of the screen you're taken into the itemListActivity where the device data should be represented. This data is queried from the server and might take a moment to show up, assuming the server being connected to is functioning properly.

By clicking the items on the list you will be taken into either a new ItemListActivity (if you click on a container item), or DeviceActivity (if you click a device).

In the DeviceActivity you see the item's "path" in the central unit hierarchy and some other information. If the item is an actuator you can click the widgets at the bottom of the screen and a message to server is sent requesting the change. If the device is a sensor the widgets are disabled. 

After starting a DeviceActivity, 10 seconds will pass and you will get a notification in the Android notification tab that states "Something has happened to this device..." and the device's name. This information is not tied to server information in any way, it is purely a cosmetic thing. The enable and disable monitoring buttons in DeviceAcitivity's actionbar are also purely cosmetic, the value is not stored in shared preferences or in any other place where the value would stay saved.

While in the DeviceActivity, new values received from server are automatically updated into the UI. If you send a request for the server to change a value the widgets will become disabled and they will be enabled the next time a new value is received from a server, or if a new value is not received in 15 seconds the stored values will be reset to UI and widgets will be set enabled again and an error toast is displayed on screen. This is accomplished by a thread that runs while activity is visible, is stopped at onPause and continued at onResume().

If you enter a DeviceActivity for a device that is an actuator, you can try shaking your phone around and waiting for a moment to get feedback. If successful, a toast should appear in the lower part of the screen and the application should generate a new random boolean or integer depending if the device is binary or decimal device. Note that if using the Significant Motion sensor, this function requires API level 18, and is (should be at least) disabled if phone's API level is lower than that. This sensor can be a little bit tricky to get to activate at a times and might need some vigorous shakes or the user standing up or sitting down and waiting for a couple of seconds for it to activate. The way shaking detection works can be changed from the ItemActivity's menu by selecting Toggle Sensors. This will switch between the "modes" of sensors being turned off altogether, setting the sensor as Significant Motion, or Accelerometer. Toasts are shown accordingly when modes are changed.

Setting a new URL for the server will happen solely through the action bar's Settings item in the EntryActivity screen (which will lead to the SettingsActivity, containing a SettingsFragment). The placement's by you most likely wanting to set a URL before accessing any devices. The URL, user name and password that you enter in the settings are used to connect to the server. The URL you enter will be checked so that it is a valid URL, but no other checks for it will be made (since it doesn't make sense to do so, user is responsible for entering a correct URL for the server he's connecting to). Settings menu also contains the possibility to change the sensors for detecting shaking, instead of having to use the DeviceActivity's menu.


Distribution of work:

Tomi implemented the user interface of the application excluding the Settings activity and fragments, where he only added some elements. He also took care of the network communication, DeviceActivity logic, ItemList logic, CentralUnitConnection logic and ConnectionManager implementation. He also wrote most of this document and added a nifty icon for the application.

Juhana implemented the Settings activity and fragments, initial Preference-saving functionalities, and alongside Tomi added shaking detection to the application and added togglable shaking sensor modes.

Both participated in bug-squishing and fine-tuning the app in general.

Results of testing:

Data is received and shown in the UI of the application. Changing login information in the shared preferences will result in failure to log in into the server (shown by log messages and the lack of elements appearing into the UI). During final tests, it became apparent the server provided access even with wrong login credentials. This is considered to be more the server's fault than the application's.

Changing a a value in the DeviceActivity, moving out of the activity and re-returning into it seems to change the value of the device. Log messages also confirm that a new message arrives from the server after being sent from the DeviceActivity widgets.

Shaking the device in DeviceActivity seems to be sending a message to the server to change the value.

When a connection is not established with the server (be it from network not being available or incorrect login information, the application will stop attempting to connect after 20 seconds. If network connection is lost when the application is running, it seems to lose all network connectivity. The application needs to be completely restarted for it to work correctly again.

Listing a big amount of devices and containers in the ItemList was tested and seemed to function well, but since this data is not available from server it cannot be tested now.

After electing not to receive notifications from particular actuator changes, the app still shows them for the reasons described above, these settings are not saved in any sensible place. A notification will always be received after 10 seconds of entering a DeviceActivity

Significant Motion sensor seems to be slightly more finicky about detecting shaking than the Accelerometer mode.