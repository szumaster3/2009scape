package content.interfaces

import com.alex.loaders.interfaces.ComponentType
import com.alex.loaders.interfaces.IComponentSettings
import com.alex.tools.Copy
import content.data.OptionMask
import shared.consts.Items
import java.util.function.Consumer

object spellbook_interface {

    fun add() {
        val copy = Copy.newInterface()
        val interfaceId = copy.targetInterface
        var parentID: Int = -1

        Copy.to(834)
            .startAt(0)
            .addComponents(Consumer { comp ->
                comp.parentId   = -1
                comp.version    = 3
                comp.type       = ComponentType.LAYER
                comp.baseX      = 0
                comp.baseY      = 0
                comp.baseWidth  = 512
                comp.baseHeight = 512
                comp.settings   = IComponentSettings(0, -1)
                parentID    = comp.componentId
            })
            .save()

        Copy.to(834)
            .startAt(1)
            .addComponents(Consumer { comp ->
                comp.name               = "blizzard_spell"
                comp.version            = 3
                comp.parentId           = parentID
                comp.type               = ComponentType.SPRITE
                comp.baseX              = 0
                comp.baseY              = 0
                comp.baseWidth          = 24
                comp.baseHeight         = 24
                comp.spriteId           = 1707
                comp.optionCircumfix    = "Cast"
                comp.optionMask         = OptionMask.COMBAT_SPELL
                comp.settings           = IComponentSettings(20480, -1)
                comp.hasScripts         = true
                comp.onLoadScript       = arrayOf(
                    6,
                    -2147483645,
                    (parentID shl 16) or 0,
                    1708,
                    1707,
                    9,
                    "Blizzard",
                    "A low level Ice missile",
                    Items.MIST_RUNE_4695,
                    1,
                    -1,
                    0
                )
            })
            .save()
    }
}