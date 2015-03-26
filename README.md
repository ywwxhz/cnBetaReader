# cnBeta reader

This is the third-party of cnbeta reader client for Android 4.0+

## Quick Overview

 - Screenshots

    <img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215315.png" alt="screenshot" title="screenshot" height="500" />|<img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215358.png" alt="screenshot" title="screenshot" height="500" />|<img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215406.png" alt="screenshot" title="screenshot" height="500" />

    <img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215440.png" alt="screenshot" title="screenshot" height="500" />|<img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215452.png" alt="screenshot" title="screenshot" height="500" />|<img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215341.png" alt="screenshot" title="screenshot" height="500" />

    <img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215509.png" alt="screenshot" title="screenshot" height="500" />|<img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215547.png" alt="screenshot" title="screenshot" height="500" />|<img src="https://git.oschina.net/ywwxhz/cnBeta-reader/raw/master/screenshots/device-2015-03-26-215528.png" alt="screenshot" title="screenshot" height="500" />

## Pre-requisites

Android Studio 1.0+

Android SDK v22


## Build from Source

1. clone or download source


2. change app/build.gradle signingConfigs to your own configuration


<pre>
    signingConfigs {
         release {
             storeFile file(System.getenv("KEY_STROE_FILE"))
             storePassword System.getenv("KEY_STROE_PASSWORD")
             keyAlias System.getenv("KEY_ALIAS_RELEASE")
             keyPassword System.getenv("KEY_PASSWORD")
         }
         debug {
             storeFile file(System.getenv("KEY_STROE_FILE"))
             storePassword System.getenv("KEY_STROE_PASSWORD")
             keyAlias System.getenv("KEY_ALIAS_DEBUG")
             keyPassword System.getenv("KEY_PASSWORD")
         }
     }
</pre>



3. compiler


    <code>./gradlew assembleDebug</code>

##License

Copyright 2015 远望の无限 (ywwxhz)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.