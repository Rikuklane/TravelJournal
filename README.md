# TravelPad
An android app to journal your travels.

## Team
 - Marilin Kuusk
 - Ragnar Kadai
 - Triin Schaffrik
 - Richard Kuklane

## Features
 ideas: https://docs.google.com/document/d/1kQxIckrEbaYWjapckBQWjk_aQPymi3dHBS-909Pqato/edit#

## Technical README

TravelJournal is a Kotlin application that is meant for Android phones from SDK27 to SDK33. The building is done with standard Gradle. The necessary dependencies are core-ktx, room database and some more general ones (lifecycles, fragments).

The source code is hosted on github, it can be cloned on the host computer, build/run on Android Studios on an emulator (API 27-33) or on an Android phone with the equivalent version, where the debugging mode is enabled in developer settings. Another way to run it is to download the apk of the application and run it from the android phone directly.

The application was tested from emulators such as Nexus 5X API 33, Pixel XL API 33 and real phones like Xiaomi mi 9 lite (API 29), mi 10 lite (API 31) and Redmi Note 10 Pro (API 31).

## The concept story of the app
The concept came from one of the teammates, who got the idea whilst traveling around Europe. She wanted an application that would act as a swiss army knife of an app that would give the user sightseeing, documentation, packing list and trip info that they would have neatly in one place but also to keep a journal of all her past trips.

The application is meant for people traveling who want to organize everything from trip traveling to sightseeing info in one application. TravelJournal contains a trip planning feature to remind the user the dates the trips is taking place, a sightseeing feature that is a map showing the user surrounding sightseeing locations with links to learn more, a packing list feature where the user can write, look, and check out items that they take to the trip, and a documents feature that is useful for having certain documents on the go.

TravelJournal is meant for users who want to keep a digital journal of their travels or are planning on taking a trip and want to do it in a more efficient way.

## Brief report about project experience
### Which functional feature blocks does the project fulfill?
 1. Persistent Storage - the app uses a local, Room Database
 2. Location-awareness or Sensors: the app uses Map View and GPS
 3. Integrating with Web Services: the app uses MediaWiki API

### What other technologies, libraries you used in your project.
We were planning on using some sort of Rich Text Editor library to create more visual trip summaries, but couldn’t find a one that would match our needs and be up to date enough.

### Who worked on what? What went well? What went south?
Originally, we had a vague plan and work division set, but due to all team members being working students, the planning and work division got a bit chaotic and a lot of work was left at the last minute.

We couldn’t realize the project fully in the way we had hoped due to lack of time and some misunderstanding of what the final product should look like. Despite this, we managed to create a minimum viable product (MVP) of our app idea and got all the basic functionalities to work.

Everyone worked on everything a little bit, but everyone also had their own main focus. Richard created the base project, with necessary fragments, navigation, menu bar etc. Richard also did a lot of bug fixing and code optimization/clean-up, and wrote part of the report. Marilin created the initial trips view with the possibility to add, edit and delete trips, added the Maps View feature from Mini Project 2, created the possibility to add, edit and delete documents, and wrote part of the report. Triin also created a trip view, but a more advanced one with more features and a generally more user friendly interface, so we decided to keep that one. Ragnar created the Packing List feature and wrote part of the report.

Marilin and Richard did the initial project presentation and Triin and Ragnar did the final project presentation.

### What would you add/do if you had more time?
With more time, we would improve existing features. For example add a rich text editor to the “Add trip” feature. This would allow the user to maybe create a blog post type of summary of the trip or maybe even add several posts under one trip.  It would also be beneficial to add pictures inside the summary text or add a video to the trip.

The documents feature could be improved so that when planning a trip, the app would also notify the user if they need a visa or a special travel document to enter the country.

The packing list feature could be improved in a way that you can create several default packing lists. For example one for warm countries, one for cold countries. So when planning a trip, you can choose the default packing list and add more items to it if need be. This way the user wouldn’t have to add more trivial items (toothbrush, socks etc) manually each time they plan a new trip.

To the general Trips View, some searching or sorting (by date or alphabetically) mechanism would be useful. For when the user has a lot of trips and wants to find them quickly.

Could also add a DateRangePicker instead of two DatePickers in the NewTrip creation to reduce friction.

The Maps feature could be improved so that the user can save certain locations they want to visit in the world.

Additionally, some sort of trip budget or expense logging feature would be useful. To have an overview of how much a certain trip cost and for that very reason, some currency exchange calculator would also be beneficial.

### What was the most challenging problem?
It was definitely quite difficult to figure out how to realize all those features code-wise in a way that everyone would have the same idea of the final project and so that even if everyone works separately, all components would eventually work together harmoniously. Team members also had different levels of coding background so that made it even more difficult to visualize what the final project should look like.

Different life schedules also made this a bit more complicated because in order to get everything to work harmoniously, certain features had to be finished by a certain time so that everyone would see how the project is looking, what direction it is going and there would still be time to make necessary adjustments.

But in the end, this project was made for learning purposes and a lot of learning was indeed done.

## OWASP report
- MSTG-CODE-2 - The requirement is to make sure that the app has been built in release mode, with settings appropriate for a release build and make sure the app is not debuggable.
  - To test the requirement we looked up if the `AndroidManifest.xml` contains `android:debuggable`, but it didn’t and as the default value is false then it satisfied the requirement.
- MSTG-PLATFORM-1 - The app only requests the minimum set of permissions necessary. In our context it should only ask the permissions regarding the use of the camera for taking pictures, GPS location for , storage for writing and reading pictures and for scheduling an alarm for giving notifications when the document is going to expire.
  - To test the requirement we looked up what permissions and features we have defined in the `AndroidManifest.xml` and most of the permissions that we have are actually defined as dangerous. Then we checked if we are asking the user for their permissions in all of these cases, but it turns out that we were asking permission in a place that it was not necessary so we removed it and we are not asking permissions for `SCHEDULE_EXACT_ALARM` We are also not explaining why we need the permission when we are requesting it.
  - This requirement is not fulfilled entirely, to fill this requirement we would need to request access to `SCHEDULE_EXACT_ALARM` or instead of that use Firebase to schedule notifications which is an even better approach. And besides that would need to explain why the permissions are exactly needed.

