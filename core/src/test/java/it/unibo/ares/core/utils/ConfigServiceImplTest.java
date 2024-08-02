package it.unibo.ares.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.utils.configservice.ConfigServiceImpl;

class ConfigServiceImplTest {

    private static final String CONFIG_SERVICE = "it.unibo.ares.core.utils.configservice.ConfigServiceImpl";
    private static final String METHOD_NAME = "read";

    @Test
    void testFlagFalse() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        // Get the method
        final Method method = Class.forName(CONFIG_SERVICE)
                .getDeclaredMethod(
                        METHOD_NAME, String.class, String.class, Class.class);

        method.setAccessible(true);

        // Create an instance of ConfigServiceImpl
        final ConfigServiceImpl instance = ConfigServiceImpl.getInstance();

        // Invoke the method
        final Object obj = method.invoke(instance, "Test", "flag_false",
                Boolean.class);
        if (obj instanceof Optional) {
                        final Optional<Boolean> flagValue = (Optional<Boolean>) obj;

            assertNotNull(flagValue);
            assertEquals(false, flagValue.orElse(true));
        } else {
            fail("The returned object is not an instance of Optional<Boolean>");
        }
    }

    @Test
    void testFlagTrue() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        // Get the method
        final Method method = Class.forName(CONFIG_SERVICE)
                .getDeclaredMethod(
                        METHOD_NAME, String.class, String.class, Class.class);

        method.setAccessible(true);

        // Create an instance of ConfigServiceImpl
        final ConfigServiceImpl instance = ConfigServiceImpl.getInstance();

        // Invoke the method
        final Object obj = method.invoke(instance, "Test", "flag_true",
                Boolean.class);
        if (obj instanceof Optional) {
                        final Optional<Boolean> flagValue = (Optional<Boolean>) obj;

            assertNotNull(flagValue);
            assertEquals(true, flagValue.orElse(false));
        } else {
            fail("The returned object is not an instance of Optional<Boolean>");
        }
    }
}
