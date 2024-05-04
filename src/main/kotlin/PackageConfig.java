import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class PackageConfig {
    @Parameter(property = "heading")
    private String heading;
    @Parameter(property = "packageName")
    private String packageName;
    @Parameter(property = "prefixes")
    private List<String> prefixes;
    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPrefixes(List<String> prefixes) {
        this.prefixes = prefixes;
    }

    public String getHeading() {
        return heading;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<String> getPrefixes() {
        return prefixes;
    }
}
