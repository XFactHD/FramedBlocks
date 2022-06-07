package xfacthd.framedblocks.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.model.BakedModelProxy;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FramedRailSlopeModel extends BakedModelProxy
{
    private final BlockState slopeState;
    private final BlockState railState;
    private BakedModel slopeModel = null;
    private BakedModel railModel = null;

    public FramedRailSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel);

        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        Direction dir = FramedRailSlopeBlock.directionFromShape(shape);

        slopeState = FBContent.blockFramedSlope.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, dir);
        railState = Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, shape);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull IModelData extraData)
    {
        List<BakedQuad> quads = new ArrayList<>(getSlopeQuads(side, rand, extraData));

        RenderType layer = MinecraftForgeClient.getRenderType();
        if (layer == RenderType.cutout() || layer == null)
        {
            quads.addAll(getRailQuads(side, rand));
        }

        return quads;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        if (world.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getModelData();
        }
        return tileData;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) { return getSlopeModel().getParticleIcon(data); }

    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleIcon() { return getSlopeModel().getParticleIcon(); }

    private BakedModel getSlopeModel()
    {
        if (slopeModel == null)
        {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            slopeModel = dispatcher.getBlockModel(slopeState);
        }
        return slopeModel;
    }

    private List<BakedQuad> getSlopeQuads(@Nullable Direction side, @Nonnull RandomSource rand, @Nonnull IModelData extraData)
    {
        return getSlopeModel().getQuads(slopeState, side, rand, extraData);
    }

    private List<BakedQuad> getRailQuads(@Nullable Direction side, RandomSource rand)
    {
        if (railModel == null)
        {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            railModel = dispatcher.getBlockModel(railState);
        }
        return railModel.getQuads(railState, side, rand, EmptyModelData.INSTANCE);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedRailSlope.get().defaultBlockState().setValue(
                PropertyHolder.ASCENDING_RAIL_SHAPE,
                RailShape.ASCENDING_SOUTH
        );
    }
}