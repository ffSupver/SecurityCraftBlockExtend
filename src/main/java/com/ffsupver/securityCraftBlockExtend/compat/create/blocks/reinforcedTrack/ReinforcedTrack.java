package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.compat.create.blocks.CreateReinforcedBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackMaterialFactory;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.atomic.AtomicReference;

import static com.ffsupver.securityCraftBlockExtend.compat.create.blocks.CreateReinforcedBlocks.REINFORCED_TRACK;
import static net.geforcemods.securitycraft.blocks.OwnableBlock.getStoredDestroyTime;
import static net.geforcemods.securitycraft.blocks.OwnableBlock.withReinforcedDestroyTime;

public class ReinforcedTrack extends TrackBlock implements IReinforcedBlock {
    public static final TrackMaterial REINFORCED_TRACK_MATERIAL = TrackMaterialFactory.make(SecurityCraftBlockExtend.asResource("reinforced_andesite_track"))
            .block(NonNullSupplier.lazy(()->REINFORCED_TRACK))
            .customBlockFactory(ReinforcedTrack::new)
            .defaultModels()
            .build();

    protected final float destroyTimeForOwner;


    public ReinforcedTrack(Properties properties, TrackMaterial material) {
        super(withReinforcedDestroyTime(properties), material);
        destroyTimeForOwner = getStoredDestroyTime();
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (!canBeWrenched(context)){
            return InteractionResult.PASS;
        }
        return super.onSneakWrenched(state, context);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pPlacer instanceof Player player)
            MinecraftForge.EVENT_BUS.post(new OwnershipEvent(pLevel, pPos, player));
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
    }

    @Override
    public Block getVanillaBlock() {
        return AllBlocks.TRACK.get();
    }

    private boolean canBeWrenched(UseOnContext context){
        return canInteract(context.getLevel(),context.getClickedPos(),context.getPlayer());
    }

    private boolean canInteract(Level level,BlockPos pos,Player player){
        AtomicReference<Owner> owner = new AtomicReference<>();
        withBlockEntityDo(level,pos,be-> {
            if (be instanceof ReinforcedTrackEntity reinforcedTrackEntity){
                owner.set(reinforcedTrackEntity.getOwner());
            }
        });
        return owner.get() != null && owner.get().isTreatedTheSameAs(new Owner(player));
    }

    @Override
    public BlockEntityType<? extends TrackBlockEntity> getBlockEntityType() {
        return CreateReinforcedBlocks.REINFORCED_TRACK_ENTITY.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CreateReinforcedBlocks.REINFORCED_TRACK_ENTITY.create(pos, state);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean removeBE = pState.getValue(HAS_BE) && (!pState.is(pNewState.getBlock()) || !pNewState.getValue(HAS_BE));
        Owner owner = null;
        if (removeBE){
            if (pLevel.getBlockEntity(pPos) instanceof ReinforcedTrackEntity reinforcedTrackEntity){
                Owner old = reinforcedTrackEntity.getOwner();
                if (!old.isDefaultOwner()){
                    owner = old;
                }
            }
        }else {
            if (!pState.getBlock().equals(pNewState.getBlock())) {
                pLevel.removeBlockEntity(pPos);
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);

        if (owner != null && pLevel.getBlockEntity(pPos) instanceof ReinforcedTrackEntity reinforcedTrackEntity){
            reinforcedTrackEntity.setOwner(owner.getUUID(),owner.getName());
        }
    }
}
