# LifeSteal

A fabric server side implementation of the LifeSteal SMP

## Notice for when updating to v2.0.0

There has been alot of changes since v1.2.0 and as a result the config is very different (i only really added stuff)

Also if you want to disable banning of users when they reach 0 health have a look under `punishment` in the config as it has been moved

## Required Mods

> This mod is dependent on [BFAPI](https://github.com/BradBot1/BradsFabricApi)

## Commands

This mod adds a command that can be used to modify and get a players lost health, the general structure is as follows:

/`alias` [get [uuid/name]]/[set [uuid/name] value]

This allows for moderation of players health without lengthy server restarts

## Config

The config is found under `lifesteal.json` in the config directory

```json
{
  "comment-maxHealth": "The maximum *total* health that can be gained, if less than 0 it will be ignored",
  "maxHealth": -1.0,
  "comment-minHealth": "The minimum *total* health that someone can be at, when this value is reached they cannot be used to gain hearts",
  "minHealth": 0.0,
  "comment-healthToLooseOnDeath": "The amount of health to be lost when a player dies",
  "healthToLooseOnDeath": 2.0,
  "comment-looseHealthOnlyOnPlayerRelatedDeath": "This toggle dictates if a player can loose health from deaths that are not caused by a player, if true then they cannot",
  "looseHealthOnlyOnPlayerRelatedDeath": false,
  "punishment": {
    "banWhenHealthReachesZero": true,
    "banReason": {
      "text": "You have been banned due to your health reaching 0!"
    }
  },
  "comment-notEnoughHeartsMessage": "The message that is displayed when you kill a player that doesn't have enough hearts to kill you",
  "notEnoughHeartsMessage": {
    "text": "The player did not have any hearts you could steal"
  },
  "command": {
    "comment-enableCommands": "If the commands should be registered",
    "enableCommands": true,
    "comment-aliases": "The aliases that the command will register under",
    "aliases": [
      "lifesteal",
      "ls"
    ],
    "comment-permission": "The permission required to modify a players health *if you dont have a permission db the level can be set to the wanted operator level*",
    "permission": {
      "node": "lifesteal.modify",
      "level": "op_3"
    },
    "comment-successMessage": "The text sent to the player when they set another players health",
    "successMessage": {
      "color": "green",
      "text": "Done!"
    },
    "comment-successMessageFile": "The text sent to the player when they set another players health but they are not online so it stored it to be applied when the player joins",
    "successMessageFile": {
      "color": "green",
      "text": "Done! (to file)"
    },
    "comment-broadcastToOps": "If setting a players health should be broadcasted to all operators",
    "broadcastToOps": true
  },
  "recipe": {
    "comment-allowCraftingOfHealth": "If the default recipe should be registered if a custom one is not found under lifesteal:health",
    "allowCraftingOfHealth": true,
    "comment-registerRequirements": "Don't disable this unless you have a conflicting error, other mods recipes may depend on it",
    "registerRequirements": true,
    "comment-registerResults": "Don't disable this unless you have a conflicting error, other mods recipes may depend on it",
    "registerResults": true
  },
  "marks": {
    "comment-enableMarks": "If marks should be listened to, this is needed for health items",
    "enableMarks": true,
    "comment-marks": "The list of marks that will be listened to, if you remove/modify any of these ensure you update the crafting recipes mark",
    "marks": [
      "lifesteal",
      "health"
    ],
    "comment-limiter": "The maximum number of times a player can consume health before they are stopped, if <1 this is ignored",
    "limiter": -1,
    "output": {
      "comment-markOutputSuccess": "Sent to an entity when they consume a marked item sucessfully",
      "markOutputSuccess": {
        "text": "You consumed some health"
      },
      "comment-markOutputFailed": "Sent to an entity when they fail consume a marked item due to failing the limiter check",
      "markOutputFailed": {
        "color": "red",
        "text": "You have already consumed your fill of health"
      },
      "comment-broadcastToOpsMarks": "If operators should be told when a player consumes a marked item",
      "broadcastToOps": true
    }
  }
}
```

## Links

* [GitHub](https://github.com/BradBot1/LifeSteal)
* [ModRinth](https://modrinth.com/mod/lifesteal)
* [CurseForge](https://www.curseforge.com/minecraft/mc-mods/lifesteal)

## Api utilisation

This mod adds a (simple interface)[https://github.com/BradBot1/LifeSteal/blob/master/src/main/java/com/bb1/fabric/lifesteal/LifeStealable.java] for using it as an API, `LifeStealable`

`LifeStealable` allows for a mod to get a players lost health and modify it as needed while also allowing it to be implemented on custom entities so that they can be used to gain health on death

Any `LifeStealable` implementation should always call `setLostHealth` when ever the health changes

## Additions to [BFAPI](https://github.com/BradBot1/BradsFabricApi)

### Marks

LifeSteal adds a customisable set of marks (check the sub `marks` for individual marks) that default as `lifesteal` and `health`

When these marks are applied to an ItemStack and said ItemStack is used the appropriate message is sent to the user:

If the user has more gained health than the limiter allows it will send `markOutputFailed` elsewise `markOutputSuccess` will be sent

### Recipes

There are now a couple of recipe requirements/results that are added by LifeSteal

#### Requirements

LifeSteal adds 3 new requirements to custom recipies, these are:

|Requirement ID|Description|Expects|
|--------------|-----------|-------|
|losthealth>|Ensures the crafter has lost atleast the given amount of health|Integer|
|losthealth<|Ensures the crafter has lost less the given amount of health|Integer|
|losthealth=|Ensures the crafter has lost the given amount of health|Integer|

These can be utilised to lock recipes to certain health levels such as `losthealth<` being set to `-2` will only be craftable by those who have gained a heart

#### Results

LifeSteal adds 3 new results to custom recipies, these are:

|Result ID|Description|Expects|
|---------|-----------|-------|
|gainhealth|Gives the player the given amount of health|Integer|
|loosehealth|Takes the given amount of health from the player|Integer|
|sethealth|Sets the players lost health to the given amount|Integer|
