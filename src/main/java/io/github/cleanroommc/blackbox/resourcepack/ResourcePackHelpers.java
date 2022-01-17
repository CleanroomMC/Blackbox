package io.github.cleanroommc.blackbox.resourcepack;

import com.google.gson.JsonObject;
import io.github.cleanroommc.blackbox.Blackbox;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;
import net.minecraft.client.resources.data.MetadataSerializer;

import java.util.*;

public final class ResourcePackHelpers {

	private static final LinkedList<IResourcePack> packsToInject = new LinkedList<>();
	private static final Map<Class<IMetadataSection>, IMetadataSectionSerializer<IMetadataSection>> metadataSerializersToInject = new Reference2ObjectOpenHashMap<>();

	/**
	 * Silently inject a custom IResourcePack implementation into the default resource pack space.
	 *
	 * @param pack        Custom IResourcePack implementation to be injected
	 * @param prioritised If this IResourcePack should be prioritised above other resource packs
	 */
	public static void injectPack(IResourcePack pack, boolean prioritised) {
		if (prioritised) {
			packsToInject.addFirst(pack);
		} else {
			packsToInject.addLast(pack);
		}
	}

	/**
	 * Inject a custom IMetadataSection serializer into Minecraft's main MetadataSerializer
	 *
	 * @param sectionClass IMetadataSection class for type identification
	 * @param serializer   Serializer implementation for the specified IMetadataSection
	 */
	public static <T extends IMetadataSection> void injectMetadataSerializer(Class<T> sectionClass, IMetadataSectionSerializer<T> serializer) {
		if (metadataSerializersToInject.containsKey(sectionClass)) {
			Blackbox.LOGGER.error("{}'s serializer is going to be overriden.", sectionClass);
		}
		metadataSerializersToInject.put((Class<IMetadataSection>) sectionClass, (IMetadataSectionSerializer<IMetadataSection>) serializer);
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

	public static Map<Class<IMetadataSection>, IMetadataSectionSerializer<IMetadataSection>> getInjectedMetadataSerializers() {
		return Collections.unmodifiableMap(metadataSerializersToInject);
	}

	private ResourcePackHelpers() { }

}
