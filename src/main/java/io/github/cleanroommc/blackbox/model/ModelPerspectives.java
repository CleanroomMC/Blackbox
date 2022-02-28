package io.github.cleanroommc.blackbox.model;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import java.util.EnumMap;

import static net.minecraftforge.client.model.ForgeBlockStateV1.Transforms.convert;
import static net.minecraftforge.client.model.ForgeBlockStateV1.Transforms.leftify;

/**
 * Used for {@link net.minecraft.client.renderer.block.model.IBakedModel#handlePerspective(TransformType)}
 */
public class ModelPerspectives {

	public static final ModelPerspectives BLOCK;
	public static final ModelPerspectives NORMAL_ITEM;
	public static final ModelPerspectives HANDHELD_ITEM;

	static {
		EnumMap<TransformType, TRSRTransformation> block = new EnumMap<>(TransformType.class);
		block.put(TransformType.GUI, convert(0, 0, 0, 30, 225, 0, 0.625F));
		block.put(TransformType.GROUND, convert(0, 3, 0, 0, 0, 0, 0.25F));
		block.put(TransformType.FIXED, convert(0, 0, 0, 0, 0, 0, 0.5F));
		TRSRTransformation thirdPersonBlock = convert(0, 2.5F, 0, 75, 45, 0, 0.375F);
		block.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdPersonBlock);
		block.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPersonBlock));
		block.put(TransformType.FIRST_PERSON_RIGHT_HAND, convert(0, 0, 0, 0, 45, 0, 0.4F));
		block.put(TransformType.FIRST_PERSON_LEFT_HAND, convert(0, 0, 0, 0, 225, 0, 0.4F));
		BLOCK = new ModelPerspectives(block);

		EnumMap<TransformType, TRSRTransformation> item = new EnumMap<>(TransformType.class);
		item.put(TransformType.GROUND, convert(0, 2, 0, 0, 0, 0, 0.5F));
		item.put(TransformType.HEAD, convert(0, 13, 7, 0, 180, 0, 1));
		TRSRTransformation thirdPersonItem = convert(0, 3, 1, 0, 0, 0, 0.55F);
		item.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdPersonItem);
		item.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPersonItem));
		TRSRTransformation firstPersonItem = convert(1.13F, 3.2F, 1.13F, 0, -90, 25, 0.68F);
		item.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstPersonItem);
		item.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstPersonItem));
		item.put(TransformType.FIXED, convert(0, 0, 0, 0, 180, 0, 1));
		NORMAL_ITEM = new ModelPerspectives(item);

		EnumMap<TransformType, TRSRTransformation> handheld = new EnumMap<>(TransformType.class);
		handheld.putAll(item);
		handheld.put(TransformType.THIRD_PERSON_RIGHT_HAND, convert(0, 4, 0.5F, 0, -90, 55, 0.85F));
		handheld.put(TransformType.THIRD_PERSON_LEFT_HAND,  convert(0, 4, 0.5F, 0, 90, -55, 0.85F));
		handheld.put(TransformType.FIRST_PERSON_RIGHT_HAND, convert(1.13F, 3.2F, 1.13F, 0, -90, 25, 0.68F));
		handheld.put(TransformType.FIRST_PERSON_LEFT_HAND,  convert(1.13F, 3.2F, 1.13F, 0, 90, -25, 0.68F));
		HANDHELD_ITEM = new ModelPerspectives(handheld);
	}

	private final EnumMap<TransformType, TRSRTransformation> map;

	public ModelPerspectives(EnumMap<TransformType, TRSRTransformation> map) {
		this.map = map;
	}

	public TRSRTransformation resolveTransformation(TransformType transformType) {
		return map.get(transformType);
	}

	public Matrix4f resolveMatrix(TransformType transformType) {
		return resolveTransformation(transformType).getMatrix();
	}

}
