package xfacthd.framedblocks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xfacthd.framedblocks.common.block.stairs.standard.FramedDoubleStairsBlock;

@Mixin(StairBlock.class)
public class MixinStairBlock
{
    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"
            )
    )
    @SuppressWarnings({ "ConstantValue", "rawtypes" })
    private Object framedblocks$preventWaterloggedDefaultValue(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original)
    {
        if (property != BlockStateProperties.WATERLOGGED || ((Class) this.getClass()) != FramedDoubleStairsBlock.class)
        {
            return original.call(instance, property, comparable);
        }
        return instance;
    }
}
