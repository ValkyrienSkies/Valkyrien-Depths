package org.valkyrienskies.valkyrien_depths.blockentities

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.valkyrien_depths.ValkyrienDepthsMod
import kotlin.concurrent.Volatile

class RudderBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ValkyrienDepthsMod.RUDDER_BLOCK_ENTITY.get(), pos,
    blockState
), BlockEntityPhysicsListener {
    @Volatile
    override lateinit var dimension: DimensionId
    override fun physTick(
        physShip: PhysShip?,
        physLevel: PhysLevel
    ) {

    }

}