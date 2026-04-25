package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.PacketHeader
import core.net.packet.context.SceneryUpdateContext

class LocAddChangePacket : OutgoingPacket<SceneryUpdateContext> {

    override fun send(ctx: SceneryUpdateContext) {
        val player = ctx.player
        val buffer = IoBuffer(202, PacketHeader.NORMAL)

        buffer.cypherOpcode(player.session.isaacPair.output)
        val packed = (ctx.type shl 2) or (ctx.rotation and 0x3)
        buffer.p1(packed)
        val base = player.playerFlags.lastSceneGraph
        val localX = ctx.x - base.x
        val localY = ctx.y - base.y
        val tile = ((localX and 7) shl 4) or (localY and 7)
        buffer.p1(tile)

        buffer.p1add(ctx.int0)
        buffer.p1add(ctx.int1)
        buffer.p1sub(ctx.int2)

        buffer.p2add(ctx.objectId)

        buffer.ip2(ctx.int4)
        buffer.p1(ctx.int3)
        buffer.p2(ctx.int5)
        buffer.ip2add(ctx.int6)

        player.session.write(buffer)
    }
}