package net.azothe.notyourfriend.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.util.Lazy;


public class ModKeyMappings {
    public static final Lazy<KeyMapping> ABILITY_MENU = Lazy.lazy(() ->
            new KeyMapping("key.notyourfriend.ability_menu",
                    InputConstants.Type.KEYBOARD,
                    InputConstants.KEY_G,
                    ModKeyMappingCategories.NOTYOURFRIEND_CATEGORY
            ));
}
