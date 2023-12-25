import os
import re
import asyncio

testDirectory = "./Tests"
anyPattern = '{}-(server-config|client-config|out)\.json'
serverPattern = '{}-server-config\.json'
clientPattern = '{}-client-config\.json'
outPattern = '{}-out\.json'
temporaryFilePath = '{}-tmp.json'
gameTimeoutInSeconds = 30

# Represents a triplet of test files in which the server and client
# configs are the inputs to a single testtest, and the out file
# is the expected output. Each is represented by a file path
class TestTriplet:
    
    def __init__(
            self, 
            serverConfig: str, 
            clientConfig: str, 
            outFile: str
        ):
        self.serverConfig = serverConfig
        self.clientConfig = clientConfig
        self.outFile = outFile

    def __str__(self):
        return f'<Server: {self.serverConfig}, Client: {self.clientConfig}, Expected: {self.outFile}>'

    # Returns the contents of the file at the given path as a string
    def __getConfigAsString(self, configFilePath: str) -> str:
        configFile = open(f"{testDirectory}/{configFilePath}", 'r')
        config = configFile.read()
        configFile.close()
        
        return config

    def getServerConfigAsString(self):
        return self.__getConfigAsString(self.serverConfig)

    def getClientConfigAsString(self):
        return self.__getConfigAsString(self.clientConfig)
   
# Formats the given format string (representing a regex pattern)
# with the given index, compiles, and returns it
def formatAndCompile(pattern: str, index: int) -> re.Pattern:
    return re.compile(pattern.format(index))

# Determines whether the current working directory has at least
# one file matching the given regular expression
def cwdHasFileMatching(regexPattern: re.Pattern) -> bool:
    matches = [re.fullmatch(regexPattern, file) for file in os.listdir(".")]
    return any(matches)

# Within the given list of strings, finds the first full match 
# with the given pattern. ASSUMES that one exists
def findFirstMatchIn(candidates: list[str], pattern: re.Pattern) -> str:
    return next(candidate for candidate in candidates if re.fullmatch(pattern, candidate))

# Gets the triplet of test files with the given test index from 
# the current working directory. ASSUMES that a triplet exists
# with the given index
def getNthTest(testIndex: int) -> TestTriplet:
    filesInCwd = os.listdir(".")
    serverConfig = findFirstMatchIn(filesInCwd, formatAndCompile(serverPattern, testIndex))
    clientConfig = findFirstMatchIn(filesInCwd, formatAndCompile(clientPattern, testIndex))
    outFile = findFirstMatchIn(filesInCwd, formatAndCompile(outPattern, testIndex))

    return TestTriplet(serverConfig, clientConfig, outFile)

# Enters the globally defined test directory, searches for, and 
# returns all the natural number indexed test triplets
def getTestTriplets() -> list[TestTriplet]:
    originalDirectory = os.getcwd()
    os.chdir(testDirectory)
    tests = []
    testIndex = 0
    hasNextTestTriplet = cwdHasFileMatching(formatAndCompile(anyPattern, testIndex))

    while hasNextTestTriplet:
        triplet = getNthTest(testIndex)
        tests += [triplet]
        testIndex += 1
        hasNextTestTriplet = cwdHasFileMatching(formatAndCompile(anyPattern, testIndex))
    
    os.chdir(originalDirectory)

    return tests

# Runs the given command in a subprocess with the given input sent to
# the subprocess' STDIN. Returns the output (from STDOUT) of the subprocess
async def runSubprocessWithInput(command: list[str], input: str) -> str:
    process = await asyncio.create_subprocess_exec(
        *command, 
        stdin=asyncio.subprocess.PIPE, 
        stdout=asyncio.subprocess.PIPE
    )

    try:
        stdout, _  = await asyncio.wait_for(
            process.communicate(input.encode()),
            timeout=gameTimeoutInSeconds
        )
        # TODO: Examine the instances in which stdout is None, see how to represent
        # the return type of this function
        output = stdout.decode().strip() if stdout else ""
    except TimeoutError:
        output = "Test caught by timeout"

    return output

# Runs the given command in a new coroutine that executes the given command
# in a subprocess with the given input sent to the subprocess' STDIN. Returns
# the task that wraps the coroutine
def runTaskAsynchronously(command: str, input: str) -> asyncio.Task:
    task = asyncio.create_task(runSubprocessWithInput(command, input))

    return task

# Runs the server with the config in the given test in an asynchronous coroutine,
# waits a second, and then runs the client in another asynchronous coroutine, 
# waiting for the completion of both tasks, and returning the results of both
# the server and the client as a two element array of strings in that order
async def executeServerAndClient(test: TestTriplet) -> list[str]:
    print(test)
    portNumber = 50000
    serverTask = runTaskAsynchronously(["./xserver", f"{portNumber}"], test.getServerConfigAsString())
    await asyncio.sleep(2)
    clientTask = runTaskAsynchronously(["./xclients", f"{portNumber}"], test.getClientConfigAsString())
    results = await asyncio.gather(serverTask, clientTask)

    return results

# Runs both a server and client with the given configurations, writing 
# the output of the server to a temporary file using the given index.
# Returns the path to the temporary file
async def runTest(test: TestTriplet, index: int) -> str:
    print(f"Running test {index}")
    path = temporaryFilePath.format(index)

    with open(path, 'w') as actualOutFile:
        subprocessResults = await executeServerAndClient(test)
        print(f"Subprocess results: {subprocessResults[0]}")

    return path

# Deletes each file represented by a string path in the given list
def cleanup(tmpFiles: list[str]):
    [os.remove(path) for path in tmpFiles]

async def runTests():
    tests = getTestTriplets()
    tmpOutFiles = []

    for index, test in enumerate(tests):
        actualOutFile = await runTest(test, index)
        # tmpOutFiles += [actualOutFile]
        # success = checkJsonEquality(actualOutFile, test.outFile)
        # logStatus(success, index, test, actualOutFile)

    cleanup(tmpOutFiles)

asyncio.run(runTests())
