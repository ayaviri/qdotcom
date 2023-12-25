# TODO
- Populate README with project requirement and structure info (include diagrams of the static and dynamic interactions between components based on milestones)
- Go through the scattered TODOs in repo
- Please clean up JSON conversion utilities
- Prepend I to all interfaces + update group name (probably made easier with an IDE like IntelliJ)
- Review + clean up the observer + rendering components
- Implement the quiet flag (debugging)
- There is a bug in the referee in which game ending conditions aren't checked immediately after the turn is made
- Pull out the rotation of the player queue so that the referee calls it after the turn is done
- Create an abstraction over the TCP connection so that it is testable

# Sticky Fingers
- In order to pull out the initialisation of the ServerSocket in the Server component into its own function (so that that unit of code can perform error handling as needed), the ServerSocket field must be made non-final

# Stickier Fingers
- Currently, the submission of a player's name is done through a separate callable, not with the use of the existing Name callable. Though the current solution is more readable, a lot of code could be reused if we create a RemotelyNamedPlayer that implements the IPlayer interface and extends the PlayerProxy. We could reuse the constructor and the communicateWithRemote helper method in the PlayerProxy. The issue is that this player would be temporary, as we could have to eventually construct a PlayerProxy with that name, and it makes less sense upon first read.
- Sockets could be closed as a punishment in WaitingPeriod's call to attemptToGetPlayerName as opposed to being closed at the end. In our current client implementation, if the 
socket is closed as soon as the name submission timeout is hit, the referee proxy's playToCompletion ends immediately, and the player's lifeline ends there, no issue. In the case
the socket isn't closed until the end of the game, no JSON ever comes to the referee proxy, player's lifeline ends at the end of the game
- GameStates need to (briefly) exist without player proxies... I'm not entirely sure this is an issue, but it's still a sticky finger

# Questions
- A RoundResult has all of the information it needs to determine whether the round it summarises was a terminal round. Should the functionality to check that be in the RoundResult or Referee component ?
