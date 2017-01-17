Only a sample project for learning purposes

Postman link: https://www.getpostman.com/collections/a550ff666688fdfa75b3

Ideas:
* Creating a full API Service using smash.gg's API
* Chess ranking algorithm? 
* Swagger support?

Functioanlity: 
* Sort ranks based on Country, region and city
* Bruke https://rankade.com/ree#ranking-system-comparison ?

MVPS: 
* Én enkelt turneringsserie i første omgang


Shortly explained:
Use tournament slug to find: 
- event id, which you use to find groups, which in turn you use to find: 
phase groups: https://api.smash.gg/tournament/groups/phase_group/phaseGroupId

Then expand the phase group with entrants and sets to iterate wins/losses:
https://api.smash.gg//phase_group/208986?expand[]=sets&expand[]=entrants


Tourney series:
https://api.smash.gg/tournament/house-of-smash-31 (Fetch tournament slug from webpage URL)

Get all events for one series:
https://api.smash.gg/tournament/house-of-smash-37?expand[]=event


Expand on: 
- phase: &expand[]=phase
- groups: &expand[]=groups

Events:
https://api.smash.gg/tournament/house-of-smash-37/event/melee-singles
expands on:
- phase
- groups

Examples:
https://api.smash.gg/tournament/house-of-smash-37/event/melee-singles?expand[]=phase&expand[]=groups

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

**No way to iterate players for brackets**
Generate a rank score for a player based on wins vs other entrants. 
- create an array of entrant id-s for a player.
- 


**Technicalities**
- For web: http://nikgrozev.com/2014/10/16/rest-with-embedded-jetty-and-jersey-in-a-single-jar-step-by-step/
- Jackson parsing: http://wiki.fasterxml.com/JacksonInFiveMinutes#A.22Raw.22_Data_Binding_Example
- JSON java parser API example http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial