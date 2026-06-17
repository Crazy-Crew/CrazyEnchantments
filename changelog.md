## Changes 🔨
- Bumped server requirements to 26.1.2 and Java 25.
- Added Material#PlayerHead to EntityUtils#getHeadMaterial.
- Improved performance by no longer storing the `Player` object in `CEPlayer`.
- Added extra messages to `messages.yml`
- All commands have now been re-coded.
  - This allows for adding new commands, and making command maintenance much easier.
### Plugin Support
- Improved fly/combat/territory/interact checks etc related to plugins like GriefPrevention, FactionsUUID, WorldGuard and so on.
    - It supports multiple plugins now, so if you for some reason run multiple plugins that handle territory, they should work in tandem.
    - This fixes a potentially large amount of odd bugs people were having.

## Bugs Fixed 🐛
- Added null checks if entities or players did not have the MAX_HEALTH attribute which prevents quite a few null pointers
  - If there is a null pointer, a message will be logged to console... and the enchantment will not function.
- Prevent aura enchantments from activating if the player is not in their line of sight, or is invisible.
- Disable player flight only if the player has a line of sight, and is not friendly.
- Disable player fall damage if a player's flight is disabled.