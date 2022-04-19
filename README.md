# AndroidDev - Location Tracker App

# Problems
# Problem 1 - Location Callback Interval
So, as I understand it, the app uses a locaiton callback object to retrieve Location information and then display it. the locaiton callback uses another object called LocationRequest, which is similar ot a config file. The request object has a couple of methods related to an interval. Supposedly this interval is like a timer, and the object is expected to retrieve data every interval that you demand it. My app does not work this way

# Solution? - I Have Ideas
Im not sure why the app behaves in this way, but It might be helpful to declare a setFastestInterval time, because what I think is happening is the time I am giving it is being set by default as this value, in my case I gave it 60 seconds, and no matter how much faster I change it in app, it refuses to budge. I think that setting the fastest value programmatically would give it a base interval to work with and perhaps fix the problem.


# Tools and Resources
https://www.youtube.com/watch?v=_xUcYfbtfsI
