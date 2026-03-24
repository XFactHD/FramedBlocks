package io.github.xfacthd.framedblocks.client.model.overlaygen;

import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.resources.model.sprite.Material;

record SpriteInfo(Material.Baked material, Transparency transparency) { }
