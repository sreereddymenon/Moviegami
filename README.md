# Moviegami

Project created by me (Javier Ventajas Hernandez) as part of the Android Developer Nanodegree:
https://www.udacity.com/course/android-developer-nanodegree--nd801

![alt tag](https://github.com/Jventajas/Moviegami/blob/master/moviegami1.gif)
![alt tag](https://github.com/Jventajas/Moviegami/blob/master/moviegami2.gif)
![alt tag](https://github.com/Jventajas/Moviegami/blob/master/moviegami4.gif)
![alt tag](https://github.com/Jventajas/Moviegami/blob/master/moviegami3.gif)
![alt tag](https://github.com/Jventajas/Moviegami/blob/master/screenshot.png)


Main features:

- API usage: Used "themoviedb" to retrieve info about the movies in JSON format. Used youtube's api and android components to display the trailers.
- Google's Material Design: Applied google's style guidelines, layout elements and components to create a "material" UI.
- SQLITE and Content Provider: Stored movies, trailers and reviews into a database to store and read favorites.
- AsyncTasks: Used separate threads to fetch movies from the API and parse the JSON strings without blocking the UI thread.
- UI components: Used ViewPager, TabLayout and Fragments to implement the main activity flow. Used RecyclerView to create responsive lists of movies.
- Android's Parcelable interface: Implemented android's Parcelable interface to serialize movies and pass them through different contexts.
- Tablet UI: Used a Master-Detail flow to make tablet layout appealing. Used different resource folders to override phone layouts.
- Action buttons: Added OnClickListeners to delegate trailer playback to UI buttons. Added a share button to send trailers and a custom message via WhatsApp.
