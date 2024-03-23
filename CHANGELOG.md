## Changes
* The order in which enchantments are added at the start of the list instead of the end. [d45e11c](https://github.com/Crazy-Crew/CrazyEnchantments/commit/d45e11c9deabbcfeaa9e96b1066a1ef802281fd5)
* Added a config option to be able to change the amount that damage is scaled by for Rage. [61d3817](https://github.com/Crazy-Crew/CrazyEnchantments/commit/61d3817045272a836fc1a91f63912869013bdb46)
* Added new messages for give/getting slot crystals as they just used the ones for protection crystals. [21b9c23](https://github.com/Crazy-Crew/CrazyEnchantments/commit/21b9c23e9507752bfdf90fd3b2ead4cfba2944b5)

## Additions
* Added a new command, `/ce slotcrystal [amount] [player]` with tab completion and default permissions. [b7a3a8d](https://github.com/Crazy-Crew/CrazyEnchantments/commit/b7a3a8df6f2550f6612f5266520d3ea531bde621)
  * The permission is `crazyenchantments.slotcrystal`

## Enhancements
### Performance
* Increase performance by reducing getItemMeta calls when necessary.
  * [7c8663](https://github.com/Crazy-Crew/CrazyEnchantments/commit/7c86631a81cdb7253c748b42daa8541fa1972da0)
  * [9b15b57](https://github.com/Crazy-Crew/CrazyEnchantments/commit/9b15b57cfa3c0b5a4e87f0c63b0577a6a353c74c)
* Increase performance by reducing the getItemMeta calls and not creating full item stacks then comparing the objects. [db0ef3c](https://github.com/Crazy-Crew/CrazyEnchantments/commit/db0ef3ce1d20c4621ef70f9144fbdedeb3d7cd78)
* Store uuid in the remaining arraylist's instead of player objects. [c9666a8](https://github.com/Crazy-Crew/CrazyEnchantments/commit/c9666a8aee8bad521d0f10a1a00384cb63b631bb)
* Change a larger part of each of the events over to running async and then directly merge it into the main thread instead of having a queue that merges it. [2c4a0e7](https://github.com/Crazy-Crew/CrazyEnchantments/commit/2c4a0e788ef1aeef1393531d19623b065855f8a0)

### Other
* Properly remove items from inventory when necessary. `setItem` contains a nullable field so no need to create a new `ItemStack` 
  * [783d0fd](https://github.com/Crazy-Crew/CrazyEnchantments/commit/783d0fd37debe7c025e97cca6520daff5c35dc58)
  * [29a3f1b](https://github.com/Crazy-Crew/CrazyEnchantments/commit/29a3f1b4b8514b21687fb3b11ccf69edd88f404f)
* Clean up checking if books can be combined in the blacksmith gui. [aa6f6d](https://github.com/Crazy-Crew/CrazyEnchantments/commit/aa6f6d729c5719ca83ecd087cef7bb198b11df97)
* Converted all inventory menus to use InventoryHolders as to not rely on name checks anymore.
  * [7948ec4](https://github.com/Crazy-Crew/CrazyEnchantments/commit/6948ec444e1f1e519a6ddd11e3aa2f95ff032982)
  * [1b0de55](https://github.com/Crazy-Crew/CrazyEnchantments/commit/1b0de552323ce453bdc81f2e6e3715198a557011)
  * [6a3837b](https://github.com/Crazy-Crew/CrazyEnchantments/commit/6a3837bdbdc61598fc2215df342ec8393304ab95)
  * [cbdec66](https://github.com/Crazy-Crew/CrazyEnchantments/commit/cbdec66e838d567983878b0ec37d50a45176a27b)
  * [d5e087c](https://github.com/Crazy-Crew/CrazyEnchantments/commit/d5e087c0a1c263776b256ade6258974c181369ba)
  * [dad2b2f](https://github.com/Crazy-Crew/CrazyEnchantments/commit/dad2b2f4f9beb626b5641b7d06bb7df20e7c9e09)
* Improve the loop in tinkerer/filemanager by using the Map#Entry
  * [7c30648](https://github.com/Crazy-Crew/CrazyEnchantments/commit/7c30648a5d54cb788746626ae48be3c2a98b21c5) 
  * [7fe6077](https://github.com/Crazy-Crew/CrazyEnchantments/commit/7fe6077f34571614b9efaa1f7cbe3963df43594f)
* Change level check over to getting values from a hashmap instead of pulling all data from the item for every check. [9300b64](https://github.com/Crazy-Crew/CrazyEnchantments/commit/9300b647e37c139ff4255fdd16f27ed622fc51dd)
* Add a check to ensure the player's inventory is not full before trying to give an item. [abd8024](https://github.com/Crazy-Crew/CrazyEnchantments/commit/abd8024d37da844a224d78af9d74795b198d645b)
* Remove the unused data that was added to failed dust to make it so that they can be stacked. [68f8c50](https://github.com/Crazy-Crew/CrazyEnchantments/commit/68f8c50247570179c93a16997f3ea6fd228cdffc)
* Added null safety or default values to notify you gracefully that something is broken. [c725268](https://github.com/Crazy-Crew/CrazyEnchantments/commit/c7252680851cfaa842b02adbb32a34512bd29c3e)
* Updated firework damage prevention by using the new API from Spigot Upstream. [3d59ab9](https://github.com/Crazy-Crew/CrazyEnchantments/commit/3d59ab92e75a0f3f534757bb548484485dcb00cd)
* A player opped should always be able to use the enchants. [654c2fb](https://github.com/Crazy-Crew/CrazyEnchantments/commit/654c2fb78d069314fedeb861ab59bf3a98b31c11)
* Properly remove dust/scrolls or scramblers from inventory with reduced item meta checks as well. [bde17b1](https://github.com/Crazy-Crew/CrazyEnchantments/commit/bde17b111facedf38fb743bd2b400c17411fc30e)
* Add ore information to /ce limit to help users understand what all of the current limits are. [dffb151](https://github.com/Crazy-Crew/CrazyEnchantments/commit/dffb151946df581e613e4f8dac1d5198bbbcd82f)
* Remove the copy and paste of blast block list being checked for vein miner blocks to see if they are a valid block that can be broken. [6f4f444](https://github.com/Crazy-Crew/CrazyEnchantments/commit/6f4f444e0632fc8ffa8a07d3f5ba37ca62491e79)
* A general list of changes. [d4ae91f](https://github.com/Crazy-Crew/CrazyEnchantments/commit/d4ae91fd08d0caf13a6cd0f4416a1a60edf4d5b1)

## Misc
* Cleaned up the buildscript, Updated typos, cleaned up imports, refactored a chunk of the project around for better organization.

## Plugin Support
* Switch towny check to check if players are in the same town or are allied instead of checking for player damage. [edd385](https://github.com/Crazy-Crew/CrazyEnchantments/commit/edd38508418ef340b3b06ba05cbc1ea6945620ea)
* `Spartan` is no longer supported in CrazyEnchantments and will not be added back. [1716ebf](https://github.com/Crazy-Crew/CrazyEnchantments/commit/1716ebf53a8e4547b960d6ba4f83d3a2c17746d1)
  * [34a64c9](https://github.com/Crazy-Crew/CrazyEnchantments/commit/34a64c9fa0f8d187b28823e66cf94e62145b1a7f)

## Fixes:
* Fixed `EntityDamageEvent` calls by using direct entity instead of causing entity [ba67c2f](https://github.com/Crazy-Crew/CrazyEnchantments/commit/ba67c2f6b1e3538d97ea246a424443ebab548fea)
* Fixed a method in ItemBuilder being outdated [aba4ff2](https://github.com/Crazy-Crew/CrazyEnchantments/commit/aba4ff20e80042d4fb5158b66a5dfa402e7f26b8)
* Fixed an issue where entity checks weren't on the main thread. [1a3716](https://github.com/Crazy-Crew/CrazyEnchantments/commit/1a37162b0d1ea050683408ebe520b7ae52cac952)
* Fixed an issue where we were checking off-hand twice with scrambler. [4ae5914](https://github.com/Crazy-Crew/CrazyEnchantments/commit/4ae5914487f35ad833de2232852b93f7d7ad5508)
* Fixed an issue with potion effects on armor enchants. [bd7026b](https://github.com/Crazy-Crew/CrazyEnchantments/commit/bd7026b13a971307a470fa6dd3efc4ad38574e72)
* Fixed an issue where we used the wrong enchant, Vampire was supposed to be used yet Viper was. [ef1bdd4](https://github.com/Crazy-Crew/CrazyEnchantments/commit/ef1bdd45e0513388e24689c9c7069b5939190ed7)
* Fixed entity damage code due to Spigot's changes related to EntityDamageByEntityEvent. [1f9ecd9](https://github.com/Crazy-Crew/CrazyEnchantments/commit/1f9ecd9e2cceb11d2c1c5bbae01addeb1059385d)
* Fixed an npe with bow enchantments. [d928492](https://github.com/Crazy-Crew/CrazyEnchantments/commit/d928492691d0db43ba1cd447cc38919948c44475)
* Fixed hellforged not working for items in main/offhand. [f022576](https://github.com/Crazy-Crew/CrazyEnchantments/commit/f02257654de87257b1d66201f24d7cab59a865c4)
* Fixed the command order in which the args were in to apply the correct level. [b5cbf5c](https://github.com/Crazy-Crew/CrazyEnchantments/commit/b5cbf5ce4480539d928870a71fbb4887ab343de9)
* Fixed command feedback for /ce give, so it sends the proper format. [79b8bf0](https://github.com/Crazy-Crew/CrazyEnchantments/commit/79b8bf072bffa013b7182474debc4e62b81cf75b)
* Fixed event priorities to allow us to get the last say. [352f9ca](https://github.com/Crazy-Crew/CrazyEnchantments/commit/352f9ca9a63431ad7ec4569732cdc7d79528ec30)
* Makes it so that all armor effects are re-applied after the totem takes them away. [163e296](https://github.com/Crazy-Crew/CrazyEnchantments/commit/163e2963c31cf4fa4a2ab823b137803ce6c6799a)
* A large amount of issues fixed [d4ae91f](https://github.com/Crazy-Crew/CrazyEnchantments/commit/d4ae91fd08d0caf13a6cd0f4416a1a60edf4d5b1)
  * Fix mistakes in the default config.
  * Fix item names being set to "" when there is no name set.
  * Fix players in spectator mode with wings not being able to fly.
  * Fix telepathy not being able to work on stairs.
  * TempFix factions errors caused by forks.
  * Fix Aura enchants not working.
  * Fixed GKitz timer not displaying time in the correct format.
  * Fix random enchantment level.

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/CrazyEnchantments/issues)
* [Bug Reports](https://github.com/Crazy-Crew/CrazyEnchantments/issues)