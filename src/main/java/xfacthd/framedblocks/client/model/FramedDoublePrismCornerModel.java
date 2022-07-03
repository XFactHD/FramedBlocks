package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleThreewayCornerBlockEntity;

public class FramedDoublePrismCornerModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final boolean top;
    private final boolean offset;

    public FramedDoublePrismCornerModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
        this.offset = state.getValue(FramedProperties.OFFSET);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedDoubleThreewayCornerBlockEntity.getPrismBlockPair(facing, top, offset);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        if (top)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getB());
        }
        return super.getParticleIcon(data);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedDoublePrismCorner.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}