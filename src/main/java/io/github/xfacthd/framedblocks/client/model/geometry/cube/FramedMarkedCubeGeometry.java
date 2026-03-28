package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

public class FramedMarkedCubeGeometry extends FramedCubeGeometry {
    public static final Identifier SLIME_FRAME_LOCATION = Utils.id("block/slime_frame");
    public static final Identifier REDSTONE_FRAME_LOCATION = Utils.id("block/redstone_frame");
    public static final String FRAME_KEY = "frame";

    private final BlockState state;
    private final BlockStateModel frameModel;
    private final BlockState frameShaderState;

    private FramedMarkedCubeGeometry(GeometryFactory.Context ctx, BlockState frameShaderState) {
        super(ctx);
        this.state = ctx.state();
        this.frameModel = ctx.auxModels().getModel(FRAME_KEY);
        this.frameShaderState = frameShaderState;
    }

    @Override
    public boolean hasAdditionalUncachedParts() {
        return true;
    }

    @Override
    public void collectAdditionalPartsUncached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, ModelData data) {
        if (!blockData.getCamoContent().isEmpty()) {
            consumer.acceptAll(frameModel, level, pos, random, state, false, false, true, frameShaderState, null);
        }
    }

    public static FramedCubeGeometry slime(GeometryFactory.Context ctx) {
        if (ClientConfig.VIEW.showSpecialCubeOverlay()) {
            return new FramedMarkedCubeGeometry(ctx, Blocks.SLIME_BLOCK.defaultBlockState());
        }
        return new FramedCubeGeometry(ctx);
    }

    public static FramedCubeGeometry redstone(GeometryFactory.Context ctx) {
        if (ClientConfig.VIEW.showSpecialCubeOverlay()) {
            return new FramedMarkedCubeGeometry(ctx, Blocks.REDSTONE_BLOCK.defaultBlockState());
        }
        return new FramedCubeGeometry(ctx);
    }
}
