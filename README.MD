# Norwwegian Smash Ranking API #

This is mostly a sample project for learning purposes

Postman link: https://www.getpostman.com/collections/a550ff666688fdfa75b3

## Getting started ##
* Clone project, run mvn clean install and you're good to go. 

Ideas:
* Creating a full API Service using smash.gg's and challonge's API to iterate brackets
* Chess ranking algorithm? // most important right now
* Swagger support? //nice, but not prioritized

## TODOs ##
- [x] Iterate Smash.gg's bracket API
- [x] Find a rank api to use as baseline for ranking players (JSkills)
- [ ] Implement comparator for List of Smashers: http://stackoverflow.com/questions/5805602/how-to-sort-list-of-objects-by-some-property
- [ ] Iterate Challonge's bracket API
- [ ] Store player data to database (Mongo db?)
- [ ] Provide an API service (Jersey?)


## Ideas ##
Once decided upon, they will  be be added in the todo-list. 

* Sort ranks based on Country, region and city
* User submissions
  * Submit direct links to brackets
  * Tournaments gets automatically added unless they already exist in submitted tournaments list
  * Post request that only posts if not to be found in db
  * Users can vote and prioritize submitted tournaments which admins later will generate results for
  * Admins with simple http authentication allowing them to generate ranks based on users submissions
  * User submission in the format of:
  * https://api.smash.gg/tournament/house-of-smash-31/event/melee-singles, the rest is taken care of in the application 



**Technicalities**
- Avoid use of frameworks for backend functionality, use libraries instead. 
- For web: http://nikgrozev.com/2014/10/16/rest-with-embedded-jetty-and-jersey-in-a-single-jar-step-by-step/
- Jackson parsing: http://wiki.fasterxml.com/JacksonInFiveMinutes#A.22Raw.22_Data_Binding_Example
- JSON java parser API example http://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
- Java implementation of True Skill: https://github.com/nsp/JSkills
- https://github.com/goochjs/glicko2
- Consider using http://vuejs.org/ ? 


