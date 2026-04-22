package org.valkyrienskies.valkyrien_depths.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

object FloodgateBlock : Block(Properties.copy(Blocks.COPPER_BLOCK)) {
    val openedState = BlockStateProperties.OPEN
    val waterLogged = BlockStateProperties.WATERLOGGED

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(openedState, false).setValue(waterLogged, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.OPEN)
        builder.add(BlockStateProperties.WATERLOGGED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState()
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        val newState = state.cycle<Boolean?>(TrapDoorBlock.OPEN) as BlockState
        level.setBlock(pos, newState, 2)
        if (newState.getValue<Boolean?>(TrapDoorBlock.WATERLOGGED) as Boolean) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        if (state.getValue(openedState)) {
            return Shapes.empty()
        }
        return super.getCollisionShape(state, level, pos, context)
    }
}