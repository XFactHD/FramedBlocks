package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FramedRailSlopeModel extends BakedModelProxy
{
    private final BlockState slopeState;
    private final BlockState railState;
    private IBakedModel slopeModel = null;
    private IBakedModel railModel = null;

    public FramedRailSlopeModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel);

        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        Direction dir = FramedRailSlopeBlock.directionFromShape(shape);

        slopeState = FBContent.blockFramedSlope.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, dir);
        railState = Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, shape);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        List<BakedQuad> quads = new ArrayList<>(getSlopeQuads(side, rand, extraData));

        RenderType layer = MinecraftForgeClient.getRenderLayer();
        if (layer == RenderType.cutout() || layer == null)
        {
            quads.addAll(getRailQuads(side, rand));
        }

        return quads;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            return te.getModelData();
        }
        return tileData;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) { return getSlopeModel().getParticleTexture(data); }

    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleIcon() { return getSlopeModel().getParticleIcon(); }

    private IBakedModel getSlopeModel()
    {
        if (slopeModel == null)
        {
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            slopeModel = dispatcher.getBlockModel(slopeState);
        }
        return slopeModel;
    }

    private List<BakedQuad> getSlopeQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return getSlopeModel().getQuads(slopeState, side, rand, extraData);
    }

    private List<BakedQuad> getRailQuads(@Nullable Direction side, Random rand)
    {
        if (railModel == null)
        {
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
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