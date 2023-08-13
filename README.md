# Microbot
Microbot is an opensource automated oldschool runescape client based on runelite. It uses a plugin system to enable scripting. Here is a youtube channel showing off some of the scripts

[Youtube Channel](https://www.youtube.com/channel/UCEj_7N5OPJkdDi0VTMOJOpw)
 
If you have any questions, please join our [Discord](https://discord.gg/zaGrfqFEWE) server.

Documentation coming soon.

## Project Layout

- [cache](cache/src/main/java/net/runelite/cache) - Libraries used for reading/writing cache files, as well as the data in it
- [runelite-api](runelite-api/src/main/java/net/runelite/api) - RuneLite API, interfaces for accessing the client
- [runelite-client](runelite-client/src/main/java/net/runelite/client) - Game client with plugins

## Usage

Open the project in your IDE as a Maven project, build the root module and then run the RuneLite class in runelite-client.  
For more information visit the [RuneLite Wiki](https://github.com/runelite/runelite/wiki).

### License

RuneLite is licensed under the BSD 2-clause license. See the license header in the respective file to be sure.

