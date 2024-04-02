package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public enum CamoGetter
{
    NONE
    {
        @Override
        public CamoContainer<?, ?> getCamo(FramedDoubleBlockEntity be)
        {
            return EmptyCamoContainer.EMPTY;
        }

        @Override
        public BlockState getComponent(Tuple<BlockState, BlockState> blockPair)
        {
            return null;
        }
    },
    FIRST
    {
        @Override
        public CamoContainer<?, ?> getCamo(FramedDoubleBlockEntity be)
        {
            return be.getCamo();
        }

        @Override
        public BlockState getComponent(Tuple<BlockState, BlockState> blockPair)
        {
            return blockPair.getA();
        }
    },
    SECOND
    {
        @Override
        public CamoContainer<?, ?> getCamo(FramedDoubleBlockEntity be)
        {
            return be.getCamoTwo();
        }

        @Override
        public BlockState getComponent(Tuple<BlockState, BlockState> blockPair)
        {
            return blockPair.getB();
        }
    };

    public abstract CamoContainer<?, ?> getCamo(FramedDoubleBlockEntity be);

    public abstract BlockState getComponent(Tuple<BlockState, BlockState> blockPair);
}
