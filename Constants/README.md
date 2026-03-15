# Constants

A centralized library of constants for the project.
It includes animations, graphics, items, NPCs, interfaces, and other game-related constants.  
By providing named constants, it reduces magic numbers in code and improves readability and maintainability.

---

## Naming Guidelines

### Animations
Animation constants are named following the pattern:

```
const val ENTITY_OR_SCENERY_NAME_ACTION_ANIMATION_ID = AnimationId
const val ENTITY_OR_SCENERY_NAME_ACTION_QUEST_NAME_ANIMATION_ID = AnimationId
```

**Example:**
```kotlin
const val SEARCH_WALL_JUNGLE_POTION_2096 = 4280
```

Use these constants to trigger animations in scripts or during events.

### Graphics & Sounds
Graphics (GFX) and sound (SFX) constants are named like this:

```
const val DESCRIPTION_GFX_OR_SOUND_ID = Id
```

**Example:**
```kotlin
// Graphics:
const val ROTTEN_TOMATOE_THROW_30 = 30
// Sounds:
const val BF_COLLECT_COKE_1049 = 1049
```

### Interfaces
Interface constants are grouped by functional area:

```
const val DESCRIPTION_ID = InterfaceId
```

**Example:**
```kotlin
const val CANOE_TRAVEL_758 = 758
const val BANNER_HALLOWEEN_800 = 800
```

### Items, NPCs, & Scenery
Items, NPCs, and scenery objects already use descriptive names.
If you want to suggest improvements for naming conventions, feel free to post an issue.

**Example:**

```kotlin
const val ITEM_ARDOUGNE_DIARY = 12345
const val NPC_GNOME_WARRIOR = 189
const val SCENERY_LUMBRIDGE_DOOR = 555
```

### Vars (Variables)
Vars include both Varp (global variable) and Varbit (bit-level variable) constants.  
Varbits are subfields of varps for precise control, and both are saved as constants:

**Example:**

```kotlin
// Varp (global variable):
const val VARP_QUEST_PIRATES_TREASURE_PROGRESS_71 = 71
// Varbit (bit-level variable):
const val VARBIT_HORROR_FROM_THE_DEEP_SWAMP_TAR_LIGHTING_MECHANISM_46 = 46
```

## Usage

Import the objects you need, then reference constants:

```kotlin

import shared.consts.Animations
import shared.consts.Items

animate(player, Animations.MOVE_FORWARD_5015)
val diaryItem = Items.ARDOUGNE_CLOAK_2_14702
```

This ensures your code is readable and avoids hardcoding numeric IDs.

## Contributing

- Add new constants with descriptive names.
- Keep modules grouped logically (`Animations`, `NPCs`, `Items`, `Graphics`, etc.).
- Remove duplicates and obsolete entries.
- Document any new constants you add for clarity.