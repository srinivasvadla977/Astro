Hello there! thank you for checking out my App. Please find it's details below,

Short description:
My Android app Astro, helps in enhancing a Team coordination with features like GroupChat, new activities Reminders, upcoming tasks Notifications, a Calendar to mark team members Leave plans to prevent overlapping 
and to also mark the performer of any important task. This app also has a modifiable knowledge base to share essential information with team.

Features:

1) GroupChat:

   The App contains a GroupChat fragment where all users can chat and share information including images that can be taken from either gallery or camera. This images will be reduced to 20% of their quality
in order to save storage in database but they are still readable. Users can also acknowledge any message with a like, by long pressing the message. If one clicks on the likes icon of a message, a Bottom sheet
will show all users who liked the message in the order. Users can also update their username and profile picture any time.

In GroupChat fragment, a small green color icon with number of new messages will pop-up on bottom right of the screen when new messages arrives. By pressing it, we will be taken to last read message.
The App auto post messages in groupChat when a user is being added/removed from the App and when new activity is scheduled by a user.

2) Dashboard:

   i. It contains a horizontal recycler view on the top to show all users and tells if they are connected to shift or not.
   ii. Below that, we have a Zoomable image view where we can post images to show important notice to users, like any changes in plan or users shift plannings etc.
   iii. Below that, we have a vertical recycler view to show all scheduled tasks by users with filters like, upcoming tasks(ordered by task arrival time), daily/weekly tasks, one time tasks,
        all tasks(ordered by user scheduled time, latest first).
        On pressing this recylcer view item, user will be taken to a new fragment with full details of that perticular item/task. 

3) Scheduling new Task:

   Here we can schedule a new task which can be one time task or repeating task. If it is a repeating task, we can select either daily once or weekly once so that the notification will keep coming on scheduled time.
We can also mark the task as high priority so that the reminder will appears as an alert dialogue with background tune, even if we are using other applications at that time. The tasks which are not marked as
high priority will simply appears as notifications.
While scheduling an activity one can provide brief details of it including images if required.

Amazing point here is that once any team member schedules an activity, he and rest of the team members(who are in the shift) also receive notification related to that activity.

4) Tracker with Calendar:

   Tracker will help in marking the leave plans of team members so that the team can avoid overlapping of leaves. Tracker also helps in maintaining the records of the performers of special activities
that are scheduled at unusual times like, during weekends/night-shifts.

Each time when a user updates leave tracker, Astro will auto post a message in the groupChat. 

5) Knowledge Base:

   In knowledge base, team can store useful information regarding work that later comes in handy for other team members and new joiners. 
All the records in knowledge base are editable and they will also show the name of the person who edited it last time along with the time stamp.  

6) Admin Fragment:

   Admin portal is helpful in managing the users. It shows all the existing users and new user-requests in two separate recycler views. Here we can add new user or remove existing user
by providing password in alert dialogue window. 

7) Miscellaneous: 

   In Astro, we have a switch inside toolbar which is useful in changing user status like on-shift/off-shift. Once a user status changes the other team members will receive a notification stating the same.
   
A new user after installing this app, will not able to access all of the features. To do so, first they need to submit a request in new user page. Then Astro will auto post a message in groupChat informing
about new user request. Then an authorized user can approve the new user request which is visible in Admin fragment by providing password.

Note: I have attached screen shots of most of the use cases with feature-number as shown here, in separate folder called "ScreenShorts". Please be kind to check! 

