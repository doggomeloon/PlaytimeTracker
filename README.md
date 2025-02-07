# Playtime Tracker
This is a very simple playtime tracker plugin for Minecraft version 1.21.3. It should work versions 16+ but it is untested.

It also adds the following command:
## /playtime
Sends a message to the person running the command with their playtime as of that moment.
<br>"Your Total Playtime: __h __m __s"

## How it works
Everytime a player joins the server, their uuid is written down in a file named playtime.yml. This can be found in your plugins folder, aptly named "PlaytimeTracker." Every time that user leaves the server, their playtime is added to the playtime.yml file. This saves it so that any time the user wants, they may run the command /playtime to see their hours, minutes, and seconds played. When they run the command, it also adds their current time on the server, so that it is accurately updated every time they run the command.
