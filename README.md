# LifeSteal Fork

A fabric server and client side implementation of the LifeSteal SMP

## Required Mods

> This mod is not dependant on any mods

## Config

Configuration is purely managed through gamerules, here is a list of gamerules and what they do.

|Gamerule|Type|Description|Default|
|-----|----|-----------|-------|
|lifeSteal:playerKillOnly|Boolean|If a player should lose hearts when dying in any way other than to a player|true|
|lifeSteal:banWhenMinHealth|Boolean|If a player should be banned when they reach the minimum health value|true|
|lifeSteal:stealAmount|Integer|The amount of health that should be stolen upon death|2|
|lifeSteal:minPlayerHealth|Integer|The minimum health a player can reach before being banned *if the value is below 1 it is automatically corrected to 1*|1|
|lifeSteal:maxPlayerHealth|Integer|The maximum health a player can reach *set to a value of 0 or below to disable*|1|

Player Maximum health is now stored via the maximum health attribute making it easy to modify and for other mods to be compatible with as long as they dont actively enfore their own max health values.

## Links

* [GitHub](https://https://github.com/ModernAdventurer/LifeSteal/)
