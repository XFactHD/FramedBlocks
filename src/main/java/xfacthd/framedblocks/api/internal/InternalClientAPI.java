package xfacthd.framedblocks.api.internal;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.Utils;

@ApiStatus.Internal
public interface InternalClientAPI
{
    InternalClientAPI INSTANCE = Utils.loadService(InternalClientAPI.class);



    void registerModelWrapper(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger);

    void registerSpecialModelWrapper(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger);

    void registerCopyingModelWrapper(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger);

    void enqueueClientTask(long delay, Runnable task);

    BakedModel createFluidModel(Fluid fluid);
}
