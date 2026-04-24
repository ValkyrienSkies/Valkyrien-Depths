package org.valkyrienskies.valkyrien_depths.blockentities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.DirectionalBlock.FACING
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.concurrent.Volatile
import kotlin.math.abs
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.mod.api.toJOMLd
import org.valkyrienskies.valkyrien_depths.ValkyrienDepthsMod
import kotlin.math.absoluteValue

class RudderBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ValkyrienDepthsMod.RUDDER_BLOCK_ENTITY.get(), pos,
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

        val neutralFinDirection = facing.normal.toJOMLd()
        if (neutralFinDirection.lengthSquared() < MIN_DIRECTION_LENGTH_SQUARED) return

        val rudderFinDirection = getRudderFacingDirection()
        if (rudderFinDirection.lengthSquared() < MIN_DIRECTION_LENGTH_SQUARED) return

        val neutralFinModel = Vector3d(neutralFinDirection).normalize()
        val rudderFinModel = Vector3d(rudderFinDirection).normalize()
        val rudderSideModel = neutralFinModel.cross(MODEL_UP, Vector3d()).normalize()
        if (rudderSideModel.lengthSquared() < MIN_DIRECTION_LENGTH_SQUARED) return

        val rudderDeflection = rudderFinModel.dot(rudderSideModel)
        if (abs(rudderDeflection) < MIN_DEFLECTION) return

        val rudderModelPos = this.worldPosition.toJOMLd()
        val rudderWorldPos = physShip.transform.toWorld.transformPosition(Vector3d(rudderModelPos))
        val centerOfMassWorldPos = Vector3d(physShip.transform.position)
        val rudderOffset = rudderWorldPos.sub(centerOfMassWorldPos, Vector3d())

        val rudderVelocity = Vector3d(physShip.angularVelocity).cross(rudderOffset).add(physShip.velocity)
        val speedSquared = rudderVelocity.lengthSquared()
        if (speedSquared < MIN_FLOW_SPEED_SQUARED) return

        val neutralFinWorld = physShip.transform.rotation.transform(neutralFinModel, Vector3d())
        val rudderSideWorld = physShip.transform.rotation.transform(rudderSideModel, Vector3d())
        val forwardFlowSpeed = -rudderVelocity.dot(neutralFinWorld)
        if (abs(forwardFlowSpeed) < MIN_FORWARD_FLOW_SPEED) return

        val forceMagnitude = (forwardFlowSpeed * abs(forwardFlowSpeed) * rudderDeflection * RUDDER_FORCE_COEFFICIENT)
            .coerceIn(-MAX_RUDDER_FORCE, MAX_RUDDER_FORCE)
        val rudderForce = rudderSideWorld.mul(forceMagnitude, Vector3d())

        physShip.applyWorldForceToModelPos(rudderForce, rudderModelPos)
    }

    fun tick() {
        this.power = this.getPowerMagnitude()
    }

    private fun getRudderFacingDirection(): Vector3d {
        val positive = getPowerDirection().normal.toJOMLd()
        val negative = getPowerDirection().opposite.normal.toJOMLd()
        val middle = facing.normal.toJOMLd()
        val absolutePower = power.absoluteValue/15.0

        if (power > 0) {
            middle.lerp(positive, absolutePower)
        } else if (power < 0) {
            middle.lerp(negative, absolutePower)
        }

        return Vector3d(middle)
    }

    private fun getPowerDirection(): Direction {
        if (facing.axis == Direction.Axis.Y) return Direction.NORTH
        return if (facing.axisDirection == Direction.AxisDirection.POSITIVE) {
            facing.clockWise
        } else {
            facing.counterClockWise
        }
    }

    fun getPowerMagnitude(): Int {
        var power = 0

        val powerDirection = getPowerDirection()

        power += level!!.getSignal(blockPos.relative(powerDirection), powerDirection)
        power -= level!!.getSignal(blockPos.relative(powerDirection.opposite), powerDirection.opposite)

        return power
    }

    companion object {
        private val MODEL_UP = Vector3d(0.0, 1.0, 0.0)
        private const val RUDDER_FORCE_COEFFICIENT = 600.0
        private const val MAX_RUDDER_FORCE = 25_000.0
        private const val MIN_DIRECTION_LENGTH_SQUARED = 1.0e-6
        private const val MIN_FLOW_SPEED_SQUARED = 1.0e-4
        private const val MIN_FORWARD_FLOW_SPEED = 1.0e-3
        private const val MIN_DEFLECTION = 1.0e-3
    }
}
