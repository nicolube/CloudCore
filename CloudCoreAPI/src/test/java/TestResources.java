import co.aikar.locales.MessageKeyProvider;
import de.cloud.core.api.message.CoreMessageKeys;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

public class TestResources {

    @Test
    public void testResources() {
        final ResourceBundle coreEN = ResourceBundle.getBundle("core", Locale.ENGLISH);
        final ResourceBundle coreDE = ResourceBundle.getBundle("core", Locale.GERMAN);
        for (CoreMessageKeys value : CoreMessageKeys.values()) {
            testMessage(value, coreDE);
            testMessage(value, coreEN);
        }
    }

    public void testMessage(MessageKeyProvider key, ResourceBundle bundle) {
        Assert.assertTrue("No messageKey found for (" + bundle.getLocale().getLanguage() + "): " + key.getMessageKey().getKey(),
                bundle.containsKey(key.getMessageKey().getKey()));
    }
}
