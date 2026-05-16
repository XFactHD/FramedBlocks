package io.github.xfacthd.framedblocks.common.block.interactive;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FramedLeverBlock extends LeverBlock implements IFramedBlockInternal {
    public FramedLeverBlock(Properties props) {
        super(props.pushReaction(PushReaction.DESTROY)
                .noCollision()
                .strength(.5F)
                .noOcclusion()
                .sound(SoundType.WOOD)
        );
        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        BlockUtils.addStandardProperties(this, builder);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = handleUse(state, level, pos, player, hand, hit);
        if (result == InteractionResult.FAIL) {
            // Allow interacting with the block while holding a framed block
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return result;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        if (state.getValue(FACE) != AttachFace.WALL) {
            return IFramedBlockInternal.super.rotate(state, direction, mode);
        }
        return state;
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return TriState.DEFAULT;
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.FRAMED_LEVER;
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.UNSUPPORTED;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(FACE, AttachFace.FLOOR);
    }

    @Override
    public float getJadeRenderScale(BlockState state) {
        return 1.6F;
    }
}
