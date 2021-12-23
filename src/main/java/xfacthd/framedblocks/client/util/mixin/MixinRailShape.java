package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RailState.class)
public class MixinRailShape
{
    @Final
    @Shadow
    private BaseRailBlock block;

    @Inject(
            method = "connectTo",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void framedblocks_connectToFilterInvalidState(RailState state, CallbackInfo ci, BlockPos blockpos, BlockPos blockpos1, BlockPos blockpos2, BlockPos blockpos3, boolean flag, boolean flag1, boolean flag2, boolean flag3, RailShape railshape)
    {
        if (this.block.getShapeProperty().getAllValues().noneMatch(value -> value.pValue() == railshape))
        {
            ci.cancel();
        }
    }
}