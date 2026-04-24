package org.valkyrienskies.valkyrien_depths.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.DirectionalBlock.FACING
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.valkyrien_depths.blockentities.ScrewPropellerBlockEntity

object ScrewPropellerBlock : DirectionalBlock(Properties.copy(Blocks.COPPER_BLOCK)), EntityBlock {
    val waterLogged = BlockStateProperties.WATERLOGGED

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(waterLogged, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.FACING)
        builder.add(BlockStateProperties.WATERLOGGED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(FACING, context.clickedFace)
    }

    override fun newBlockEntity(
        p0: BlockPos,
        p1: BlockState
    ): BlockEntity? {
        return ScrewPropellerBlockEntity(p0, p1)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { level, pos, state, blockEntity ->
        if (level.isClientSide) return@BlockEntityTicker
        if (blockEntity is ScrewPropellerBlockEntity) {
            blockEntity.tick()
        }
    }
}
