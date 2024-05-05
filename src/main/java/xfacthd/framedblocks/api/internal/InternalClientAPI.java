package xfacthd.framedblocks.api.internal;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import xfacthd.framedblocks.api.util.Utils;

@ApiStatus.Internal
public interface InternalClientAPI
{
    InternalClientAPI INSTANCE = Utils.loadService(InternalClientAPI.class);



    void registerModelWrapper(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger);

    void registerSpecialModelWrapper(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger);

    void registerCopyingModelWrapper(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger);

    BlockDebugRenderer<FramedBlockEntity> getConnectionDebugRenderer();
}
