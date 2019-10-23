import com.android.build.gradle.api.BaseVariant
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Android  Gradle Plugin VariantScope Adapter Support
 *
 * @author y.huang
 * @since 2019-10-21
 */
public class VariantScopeCompat {


    public static File getMergedManifestFile(String variantName, Project project) {


        BaseVariant foundVariant = null

        def variants = null
        if (project.plugins.hasPlugin('com.android.application')) {
            variants = project.android.getApplicationVariants()
        } else if (project.plugins.hasPlugin('com.android.library')) {
            variants = project.android.getLibraryVariants()
        }

        variants?.all { BaseVariant variant ->
            if (variant.getName() == variantName) {
                foundVariant = variant
            }
        }
        if (foundVariant == null) {
            throw new GradleException("variant ${variantName} not found")
        }

        def variantScope = foundVariant.getMetaClass().getProperty(foundVariant, 'variantData')
        try {
            //>=3.3.0
            String taskName = variantScope.getTaskContainer().getProcessManifestTask().getName()
            if (project.plugins.hasPlugin('com.android.application')) {
                return new File(project.getTasks().findByName(taskName).getManifestOutputDirectory().get().getAsFile(), "AndroidManifest.xml")
            } else {
                return project.getTasks().findByName(taskName).getManifestOutputFile().get().getAsFile()
            }
        } catch (Exception e) {
        }

        try {
            //>=3.2.0
            return new File(variantScope.getTaskContainer().getProcessManifestTask().getManifestOutputDirectory(), "AndroidManifest.xml")
        } catch (Exception e) {

        }
        try {
            return new File(variantScope.getManifestOutputDirectory(), "AndroidManifest.xml")
        } catch (Exception e) {
            //<=2.3.3
            return variantScope.getOutputs().get(0).getScope().getManifestOutputFile()
        }
    }

}