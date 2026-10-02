package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SCBEBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SecurityCraftBlockExtend.MODID);
    /** 所有需要"强化深色色调"的方块，注册时自动加入 */
    private static final List<Supplier<? extends Block>> REINFORCED_TINT_BLOCKS = new ArrayList<>();
    private static final Map<Supplier<? extends IReinforcedBlock>,Supplier<? extends Block>> REINFORCED_BLOCKS = new HashMap<>();

    public static RegistryObject<BaseReinforcedBlock> REINFORCED_WET_SPONGE = reinforcedBlock("reinforced_wet_sponge",Blocks.WET_SPONGE);
    public static RegistryObject<BaseReinforcedBlock> REINFORCED_REINFORCED_DEEPSLATE = reinforcedBlock("reinforced_reinforced_deepslate", Blocks.REINFORCED_DEEPSLATE);


    public static void register(IEventBus modEventBus){
        BLOCKS.register(modEventBus);

        modEventBus.addListener(SCBEBlocks::onReinforcedBlockRegister);
    }
    public static void onReinforcedBlockRegister(FMLCommonSetupEvent event){
        REINFORCED_BLOCKS.forEach((iRS,block)->{
            IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(iRS.get().getVanillaBlock(), block.get());
            IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.put(block.get(), iRS.get().getVanillaBlock());
        });
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
//        copy.requiresCorrectToolForDrops = original.requiresCorrectToolForDrops;  // 原模组未复制这个
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
        RegistryObject<BaseReinforcedBlock> reg = registerReinforcedBlockMapping(name, (p) -> new BaseReinforcedBlock(p, vanillaBlock),
                reinforcedCopy(vanillaBlock, propertyEditor),registry);
        registerReinforcedBlockMapping(reg,reg);
        return reg;
    }

    private static <B extends Block> RegistryObject<B> registerReinforcedBlockMapping(
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

    public static void registerReinforcedBlockMapping(Supplier<? extends IReinforcedBlock> block, Supplier<? extends Block> blockSupplier){
        REINFORCED_BLOCKS.put(block, blockSupplier);
    }



    public static List<Supplier<? extends Block>> getReinforcedTintBlocks() {
        return Collections.unmodifiableList(REINFORCED_TINT_BLOCKS);
    }
}
