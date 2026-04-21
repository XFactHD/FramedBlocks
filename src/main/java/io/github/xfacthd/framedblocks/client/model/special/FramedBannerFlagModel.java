package io.github.xfacthd.framedblocks.client.model.special;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.render.fakelevel.DelegatingBlockRenderFakeLevel;
import io.github.xfacthd.framedblocks.client.model.block.AdvancedBlockModelRenderState;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.List;
import java.util.Map;

public final class FramedBannerFlagModel {
    private static final Direction[] DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final Matrix4fc IDENTITY = new Matrix4f();

    private final BlockModel[] models = new BlockModel[4];

    public FramedBannerFlagModel(Map<BlockState, BlockStateModel> models) {
        for (Direction dir : DIRECTIONS) {
            BlockState state = FBContent.BLOCK_FRAMED_WALL_BANNER.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir);
            // Abuse glowing flag to indicate top half
            this.models[dir.get2DDataValue()] = new BannerFlagBlockModel(
                    models.get(state.setValue(FramedProperties.GLOWING, true)),
                    models.get(state)
            );
        }
    }

    public BlockModel getModel(int rotation) {
        int idx = ((rotation + 8) >> 2) & 0b11;
        return getModel(DIRECTIONS[idx]);
    }

    public BlockModel getModel(Direction facing) {
        return models[facing.get2DDataValue()];
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static final class BannerFlagBlockModel implements BlockModel {
        private final BlockStateModel topModel;
        private final BlockStateModel bottomModel;

        BannerFlagBlockModel(BlockStateModel topModel, BlockStateModel bottomModel) {
            this.topModel = topModel;
            this.bottomModel = bottomModel;
        }

        @Override
        public void update(BlockModelRenderState output, BlockState state, BlockDisplayContext context, long seed) {
            BlockAndTintGetter level;
            BlockPos topPos;
            BlockPos bottomPos;
            if (context instanceof DisplayContext ctx) {
                level = ctx;
                topPos = ctx.posTop;
                bottomPos = ctx.posBottom;
            } else {
                level = BlockAndTintGetter.EMPTY;
                topPos = bottomPos = BlockPos.ZERO;
            }
            int materialFlags = topModel.materialFlags(level, topPos, state);
            List<BlockStateModelPart> partList = output.setupModel(IDENTITY, (materialFlags & BakedQuad.FLAG_TRANSLUCENT) != 0);
            topModel.collectParts(level, topPos, state, output.scratchRandomSource(seed), partList);
            bottomModel.collectParts(level, bottomPos, state, output.scratchRandomSource(seed), partList);
            if (output instanceof AdvancedBlockModelRenderState advOut) {
                advOut.setAnimated((materialFlags & BakedQuad.FLAG_ANIMATED) != 0);
            }
            IClientBlockExtensions.of(state).collectDynamicTintValues(state, level, topPos, output.tintLayers());
        }
    }

    @SuppressWarnings("ExtendsUtilityClass")
    public static final class DisplayContext extends BlockDisplayContext implements DelegatingBlockRenderFakeLevel {
        private final BlockAndTintGetter realLevel;
        private final BlockPos posTop;
        private final BlockPos posBottom;
        private final BlockState state;
        private final ModelData modelData;

        public DisplayContext(BlockAndTintGetter realLevel, BlockPos posTop, ModelData modelData) {
            this.realLevel = realLevel;
            this.posTop = posTop;
            this.posBottom = posTop.below();
            this.state = unpackCamoState(modelData);
            this.modelData = modelData;
        }

        private static BlockState unpackCamoState(ModelData modelData) {
            AbstractFramedBlockData blockData = modelData.get(AbstractFramedBlockData.PROPERTY);
            if (!(blockData instanceof FramedBlockData oneBlockData)) {
                return Blocks.AIR.defaultBlockState();
            }
            return oneBlockData.getCamoContent().getAppearanceState();
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            if (pos.equals(posTop) || pos.equals(posBottom)) {
                return state;
            }
            return Blocks.AIR.defaultBlockState();
        }

        @Override
        public ModelData getModelData(BlockPos pos) {
            if (pos.equals(posTop) || pos.equals(posBottom)) {
                return modelData();
            }
            return ModelData.EMPTY;
        }

        @Override
        public int getBlockTint(BlockPos pos, ColorResolver resolver) {
            if (pos.equals(posTop) || pos.equals(posBottom)) {
                return realLevel().getBlockTint(pos, resolver);
            }
            return -1;
        }

        @Override
        public BlockAndTintGetter realLevel() {
            return realLevel;
        }

        @Override
        public BlockPos pos() {
            return posTop;
        }

        @Override
        public BlockState state() {
            return state;
        }

        @Override
        public ModelData modelData() {
            return modelData;
        }
    }
}
