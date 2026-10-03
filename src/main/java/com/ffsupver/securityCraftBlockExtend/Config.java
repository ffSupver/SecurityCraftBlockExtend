package com.ffsupver.securityCraftBlockExtend;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SecurityCraftBlockExtend.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();


    public static final ForgeConfigSpec.IntValue CHAINED_REINFORCED_MAX_RANGE = BUILDER
            .comment("Maximum range of Chained Reinforcer. Must be between 2 and 64.")
            .defineInRange("chained_reinforcer_max_range", 32, 2, 64);

    public static final ForgeConfigSpec.IntValue CHAINED_REINFORCED_MAX_BLOCKS = BUILDER
            .comment("Maximum block count of Chained Reinforcer. Must be between 100 and 10000.")
            .defineInRange("chained_reinforcer_max_blocks", 1000, 100, 10000);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int chainedReinforcerMaxRange = 32;
    public static int chainedReinforcerMaxBlocks = 1000;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        // 只在加载/重载本模组的配置时更新缓存
        if (event.getConfig().getSpec() == SPEC) {
            chainedReinforcerMaxRange = CHAINED_REINFORCED_MAX_RANGE.get();
            chainedReinforcerMaxBlocks = CHAINED_REINFORCED_MAX_BLOCKS.get();
        }
    }
}
