package xfacthd.framedblocks.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.shapes.ReloadableShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeCache;
import xfacthd.framedblocks.api.util.Utils;

@ApiStatus.Internal
public interface InternalAPI
{
    InternalAPI INSTANCE = Utils.loadService(InternalAPI.class);



    @Nullable
    CamoContainerFactory<?> findCamoFactory(ItemStack stack);

    boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack);

    void enqueueCullingUpdate(Level level, BlockPos pos);

    BlockState getAppearance(
            IFramedBlock framedBlock,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    );

    void registerShapeCache(ShapeCache<?> cache);

    void registerReloadableShapeProvider(ReloadableShapeProvider provider);
}
