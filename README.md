# moviesRecommendation

This is a Movies Recommendation Application built for Android using TMDB Movies API. The user will get movie recommendations based 
on his choice of favorite movie. 

![screenshot_20180621-130730](https://user-images.githubusercontent.com/13464773/41706293-0f916a00-7559-11e8-88e1-1baad303f221.png)

## Getting Started 

These instructions will get you a copy of the project up and running on your local machine

### Prerequisites

Android Studio(>v1.4), TMDB Movies API-Key(https://www.themoviedb.org/) 

*NOTE: The TMDB Movies API-Key needs to be inserted against the corresponding string in the strings.xml file, located at 
moviesRecommendation/app/src/main/res/values/strings.xml*

### Configuration 
This project was built under the following configuration:

    compileSdkVersion 23
    buildToolsVersion "23.0.2"
 
    defaultConfig {
        applicationId "com.example.android.popularmovies"
        minSdkVersion 15
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    
### Running the Application

To run this application:

      * Clone this repository
      * Extract the zip file
      * Import the extracted zip file into Android Studio IDE
      * Configure the project as per the requirement
      * Add the TMDB API-Key(*Refer prerequisite*)
      * Run the application on the desired device from Android Studio
