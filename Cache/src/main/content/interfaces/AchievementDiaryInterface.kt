package content.interfaces

import com.alex.loaders.interfaces.ComponentType
import com.alex.loaders.interfaces.IComponentSettings
import com.alex.tools.IfaceCopy
import shared.consts.Components

object AchievementDiaryInterface {

    fun add() {
        // Modify component 31.
        IfaceCopy.to(Components.AREA_TASK_259)
            .from(Components.AREA_TASK_259)
            .startAt(31)
            .copy(31)
            .modify {
                name               = "area_task_5"
                parentId           = 16973831
                text               = "Ardougne"
                shadow             = true
                optionMask         = 2
                settings           = IComponentSettings(2, -1)
                rightClickOptions  = arrayOf("Read Journal", "", "", "", "", "", "", "", "", " ")
                onMouseHoverScript = arrayOf(45, componentHash, 16777215)
                onMouseLeaveScript = arrayOf(45, componentHash, 16711680)
                onVarpTransmit     = arrayOf(58)
            }
            .save()

        // Magic of area task interface:
        // Component 7 is the layer for all parent components and use script 1393 on load.
        // The script calculates the vertical position for component 31 using internal transformations
        // and offsets, then positions it inside the scroll layer.
        //
        // Based on this calculated position, the script also adjusts the scroll height of component 7
        // (baseY + 95) and initializes the scrollbar by linking component 10 to component 7.
        //
        // Since we currently cannot use the script without editing it this,
        // we replicate the layout logic by using a fixed baseY value so that additional
        // components align correctly within the scrollable task list.
        val baseY = 455
        val step = 14
        val labels = listOf("Easy", "Medium", "Hard")
        val prefix = "area_task_5_"

        labels.forEachIndexed { index, text ->
            IfaceCopy.to(Components.AREA_TASK_259)
                .startAt(34 + index)
                .addComponent {
                    name               = prefix + text.lowercase()
                    version            = 3
                    parentId           = 16973831
                    type               = ComponentType.TEXT
                    baseX              = 28
                    this.baseY         = baseY + step * index
                    baseWidth          = 152
                    baseHeight         = 15
                    fontId             = 494
                    color              = 16711680
                    this.text          = text
                    shadow             = true
                    optionMask         = 2
                    settings           = IComponentSettings(2, -1)
                    rightClickOptions  = arrayOf("Read Journal", "", "", "", "", "", "", "", "", " ")
                    onMouseHoverScript = arrayOf(45, componentHash, 16777215)
                    onMouseLeaveScript = arrayOf(45, componentHash, 16711680)
                    onVarpTransmit     = arrayOf(58)
                }
                .save()

        }
    }
}