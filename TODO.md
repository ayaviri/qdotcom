# TODO
- Populate README with project requirement and structure info (include diagrams of the static and dynamic interactions between components based on milestones)
- Go through the scattered TODOs in repo
- Please clean up JSON conversion utilities
- Prepend I to all interfaces + update group name (probably made easier with an IDE like IntelliJ)
- Review + clean up the observer + rendering components
- Create an abstraction over the TCP connection so that it is testable
- THE JAVA SUBPROCESSES DON'T SEEM TO BE CLEANED UP ONCE THE TEST SCRIPT FINISHES RUNNING

# Sticky Fingers
- In order to pull out the initialisation of the ServerSocket in the Server component into its own function (so that that unit of code can perform error handling as needed), the ServerSocket field must be made non-final

# Stickier Fingers
- NameSubmission over a new kind of player that requests name over TCP 
- Sockets could be closed as a punishment in WaitingPeriod's call to attemptToGetPlayerName as opposed to being closed at the end. In our current client implementation, if the 
socket is closed as soon as the name submission timeout is hit, the referee proxy's playToCompletion ends immediately, and the player's lifeline ends there, no issue. In the case
the socket isn't closed until the end of the game, no JSON ever comes to the referee proxy, player's lifeline ends at the end of the game
- GameStates need to (briefly) exist without player proxies...

# Questions
- A RoundResult has all of the information it needs to determine whether the round it summarises was a terminal round. Should the functionality to check that be in the RoundResult or Referee component ?
