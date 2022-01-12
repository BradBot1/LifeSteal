# LifeSteal Fork

A fabric server or client side implementation of the LifeSteal SMP.

This mod was forked from [BradBot1's](https://github.com/BradBot1/) LifeSteal [Mod](https://github.com/BradBot1/LifeSteal), please make sure to check it out as although there are quite a few changes this mod was heavily based off of his.

## Required Mods

> This fork is not dependant on any other mods

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
* [My GitHub](https://github.com/ModernAdventurer/LifeSteal/)