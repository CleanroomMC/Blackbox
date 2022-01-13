package io.github.cleanroommc.blackbox.shaders;

import io.github.cleanroommc.blackbox.Blackbox;
import net.minecraft.client.shader.ShaderLoader;
import net.minecraftforge.client.EnumHelperClient;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class ShaderTypes {

	public static final ShaderLoader.ShaderType GEOMETRY = add("GEOMETRY", ".geo", GL32.GL_GEOMETRY_SHADER);
	public static final ShaderLoader.ShaderType TESS_CONTROL = add("TESS_CONTROL", ".tsc", GL40.GL_TESS_CONTROL_SHADER);
	public static final ShaderLoader.ShaderType TESS_EVALUATION = add("TESS_EVALUATION", ".tse", GL40.GL_TESS_EVALUATION_SHADER);
	public static final ShaderLoader.ShaderType COMPUTE = add("COMPUTE", ".cmp", GL43.GL_COMPUTE_SHADER);

	public static void init() {
		Blackbox.LOGGER.info("Intializing custom ShaderTypes.");
		Blackbox.LOGGER.info("Initialized ShaderTypes: {}", Arrays.stream(ShaderLoader.ShaderType.values()).map(e -> e.name()).collect(Collectors.joining(", ")));
		/*
		try {
			// Add ShaderType.class as a clientType
			Field clientTypesField = EnumHelperClient.class.getDeclaredField("clientTypes");
			clientTypesField.setAccessible(true);
			Class<?>[][] clientTypes = (Class<?>[][]) clientTypesField.get(null);
			Class<?>[] shaderTypeArgs = { ShaderLoader.ShaderType.class, String.class, String.class, int.class };
			clientTypesField.set(null, ArrayUtils.add(clientTypes, shaderTypeArgs));
		} catch (ReflectiveOperationException e) {
			Blackbox.LOGGER.fatal("Not able to reflectively add new ShaderTypes to Minecraft!", e);
		}
		 */
	}

	public static ShaderLoader.ShaderType add(String name, String shaderExtension, int shaderMode) {
		return EnumHelperClient.addEnum(ShaderLoader.ShaderType.class, name, new Class<?>[] { String.class, String.class, int.class }, name.toLowerCase(Locale.ENGLISH), shaderExtension, shaderMode);
	}

	/*
	VERTEX(GL20.GL_VERTEX_SHADER, OpenGLVersion.GL20),
	FRAGMENT(GL20.GL_FRAGMENT_SHADER, OpenGLVersion.GL20),
	GEOMETRY(GL32.GL_GEOMETRY_SHADER, OpenGLVersion.GL32),
	TESS_CONTROL(GL40.GL_TESS_CONTROL_SHADER, OpenGLVersion.GL40),
	TESS_EVALUATION(GL40.GL_TESS_EVALUATION_SHADER, OpenGLVersion.GL40),
	COMPUTE(GL43.GL_COMPUTE_SHADER, OpenGLVersion.GL43);

	public final int glCode;
	public final OpenGLVersion openGLVersion;

	ShaderTypes(int glCode, OpenGLVersion openGLVersion) {
		this.glCode = glCode;
		this.openGLVersion = openGLVersion;
	}
	 */

}
