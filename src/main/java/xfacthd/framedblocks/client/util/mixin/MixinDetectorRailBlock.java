package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

@Mixin(DetectorRailBlock.class)
public class MixinDetectorRailBlock
{
    @ModifyArg(
            method = "<init>*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"),
            index = 0,
            require = 0
    )
    private Property<?> framedblocks_modifyRailShapeProperty(Property<?> property)
    {
        if (isFramedBlock(this) && property == DetectorRailBlock.SHAPE)
        {
            return PropertyHolder.ASCENDING_RAIL_SHAPE;
        }
        return property;
    }

    @ModifyArg(
            method = "<init>*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"),
            index = 1,
            require = 0
    )
    private Comparable<?> framedblocks_modifyDefaultRailShape(Comparable<?> comparable)
    {
        if (isFramedBlock(this) && comparable instanceof RailShape)
        {
            return RailShape.ASCENDING_NORTH;
        }
        return comparable;
    }

    @ModifyArg(
            method = "registerDefaultState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"),
            index = 0,
            require = 0
    )
    private Property<?> framedblocks_modifyRailShapeProperty_ForgePatch(Property<?> property)
    {
        if (isFramedBlock(this) && property == DetectorRailBlock.SHAPE)
        {
            return PropertyHolder.ASCENDING_RAIL_SHAPE;
        }
        return property;
    }

    @ModifyArg(
            method = "registerDefaultState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"),
            index = 1,
            require = 0
    )
    private Comparable<?> framedblocks_modifyDefaultRailShape_ForgePatch(Comparable<?> comparable)
    {
        if (isFramedBlock(this) && comparable instanceof RailShape)
        {
            return RailShape.ASCENDING_NORTH;
        }
        return comparable;
    }

    @Unique
    private static boolean isFramedBlock(Object self)
    {
        return self instanceof IFramedBlock && ((DetectorRailBlock) self).defaultBlockState().hasProperty(PropertyHolder.ASCENDING_RAIL_SHAPE);
    }
}
