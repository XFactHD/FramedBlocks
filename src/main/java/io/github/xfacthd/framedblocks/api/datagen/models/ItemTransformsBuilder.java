package io.github.xfacthd.framedblocks.api.datagen.models;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import org.joml.Vector3f;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
final class ItemTransformsBuilder {
    private ItemTransform thirdPersonLeftHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform thirdPersonRightHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform firstPersonLeftHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform firstPersonRightHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform head = ItemTransform.NO_TRANSFORM;
    private ItemTransform gui = ItemTransform.NO_TRANSFORM;
    private ItemTransform ground = ItemTransform.NO_TRANSFORM;
    private ItemTransform fixed = ItemTransform.NO_TRANSFORM;
    private ItemTransform fixedFromBottom = ItemTransform.NO_TRANSFORM;

    public ItemTransformsBuilder thirdPersonLeftHand(ItemTransform xform) {
        this.thirdPersonLeftHand = xform;
        return this;
    }

    public ItemTransformsBuilder thirdPersonRightHand(ItemTransform xform) {
        this.thirdPersonRightHand = xform;
        return this;
    }

    public ItemTransformsBuilder firstPersonLeftHand(ItemTransform xform) {
        this.firstPersonLeftHand = xform;
        return this;
    }

    public ItemTransformsBuilder firstPersonRightHand(ItemTransform xform) {
        this.firstPersonRightHand = xform;
        return this;
    }

    public ItemTransformsBuilder head(ItemTransform xform) {
        this.head = xform;
        return this;
    }

    public ItemTransformsBuilder gui(ItemTransform xform) {
        this.gui = xform;
        return this;
    }

    public ItemTransformsBuilder ground(ItemTransform xform) {
        this.ground = xform;
        return this;
    }

    public ItemTransformsBuilder fixed(ItemTransform xform) {
        this.fixed = xform;
        return this;
    }

    public ItemTransformsBuilder fixedFromBottom(ItemTransform xform) {
        this.fixedFromBottom = xform;
        return this;
    }

    public ItemTransforms build() {
        return new ItemTransforms(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed, fixedFromBottom, ImmutableMap.of());
    }

    public static final class TransformBuilder {
        private Vector3f rotation = ItemTransform.Deserializer.DEFAULT_ROTATION;
        private Vector3f translation = ItemTransform.Deserializer.DEFAULT_TRANSLATION;
        private Vector3f scale = ItemTransform.Deserializer.DEFAULT_SCALE;
        private Vector3f rightRotation = ItemTransform.Deserializer.DEFAULT_ROTATION;

        public TransformBuilder rotation(Vector3f rotation) {
            this.rotation = rotation;
            return this;
        }

        public TransformBuilder translation(Vector3f translation) {
            this.translation = translation;
            return this;
        }

        public TransformBuilder scale(Vector3f scale) {
            this.scale = scale;
            return this;
        }

        public TransformBuilder rightRotation(Vector3f rightRotation) {
            this.rightRotation = rightRotation;
            return this;
        }

        public ItemTransform build() {
            return new ItemTransform(rotation, translation, scale, rightRotation);
        }
    }
}
