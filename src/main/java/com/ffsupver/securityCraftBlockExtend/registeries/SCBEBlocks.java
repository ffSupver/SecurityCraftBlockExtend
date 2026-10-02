package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SCBEBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SecurityCraftBlockExtend.MODID);
    /** 所有需要"强化深色色调"的方块，注册时自动加入 */
    private static final List<Supplier<? extends Block>> REINFORCED_TINT_BLOCKS = new ArrayList<>();

    public static RegistryObject<BaseReinforcedBlock> REINFORCED_WET_SPONGE = reinforcedBlock("reinforced_wet_sponge",Blocks.WET_SPONGE);
    public static RegistryObject<BaseReinforcedBlock> REINFORCED_REINFORCED_DEEPSLATE = reinforcedBlock("reinforced_reinforced_deepslate", Blocks.REINFORCED_DEEPSLATE);


    public static void register(IEventBus modEventBus){
        BLOCKS.register(modEventBus);
    }

    private static BlockBehaviour.Properties ofFullCopy(BlockBehaviour blockBehaviour) {
        BlockBehaviour.Properties copy = BlockBehaviour.Properties.copy(blockBehaviour);
        BlockBehaviour.Properties original = blockBehaviour.properties;
        copy.jumpFactor = original.jumpFactor;
        copy.isRedstoneConductor = original.isRedstoneConductor;
        copy.isValidSpawn = original.isValidSpawn;
        copy.hasPostProcess = original.hasPostProcess;
        copy.isSuffocating = original.isSuffocating;
        copy.isViewBlocking = original.isViewBlocking;
        copy.drops = original.drops;
        return copy;
    }


    public static final BlockBehaviour.Properties reinforcedCopy(Block block, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
        return (BlockBehaviour.Properties)propertyEditor.apply(ofFullCopy(block).explosionResistance(Float.MAX_VALUE));
    }

    public static RegistryObject<BaseReinforcedBlock> reinforcedBlock(String name, Block vanillaBlock) {
        return reinforcedBlock(name, vanillaBlock, BLOCKS);
    }

    public static RegistryObject<BaseReinforcedBlock> reinforcedBlock(String name, RegistryObject<Block> vanillaBlock, DeferredRegister<Block> registry) {
        return reinforcedBlock(name, vanillaBlock.get(), registry);
    }
    public static RegistryObject<BaseReinforcedBlock> reinforcedBlock(String name, Block vanillaBlock, DeferredRegister<Block> registry) {
        return reinforcedBlock(name, vanillaBlock, UnaryOperator.identity(), registry);
    }

    public static RegistryObject<BaseReinforcedBlock> reinforcedBlock(
            String name, Block vanillaBlock,
            UnaryOperator<BlockBehaviour.Properties> propertyEditor,
            DeferredRegister<Block> registry
            ) {
        return registerReinforcedBlock(name, (p) -> new BaseReinforcedBlock(p, vanillaBlock),
                reinforcedCopy(vanillaBlock, propertyEditor),registry);
    }

    private static <B extends Block> RegistryObject<B> registerReinforcedBlock(
            String name,
            Function<BlockBehaviour.Properties, ? extends B> constructor,
            BlockBehaviour.Properties properties,
            DeferredRegister<Block> registry
            ) {

        RegistryObject<B> reg = registerBlock(name, constructor, properties,registry);
        REINFORCED_TINT_BLOCKS.add(reg);
        return reg;
    }

    private static <B extends Block> RegistryObject<B> registerBlock(
            String name,
            Function<BlockBehaviour.Properties, ? extends B> constructor,
            BlockBehaviour.Properties properties,
            DeferredRegister<Block> registry
            ) {
        Supplier<Block> block = () -> constructor.apply(properties);
        @SuppressWarnings("unchecked")
        RegistryObject<B> reg = (RegistryObject<B>) registry.register(name, block);
        SCBEItems.ITEMS.register(name, () -> new BlockItem(reg.get(), new Item.Properties()));
        return reg;
    }

    public static void registerReinforcedTintBlock(Supplier<? extends Block> block){
        REINFORCED_TINT_BLOCKS.add(block);
    }

    public static List<Supplier<? extends Block>> getReinforcedTintBlocks() {
        return Collections.unmodifiableList(REINFORCED_TINT_BLOCKS);
    }
}
