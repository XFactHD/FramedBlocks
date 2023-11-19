package xfacthd.framedblocks.api.internal;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

@ApiStatus.Internal
public interface InternalClientAPI
{
    InternalClientAPI INSTANCE = Utils.loadService(InternalClientAPI.class);



    void registerModelWrapper(
            Holder<Block> block,
            GeometryFactory geometryFactory,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    );

    void registerSpecialModelWrapper(
            Holder<Block> block,
            ModelFactory modelFactory,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    );

    void registerCopyingModelWrapper(
            Holder<Block> block,
            Holder<Block> srcBlock,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    );

    void enqueueClientTask(long delay, Runnable task);

    BakedModel createFluidModel(Fluid fluid);
}
