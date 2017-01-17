Only a sample project for learning purposes

Ideas:
* Creating a full API Service using smash.gg's API
* Chess ranking algorithm? 
* Swagger support?

Functioanlity: 
* Sort ranks based on Country, region and city
* Bruke https://rankade.com/ree#ranking-system-comparison ?

MVPS: 
* Én enkelt turneringsserie i første omgang


Tourney series:
https://api.smash.gg/tournament/house-of-smash-31 (Fetch tournament slug from webpage URL)

Get all events for one series:
https://api.smash.gg/tournament/house-of-smash-31?expand[]=event

Expand on: 
- phase: &expand[]=phase
- groups: &expand[]=groups

Events:
https://api.smash.gg/tournament/house-of-smash-31/event/melee-singles
expands on:
- phase
- groups

Examples:
https://api.smash.gg/tournament/house-of-smash-31/event/melee-singles?expand[]=phase&expand[]=groups

**Phase groups:**

https://api.smash.gg/phase_group/phaseGroupId

https://api.smash.gg/phase_group/208986
Expands on:
- sets
- entrants
- standings
- selections

Example: https://api.smash.gg/phase_group/208986?expand[]=sets&expand[]=standings&expand[]=standings

Iterate all sets played for a phase group? 

https://api.smash.gg/phase_group/208986?expand[]=sets

objectclasses.Player:
https://api.smash.gg/player/10627


**Technicalities**
- For web: http://nikgrozev.com/2014/10/16/rest-with-embedded-jetty-and-jersey-in-a-single-jar-step-by-step/
- Jackson parsing: http://wiki.fasterxml.com/JacksonInFiveMinutes#A.22Raw.22_Data_Binding_Example
- JSON java parser API example http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial