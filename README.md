# Hashtag Generator App

This is an Android application that generates relevant hashtags based on a user-provided prompt. 
It uses OpenAI's GPT-3.5 API to generate hashtags dynamically.

## Features

- Enter a prompt to generate hashtags.
- Specify the number of hashtags you want.
- Copy hashtags to the clipboard with a single tap.
- Simple and clean UI.


## Screenshots

### Home Screen
![Home Screen](/Screenshot01.png)

### Generated Hashtags
![Generated Hashtags](images/Screenshot02.png)

## Setup and Installation

1. Clone the repository or download the source code.
2. Open the project in Android Studio.
3. Add your OpenAI API key in `MainActivity.java`:
   ```java
   private static final String OPENAI_API_KEY = "your-api-key-here";
   ```
4. Build and run the application on an Android device or emulator.

## Usage

1. Enter a prompt describing the topic for hashtags.
2. Specify the number of hashtags you want.
3. Click "Generate" to fetch hashtags from OpenAI API.
4. Copy the hashtags and use them on social media.

## Dependencies

- `OkHttp` for making network requests.
- `org.json` for JSON parsing.

## License

This project is open-source and free to use.
