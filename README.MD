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
- [ ] Store player data to MongoDB: started, not done prototyping
- [ ] Iterate Challonge's bracket API
- [ ] Provide an API service (Jersey?)
- [ ] When inserting to Database, do this:
  collection.insert(SmasherAdaptor.toDBObject(Smasher));
- [ ] Tip: https://github.com/trishagee/mongodb-getting-started/blob/master/src/test/java/com/mechanitis/mongodb/gettingstarted/Exercise3InsertTest.java



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
- use simple java json objects when generating json output ye? Refactor with Jackson later
- For web: http://nikgrozev.com/2014/10/16/rest-with-embedded-jetty-and-jersey-in-a-single-jar-step-by-step/
- Java implementation of True Skill: https://github.com/nsp/JSkills
- Consider using http://vuejs.org/ for frontend implementation?
- http://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/

**Known weaknesses**
- Gameinfo is never updatead when updating new ranks. Algorithm / player ranks are still updated according to this site: http://boson.research.microsoft.com/trueskill/rankcalculator.aspx


