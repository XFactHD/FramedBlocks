package xfacthd.framedblocks.client.overlaygen;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.Arrays;

record OverlayCacheKey(Direction face, int[] keyData, TextureAtlasSprite sprite)
{
    public void pos(int vert, Vector3f out)
    {
        int offset = vert * 4;
        out.set(
                Float.intBitsToFloat(keyData[offset]),
                Float.intBitsToFloat(keyData[offset + 1]),
                Float.intBitsToFloat(keyData[offset + 2])
        );
    }

    public void normal(int vert, Vector3f out)
    {
        int offset = vert * 4 + 3;
        int packedNormal = keyData[offset];
        out.set(
                ((byte) (packedNormal & 0xFF)) / 127F,
                ((byte) ((packedNormal >> 8) & 0xFF)) / 127F,
                ((byte) ((packedNormal >> 16) & 0xFF)) / 127F
        );
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        OverlayCacheKey key = (OverlayCacheKey) o;
        return face == key.face && Arrays.equals(keyData, key.keyData) && sprite == key.sprite;
    }

    @Override
    public int hashCode()
    {
        int result = face.hashCode();
        result = 31 * result + Arrays.hashCode(keyData);
        result = 31 * result + sprite.hashCode();
        return result;
    }
}
