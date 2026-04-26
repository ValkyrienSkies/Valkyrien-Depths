package org.valkyrienskies.valkyrien_depths.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

object FloodgateBlock : Block(Properties.copy(Blocks.COPPER_BLOCK).noOcclusion()), SimpleWaterloggedBlock {
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
        val fluidState = context.level.getFluidState(context.clickedPos)
        return this.defaultBlockState().setValue(waterLogged, fluidState.type == Fluids.WATER)
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        var newState = state.cycle<Boolean?>(openedState) as BlockState
        if (!newState.getValue(openedState)) newState = newState.setValue(waterLogged, false)
        level.setBlock(pos, newState, 2)
        if (newState.getValue(waterLogged) && !newState.getValue(openedState)) {
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

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(waterLogged)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(waterLogged)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun canPlaceLiquid(
        getter: BlockGetter,
        pos: BlockPos,
        state: BlockState,
        fluid: Fluid
    ): Boolean {
        return state.getValue(openedState) && !state.getValue(waterLogged) && fluid == Fluids.WATER
    }

    override fun placeLiquid(level: LevelAccessor, pos: BlockPos, state: BlockState, fluidState: FluidState): Boolean {
        if (!state.getValue(openedState) || state.getValue(waterLogged) || fluidState.type != Fluids.WATER) {
            return false
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(waterLogged, true), 3)
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        return true
    }

    override fun canBeReplaced(state: BlockState, fluid: Fluid): Boolean {
        return false
    }

    override fun isCollisionShapeFullBlock(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return true
    }
}
