package xfacthd.framedblocks.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface AccessorIngredient
{
    @Accessor("values")
    Ingredient.Value[] framedblocks$getValues();
}
