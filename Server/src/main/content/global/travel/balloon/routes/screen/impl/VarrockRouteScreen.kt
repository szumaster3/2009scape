package content.global.travel.balloon.routes.screen.impl

import core.api.sendAngleOnInterface
import core.api.sendAnimationOnInterface
import core.api.sendModelOnInterface
import core.game.node.entity.player.Player

object VarrockRouteScreen {

    fun firstStage(p: Player, c: Int) {
        // Floor
        sendModelOnInterface(p,c,40,19731)
        sendModelOnInterface(p,c,45,19735)
        sendModelOnInterface(p,c,50,19736)
        sendModelOnInterface(p,c,55,19737)

        // Landing base
        sendModelOnInterface(p,c,99,19572)

        // Mountain peak
        sendModelOnInterface(p,c,78,19607)
        sendModelOnInterface(p,c,79,19611)

        // Evergreen
        sendModelOnInterface(p,c,120,19570) // Tree crown
        sendModelOnInterface(p,c,100,19569) // Tree branch
        sendModelOnInterface(p,c,80,19568)  // Tree trunk

        sendModelOnInterface(p,c,83,19616)  // Stone

        sendModelOnInterface(p,c,86,19616)  // Stone

        // Dead tree
        sendModelOnInterface(p,c,127,19529)  // Tree crown
        sendModelOnInterface(p,c,107,19528)  // Tree branch
        sendModelOnInterface(p,c,87,19523)  // Tree base

        sendModelOnInterface(p,c,108,19530)  // Tree
        sendModelOnInterface(p,c,88,19523)  // Tree

        // Dead tree
        sendModelOnInterface(p,c,129,19529)  // Tree crown
        sendModelOnInterface(p,c,109,19528)  // Tree branch
        sendModelOnInterface(p,c,89,19523)  // Tree base

        sendModelOnInterface(p,c,110,19530)  // Tree
        sendModelOnInterface(p,c,90,19523)  // Tree

        // Dead tree
        sendModelOnInterface(p,c,131,19529)  // Tree crown
        sendModelOnInterface(p,c,111,19528)  // Tree branch
        sendModelOnInterface(p,c,91,19523)  // Tree base

        // Small trees.
        sendModelOnInterface(p,c,113,19521) // Tree top
        sendModelOnInterface(p,c,93,19519)  // Tree base

        // Small trees.
        sendModelOnInterface(p,c,114,19521) // Tree top
        sendModelOnInterface(p,c,94,19519)  // Tree base


        // Evergreen
        sendModelOnInterface(p,c,135,19570) // Tree crown
        sendModelOnInterface(p,c,115,19569) // Tree branch
        sendModelOnInterface(p,c,95,19568)  // Tree trunk


        // Evergreen
        sendModelOnInterface(p,c,137,19570) // Tree crown
        sendModelOnInterface(p,c,117,19569) // Tree branch
        sendModelOnInterface(p,c,97,19568)  // Tree trunk

        sendModelOnInterface(p,c,122,19779)
        sendAngleOnInterface(p,c,122,2100,200,300)
        sendAnimationOnInterface(p,341,c,122)

        sendModelOnInterface(p,c,161,19779)
        sendAngleOnInterface(p,c,161,2100,200,300)
        sendAnimationOnInterface(p,341,c,161)

        sendModelOnInterface(p,c,163,19779)
        sendAngleOnInterface(p,c,163,2100,200,300)
        sendAnimationOnInterface(p,341,c,163)

        sendModelOnInterface(p,c,182,19779)
        sendAngleOnInterface(p,c,182,2100,200,300)
        sendAnimationOnInterface(p,341,c,182)

        sendModelOnInterface(p,c,170,19779)
        sendAngleOnInterface(p,c,170,2100,200,300)
        sendAnimationOnInterface(p,341,c,170)

        sendModelOnInterface(p,c,174,19779)
        sendAngleOnInterface(p,c,174,2100,200,300)
        sendAnimationOnInterface(p,341,c,174)

        sendModelOnInterface(p,c,157,19779)
        sendAngleOnInterface(p,c,157,2100,200,300)
        sendAnimationOnInterface(p,341,c,157)

        sendModelOnInterface(p,c,216,19779)
        sendAngleOnInterface(p,c,216,2100,200,300)
        sendAnimationOnInterface(p,341,c,216)

        sendModelOnInterface(p,c,233,19779)
        sendAngleOnInterface(p,c,233,2100,200,300)
        sendAnimationOnInterface(p,341,c,233)

        // Clouds
        sendModelOnInterface(p,c,198,19525) // Left
        sendModelOnInterface(p,c,199,19524) // Center
        sendModelOnInterface(p,c,200,19526) // Right

        sendModelOnInterface(p,c,144,19525) // Left
        sendModelOnInterface(p,c,145,19524) // Center
        sendModelOnInterface(p,c,146,19526) // Right

        sendModelOnInterface(p,c,205,19525) // Left
        sendModelOnInterface(p,c,206,19524) // Center
        sendModelOnInterface(p,c,207,19526) // Right

        sendModelOnInterface(p,c,187,19525) // Left
        sendModelOnInterface(p,c,188,19524) // Center
        sendModelOnInterface(p,c,189,19526) // Right

        sendModelOnInterface(p,c,192,19525) // Left
        sendModelOnInterface(p,c,193,19524) // Center
        sendModelOnInterface(p,c,194,19526) // Right

    }

    fun secondStage(p: Player, c: Int) {
        // Floor.
        sendModelOnInterface(p,c,40,19738)
        sendModelOnInterface(p,c,45,19739)
        sendModelOnInterface(p,c,50,19740)
        sendModelOnInterface(p,c,55,19741)
    }

    fun thirdStage(p: Player, c: Int) {
        // Floor
        sendModelOnInterface(p,c,40,19742)
        sendModelOnInterface(p,c,45,19732)
        sendModelOnInterface(p,c,50,19733)
        sendModelOnInterface(p,c,55,19734)
    }
}