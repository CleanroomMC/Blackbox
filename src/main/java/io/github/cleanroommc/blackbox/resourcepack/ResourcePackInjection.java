package io.github.cleanroommc.blackbox.resourcepack;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ResourcePackInjection {

	private static final LinkedList<IResourcePack> packsToInject = new LinkedList<>();

	/**
	 * Silently inject a custom IResourcePack implementation into the default resource pack space.
	 *
	 * @param pack        Custom IResourcePack implementation to be injected
	 * @param prioritised If this IResourcePack should be prioritised above other resource packs
	 */
	public static void inject(IResourcePack pack, boolean prioritised) {
		if (prioritised) {
			packsToInject.addFirst(pack);
		} else {
			packsToInject.addLast(pack);
		}
	}

	/**
	 * Helper for generating an IMetadataSection for a custom IResourcePack implementation
	 *
	 * @param serializer          1st argument of {@link IResourcePack#getPackMetadata(MetadataSerializer, String)}
	 * @param metadataSectionName 2nd argument of {@link IResourcePack#getPackMetadata(MetadataSerializer, String)}
	 * @param description         Custom description for the metadata to include
	 * @return                    Generated metadata
	 */
	public static <T extends IMetadataSection> T generateMetadata(MetadataSerializer serializer, String metadataSectionName, String description) {
		JsonObject metadata = new JsonObject();
		JsonObject packObj = new JsonObject();
		metadata.add("pack", packObj);
		packObj.addProperty("description", description);
		packObj.addProperty("pack_format", 2);
		return serializer.parseMetadataSection(metadataSectionName, metadata);
	}

	public static List<IResourcePack> getInjectedPacks() {
		return Collections.unmodifiableList(packsToInject);
	}

	private ResourcePackInjection() { }

}
