# qdotcom

## What Is This ?

An implementation of the Q Game, inspired by Qwirkle. Completed for CS4500 under Matthias Felleisen. [Here is an overview of the design process, in his words](https://github.com/mfelleisen/Qwirkle?tab=readme-ov-file#the-idea).

## Development Requirements

- [Java 20+](https://jdk.java.net/archive/)
- [Maven 3.9+](https://maven.apache.org/install.html)

## Build

```
$ make build
```

## Running a game

To start a new game server

```
$ ./xserver Tests/{n}-server-config.json
```

To join a game using the client component

```
$ ./xclients Tests/{n}-client-config.json
```

## Testing & Code Formatting

```
$ make test
$ make lint
```
