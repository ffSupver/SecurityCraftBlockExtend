package com.ffsupver.securityCraftBlockExtend.dataGen;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.flanks255.simplylight.blocks.LampBlock;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackShape;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class SCBEBlockStateProvider extends BlockStateProvider {

    public SCBEBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SecurityCraftBlockExtend.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        reinforcedCubeBottomTop(
                "reinforced_reinforced_deepslate",
                SCBEBlocks.REINFORCED_REINFORCED_DEEPSLATE,
                mcLoc("block/reinforced_deepslate_bottom"),
                mcLoc("block/reinforced_deepslate_top"),
                mcLoc("block/reinforced_deepslate_side"),
                null
        );

         reinforcedCubeAll(
                 "reinforced_wet_sponge",
                 SCBEBlocks.REINFORCED_WET_SPONGE,
                 mcLoc("block/wet_sponge"),
                 null
         );

        // 兼容模块方块：统一由 Mods 收集，未加载模块不会返回数据
        Map<Block, List<SCBEBlockModelData>> simpleGroups = new LinkedHashMap<>();

        for (SCBEBlockModelData data : Mods.collectBlockModelData()) {
            switch (data.type()) {
                case SIMPLE -> simpleGroups
                        .computeIfAbsent(data.block().get(), k -> new ArrayList<>())
                        .add(data);
                case STAIRS -> reinforcedStairs(data.name(), data.block().get(), data.textures().get("side"));
                case SLAB -> reinforcedSlab(data.name(), data.block().get(), data.textures().get("side"));
                case WALL -> reinforcedWall(data.name(), (WallBlock) data.block().get(), data.textures().get("wall"));
                case PILLAR -> reinforcedPillar(data.name(), data.block(), data.parentModel(), data.textures());
                case CREATE_CONNECTED_PANE -> reinforcedPane(data.name(), (ReinforcedPaneBlock) data.block().get(), data.renderType(), data.textures());
                case CREATE_BARS -> reinforcedBars(data.name(), (ReinforcedPaneBlock) data.block().get(),
                        data.renderType(), data.textures());
                case CREATE_TRACK -> reinforcedTrack(data);
                case ON2STATE -> reinforcedOn2State(data.name(), data.block().get(),data.parentModel(), data.renderType(), data.textures());
            }
        }

        simpleGroups.forEach(this::simpleGroup);
    }

    private void simpleGroup(Block block, List<SCBEBlockModelData> dataList) {
        if (dataList.isEmpty()){
            return;
        }

        List<ModelFile> blockModels = new ArrayList<>();

        for (SCBEBlockModelData data : dataList) {
            blockModels.add(modelWithParent(data.name(), data.parentModel(), data.textures(), data.renderType()));
        }

        if (blockModels.size() == 1){
            simpleBlock(block, blockModels.get(0));
        }else {
            getVariantBuilder(block)
                    .partialState()
                    .addModels(blockModels.stream().map(ConfiguredModel::new).toArray(ConfiguredModel[]::new));
        }

        SCBEBlockModelData first = dataList.get(0);
        String itemModelName = ForgeRegistries.BLOCKS.getKey(block).getPath();
        if (first.itemModel() != null) {
            ItemModelBuilder itemBuilder = itemModels().getBuilder(itemModelName)
                    .parent(new UncheckedModelFile(first.itemModel().parentModel()));
            first.itemModel().textures().forEach(itemBuilder::texture);
        } else {
            itemModels().withExistingParent(itemModelName, modLoc("block/" + first.name()));
        }
    }

    // ========== 通用方法 ==========

    private ModelFile modelWithParent(String name,
                                      ResourceLocation parentModel,
                                      Map<String, ResourceLocation> textures, String renderType
    ){
        BlockModelBuilder builder = models().getBuilder(name)
                .parent(new UncheckedModelFile(
                        parentModel));

        if (renderType != null && !renderType.isEmpty()) {
            builder.renderType(renderType);
        }

        textures.forEach(builder::texture);

        return builder;
    }

    /**
     * 为方块生成 blockstate、block model 和 item model。
     *
     * @param name        方块注册名（也用作模型文件名）
     * @param block       方块的 RegistryObject
     * @param parentModel 父模型路径，例如 "block/reinforced_cube_all"（securitycraft 命名空间）
     * @param textures    纹理映射，key 为父模型中的占位符（如 "all"、"bottom"、"top"、"side"）
     * @param renderType  渲染类型
     */
    private void reinforcedBlock(String name,
                                 Supplier<? extends Block> block,
                                 ResourceLocation parentModel,
                                 Map<String, ResourceLocation> textures,String renderType,
                                 SCBEBlockModelData itemModel) {
        ModelFile blockModel = modelWithParent(name, parentModel, textures, renderType);

        simpleBlock(block.get(), blockModel);
        if (itemModel != null){
            ItemModelBuilder itemBuilder = itemModels().getBuilder(name)
                    .parent(new UncheckedModelFile(itemModel.parentModel()));
            itemModel.textures().forEach(itemBuilder::texture);
        }else {
            itemModels().withExistingParent(name, modLoc("block/" + name));
        }
    }

    private void reinforcedBlock(String name,
                                 Supplier<? extends Block> block,
                                 ResourceLocation parentModel,
                                 Map<String, ResourceLocation> textures,String renderType) {
        reinforcedBlock(name,block,parentModel,textures,renderType,null);
    }

    /**
     * 便捷方法：父模型为 securitycraft:block/reinforced_cube_all，纹理占位符为 "all"
     */
    private void reinforcedCubeAll(String name,
                                   RegistryObject<? extends Block> block,
                                   ResourceLocation allTexture,String renderType) {
        reinforcedBlock(name, block, securitycraftLoc("block/reinforced_cube_all"), Map.of("all", allTexture),renderType);
    }

    /**
     * 便捷方法：父模型为 securitycraft:block/reinforced_cube_bottom_top，纹理占位符为 bottom/top/side
     */
    private void reinforcedCubeBottomTop(String name,
                                         RegistryObject<? extends Block> block,
                                         ResourceLocation bottom,
                                         ResourceLocation top,
                                         ResourceLocation side,
                                         String renderType) {
        reinforcedBlock(name, block, securitycraftLoc("block/reinforced_cube_bottom_top"), Map.of(
                "bottom", bottom,
                "top", top,
                "side", side
        ), renderType);
    }

    /**
     * 便捷方法：父模型为 securitycraft:block/reinforced_cube_column，纹理占位符为 end/side
     */
    private void reinforcedCubeColumn(String name,
                                      Supplier<? extends Block> block,
                                      ResourceLocation end,
                                      ResourceLocation side,
                                      String renderType) {
        reinforcedBlock(name, block, securitycraftLoc("block/reinforced_cube_column"), Map.of(
                "end", end,
                "side", side
        ), renderType);
    }

    // --- 专用生成方法 ---

    // ============ 楼梯：手动构建变体 ============

    private void reinforcedStairs(String name, Block block, ResourceLocation texture) {
        // 生成三个基础模型：直、内角、外角
        ModelFile stairs      = models().withExistingParent(name, securitycraftLoc("block/reinforced_stairs"))
                .texture("bottom", texture).texture("top", texture).texture("side", texture);
        ModelFile stairsInner = models().withExistingParent(name + "_inner", securitycraftLoc("block/reinforced_inner_stairs"))
                .texture("bottom", texture).texture("top", texture).texture("side", texture);
        ModelFile stairsOuter = models().withExistingParent(name + "_outer", securitycraftLoc("block/reinforced_outer_stairs"))
                .texture("bottom", texture).texture("top", texture).texture("side", texture);

        getVariantBuilder(block).forAllStatesExcept(state -> {
            Direction facing = state.getValue(ReinforcedStairsBlock.FACING);
            Half half = state.getValue(ReinforcedStairsBlock.HALF);
            StairsShape shape = state.getValue(ReinforcedStairsBlock.SHAPE);

            int yRot = (int) facing.getClockWise().toYRot();
            if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                yRot += 270;
            }
            if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                yRot += 90;
            }
            yRot %= 360;
            boolean uvlock = yRot != 0 || half == Half.TOP;

            ModelFile model = shape == StairsShape.STRAIGHT ? stairs
                    : (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT) ? stairsInner
                    : stairsOuter;

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(half == Half.BOTTOM ? 0 : 180)
                    .rotationY(yRot)
                    .uvLock(uvlock)
                    .build();
        }, ReinforcedStairsBlock.WATERLOGGED);

        itemModels().withExistingParent(name, modLoc("block/" + name));
    }

    // ============ 台阶：手动构建变体 ============

    private void reinforcedSlab(String name, Block block, ResourceLocation texture) {
        ModelFile slabBottom = models().withExistingParent(name, securitycraftLoc("block/reinforced_slab"))
                .texture("bottom", texture).texture("top", texture).texture("side", texture);
        ModelFile slabTop = models().withExistingParent(name + "_top", securitycraftLoc("block/reinforced_slab_top"))
                .texture("bottom", texture).texture("top", texture).texture("side", texture);
        ModelFile slabDouble = models().withExistingParent(name + "_double", securitycraftLoc("block/reinforced_cube_all"))
                .texture("all", texture);

        getVariantBuilder(block)
                .partialState().with(ReinforcedSlabBlock.TYPE, SlabType.BOTTOM)
                .addModels(new ConfiguredModel(slabBottom))
                .partialState().with(ReinforcedSlabBlock.TYPE, SlabType.TOP)
                .addModels(new ConfiguredModel(slabTop))
                .partialState().with(ReinforcedSlabBlock.TYPE, SlabType.DOUBLE)
                .addModels(new ConfiguredModel(slabDouble));

        itemModels().withExistingParent(name, modLoc("block/" + name));
    }

    private void reinforcedWall(String name, WallBlock block, ResourceLocation texture) {
        // wallBlock
        ModelFile post = models().getBuilder(name + "_post")
                .parent(new UncheckedModelFile(securitycraftLoc("block/template_reinforced_wall_post")))
                .texture("wall", texture);
        ModelFile side = models().getBuilder(name + "_side")
                .parent(new UncheckedModelFile(securitycraftLoc("block/template_reinforced_wall_side")))
                .texture("wall", texture);
        ModelFile sideTall = models().getBuilder(name + "_side_tall")
                .parent(new UncheckedModelFile(securitycraftLoc("block/template_reinforced_wall_side_tall")))
                .texture("wall", texture);

        getMultipartBuilder(block)
                .part().modelFile(post).addModel()
                .condition(WallBlock.UP, true).end()
                .part().modelFile(side).uvLock(true).addModel()
                .condition(WallBlock.NORTH_WALL, WallSide.LOW).end()
                .part().modelFile(side).uvLock(true).rotationY(90).addModel()
                .condition(WallBlock.EAST_WALL, WallSide.LOW).end()
                .part().modelFile(side).uvLock(true).rotationY(180).addModel()
                .condition(WallBlock.SOUTH_WALL, WallSide.LOW).end()
                .part().modelFile(side).uvLock(true).rotationY(270).addModel()
                .condition(WallBlock.WEST_WALL, WallSide.LOW).end()
                .part().modelFile(sideTall).uvLock(true).addModel()
                .condition(WallBlock.NORTH_WALL, WallSide.TALL).end()
                .part().modelFile(sideTall).uvLock(true).rotationY(90).addModel()
                .condition(WallBlock.EAST_WALL, WallSide.TALL).end()
                .part().modelFile(sideTall).uvLock(true).rotationY(180).addModel()
                .condition(WallBlock.SOUTH_WALL, WallSide.TALL).end()
                .part().modelFile(sideTall).uvLock(true).rotationY(270).addModel()
                .condition(WallBlock.WEST_WALL, WallSide.TALL).end();

        itemModels().withExistingParent(name, securitycraftLoc("block/reinforced_wall_inventory"))
                .texture("wall", texture);
    }

    private void reinforcedPillar(String name,
                                  Supplier<? extends Block> block,
                                  ResourceLocation parentModel,
                                  Map<String, ResourceLocation> textures) {

        ResourceLocation end  = textures.get("end");
        ResourceLocation side = textures.get("side");

        // 竖直模型：直接使用 data.parentModel
        ModelFile vertical = models().withExistingParent(name, parentModel)
                .texture("end", end)
                .texture("side", side);

        // 水平模型：父模型换成 horizontal 变体
        ModelFile horizontal = models()
                .withExistingParent(name + "_horizontal",
                        securitycraftLoc("block/reinforced_cube_column_horizontal"))
                .texture("end", end)
                .texture("side", side);

        getVariantBuilder(block.get())
                .forAllStatesExcept(state -> {
                            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                            if (axis == Direction.Axis.Y) {
                                return ConfiguredModel.builder()
                                        .modelFile(vertical)
                                        .uvLock(false)
                                        .build();
                            }
                            return ConfiguredModel.builder()
                                    .modelFile(horizontal)
                                    .uvLock(false)
                                    .rotationX(90)
                                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                                    .build();
                        },
                        BlockStateProperties.WATERLOGGED,
                        BlockStateProperties.NORTH,
                        BlockStateProperties.SOUTH,
                        BlockStateProperties.EAST,
                        BlockStateProperties.WEST
                );

        itemModels().withExistingParent(name, modLoc("block/" + name));
    }


    private void reinforcedPane(String name, ReinforcedPaneBlock block, String renderType,
                                Map<String, ResourceLocation> textures) {
        ResourceLocation paneTexture = textures.get("pane");
        ResourceLocation edgeTexture = textures.get("edge");

        ModelFile post     = buildPaneModel(name + "_post",     Create.asResource("block/connected_glass_pane/post"),     renderType, paneTexture, edgeTexture);
        ModelFile side     = buildPaneModel(name + "_side",     Create.asResource("block/connected_glass_pane/side"),     renderType, paneTexture, edgeTexture);
        ModelFile sideAlt  = buildPaneModel(name + "_side_alt", Create.asResource("block/connected_glass_pane/side_alt"), renderType, paneTexture, edgeTexture);
        ModelFile noSide   = buildPaneModel(name + "_noside",     Create.asResource("block/connected_glass_pane/noside"),     renderType, paneTexture, edgeTexture);
        ModelFile noSideAlt= buildPaneModel(name + "_noside_alt", Create.asResource("block/connected_glass_pane/noside_alt"), renderType, paneTexture, edgeTexture);

        getMultipartBuilder(block)
                .part().modelFile(post).addModel().end()

                .part().modelFile(side).addModel()
                .condition(BlockStateProperties.NORTH, true).end()

                .part().modelFile(noSide).addModel()
                .condition(BlockStateProperties.NORTH, false).end()

                .part().modelFile(side).rotationY(90).addModel()
                .condition(BlockStateProperties.EAST, true).end()

                .part().modelFile(sideAlt).addModel()
                .condition(BlockStateProperties.SOUTH, true).end()

                .part().modelFile(sideAlt).rotationY(90).addModel()
                .condition(BlockStateProperties.WEST, true).end()

                .part().modelFile(noSideAlt).addModel()
                .condition(BlockStateProperties.EAST, false).end()

                .part().modelFile(noSideAlt).rotationY(90).addModel()
                .condition(BlockStateProperties.SOUTH, false).end()

                .part().modelFile(noSide).rotationY(270).addModel()
                .condition(BlockStateProperties.WEST, false).end();

        // 物品模型：使用原版 item/generated
        itemModels().withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", paneTexture);
    }

    /**
     * 构造单个 pane 模型。
     * @param edgeTexture 为 null 时仅设置 pane 纹理（noside 系列只需要 pane）。
     */
    private ModelFile buildPaneModel(String name, ResourceLocation parentPath, String renderType,
                                     ResourceLocation paneTexture, ResourceLocation edgeTexture) {
        Map<String, ResourceLocation> textures = new HashMap<>();
        textures.put("pane", paneTexture);
        if (edgeTexture != null) {
            textures.put("edge", edgeTexture);
        }
        return buildSubModel(name, parentPath, renderType, textures);
    }

    private void reinforcedBars(String name, ReinforcedPaneBlock block, String renderType,
                                Map<String, ResourceLocation> textures) {
        // 六个部件模型，父模型全部指向 Create 的 bars 模板
        ModelFile postEnds = buildSubModel(name + "_post_ends", Create.asResource("block/bars/post_ends"), renderType, textures);
        ModelFile post     = buildSubModel(name + "_post",      Create.asResource("block/bars/post"),      renderType, textures);
        ModelFile cap      = buildSubModel(name + "_cap",       Create.asResource("block/bars/cap"),       renderType, textures);
        ModelFile capAlt   = buildSubModel(name + "_cap_alt",   Create.asResource("block/bars/cap_alt"),   renderType, textures);
        ModelFile side     = buildSubModel(name + "_side",      Create.asResource("block/bars/side"),      renderType, textures);
        ModelFile sideAlt  = buildSubModel(name + "_side_alt",  Create.asResource("block/bars/side_alt"),  renderType, textures);

        getMultipartBuilder(block)
                // post_ends 恒显示
                .part().modelFile(postEnds).addModel().end()

                // 四周都不连接 → post
                .part().modelFile(post).addModel()
                .condition(BlockStateProperties.NORTH, false)
                .condition(BlockStateProperties.EAST,  false)
                .condition(BlockStateProperties.SOUTH, false)
                .condition(BlockStateProperties.WEST,  false).end()

                // 只有 N/E/S/W 一个方向连接 → cap / cap_alt
                .part().modelFile(cap).addModel()
                .condition(BlockStateProperties.NORTH, true)
                .condition(BlockStateProperties.EAST,  false)
                .condition(BlockStateProperties.SOUTH, false)
                .condition(BlockStateProperties.WEST,  false).end()
                .part().modelFile(cap).rotationY(90).addModel()
                .condition(BlockStateProperties.NORTH, false)
                .condition(BlockStateProperties.EAST,  true)
                .condition(BlockStateProperties.SOUTH, false)
                .condition(BlockStateProperties.WEST,  false).end()
                .part().modelFile(capAlt).addModel()
                .condition(BlockStateProperties.NORTH, false)
                .condition(BlockStateProperties.EAST,  false)
                .condition(BlockStateProperties.SOUTH, true)
                .condition(BlockStateProperties.WEST,  false).end()
                .part().modelFile(capAlt).rotationY(90).addModel()
                .condition(BlockStateProperties.NORTH, false)
                .condition(BlockStateProperties.EAST,  false)
                .condition(BlockStateProperties.SOUTH, false)
                .condition(BlockStateProperties.WEST,  true).end()

                // 每个方向连接 → side / side_alt，带朝向旋转
                .part().modelFile(side).addModel()
                .condition(BlockStateProperties.NORTH, true).end()
                .part().modelFile(side).rotationY(90).addModel()
                .condition(BlockStateProperties.EAST,  true).end()
                .part().modelFile(sideAlt).addModel()
                .condition(BlockStateProperties.SOUTH, true).end()
                .part().modelFile(sideAlt).rotationY(90).addModel()
                .condition(BlockStateProperties.WEST,  true).end();

        // 物品模型：使用原版 item/generated
        itemModels().withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", textures.get("bars"));
    }

    private ModelFile buildSubModel(String name,
                                    ResourceLocation parent,
                                    String renderType,
                                    Map<String, ResourceLocation> textures) {
        BlockModelBuilder builder = models().getBuilder(name)
                .parent(new UncheckedModelFile(parent));
        textures.forEach(builder::texture);
        if (renderType != null && !renderType.isEmpty()) {
            builder.renderType(renderType);
        }
        return builder;
    }

    private void reinforcedTrack(SCBEBlockModelData data) {
        String name = data.name();
        Block block = data.block().get();
        String renderType = data.renderType();

        // TrackShape -> Create 原版模型子路径（相对 create:block/track/）
        Map<TrackShape, String> modelByShape = new LinkedHashMap<>();
        // TrackShape -> Y 轴旋转（度数）
        Map<TrackShape, Integer> rotByShape = new HashMap<>();

        modelByShape.put(TrackShape.AE,     "ascending");   rotByShape.put(TrackShape.AE,     270);
        modelByShape.put(TrackShape.AN,     "ascending");   rotByShape.put(TrackShape.AN,     180);
        modelByShape.put(TrackShape.AS,     "ascending");   rotByShape.put(TrackShape.AS,     0);
        modelByShape.put(TrackShape.AW,     "ascending");   rotByShape.put(TrackShape.AW,     90);
        modelByShape.put(TrackShape.CR_D,   "cross_diag");  rotByShape.put(TrackShape.CR_D,   0);
        modelByShape.put(TrackShape.CR_NDX, "cross_d2_xo"); rotByShape.put(TrackShape.CR_NDX, 0);
        modelByShape.put(TrackShape.CR_NDZ, "cross_d2_zo"); rotByShape.put(TrackShape.CR_NDZ, 0);
        modelByShape.put(TrackShape.CR_O,   "cross_ortho"); rotByShape.put(TrackShape.CR_O,   0);
        modelByShape.put(TrackShape.CR_PDX, "cross_d1_xo"); rotByShape.put(TrackShape.CR_PDX, 0);
        modelByShape.put(TrackShape.CR_PDZ, "cross_d1_zo"); rotByShape.put(TrackShape.CR_PDZ, 0);
        modelByShape.put(TrackShape.ND,     "diag_2");      rotByShape.put(TrackShape.ND,     0);
        modelByShape.put(TrackShape.PD,     "diag");        rotByShape.put(TrackShape.PD,     0);
        modelByShape.put(TrackShape.TE,     "teleport");    rotByShape.put(TrackShape.TE,     270);
        modelByShape.put(TrackShape.TN,     "teleport");    rotByShape.put(TrackShape.TN,     180);
        modelByShape.put(TrackShape.TS,     "teleport");    rotByShape.put(TrackShape.TS,     0);
        modelByShape.put(TrackShape.TW,     "teleport");    rotByShape.put(TrackShape.TW,     90);
        modelByShape.put(TrackShape.XO,     "x_ortho");     rotByShape.put(TrackShape.XO,     0);
        modelByShape.put(TrackShape.ZO,     "z_ortho");     rotByShape.put(TrackShape.ZO,     0);

        // 每个唯一模型名生成一个自定义模型文件：<modid>:block/<name>/<modelName>
        // parent 指向 create:block/track/<modelName>，附加 render_type
        Map<String, ModelFile> modelFiles = new HashMap<>();
        for (String modelName : new LinkedHashSet<>(modelByShape.values())) {
            BlockModelBuilder builder = models()
                    .withExistingParent("block/"+name + "/" + modelName,
                            Create.asResource("block/track/" + modelName));
            if (renderType != null && !renderType.isEmpty()) {
                builder.renderType(renderType);
            }
            modelFiles.put(modelName, builder);
        }

        ModelFile airModel = new UncheckedModelFile(mcLoc("block/air"));

        getVariantBuilder(block).forAllStatesExcept(state -> {
            TrackShape shape = state.getValue(TrackBlock.SHAPE);
            String modelName = modelByShape.get(shape);
            if (modelName == null) {
                // TrackShape.NONE
                return ConfiguredModel.builder().modelFile(airModel).build();
            }
            return ConfiguredModel.builder()
                    .modelFile(modelFiles.get(modelName))
                    .rotationY(rotByShape.getOrDefault(shape, 0))
                    .build();
        }, TrackBlock.HAS_BE, TrackBlock.WATERLOGGED);

        // 物品模型：使用原版 item/generated
        itemModels().withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", data.textures().get("item"));
    }

    private void reinforcedOn2State(String name, Block block,ResourceLocation parent, String renderType,
                                    Map<String, ResourceLocation> textures) {
        String onName = parent.getPath().equals("default_on") ? name : name + "_lamp_on";
        String offName = parent.getPath().equals("default_on") ? name + "_lamp_on" : name;
        System.out.println("onName: " + onName + ", offName: " + offName+" processingBlock:"+name);
        getVariantBuilder(block)
                .partialState().with(LampBlock.ON,true)
                .addModels(new ConfiguredModel(modelWithParent(
                        onName,modLoc("block/reinforced_lamp"),
                        Map.of("0",textures.get("on_all"),"particle",textures.get("on_all")),
                        renderType
                )))
                .partialState().with(LampBlock.ON,false)
                .addModels(new ConfiguredModel(modelWithParent(
                        offName,modLoc("block/reinforced_lamp"),
                        Map.of("0",textures.get("off_all"),"particle",textures.get("off_all")),
                        renderType
                )));
        itemModels().withExistingParent(name,modLoc("block/"+name));
    }

    // ========== 工具方法 ==========

    /** 快速创建 minecraft 命名空间的 ResourceLocation */
    public ResourceLocation mcLoc(String path) {
        return ResourceLocation.tryBuild("minecraft", path);
    }
    public static ResourceLocation securitycraftLoc(String path) {
        return ResourceLocation.tryBuild("securitycraft", path);
    }
}