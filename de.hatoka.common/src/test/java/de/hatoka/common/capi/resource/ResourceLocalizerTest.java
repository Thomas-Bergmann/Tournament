package de.hatoka.common.capi.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.junit.Test;

import de.hatoka.common.capi.business.CountryHelper;

public class ResourceLocalizerTest
{
    private static final String RESOURCE_PREFIX = "de/hatoka/common/capi/resource/";

    private static ResourceLocalizer UNDER_TEST = new ResourceLocalizer(new LocalizationBundle(RESOURCE_PREFIX
                    + "resourceLocalizer", Locale.US, CountryHelper.UTC));

    @Test
    public void testInjectText()
    {
        assertEquals("resolved text", "A single text parameter is injected 'injectedText'.", UNDER_TEST.getText("inject.single", "Login", "injectedText"));
    }

    @Test
    public void testSimpleText()
    {
        assertEquals("resolved text", "Login Form", UNDER_TEST.getText("simple.text", "Login"));
        assertEquals("fallback text", "Login", UNDER_TEST.getText("simple.text.fallback", "Login"));
        assertNull("no fallback text", UNDER_TEST.getText("simple.text.nofallback", null));
    }

    @Test
    public void testFormatTime()
    {
        assertEquals("minutes converted to hours and minutes", "1:30", UNDER_TEST.formatDuration("90"));
    }

    @Test
    public void testMoney()
    {
        assertEquals("1,234.57 $", UNDER_TEST.formatMoney("1234.567", "USD"));
    }


    @Test
    public void testPercentage()
    {
        assertEquals("12.3%", UNDER_TEST.formatPercentage("0.1234", "1"));
    }
}
