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
import xfacthd.framedblocks.common.blockentity.FramedDoubleCornerBlockEntity;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.CornerType;

public class FramedDoubleCornerModel extends FramedDoubleBlockModel
{
    private final CornerType type;
    private final Direction facing;

    public FramedDoubleCornerModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.type = state.getValue(PropertyHolder.CORNER_TYPE);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedDoubleCornerBlockEntity.getBlockPair(type, facing);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        if (type == CornerType.BOTTOM)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getA());
        }
        else if (type.isTop())
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getB());
        }
        return super.getParticleIcon(data);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedDoubleCorner.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}