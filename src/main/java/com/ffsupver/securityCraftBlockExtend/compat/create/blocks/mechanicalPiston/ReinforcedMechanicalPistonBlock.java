package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston;

import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.CreateReinforcedBlocks;
import com.ffsupver.securityCraftBlockExtend.mixin.LinearActuatorBlockEntityAccessor;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;

import java.util.concurrent.atomic.AtomicReference;

import static net.geforcemods.securitycraft.blocks.OwnableBlock.getStoredDestroyTime;
import static net.geforcemods.securitycraft.blocks.OwnableBlock.withReinforcedDestroyTime;

public class ReinforcedMechanicalPistonBlock  extends DirectionalAxisKineticBlock implements IBE<ReinforcedMechanicalPistonBlockEntity>, IReinforcedBlock {

    public static final EnumProperty<MechanicalPistonBlock.PistonState> STATE = EnumProperty.create("state",MechanicalPistonBlock.PistonState.class);
    protected boolean isSticky;
    protected final float destroyTimeForOwner;

    public static ReinforcedMechanicalPistonBlock normal(BlockBehaviour.Properties properties) {
        return new ReinforcedMechanicalPistonBlock(properties, false);
    }

    public static ReinforcedMechanicalPistonBlock sticky(BlockBehaviour.Properties properties) {
        return new ReinforcedMechanicalPistonBlock(properties, true);
    }

