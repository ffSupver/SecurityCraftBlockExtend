package com.ffsupver.securityCraftBlockExtend;

import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockStateProvider;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBELootTableProvider;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEItems;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBETabs;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SecurityCraftBlockExtend.MODID)
public class SecurityCraftBlockExtend
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "securitycraft_block_extend";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public SecurityCraftBlockExtend(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();


        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(SecurityCraftBlockExtend::gatherData);

        SCBEItems.register(modEventBus);
        SCBEBlocks.register(modEventBus);
        SCBETabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        //common setup
//        LOGGER.info("HELLO FROM COMMON SETUP");

//        if (Config.logDirtBlock)
//            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        System.out.println("gatherData");

        DataGenerator gen = event.getGenerator();
        ExistingFileHelper efh = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(),
                new SCBEBlockStateProvider(gen.getPackOutput(), efh));
        gen.addProvider(event.includeServer(),
                new SCBELootTableProvider(gen.getPackOutput()));
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
//        @SubscribeEvent
//        public static void onClientSetup(FMLClientSetupEvent event)
//        {
//            // client setup
//            LOGGER.info("HELLO FROM CLIENT SETUP");
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
//        }

//        @SubscribeEvent
//        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
//            event.register((state, level, pos, tintIndex) -> {
//                if (tintIndex == 0) {
//                    // 基础色调：默认白色，如果你的方块有自定义基础色，改成对应值
//                    int baseTint = 0xFFFFFF;
//
//                    // 获取 IOwnable，用于按所有者调整色调
//                    IOwnable ownable = null;
//                    if (level != null && pos != null) {
//                        BlockEntity be = level.getBlockEntity(pos);
//                        if (be instanceof IOwnable o) {
//                            ownable = o;
//                        }
//                    }
//
//                    // 复用 SecurityCraft 的深色混合逻辑
//                    return ClientHandler.mixWithReinforcedTintIfEnabled(baseTint, ownable);
//                }
//                return 0xFFFFFF;
//            }, SCBEBlocks.REINFORCED_REINFORCED_DEEPSLATE.get());
//        }
//
//        @SubscribeEvent
//        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
//            event.register((stack, tintIndex) -> {
//                if (tintIndex == 0) {
//                    return ClientHandler.mixWithReinforcedTintIfEnabled(0xFFFFFF, null);
//                }
//                return 0xFFFFFF;
//            }, SCBEBlocks.REINFORCED_WET_SPONGE.get());
//        }
    }
}
