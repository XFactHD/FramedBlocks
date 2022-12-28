package xfacthd.framedblocks.client.model.rail;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedFancyRailSlopeBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedFancyRailSlopeModel extends FramedDoubleBlockModel
{
    private final BlockType type;
    private final RailShape shape;
    private final boolean powered;

    public FramedFancyRailSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.type = (BlockType) ((IFramedBlock) state.getBlock()).getBlockType();
        this.shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        this.powered = type != BlockType.FRAMED_FANCY_RAIL_SLOPE && state.getValue(BlockStateProperties.POWERED);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedFancyRailSlopeBlockEntity.getBlockPair(type, shape, powered);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getA());
    }



    public static BlockState itemSourceNormal()
    {
        return FBContent.blockFramedFancyRailSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }

    public static BlockState itemSourcePowered()
    {
        return FBContent.blockFramedFancyPoweredRailSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }

    public static BlockState itemSourceDetector()
    {
        return FBContent.blockFramedFancyDetectorRailSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }

    public static BlockState itemSourceActivator()
    {
        return FBContent.blockFramedFancyActivatorRailSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }
}
