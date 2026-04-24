package org.valkyrienskies.valkyrien_depths

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import org.valkyrienskies.mod.api.vsApi
import org.valkyrienskies.valkyrien_depths.blockentities.RudderBlockEntity
import org.valkyrienskies.valkyrien_depths.blocks.FloodgateBlock
import org.valkyrienskies.valkyrien_depths.blocks.RudderBlock
import org.valkyrienskies.valkyrien_depths.client.ValkyrienDepthsModClient
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("valkyrien_depths")
object ValkyrienDepthsMod {
    const val MOD_ID = "valkyrien_depths"

    //Deferred Registries
    private val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
    private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)
    private val ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID)
    private val BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID)

    // Put RegistryObjects here:

    private val FLOODGATE_BLOCK = registerBlockAndItem("floodgate", { FloodgateBlock })
    private val RUDDER_BLOCK = registerBlockAndItem("rudder", { RudderBlock })

    public val RUDDER_BLOCK_ENTITY = BLOCK_ENTITIES.register("rudder") { BlockEntityType.Builder.of<RudderBlockEntity>(::RudderBlockEntity,
        RudderBlock).build(null) }

    // end of RegistryObjects

    init {
        MOD_BUS.addListener(::init)
        if (FMLEnvironment.dist.isClient) {
            MOD_BUS.addListener(ValkyrienDepthsModClient.Companion::clientInit)
        }

        BLOCKS.register(MOD_BUS)
        ITEMS.register(MOD_BUS)
        BLOCK_ENTITIES.register(MOD_BUS)
    }

    // Helper function, taken from VS2.
    private fun registerBlockAndItem(registryName: String, blockSupplier: () -> Block): RegistryObject<Block> {
        val blockRegistry = BLOCKS.register(registryName, blockSupplier)
        ITEMS.register(registryName) { BlockItem(blockRegistry.get(), Item.Properties()) }
        return blockRegistry
    }

    @JvmStatic
    fun init (event: FMLCommonSetupEvent) {
        // Put anything initialized on forge-side here.

    }
}
