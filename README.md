JavaBot-IRC
===========
An IRC bot, designed for simplicity and customizability.
===========

Contents of this bot is as such:         
1. JavaBot          
2. PircBot (framework for JavaBot)         
3. openNLP          
4. Apache Commons         
5. Apache Commons Codec         
5. HSQLDB         
4. Informa         
5. Jaspyt         
6. Java Simple Plugin Framework  
7. log4j
5. Spring

(Don't worry, all are open source)                             

Files in root directory:         
config.txt - Configuration, refer to config.txt.template               
humor.txt - Humor (add anything you want)     
log4j.properties - log4j properties config file, edit only if you are experienced.                

=========================================================================

1. Configure this bot.       
                
Edit the config file, "config.txt.template". Channels are seperated by spaces.         
Make sure you use a registered nick and that you have access to that nick.          
An utility to configure the config file will be coming soon.   

All commands will require the prefix you set in the config file.    

Once you're done with the configuration, rename files/config.txt.template to config.txt,                 
database/users.odb.template to users.odb and etc.
         
2. Running this bot         
         
Make sure you have JVM 7. Then, run java -jar JavaBot.jar & in the terminal. You need to run it in the background or          
else it may quit.         
         
3. Authenciating yourself         
         
Check in the terminal for the nick you have chosen in case the nick you chose isn't valid.         
         
Afterwards, copy the nick and send a private message to the nick in the terminal:         
useradd [your chosen username without brackets, may not be your nick] [password]         
         
JavaBot will return a message displaying the information you need to enter into the database. An example is this:         
"Add these details to the database: ID blahblahblah blahblahblah"         
         
Make sure OpenOffice Base/LibreOffice Base is installed. An utility to run SQL to edit those databases will be coming soon.         
         
Open up /database/users.odb and open table "users"         
         
Enter a suitable ID (since it is the primary key, do not include a duplicate ID. Increment the largest ID. 
For example, if the largest ID is 5, just use 6.) into the ID field, enter "blahblahblah" into the username 
field and enter "blahblahblah" into the password field.         
         
Now, authenciation is secured. You can log in through:         
login [your chosen username without brackets, may not be your nick] [password]"         

Once you're authenciated, you're done!         
         
To log out, simply do:         
logout     
         
You can skip authenciation, but beware, anyone can use JavaBot's quit, part, join commands.         
         
4. Learn the commands and features         
In JavaBot beta 1.4.0. a useful help feature has not been included yet. I will include it soon, but meanwhile          
here's the list of commands:         

Authenciation:         
"quit" -> quits         
"join" [channel] -> joins [channel]         
"part" -> parts current channel         
"part" [channel] -> parts [channel]         

"login" -> authenciate. Required to use any authenciation-needed commands         
"useradd" -> add a user. You need to authenciate before doing so.         
"logout" -> log out         

Normal commands:         
"announce [message]" -> highlights everyone's nick and give a message.         
"roll" -> Roll a 6-sided dice         
"flip" -> Flip a coin.         
"hm" -> Start a game of hangman. Words can be configured at /database/hangman.odb           
"humor" -> Generates random humor (humor file can be edited at /files/humor.txt         
"iplookup [website]" -> looks up the servers of a site         
"learn" -> Generates a random tutorial from linuxtuts.sytes.net/wordpress.         
"t3" -> Starts a modified game of tic tac toe. Try it out!          

"wz2100" -> Lists the games going on in Warzone2100. More game APIs coming soon!         

Here's the features:         
Spam filter - Config in config.txt                  
Protection mode - Goes against anyone who bans/kicks the bot or sets an universal ban. Disabled by default.          
 - Edit "protectionMode" in config.txt         

5. Look at the logs.         
They are located at /logs/log_TIMESTAMP.txt         

=========================================================================

### Change log ###

##Version 1.0.0 ## 
- First build of JavaBot! 

## Version 1.1.0 ## 
- Added advanced spam filters 
  - kicks if a person types >x messages in y seconds 
	- kicks if a person types messages longer than y characters 
	- kicks if a person joins that has a similar name to another person.

## Version 1.2.1 ## 
- Removed buggy spam filters. 
- Added games (commands with the prefix !) 
- Modularised the code 
- Formatted the code 
- Bug fixes 
- Less flooding.

## Version 1.3.0 ##
- Major upgrade!

- Added hangman game
- Added NLP implementation with training mode.

## Version 1.4.0 ##
- Major upgrade!

- Added plugin functionality and many plugins
- Removed NLP implementaiton
- Added secure authenciation
- Added flood protection (less buggy now, extremely stable)
- Added channel protection mode
- New config file