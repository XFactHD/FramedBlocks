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

public class FramedDoubleThreewayCornerModel extends FramedDoubleBlockModel
{
    private final BlockState state;

    public FramedDoubleThreewayCornerModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel, true);
        this.state = state;
    }

    public FramedDoubleThreewayCornerModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedDoubleThreewayCorner.get().getDefaultState().with(PropertyHolder.FACING_HOR, Direction.WEST),
                baseModel
        );
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        Direction facing = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        BlockState stateOne = FBContent.blockFramedInnerThreewayCorner.get().getDefaultState()
                .with(PropertyHolder.TOP, top)
                .with(PropertyHolder.FACING_HOR, facing.rotateYCCW());
        BlockState stateTwo = FBContent.blockFramedThreewayCorner.get().getDefaultState()
                .with(PropertyHolder.TOP, !top)
                .with(PropertyHolder.FACING_HOR, facing.getOpposite());

        return new Tuple<>(stateOne, stateTwo);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data)
    {
        if (state.get(PropertyHolder.TOP))
        {
            return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_LEFT, getModels().getB());
        }
        return super.getParticleTexture(data);
    }
}