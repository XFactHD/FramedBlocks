package xfacthd.framedblocks.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

public abstract class FramedSpecialDoubleBlockItem extends BlockItem
{
    public FramedSpecialDoubleBlockItem(Block block, Properties props)
    {
        super(block, props);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx)
    {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState originalState = level.getBlockState(pos);
        if (ctx.canPlace() && originalState.is(getBlock()))
        {
            BlockState newState = getReplacementState(ctx, originalState);
            if (newState != null)
            {
                if (!level.isClientSide())
                {
                    boolean writeToCamoTwo = shouldWriteToCamoTwo(ctx, originalState);
                    Utils.wrapInStateCopy(
                            level,
                            pos,
                            ctx.getPlayer(),
                            ctx.getItemInHand(),
                            writeToCamoTwo,
                            true,
                            () -> level.setBlockAndUpdate(pos, newState)
                    );

                    //noinspection deprecation
                    SoundType sound = FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState().getSoundType();
                    level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.place(ctx);
    }

    @Nullable
    protected abstract BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState);

    protected abstract boolean shouldWriteToCamoTwo(BlockPlaceContext ctx, BlockState originalState);
}
