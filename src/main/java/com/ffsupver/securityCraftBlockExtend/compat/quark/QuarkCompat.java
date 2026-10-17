package com.ffsupver.securityCraftBlockExtend.compat.quark;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.compat.SCBEModCompat;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.violetmoon.quark.content.building.block.QuarkVerticalSlabBlock;
import org.violetmoon.quark.content.world.block.MyaliteColorLogic;
import org.violetmoon.zeta.block.ZetaStairsBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class QuarkCompat implements SCBEModCompat {
    protected static final DeferredRegister<Block> BLOCKS = SCBEBlocks.BLOCKS;
    public static final Map<Block,RegistryObject<? extends Block>> QUARK_REINFORCED_BLOCKS = new HashMap<>();
    private boolean populated = false;

    private final List<Supplier<ItemLike>> creativeTabItems = new ArrayList<>();
    private final List<SCBEBlockModelData> modelDataList = new ArrayList<>();
    private final List<SCBEBlockLootData> lootTableProviders = new ArrayList<>();
    private final List<SCBEBlockTagData> blockTagDataList = new ArrayList<>();

    private static final SCBEBlocks.ColorProcessor MYALITE_PROCESSOR =
            new SCBEBlocks.ColorProcessor() {
                @Override
                public int process(BlockState state, BlockAndTintGetter level, BlockPos pos,
                                   int tintIndex, int currentTint) {
                    BlockPos colorPos = pos;
                    if (colorPos == null) {
                        Minecraft mc = Minecraft.getInstance();
                        colorPos = BlockPos.ZERO;
                        if (mc.player != null) {
                            colorPos = mc.player.blockPosition();
                            HitResult hit = mc.hitResult;
                            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                                colorPos = ((BlockHitResult) hit).getBlockPos();
                            }
                        }
                    }
                    int myaliteTint = MyaliteColorLogic.getColor(colorPos);
                    return FastColor.ARGB32.multiply(currentTint, myaliteTint);
                }

                @Override
                public int processItem(ItemStack stack, int tintIndex, int currentTint) {
                    Minecraft mc = Minecraft.getInstance();
                    BlockPos pos = BlockPos.ZERO;
                    if (mc.player != null) {
                        pos = mc.player.blockPosition();
                        HitResult hit = mc.hitResult;
                        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                            pos = ((BlockHitResult) hit).getBlockPos();
                        }
                    }
                    int myaliteTint = MyaliteColorLogic.getColor(pos);
                    return FastColor.ARGB32.multiply(currentTint, myaliteTint);
                }
            };

    @Override
    public String getModId() {
        return Mods.ModIds.QUARK.ModId;
    }

    @Override
    public void init(IEventBus eventBus) {
        eventBus.addListener(EventPriority.HIGH,this::onRegister);
//        MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdated);
    }

    private void onRegister(RegisterEvent event) {
        if (populated) return;
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) return;
        populated = true;

        System.out.println("OnRegister QuarkCompat:\n"+ QuarkBlocksCapture.QUARK_BLOCKS.size());
        for (Map.Entry<ResourceLocation, Block> bE : QuarkBlocksCapture.QUARK_BLOCKS.entrySet()){
            Block block = bE.getValue();
            ResourceLocation originalId = bE.getKey();
            String name = "reinforced_" + originalId.getPath();
            if (block instanceof QuarkVerticalSlabBlock quarkVerticalSlabBlock){
                Block parent = quarkVerticalSlabBlock.parent.get();
                ResourceLocation parentId = ForgeRegistries.BLOCKS.getKey(parent);
                if (parentId == null) continue;

                System.out.println("OnRegister QuarkCompat VerticalSlabBlock: "+originalId+" parent: "+parentId+ " parentBlock: "+parent+" c:"+parent.getClass().getName());

                RegistryObject<ReinforcedVerticalSlab> reg = SCBEBlocks.registerBlock(
                        name,
                        p->new ReinforcedVerticalSlab(p, block),
                        ()->SCBEBlocks.reinforcedCopy(block, UnaryOperator.identity()),
                        BLOCKS
                );
                SCBEBlockTagData.Builder builder = new SCBEBlockTagData.Builder(reg);

                System.out.println(name+"contains"+QuarkBlockTags.BLOCK_TAGS.containsKey(originalId.getPath())+" pPath:"+originalId.getPath());
                if (QuarkBlockTags.BLOCK_TAGS.containsKey(originalId.getPath())) {
                    QuarkBlockTags.BLOCK_TAGS.get(originalId.getPath()).forEach(builder::add);
                }


                modelDataList.add(new SCBEBlockModelData(
                        name,
                        reg,
                        ResourceLocation.tryBuild("quark", "block/vertical_slab"),
                        Map.of("parentId", originalId),
                        null,
                        SCBEBlockModelData.BlockType.VERTICAL_SLAB,
                        null
                ));
                creativeTabItems.add(reg::get);
                lootTableProviders.add(new SCBEBlockLootData(reg,SCBEBlockLootData.BlockLootType.VERTICAL_SLAB));
                SCBEBlocks.registerReinforcedBlockMapping(reg,reg);
                SCBEBlocks.registerReinforcedTintBlock(reg);
                if (originalId.getPath().contains("myalite")){ // 幻境石需要特殊处理
                    SCBEBlocks.REINFORCED_TINT_BLOCK_PROCESSORS.put(reg,MYALITE_PROCESSOR);
                }
                blockTagDataList.add(builder.reinforced().build());
                QUARK_REINFORCED_BLOCKS.put(block, reg);
            }else if (block instanceof ZetaStairsBlock zetaStairsBlock){
                RegistryObject<ReinforcedStairsBlock> reg = SCBEBlocks.registerBlock(
                        name,
                        p->new ReinforcedStairsBlock(p, block),
                        ()->SCBEBlocks.reinforcedCopy(block, UnaryOperator.identity()),
                        BLOCKS
                );
                SCBEBlockTagData.Builder builder = new SCBEBlockTagData.Builder(reg);
                if (QuarkBlockTags.BLOCK_TAGS.containsKey(originalId.getPath())) {
                    QuarkBlockTags.BLOCK_TAGS.get(originalId.getPath()).forEach(builder::add);
                }
                modelDataList.add(new SCBEBlockModelData(
                        name,
                        reg,
                        ResourceLocation.tryBuild(SecurityCraft.MODID, "block/reinforced_stairs"), // 不使用这个参数
                        Map.of("parentId", originalId),
                        null,
                        SCBEBlockModelData.BlockType.STAIRS,
                        null
                ));
                creativeTabItems.add(reg::get);
                lootTableProviders.add(SCBEBlockLootData.dropSelf(reg));
                SCBEBlocks.registerReinforcedBlockMapping(reg,reg);
                SCBEBlocks.registerReinforcedTintBlock(reg);
                if (originalId.getPath().contains("myalite")){ // 幻境石需要特殊处理
                    SCBEBlocks.REINFORCED_TINT_BLOCK_PROCESSORS.put(reg,MYALITE_PROCESSOR);
                }
                blockTagDataList.add(builder.reinforced().build());
                QUARK_REINFORCED_BLOCKS.put(block, reg);
            }
        }
    }

    @Override
    public List<SCBEBlockModelData> getBlockModelData() {
        return modelDataList;
    }

    @Override
    public List<Supplier<ItemLike>> getCreativeTabItems() {
        return creativeTabItems;
    }

    @Override
    public List<SCBEBlockLootData> getBlockLootData() {
        return lootTableProviders;
    }

    @Override
    public List<SCBEBlockTagData> getBlockTagData() {
        return blockTagDataList;
    }
}
