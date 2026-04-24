package org.valkyrienskies.valkyrien_depths.blockentities

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.DirectionalBlock.FACING
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.valkyrien_depths.ValkyrienDepthsMod
import kotlin.concurrent.Volatile

class ScrewPropellerBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ValkyrienDepthsMod.SCREW_PROPELLER_BLOCK_ENTITY.get(), pos,
    blockState
), BlockEntityPhysicsListener {
    @Volatile
    override lateinit var dimension: DimensionId

    @Volatile
    var power : Int = 0

    @Volatile
    var facing = blockState.getValue(FACING)

    override fun physTick(
        physShip: PhysShip?,
        physLevel: PhysLevel
    ) {
        if (physShip == null) return
        if (physShip.liquidOverlap <= 0.0) return
    }
}