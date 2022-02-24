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
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedInverseDoubleSlopeSlabModel extends FramedDoubleBlockModel
{
    private final Direction facing;

    public FramedInverseDoubleSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
    }

    public FramedInverseDoubleSlopeSlabModel(BakedModel baseModel)
    {
        this(
                FBContent.blockFramedInverseDoubleSlopeSlab.get().defaultBlockState(),
                baseModel
        );
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState defState = FBContent.blockFramedSlopeSlab.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, false)
                        .setValue(FramedProperties.TOP, true),
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, true)
                        .setValue(FramedProperties.TOP, false)
        );
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
    }
}
