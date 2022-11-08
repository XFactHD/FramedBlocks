package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

@Mixin(PoweredRailBlock.class)
public class MixinPoweredRailBlock
{
    @ModifyArg(
            method = "<init>*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"),
            index = 0,
            require = 0
    )
    private Property<?> framedblocks_modifyRailShapeProperty(Property<?> property)
    {
        if (isFramedBlock(this) && property == PoweredRailBlock.SHAPE)
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", remap = true),
            index = 0,
            require = 0,
            remap = false
    )
    private Property<?> framedblocks_modifyRailShapeProperty_ForgePatch(Property<?> property)
    {
        if (isFramedBlock(this) && property == PoweredRailBlock.SHAPE)
        {
            return PropertyHolder.ASCENDING_RAIL_SHAPE;
        }
        return property;
    }

    @ModifyArg(
            method = "registerDefaultState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", remap = true),
            index = 1,
            require = 0,
            remap = false
    )
    private Comparable<?> framedblocks_modifyDefaultRailShape_ForgePatch(Comparable<?> comparable)
    {
        if (isFramedBlock(this) && comparable instanceof RailShape)
        {
            return RailShape.ASCENDING_NORTH;
        }
        return comparable;
    }

    @Redirect(
            method = "isSameRailWithPower",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PoweredRailBlock;getRailDirection(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/vehicle/AbstractMinecart;)Lnet/minecraft/world/level/block/state/properties/RailShape;")
    )
    @SuppressWarnings("MethodMayBeStatic")
    private RailShape framedblocks_fixIncorrectRailDirectionSource(PoweredRailBlock instance, BlockState state, BlockGetter level, BlockPos pos, AbstractMinecart cart)
    {
        return ((BaseRailBlock) state.getBlock()).getRailDirection(state, level, pos, cart);
    }

    @Redirect(
            method = "findPoweredRailSignal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;")
    )
    @SuppressWarnings("MethodMayBeStatic")
    private Comparable<?> framedblocks_useDynamicShapeProperty(BlockState state, Property<?> property)
    {
        return state.getValue(((PoweredRailBlock) state.getBlock()).getShapeProperty());
    }

    @Unique
    private static boolean isFramedBlock(Object self)
    {
        return self instanceof IFramedBlock && ((PoweredRailBlock) self).defaultBlockState().hasProperty(PropertyHolder.ASCENDING_RAIL_SHAPE);
    }
}
