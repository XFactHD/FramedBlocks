package io.github.xfacthd.framedblocks.client.model;

import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.quadmap.QuadMapBuilderInternal;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class ReinforcementModel
{
    public static final BlockState SHADER_STATE = Blocks.OBSIDIAN.defaultBlockState();
    public static final Identifier MODEL_ID = Utils.id("block/framed_reinforcement");
    private static final ModelBaker.SharedOperationKey<ReinforcementModel> REINFORCEMENT_KEY = ModelUtils.makeSharedOpsKey(
            baker -> new ReinforcementModel(SimpleModelWrapper.bake(baker, ReinforcementModel.MODEL_ID, BlockModelRotation.IDENTITY))
    );
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockStateModelPart baseModel;
    private final Material.Baked particleMaterial;
    private final @Nullable BlockStateModelPart[] cachedFilteredParts = new BlockStateModelPart[256];

    public static ReinforcementModel getOrCreate(ModelBaker baker)
    {
        return baker.compute(REINFORCEMENT_KEY);
    }

    private ReinforcementModel(BlockStateModelPart baseModel)
    {
        this.baseModel = baseModel;
        this.particleMaterial = baseModel.particleMaterial();
    }

    public BlockStateModelPart getFiltered(int faceMask, TriState ambientOcclusion)
    {
        faceMask |= ambientOcclusion.ordinal() << 6;

        BlockStateModelPart part = cachedFilteredParts[faceMask];
        if (part == null)
        {
            QuadMapBuilderInternal quadMap = QuadMapBuilderInternal.create();
            for (Direction side : DIRECTIONS)
            {
                if ((faceMask & (1 << side.ordinal())) != 0)
                {
                    quadMap.getOrCreate(side).add(baseModel.getQuads(side).getFirst());
                }
            }
            cachedFilteredParts[faceMask] = part = new FramedBlockStateModelPart(quadMap.build(), ambientOcclusion, particleMaterial, SHADER_STATE);
        }
        return part;
    }
}