    protected ReinforcedMechanicalPistonBlock(BlockBehaviour.Properties properties, boolean sticky) {
        super(withReinforcedDestroyTime(properties));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(STATE, MechanicalPistonBlock.PistonState.RETRACTED));
        isSticky = sticky;
        destroyTimeForOwner = getStoredDestroyTime();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (!player.mayBuild())
            return InteractionResult.PASS;
        if (player.isShiftKeyDown())
            return InteractionResult.PASS;
        if (!player.getItemInHand(handIn)
                .is(Tags.Items.SLIMEBALLS)) {
            if (player.getItemInHand(handIn)
                    .isEmpty()) {
                withBlockEntityDo(worldIn, pos, be -> be.assembleNextTick = true);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (state.getValue(STATE) != MechanicalPistonBlock.PistonState.RETRACTED)
            return InteractionResult.PASS;
        Direction direction = state.getValue(FACING);
        if (hit.getDirection() != direction)
            return InteractionResult.PASS;
        if (((ReinforcedMechanicalPistonBlock) state.getBlock()).isSticky)
            return InteractionResult.PASS;
        if (worldIn.isClientSide) {
            Vec3 vec = hit.getLocation();
            worldIn.addParticle(ParticleTypes.ITEM_SLIME, vec.x, vec.y, vec.z, 0, 0, 0);
            return InteractionResult.SUCCESS;
        }
        AllSoundEvents.SLIME_ADDED.playOnServer(worldIn, pos, .5f, 1);
        if (!player.isCreative())
            player.getItemInHand(handIn)
                    .shrink(1);
        worldIn.setBlockAndUpdate(pos, AllBlocks.STICKY_MECHANICAL_PISTON.getDefaultState()
                .setValue(FACING, direction)
                .setValue(AXIS_ALONG_FIRST_COORDINATE, state.getValue(AXIS_ALONG_FIRST_COORDINATE)));
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        Direction direction = state.getValue(FACING);
        if (!fromPos.equals(pos.relative(direction.getOpposite())))
            return;
        if (!level.isClientSide && !level.getBlockTicks()
                .willTickThisTick(pos, this))
            level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
        Direction direction = state.getValue(FACING);
        BlockState pole = worldIn.getBlockState(pos.relative(direction.getOpposite()));
        if (!AllBlocks.PISTON_EXTENSION_POLE.has(pole))
            return;
        if (pole.getValue(PistonExtensionPoleBlock.FACING)
                .getAxis() != direction.getAxis())
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            if (be.getLastAssemblyException() == null)
                return;
            ((LinearActuatorBlockEntityAccessor)be).setLastException(null);
            be.sendData();
        });
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!canBeWrenched(context)){
            return InteractionResult.PASS;
        }

        if (state.getValue(STATE) != MechanicalPistonBlock.PistonState.RETRACTED)
            return InteractionResult.PASS;
        return super.onWrenched(state, context);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (!canBeWrenched(context)){
            return InteractionResult.PASS;
        }
        return super.onSneakWrenched(state, context);
    }

    private boolean canBeWrenched(UseOnContext context){
        AtomicReference<Owner> owner = new AtomicReference<>();
        withBlockEntityDo(context.getLevel(),context.getClickedPos(),be-> owner.set(be.getOwner()));
        return owner.get().isTreatedTheSameAs(new Owner(context.getPlayer()));
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player player)
            MinecraftForge.EVENT_BUS.post(new OwnershipEvent(worldIn, pos, player));
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
    }

    @Override
    public Block getVanillaBlock() {
        return isSticky? AllBlocks.STICKY_MECHANICAL_PISTON.get() : AllBlocks.MECHANICAL_PISTON.get();
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        Direction direction = state.getValue(FACING);
        BlockPos pistonHead = null;
        BlockPos pistonBase = pos;
        boolean dropBlocks = player == null || !player.isCreative();

        Integer maxPoles = maxAllowedPistonPoles();
        for (int offset = 1; offset < maxPoles; offset++) {
            BlockPos currentPos = pos.relative(direction, offset);
            BlockState block = worldIn.getBlockState(currentPos);

            if (isExtensionPole(block) && direction.getAxis() == block.getValue(BlockStateProperties.FACING)
                    .getAxis())
                continue;

            if (isPistonHead(block) && block.getValue(BlockStateProperties.FACING) == direction) {
                pistonHead = currentPos;
            }

            break;
        }

        if (pistonHead != null && pistonBase != null) {
            BlockPos.betweenClosedStream(pistonBase, pistonHead)
                    .filter(p -> !p.equals(pos))
                    .forEach(p -> worldIn.destroyBlock(p, dropBlocks));
        }

        for (int offset = 1; offset < maxPoles; offset++) {
            BlockPos currentPos = pos.relative(direction.getOpposite(), offset);
            BlockState block = worldIn.getBlockState(currentPos);

            if (isExtensionPole(block) && direction.getAxis() == block.getValue(BlockStateProperties.FACING)
                    .getAxis()) {
                worldIn.destroyBlock(currentPos, dropBlocks);
                continue;
            }

            break;
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    public static int maxAllowedPistonPoles() {
        return AllConfigs.server().kinetics.maxPistonPoles.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        if (state.getValue(STATE) == MechanicalPistonBlock.PistonState.EXTENDED)
            return AllShapes.MECHANICAL_PISTON_EXTENDED.get(state.getValue(FACING));

        if (state.getValue(STATE) == MechanicalPistonBlock.PistonState.MOVING)
            return AllShapes.MECHANICAL_PISTON.get(state.getValue(FACING));

        return Shapes.block();
    }

    @Override
    public Class<ReinforcedMechanicalPistonBlockEntity> getBlockEntityClass() {
        return ReinforcedMechanicalPistonBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ReinforcedMechanicalPistonBlockEntity> getBlockEntityType() {
        return CreateReinforcedBlocks.REINFORCED_MECHANICAL_PISTON_BLOCK_ENTITY.get();
    }

    public static boolean isPiston(BlockState state) {
        return CreateReinforcedBlocks.REINFORCED_MECHANICAL_PISTON_BLOCK.has(state) || CreateReinforcedBlocks.REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK.has(state);
    }

    public static boolean isStickyPiston(BlockState state) {
        return CreateReinforcedBlocks.REINFORCED_STICKY_MECHANICAL_PISTON_BLOCK.has(state);
    }

    public static boolean isExtensionPole(BlockState state) {
        return AllBlocks.PISTON_EXTENSION_POLE.has(state);
    }

    public static boolean isPistonHead(BlockState state) {
        return AllBlocks.MECHANICAL_PISTON_HEAD.has(state);
    }
}