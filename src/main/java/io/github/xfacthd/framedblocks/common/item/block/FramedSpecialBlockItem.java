package io.github.xfacthd.framedblocks.common.item.block;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.item.FramedBlockItem;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public abstract class FramedSpecialBlockItem extends FramedBlockItem {
    private final boolean doubleBlock;
    private final boolean supportsManualStateForReplacement;

    public FramedSpecialBlockItem(Block block, boolean doubleBlock, boolean supportsManualStateForReplacement, Properties props) {
        super(block, props);
        this.doubleBlock = doubleBlock;
        this.supportsManualStateForReplacement = supportsManualStateForReplacement;
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        ReplaceResult replaceResult = tryReplace(ctx);
        return switch (replaceResult) {
            case SUCCESS -> InteractionResult.SUCCESS;
            case PASS -> super.place(ctx);
            case FAIL -> InteractionResult.FAIL;
        };
    }

    private ReplaceResult tryReplace(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState originalState = level.getBlockState(pos);
        if (!ctx.canPlace() || !originalState.is(getBlock()) || ctx.getPlayer() == null) {
            return ReplaceResult.PASS;
        }

        boolean manual = isStateCyclingActive(ctx.getPlayer());
        if ((manual && !supportsManualStateForReplacement)) {
            return ReplaceResult.FAIL;
        }

        BlockState newState;
        if (manual) {
            BlockState manualState = getPlacementState(ctx, _ -> null);
            newState = manualState != null ? getReplacementState(ctx, originalState, manualState) : null;
        } else {
            newState = getReplacementState(ctx, originalState, null);
        }
        if (newState == null) {
            return ReplaceResult.PASS;
        }

        if (!level.isClientSide()) {
            boolean writeToCamoTwo = shouldWriteToCamoTwo(ctx, originalState);
            CamoList camos = ctx.getItemInHand().getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
            BlockUtils.wrapInStateCopy(
                    level,
                    pos,
                    ctx.getPlayer(),
                    ctx.getItemInHand(),
                    writeToCamoTwo,
                    true,
                    () -> level.setBlockAndUpdate(pos, newState)
            );

            CamoContainer<?, ?> camo = EmptyCamoContainer.EMPTY;
            if (doubleBlock && !camos.isEmpty() && level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be) {
                camo = camos.getCamo(0);
                be.setCamo(camo, !writeToCamoTwo);
            }
            SoundUtils.playPlaceSound(ctx, camo.getContent().getSoundType(), true);
        }
        return ReplaceResult.SUCCESS;
    }

    protected abstract @Nullable BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState, @Nullable BlockState manualState);

    protected abstract boolean shouldWriteToCamoTwo(BlockPlaceContext ctx, BlockState originalState);

    public static abstract class Single extends FramedSpecialBlockItem {
        public Single(Block block, boolean supportsManualStateForReplacement, Properties props) {
            super(block, false, supportsManualStateForReplacement, props);
        }

        @Override
        protected boolean shouldWriteToCamoTwo(BlockPlaceContext ctx, BlockState originalState) {
            return false;
        }
    }

    private enum ReplaceResult {
        SUCCESS,
        PASS,
        FAIL
    }
}
