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
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedFlatInverseDoubleSlopeSlabCornerBlockEntity;

public class FramedFlatInverseDoubleSlopeSlabCornerModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final boolean top;

    public FramedFlatInverseDoubleSlopeSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedFlatInverseDoubleSlopeSlabCornerBlockEntity.getBlockPair(facing, top);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return getSpriteOrDefault(
                data,
                top ? FramedDoubleBlockEntity.DATA_LEFT : FramedDoubleBlockEntity.DATA_RIGHT,
                top ? getModels().getA() : getModels().getB()
        );
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
