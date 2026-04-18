package core.game.system.command.rottenpotato

import core.api.*
import core.game.dialogue.Dialogue
import core.game.dialogue.IfTopic
import core.game.dialogue.InputType
import core.game.dialogue.Topic
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.colorize

@Initializable
class RPUseWithSceneryDialogue(player: Player? = null) : Dialogue(player) {

    val ID = 38575797

    private lateinit var scenery: Scenery

    private var selectedId: Int? = null
    private var selectedLocation: Location? = null
    private var rotation: Int = 0
    private var type: Int = 10

    override fun newInstance(player: Player?): Dialogue = RPUseWithSceneryDialogue(player)

    override fun open(vararg args: Any?): Boolean {
        scenery = args[0] as Scenery

        options(
            "Remove Scenery",
            "Add Scenery",
            "Transform Scenery",
            "Copy Object ID"
        )
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (buttonId) {

            // Remove
            1 -> {
                end()
                scenery.remove()
                sendMessage(player, colorize("%RObject removed."))
            }

            2 -> {
                menu()
            }


            3 -> {
                end()
                scenery.transform(scenery.id + 1)
                sendMessage(player, colorize("%RObject transformed."))
            }

            4 -> {
                end()
                sendMessage(player, colorize("%RObject ID: ${scenery.id}"))
            }

            5 -> {
                end()
                player.teleport(scenery.location)
            }

            // Set ID
            10 -> {
                sendInputDialogue(player, InputType.STRING_SHORT, "Enter scenery id") { value ->
                    selectedId = value as Int
                    sendChat(player, "ID set to: $selectedId")
                    menu()
                }
            }

            11 -> {
                showTopics(
                    Topic("My location", 10),
                    Topic("Set Location", 11),
                )
                selectedLocation = player.location
                sendChat(player, "Location set to: $selectedLocation")
                menu()
            }

            // Rotation
            12 -> {
                sendInputDialogue(player, InputType.STRING_SHORT, "Enter rotation (0-7)") { value ->
                    val rot = value as Int ?: 0

                    if (rot == null || rot !in 0..7) {
                        sendChat(player!!, "Rotation must be between 0 and 7.")
                        return@sendInputDialogue
                    }

                    rotation = rot
                    menu()
                }
            }

            // Type
            13 -> {
                sendInputDialogue(player, InputType.STRING_SHORT, "Enter type") { value ->
                    type = value as Int ?: 10
                    menu()
                }
            }

            20 -> {
                val id = selectedId
                val loc = selectedLocation

                if (id == null || loc == null) {
                    sendMessage(player, "Missing id or location.")
                    return true
                }

                addScenery(id, loc, rotation, type)
                sendMessage(player, colorize("%GSpawned object: $id"))

                end()
            }

        }
        return true
    }

    private fun menu() {
        showTopics(
            Topic("Set ID [${selectedId ?: "not set"}]", 10),
            Topic("Set Location [${selectedLocation ?: "not set"}]", 11),
            Topic("Set Rotation [$rotation]", 12),
            Topic("Set Type [$type]", 13),
            IfTopic("Spawn Scenery", 20, selectedId != null && selectedLocation != null)
        )
    }

    override fun getIds(): IntArray = intArrayOf(ID)
}