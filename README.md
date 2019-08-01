# Client-Server Chat Application
By Ofek Bader and Snir Yefet

### How to start
---
1. Clone\Download the Project
2. Start Server.jar and Clients.jar (you can open multi Clients at the same time)
3. On the Server.jar click "Start"
4. Connect your Client/Clients to the Server (Don't forget to set your nickname)
5. Start chatting

### Client's GUI
---
The Client's GUI contains main one Chat Box,and one send Message Box .
The Client's GUI also contains the Following buttons :

1. **Connect/Disconnect** - Connects/Disconnect the Client with the given name to/from the Server Address that was given.(The default is LocalHost / 127.0.0.1). 
2. **Clear** - Clears the chat box.
3. **Show Online** - Shows all clients which connects to the same Server at the current time.
4. **Send** - Sends the data on the "Send Message Box" to the recipient that was given (If no recipient was mentioned the message will be send as a broadcast to all of the Clients connected at the moment).


### Simulation
---
Example of chat between three Clients - Alice, Bob and Canny.

* Alice Bob and Canny connecting to the server.
Notice that each client is getting alert of new members login in.
![](https://github.com/Sniryefet/Chat/blob/master/Pictures/multi%20client%20connection.PNG)



* Alice whispers Bob.
Notice Canny is logged in but don't see the message.
![](https://github.com/Sniryefet/Chat/blob/master/Pictures/whisper.PNG)



* Alice Broadcast. 
Notice this time Bob and Canny recieved  the message.
![](https://github.com/Sniryefet/Chat/blob/master/Pictures/Broadcast.PNG)



* Alice checks all online Clients using "Show Online" Button.
![](https://github.com/Sniryefet/Chat/blob/master/Pictures/show%20online.PNG)



* Alice Disconnecting.
Notice that each online Client receives a message informing him about her disconnection.
![](https://github.com/Sniryefet/Chat/blob/master/Pictures/Alice%20diconnect.PNG)

