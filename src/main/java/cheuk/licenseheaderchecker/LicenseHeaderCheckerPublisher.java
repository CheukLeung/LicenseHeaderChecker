/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cheuk.licenseheaderchecker;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;
import cheuk.licenseheaderchecker.resource.LicenseHeaderCheckerReport;
import cheuk.licenseheaderchecker.resource.SourceFile;
import hudson.FilePath;
import hudson.model.Action;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author cheuk
 */
public class LicenseHeaderCheckerPublisher extends Publisher{
    
    public String licenseHeaderCheckerDir;
    HashMap<String, SourceFile> sourceFileHash;
    
    @DataBoundConstructor
    public LicenseHeaderCheckerPublisher(String licenseHeaderCheckerDir) {
        this.licenseHeaderCheckerDir = licenseHeaderCheckerDir;
    }
    
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
    

    @Override
    @SuppressWarnings("null")
    public boolean perform (AbstractBuild build, Launcher launcher, BuildListener listener){
        listener.getLogger().println("Running License Header Checker  result reporter");
        List<LicenseHeaderCheckerReport> licenseHeaderCheckerReports = LicenseHeaderCheckerParser.doParse(build.getWorkspace().child(licenseHeaderCheckerDir));
        listener.getLogger().println("" + LicenseHeaderCheckerReport.getSize() + " warning(s) are found.");
        
        sourceFileHash = new HashMap<String, SourceFile>();
        setSourceFileHash(build, licenseHeaderCheckerReports);
        
        final LicenseHeaderCheckerResult result = new LicenseHeaderCheckerResult(build, licenseHeaderCheckerReports, sourceFileHash);
        final LicenseHeaderCheckerBuildAction action = LicenseHeaderCheckerBuildAction.load(build, result);
        build.getActions().add(action);
        
        return true;
    }
    
    @Override
    public Action getProjectAction(AbstractProject<?, ?> project){
        return new LicenseHeaderCheckerProjectAction(project);
    }
    
    @Override
    public LicenseHeaderCheckerPublisher.DescriptorImpl getDescriptor(){
        return (LicenseHeaderCheckerPublisher.DescriptorImpl)super.getDescriptor();
    }

    private void setSourceFileHash(AbstractBuild<?, ?> build, List<LicenseHeaderCheckerReport> licenseHeaderCheckerReports) {
        Iterator<LicenseHeaderCheckerReport> it = licenseHeaderCheckerReports.iterator();
        while (it.hasNext()){
            LicenseHeaderCheckerReport report = it.next();
            SourceFile sourceFile;
            String fileName = report.getSource();
            if (sourceFileHash.containsKey(fileName)){
                sourceFile = sourceFileHash.get(fileName);
            }
            else {
                sourceFile = new SourceFile(build, fileName);
                sourceFileHash.put(fileName, sourceFile);
            }
            sourceFile.addReport(report);
            
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> type){
            return true;
        }
        
        @Override
        public String getDisplayName(){
            return "License Header Checker result reporter";
        }
    }
}
