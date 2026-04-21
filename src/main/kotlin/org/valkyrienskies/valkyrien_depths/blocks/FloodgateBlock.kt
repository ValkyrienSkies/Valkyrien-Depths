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
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
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
        if (player.getItemInHand(hand).isEmpty) {
            if (!level.isClientSide) {
                val lastOpen = state.getValue(openedState)
                state.setValue(openedState, !lastOpen)
                level.setBlock(pos, state, 10)
                level.blockUpdated(pos, state.block)
                return InteractionResult.SUCCESS
            }
        }
        return super.use(state, level, pos, player, hand, hit)
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