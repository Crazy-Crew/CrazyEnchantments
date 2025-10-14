## Changes
- Added missing blocks to BlockList.yml [#885](https://github.com/Crazy-Crew/CrazyEnchantments/pull/885)
- Added a check to BlackSmith to ensure conflicting enchants can't be combined [#891](https://github.com/Crazy-Crew/CrazyEnchantments/pull/891)
- Update limit info command [#892](https://github.com/Crazy-Crew/CrazyEnchantments/pull/892)
- Added the ability to define enchant conflicts (You must add the config options below to your configuration to make use of this.)
    - https://github.com/Crazy-Crew/CrazyEnchantments/blob/035e4258c63e32173b8a380fb9b91f79b5059f91/paper/src/main/resources/Messages.yml#L14
    - https://github.com/Crazy-Crew/CrazyEnchantments/blob/a6d2031688eb6457943660bd59f87c63cf9b5c3b/paper/src/main/resources/Enchantments.yml#L1802
    - https://github.com/Crazy-Crew/CrazyEnchantments/blob/a6d2031688eb6457943660bd59f87c63cf9b5c3b/paper/src/main/resources/Enchantments.yml#L1826
- Improved performance heavily by removing a fair chunk of reliance on ItemMeta.
- Added better logging if things we need are missing.
- Removed oraxen deprecated durability checks.
- Replace the `Metrics` class with bStats shaded.
- Remove pre-process command event for /heal or /ci.
- Update folia support/

## New Enchantments
- TreeFeller

You must add the new config options to each file.

https://github.com/Crazy-Crew/CrazyEnchantments/blob/3ed037006265a1b9192ecd45083ec5612453d804/paper/src/main/resources/Enchantments.yml#L696
https://github.com/Crazy-Crew/CrazyEnchantments/blob/3ed037006265a1b9192ecd45083ec5612453d804/paper/src/main/resources/Tinker.yml#L151
https://github.com/Crazy-Crew/CrazyEnchantments/blob/3ed037006265a1b9192ecd45083ec5612453d804/paper/src/main/resources/config.yml#L295

## Fixes
- Fixed an issue where blast worked based on the tool used to click, now it works based on the tool used when the block is broken. [#893](https://github.com/Crazy-Crew/CrazyEnchantments/pull/893)
- Fixed an issue with loot drop height [#889](https://github.com/Crazy-Crew/CrazyEnchantments/pull/889)
- Fixed an NPE on startup if player field is not found in the config files.