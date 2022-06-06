package com.cleanroommc.blackbox.util;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraft.client.resources.I18n;

public class I18nHelper {

    public static String getTranslatedString(String category, String key) {
        return I18n.format(String.join(".", Blackbox.ID, category, key));
    }

    private I18nHelper() { }

}
