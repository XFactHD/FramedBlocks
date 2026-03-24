package io.github.xfacthd.framedblocks.client.model.special;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.standalone.CachingModel;
import io.github.xfacthd.framedblocks.client.model.block.FramedBlockModel;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.LatchType;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.Map;

public final class FramedChestLidModel implements CachingModel
{
    private static final Direction[] DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final ChestType[] TYPES = ChestType.values();
    private static final LatchType[] LATCHES = LatchType.values();
    private static final int DIRECTION_COUNT = DIRECTIONS.length;
    private static final int TYPE_COUNT = TYPES.length;
    private static final int MODEL_COUNT = DIRECTION_COUNT * TYPE_COUNT * LATCHES.length;
    private static final Matrix4fc IDENTITY = new Matrix4f();

    private final FramedBlockModel[] models = new FramedBlockModel[MODEL_COUNT];

    public FramedChestLidModel(Map<BlockState, BlockStateModel> models)
    {
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            for (ChestType type : TYPES)
            {
                for (LatchType latch : LATCHES)
                {
                    BlockState state = FBContent.BLOCK_FRAMED_CHEST.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(BlockStateProperties.CHEST_TYPE, type)
                            .setValue(PropertyHolder.LATCH_TYPE, latch);
                    int idx = makeModelIndex(dir, type, latch);
                    BlockStateModel model = models.get(state);
                    this.models[idx] = new FramedBlockModel(model, IDENTITY);
                }
            }
        }
    }

    public BlockModel getModel(Direction dir, ChestType type, LatchType latch)
    {
        return models[makeModelIndex(dir, type, latch)];
    }

    @Override
    public void clearCache()
    {
        for (FramedBlockModel model : models)
        {
            if (model.getModel() instanceof AbstractFramedBlockStateModel fbModel)
            {
                fbModel.clearCache();
            }
        }
    }

    private static int makeModelIndex(Direction dir, ChestType type, LatchType latch)
    {
        return dir.get2DDataValue() + (type.ordinal() * DIRECTION_COUNT) + (latch.ordinal() * DIRECTION_COUNT * TYPE_COUNT);
    }
}
