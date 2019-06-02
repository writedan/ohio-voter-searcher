# Ohio Voter Searcher
A hacked together Java program for searching through the 
[Ohio Voter Database](https://www6.sos.state.oh.us/ords/f?p=111:1). I make no claims as to the
accuracy of the information, take it up with the respective county Board of Elections.

This program and its source code is licensed under the terms of the GPLv3 as provided.

This program is very greedy with bandwidth usage, hard disk usage, and CPU utilization. 
***Contributions are welcome to improve on that.*** It **will** save all 88 county voter files
to your hard disk which **will** take a lot of space. You have been warned.

This program outputs data in bytes that are meant to be read by another program 
and transformed into a nicer presentation. To find out more information, check the
[Serializer](https://github.com/writedan/ohio-voter-searcher/blob/master/src/com/writefamily/daniel/VoterSearcher/Serializer.java) class.

## Usage Guide ##
You can filter be the first name, last name, birth year, party affiliation, 
"residential city", and county. (Residential city as provided by the Board of Elections
of each county may not be the actual residential city of a voter).

Values are separate by semi-colons.

`java -jar voter_searcher.jar LAST_NAME="Write" FIRST_NAME="Daniel;James" COUNTY="Lorain;Cuyahoga"`

Limiting by county will cause only those counties to be downloaded, so its best practice
to set that flag is you know it or can give a range of possible values. A lot of information
will be provided about each voter as a serialized `java.util.Map`. You can find more
information on this data at the Ohio Secretary of State website at the link provided above.