# Location-Reminder

## About
Todo App to remind user to do something when the user enter a custom area and store all todos locally with room db

## Google Maps API Key Instructions
Clone the project to your local machine.
Open the project using Android Studio.
This app will only work if you include your Google maps API key in `google_maps_api.xml`.
You will find the placeholder for the API key already there in the file.

## Features
- Fully unit tested with instrumented tests for fragments and activities
- Create a Todo backed by selecting a point of interest on the map
- Get a notification when you move close to that point of interest

## Technical Details

- room Database
- navigation component
- custom google map style
- location permissions
- google Maps SDK Android - Display Google Map, click on Point of Interest.
- koin - A pragmatic lightweight dependency injection framework for Kotlin.
- recyclerView
- single activity 
- unit Test, Instrumented Test using Mockito and Espresso.
- kotlin Coroutines
- MVVM (repository pattern) + ViewModel + Repository + Data Binding + LiveData
- workManager
- get device location , move camera to current user location.
- geofencing API - Provide contextual renubders when users enter or leave an area of interest.
- retrofit
- localized for multiple languages
