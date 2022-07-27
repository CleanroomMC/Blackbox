package com.cleanroommc.blackbox.config;

import com.cleanroommc.blackbox.Blackbox;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.*;

public class BlackboxConfigGui extends GuiConfig {

    private static final String CATEGORY_STRING = "blackbox.options.category.";

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> elements = new ArrayList<>();
        for (Class<?> clazz : ConfigManager.getModConfigClasses(Blackbox.ID)) {
            elements.add(ConfigElement.from(clazz));
        }
        Map<String, DummyCategoryElement> majorCategories = new Object2ObjectOpenHashMap<>(8);
        Iterator<IConfigElement> elementsIter = elements.iterator();
        while (elementsIter.hasNext()) {
            IConfigElement element = elementsIter.next();
            if (element instanceof DummyCategoryElement) {
                int openingSlash = element.getName().indexOf('/', 9);
                if (openingSlash != -1) {
                    int closingSlash = element.getName().indexOf('/', 10);
                    if (closingSlash != -1) {
                        String majorCategoryName = element.getName().substring(10, closingSlash);
                        DummyCategoryElement majorCategory = majorCategories.get(majorCategoryName);
                        List<IConfigElement> childElements;
                        if (majorCategory == null) {
                            childElements = new ArrayList<>();
                            majorCategories.put(majorCategoryName, new DummyCategoryElement("/blackbox/" + majorCategoryName, CATEGORY_STRING + majorCategoryName, childElements));
                        } else {
                            childElements = majorCategory.getChildElements();
                        }
                        childElements.add(element);
                        elementsIter.remove();
                    }
                }
            }
        }
        elements.addAll(majorCategories.values());
        elements.sort(Comparator.comparing(e -> I18n.format(e.getLanguageKey())));
        return elements;
    }

    public BlackboxConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), Blackbox.ID, false, false, Blackbox.NAME);
    }

}
