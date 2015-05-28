Poker-24-Game
========================================================================
java distributed computing (GUI)
--------------------------------
uncomment the line 65 in playground.java and line 48-59 and 76 in JPoker24GameServer.java to enable clients playing on different machine

###STEPS:
1. cd glassfish/
2. cd bin/
3. ./asadmin start-domain
4. run JPoker24GameServer.java
5. run JPoker24Game.java
6. register for the first time or log in directly

###RULES:
1. Server will try to assign the user to an existing game that is not started. If there are no game available, the user will be assigned to a new game.
2. The server will start a game when either:
   a. The fourth user joined the game
   b. 10 seconds has passed since the first user is assigned (new game), and there are at least 2 users joined
3. The server will notify all users in the game when a game can be started.
4. Type answers in numbers (J,Q,K WILL NOT BE ACCEPTED)
5. The winner should be announced, together with the correct answer. And game can be restarted.

###Screenshoots
![alt tag](https://github.com/w34ma/Poker-24-Game/blob/game/pics/login.png)

![alt tag](https://github.com/w34ma/Poker-24-Game/blob/game/pics/register.png)

![alt tag](https://github.com/w34ma/Poker-24-Game/blob/game/pics/info.png)

![alt tag](https://github.com/w34ma/Poker-24-Game/blob/game/pics/play.png)

![alt tag](https://github.com/w34ma/Poker-24-Game/blob/game/pics/leadboard.png)

