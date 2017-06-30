# Project 3 - MySimpleTweets

MySimplyTweets is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **X** hours spent in total

## User Stories

The following **required** functionality is completed:

* [X]	User can **sign in to Twitter** using OAuth login
* [X]	User can **view tweets from their home timeline**
* [X] User is displayed the username, name, and body for each tweet
* [X] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
* [X] User can **compose and post a new tweet**
* [X] User can click a “Compose” icon in the Action Bar on the top right
* [X] User can then enter a new tweet and post this to twitter
* [X] User is taken back to home timeline with **new tweet visible** in timeline
* [X] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh

The following **optional** features are implemented:

* [X] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [X] User can **pull down to refresh tweets timeline**
* [X] User is using **"Twitter branded" colors and styles**
* [X] User can **select "reply" from detail view to respond to a tweet**
* [X] User that wrote the original tweet is **automatically "@" replied in compose**
* [X] User can tap a tweet to **open a detailed tweet view**
* [X] User can **take favorite (and unfavorite) or reweet** actions on a tweet
* [X] User can **see embedded image media within a tweet** on detail view.

The following **bonus** features are implemented:

* [X] Compose tweet functionality is built using modal overlay
* [X] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [X] User can see embedded image media within the tweet detail view
* [X] Reply tweet functionality is built using a modal overlay, user can reply from detail view and from Timeline view
* [X] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.


## Video Walkthrough

Here's a walkthrough of implemented user stories:

[Walkthrough](https://github.com/HanaPearlman/MySimpleTweets/blob/master/demo.mp4)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright [2017] [Hana Pearlman]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
