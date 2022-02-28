package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

public class FramedDoublePrismCornerModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoublePrismCornerModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, true);
        this.state = state;
    }

    public FramedDoublePrismCornerModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedDoublePrismCorner.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, Direction.WEST),
                baseModel
        );
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);
        boolean top = state.getValue(PropertyHolder.TOP);
        boolean offset = state.getValue(PropertyHolder.OFFSET);

        BlockState stateOne = FBContent.blockFramedInnerPrismCorner.get().defaultBlockState()
                .setValue(PropertyHolder.TOP, top)
                .setValue(PropertyHolder.FACING_HOR, facing)
                .setValue(PropertyHolder.OFFSET, offset);
        BlockState stateTwo = FBContent.blockFramedPrismCorner.get().defaultBlockState()
                .setValue(PropertyHolder.TOP, !top)
                .setValue(PropertyHolder.FACING_HOR, facing.getOpposite())
                .setValue(PropertyHolder.OFFSET, !offset);

        return new Tuple<>(stateOne, stateTwo);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data)
    {
        if (state.getValue(PropertyHolder.TOP))
        {
            return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_LEFT, getModels().getB());
        }
        return super.getParticleTexture(data);
    }
}