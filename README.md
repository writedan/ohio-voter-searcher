# Ohio Voter Searcher
A hacked together Java program for searching through the 
[Ohio Voter Database](https://www6.sos.state.oh.us/ords/f?p=111:1). I make no claims as to the
accuracy of the information, take it up with the respective county Board of Elections.

This program and its source code is licensed under the terms of the GPLv3 as provided.

This program may use exorbiant amounts of RAM and CPU utilization. You have been warned. I
have observed that the Java heap space must be permitted to use upwards of 8G.

This program directly depends on Apache Commons HttpComponents (HttpClient module),
Apache Commons CSV, and Apache Commons IO.

## Features
* Rudimentary machine learning to improve search efficiency
* Automatic sync with the Voter File Database.