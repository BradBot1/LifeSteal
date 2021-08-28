# LifeSteal

A fabric server side implementation of the LifeSteal SMP

## Required Mods

> This mod is dependent on [BFAPI](https://github.com/BradBot1/BradsFabricApi)

## Config

The config is found under `lifesteal.json` in the config directory

|Field|Type|Description|Default|
|-----|----|-----------|-------|
|maxHealth|Double|The max health a player can have (including default health) before lifesteal no longer adds health *If less than or eaual to 0 then this is ignored*|-1|
|minHealth|Double|The minimum health a player can have (including default health) before lifesteal no longer removes health *Setting to 0 results in the player being able to reach no health (always have this value above 0 if banWhenHealthReachesZero is false)*|0|
|banWhenHealthReachesZero|Boolean|If the players max health is 0 the player will be banned for the reason given by banReason|true|
|healthToLooseOnDeath|Double|The amount of health to loose on a death *2 health is one heart*|2|
|looseHealthOnlyOnPlayerRelatedDeath|Boolean|If true the player can only loose health when the death was caused by another player|true|
|banReason|String|If banWhenHealthReachesZero is set to true this is the ban reason given to the player|You have been banned due to your health reaching 0!|

```json
{
  "maxHealth": -1.0,
  "minHealth": 0.0,
  "banWhenHealthReachesZero": true,
  "healthToLooseOnDeath": 2.0,
  "looseHealthOnlyOnPlayerRelatedDeath": true,
  "banReason": "You have been banned due to your health reaching 0!",
  "storage": {}
}
```

## Links

* [GitHub](https://github.com/BradBot1/LifeSteal)
* [ModRinth](https://modrinth.com/mod/lifesteal)
* [CurseForge](https://www.curseforge.com/minecraft/mc-mods/lifesteal)