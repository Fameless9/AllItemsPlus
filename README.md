# All Items Plus Overview

All Items Plus is a spigot plugin developed for private minecraft servers.
It is a game mode in which the players' objective is to collect all the items,
kill all the mobs, find all the biomes and finish all advancements in the game.
You can select whether you would like to activate/deactivate certain objectives
or objective types (items, mobs, biomes, advancements).

## Features

#### Menu
```
The Menu (/settings | /menu) is the main hub for the plugin. Here you can toggle
the different objectives on and off, as well as change the settings of the plugin.
```

#### Chain Mode
```
The plugin also comes with a chain mode, which gives a randomized chain of objectives
for you to complete. Toggle the chain mode on/off in the menu.
```

#### Timer
```
The plugin has a built-in timer, keeping track of how long it takes you to finish all your objectives.
Toggle the timer on/off using /timer toggle
```

#### Missing Objectives
```
The plugin has a GUI that displays the items, mobs, biomes and advancements
you have left to complete. You can open the GUI by running /missing | /missingobjectives.
```

#### Finished Objectives
```
Similar to the missing objectives, the plugin has a GUI that displays all the objectives that
have been finished. Open this GUI by running /finished | /finishedobjectives.
```

#### Leaderboard
```
The plugin also comes with a leaderboard, displaying each player with the respective amount of
objectives they have completed. You can open the leaderboard by running /leaderboard.
```

#### Bossbar
```
The plugin also has a bossbar, which cycles through a set of messages that show you the
amount of objectives you have completed. Toggle the bossbar on/off by running /togglebossbar | /tb.
```

#### Language:
```
The plugin is available in 2 languages as of writing this. English and German. You can change the language
using /language | /lang. If you would like to help translate the plugin to your language, please follow the
language contributing instructions.
```

## Support

The plugin was developed and tested on spigot 1.21. I encourage you to use it on this version only,
otherwise you might encounter bugs or errors. If you still encounter any bugs or errors,
please report them.

## Contributing

I am always happy to receive contributions from the community. If you would like to contribute to the plugin,
please follow these contributing guidelines:

1. Fork the repository
2. Create a new branch
3. Make your changes
4. Create a pull request
5. Wait for the pull request to be reviewed
6. If the pull request is accepted, it will be merged into the main branch


If you want to contribute to the language files, please follow the language contributing instructions:

1. Fork the repository
2. Create a new branch
3. Add an entry for your language in the Language.java enum file.
4. Create a new language file in the resources package. Name it lang_xx.json. Replace xx with the language code.
5. Add a skull representing the Flag of your country to the Language GUI in LanguageCommand.java.
6. Add a respective handle in the click listener in LanguageCommand.java.
7. Translate the strings in the language file. Make sure that the keys are the same as in the other language files.
8. Create a pull request
9. Wait for the pull request to be reviewed
10. If the pull request is accepted, it will be merged into the main branch


If you do not know how to implement your language into the code and still want to contribute,
feel free to just contribute a translated language file. I will take care of the rest.<br />
Otherwise, follow the steps above.

Contributors:
1. You could be the first contributor!

Thank you!


### Please report any bugs on the issues page of this repository.
