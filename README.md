# esde21

Tomi Lämsä, 2185538, lamsatom@gmail.com
Juhana Pikkarainen, 1957819, juhana.pikkarainen@gmail.com


Instructions:

The application starts in EntryActivity, where dummy central unit and devices are created (in the populateCU method). By clicking the "ENTER!" button in the middle of the screen you're taken into the itemListActivity where the device data should be represented. By clicking the items on the list you will be taken into either a new ItemListActivity (if you click on a container item), or DeviceActivity (if you click a device).

In the DeviceActivity you see the item's "path" in the central unit hierarchy and some other information. If the item is an actuator (such as "another sodding lamp" or "A bloody ceiling lamp") you can click the widgets at the bottom of the screen. If the device is a sensor (such as "Fancy hi-tech lamp's sensor") the widgets are disabled. After starting a DeviceActivity, 10 seconds will pass and you will get a notification in the Android notification tab that states "Something has happened to this device..." and the device's name.

Setting a new URL for the server will happen solely through the action bar's Settings item in the EntryActivity screen (which will lead to the SettingsActivity, containing a SettingsFragment). The placement's by you most likely wanting to set a URL before accessing any devices. The implementation is currently only on a UI level, lacking any functionality beyond saving the value of the URL into preferences.xml.