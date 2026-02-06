package io.github.xfacthd.framedblocks.client.model.baked;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockModel;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.SingleBlockFakeLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class FramedDoubleBlockModel extends AbstractFramedBlockModel
{
    private final BlockAndTintGetter dummyLevel;
    private final DoubleBlockTopInteractionMode particleMode;
    private final DoubleBlockParts parts;
    private final boolean canCullOne;
    private final boolean canCullTwo;
    @Nullable
    private PartModels models = null;

    public FramedDoubleBlockModel(GeometryFactory.Context ctx, NullCullPredicate cullPredicate, ItemModelInfo itemModelInfo)
    {
        super(ctx.baseModel(), ctx.state(), itemModelInfo);
        BlockState state = ctx.state();
        DoubleBlockStateCache cache = ((IFramedDoubleBlock) state.getBlock()).getCache(state);
        this.parts = cache.getParts();
        ModelData dummyData = ModelData.of(AbstractFramedBlockData.PROPERTY, new FramedDoubleBlockData(
                parts, FramedBlockData.EMPTY, new FramedBlockData(EmptyCamoContainer.EMPTY, true)
        ));
        this.dummyLevel = SingleBlockFakeLevel.withoutRealLevel(state, dummyData);
        this.particleMode = cache.getTopInteractionMode();
        this.canCullOne = cullPredicate.testLeft(state);
        this.canCullTwo = cullPredicate.testRight(state);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> outParts)
    {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        if (fbData == null)
        {
            level = dummyLevel;
            pos = BlockPos.ZERO;
        }

        // TODO: re-implement null-face culling
        /*
        boolean oneMasked = false;//canCullOne && hasSolidCamo(fbData, true);
        boolean twoMasked = false;//canCullTwo && hasSolidCamo(fbData, false);
        */

        PartModels models = getModels();
        models.modelOne.collectParts(level, pos, parts.stateOne(), random, outParts);
        models.modelTwo.collectParts(level, pos, parts.stateTwo(), random, outParts);
    }

    private static boolean hasSolidCamo(@Nullable AbstractFramedBlockData fbData, boolean secondary)
    {
        return fbData != null && fbData.unwrap(secondary).getCamoContent().isSolid();
    }

    @Override
    public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state)
    {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return switch (particleMode)
        {
            case FIRST -> getSpriteOrDefault(level, pos, state, fbData, false);
            case SECOND -> getSpriteOrDefault(level, pos, state, fbData, true);
            case EITHER ->
            {
                TextureAtlasSprite sprite = getSprite(level, pos, state, fbData, false);
                if (sprite != null)
                {
                    yield sprite;
                }

                sprite = getSprite(level, pos, state, fbData, true);
                if (sprite != null)
                {
                    yield sprite;
                }

                //noinspection deprecation
                yield delegate.particleIcon();
            }
        };
    }

    private PartModels getModels()
    {
        if (models == null)
        {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            models = new PartModels(
                    dispatcher.getBlockModel(parts.stateOne()),
                    dispatcher.getBlockModel(parts.stateTwo())
            );
        }
        return models;
    }

    /**
     * Returns the camo-dependent particle texture of the side given by {@code key} when the camo is not air,
     * else returns the basic "framed block" sprite
     */
    private TextureAtlasSprite getSpriteOrDefault(BlockAndTintGetter level, BlockPos pos, BlockState state, @Nullable AbstractFramedBlockData data, boolean secondary)
    {
        TextureAtlasSprite sprite = getSprite(level, pos, state, data, secondary);
        //noinspection deprecation
        return sprite != null ? sprite : delegate.particleIcon();
    }

    @Nullable
    private TextureAtlasSprite getSprite(BlockAndTintGetter level, BlockPos pos, BlockState state, @Nullable AbstractFramedBlockData data, boolean secondary)
    {
        if (data != null)
        {
            FramedBlockData fbData = data.unwrap(secondary);
            if (!fbData.getCamoContent().isEmpty())
            {
                BlockStateModel model = secondary ? getModels().modelTwo : getModels().modelOne;
                return model.particleIcon(level, pos, state);
            }
        }
        return null;
    }

    private record PartModels(BlockStateModel modelOne, BlockStateModel modelTwo) { }
}
