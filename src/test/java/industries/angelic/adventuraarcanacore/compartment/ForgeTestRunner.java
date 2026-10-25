package industries.angelic.adventuraarcanacore.compartment;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.LaunchClassLoader;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/** Forge's registries require Loader to be loaded by LaunchClassLoader. */
public class ForgeTestRunner extends BlockJUnit4ClassRunner {

    public ForgeTestRunner(Class<?> testClass) throws InitializationError {
        super(loadTest(testClass));
    }

    @Override
    public void run(RunNotifier notifier) {
        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(
                getTestClass().getJavaClass()
                    .getClassLoader());
            super.run(notifier);
        } finally {
            thread.setContextClassLoader(previous);
        }
    }

    private static Class<?> loadTest(Class<?> testClass) throws InitializationError {
        try {
            List<URL> urls = new ArrayList<>();
            for (String entry : System.getProperty("java.class.path")
                .split(File.pathSeparator)) {
                urls.add(
                    new File(entry).toURI()
                        .toURL());
            }
            LaunchClassLoader loader = new LaunchClassLoader(urls.toArray(new URL[0]));
            loader.addClassLoaderExclusion("org.junit.");
            loader.addClassLoaderExclusion("org.hamcrest.");
            loader.addClassLoaderExclusion(ForgeTestRunner.class.getName());
            return Class.forName(testClass.getName(), true, loader);
        } catch (Exception exception) {
            throw new InitializationError(exception);
        }
    }
}
