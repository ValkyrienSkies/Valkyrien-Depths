package org.valkyrienskies.valkyrien_depths.blockentities

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.DirectionalBlock.FACING
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.mod.api.toJOMLd
import org.valkyrienskies.valkyrien_depths.ValkyrienDepthsMod
import kotlin.concurrent.Volatile

class ScrewPropellerBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ValkyrienDepthsMod.SCREW_PROPELLER_BLOCK_ENTITY.get(), pos,
    blockState
), BlockEntityPhysicsListener {
    @Volatile
    override lateinit var dimension: DimensionId

    @Volatile
    var powered = false

    @Volatile
    var facing = blockState.getValue(FACING)

    override fun physTick(
        physShip: PhysShip?,
        physLevel: PhysLevel
    ) {
        if (physShip == null) return
        if (physShip.liquidOverlap <= 0.0) return
        if (!powered) return

        val propellerDirection = facing.normal.toJOMLd()
        if (propellerDirection.lengthSquared() < MIN_DIRECTION_LENGTH_SQUARED) return

        val thrustDirectionWorld = physShip.transform.rotation.transform(
            Vector3d(propellerDirection).normalize().negate(),
            Vector3d()
        )
        val propellerModelPos = worldPosition.toJOMLd().add(0.5, 0.5, 0.5)
        val thrustForce = thrustDirectionWorld.mul(THRUST_FORCE, Vector3d())

        physShip.applyWorldForceToModelPos(thrustForce, propellerModelPos)
    }

    fun tick() {
        facing = blockState.getValue(FACING)
        powered = level?.hasNeighborSignal(blockPos) == true
    }

    companion object {
        private const val THRUST_FORCE = 15_000.0
        private const val MIN_DIRECTION_LENGTH_SQUARED = 1.0e-6
    }
}
