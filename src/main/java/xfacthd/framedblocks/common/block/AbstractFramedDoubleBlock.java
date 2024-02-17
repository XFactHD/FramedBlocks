package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.FramedDoubleBlockRenderProperties;

import java.util.function.Consumer;

public abstract class AbstractFramedDoubleBlock extends FramedBlock implements IFramedDoubleBlock
{
    public AbstractFramedDoubleBlock(BlockType blockType)
    {
        super(blockType);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            return be.getSoundType();
        }
        return getSoundType(state);
    }

    @Override
    public boolean playBreakSound(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            BlockState stateOne = be.getCamo().getState();
            if (stateOne.isAir())
            {
                stateOne = FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState();
            }
            IFramedBlock.playCamoBreakSound(level, pos, stateOne);

            BlockState stateTwo = be.getCamoTwo().getState();
            if (stateTwo.isAir())
            {
                stateTwo = FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState();
            }
            if (stateTwo.getSoundType() != stateOne.getSoundType())
            {
                IFramedBlock.playCamoBreakSound(level, pos, stateTwo);
            }

            return true;
        }
        return false;
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedDoubleBlockRenderProperties.INSTANCE);
    }

    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);



    public static Tuple<BlockState, BlockState> getStatePair(BlockState state)
    {
        return ((IFramedDoubleBlock) state.getBlock()).getCache(state).getBlockPair();
    }
}