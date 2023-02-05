# Android Prework - _Did I just drink water?_

Submitted by: [ember arlynx](https://ember.software)

**Did I just drink water** features Ralph (the water guy) encouraging you to drink water! when the "Sip sip" button is pressed, displays a list of previous sips, and allows for deleting both individual and all sips.

spent: **3** hours spent in total, mostly reading up on 

## Required Features

The following **required** functionality is completed:

* [x] Image and introductory message displayed on screen
* [x] Button displayed on screen
* [ ] Toast with message appears when button is pressed

The following **optional** features are implemented:

* [x] Persist sip data using the Room component of Jetpack.
* [x] Use RecyclerView to pseudo-efficiently display an enormous amount of sips.
* [x] Generated adorable water guy using DALL-E.

## Video Walkthrough

Here's a walkthrough of implemented features (minus the toast, bc i took the recording before the commit that added this parenthetical):

<img src='https://i.imgur.com/07LZ97j.gif' title='Video Walkthrough' width='1280' alt='Walkthrough of features of sip app' />

GIF created with ffmpeg!

## Notes

I had to decide how to represent `Instant` inside the database. Apparently `Room` does not natively know what an Instant is supposed to be. I hope I did not make a mistake when selecting the timezone stuff.

## License

    Copyright 2023 ember arlynx

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.