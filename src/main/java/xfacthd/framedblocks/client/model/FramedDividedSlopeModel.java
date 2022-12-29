package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDividedSlopeBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedDividedSlopeModel extends FramedDoubleBlockModel
{
    private final SlopeType type;
    private final Direction facing;
    private final boolean horizontal;

    public FramedDividedSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.type = state.getValue(PropertyHolder.SLOPE_TYPE);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.horizontal = type == SlopeType.HORIZONTAL;
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedDividedSlopeBlockEntity.getBlockPair(type, facing);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        if (horizontal)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
        }
        return super.getParticleIcon(data);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedDividedSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
