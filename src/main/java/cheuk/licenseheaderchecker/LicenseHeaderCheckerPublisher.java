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
import cheuk.licenseheaderchecker.resource.LicenseHeaderCheckerFile;
import hudson.model.Action;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author cheuk
 */
public class LicenseHeaderCheckerPublisher extends Publisher{
    
    public String sourceDir;
    public String licenseDir;
    
    @DataBoundConstructor
    public LicenseHeaderCheckerPublisher(String sourceDir, String licenseDir) {
        this.sourceDir = sourceDir;
        this.licenseDir = licenseDir;
    }
    
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
    

    @Override
    @SuppressWarnings("null")
    public boolean perform (AbstractBuild build, Launcher launcher, BuildListener listener){
        listener.getLogger().println("Running License Header Checker  result reporter");
        LicenseHeaderCheckerFile.initialize();
        List<LicenseHeaderCheckerFile> licenseTemplates = LicenseHeaderCheckerParser.doParse(build.getWorkspace(), build.getWorkspace().child(licenseDir));
        List<LicenseHeaderCheckerFile> sourceFiles = LicenseHeaderCheckerParser.doParse(build.getWorkspace(), build.getWorkspace().child(sourceDir));
        Iterator<LicenseHeaderCheckerFile> it = sourceFiles.iterator();
        while (it.hasNext()){
            LicenseHeaderCheckerFile currentFile = it.next();
            currentFile.setMatchedTemplateID(LicenseHeaderCheckerParser.checkAgainst(currentFile, licenseTemplates));
            if (currentFile.getMatchedTemplateID() == -1){
                listener.getLogger().println(currentFile.getFilename() + "has no license header!");
            }
        }
        
        final LicenseHeaderCheckerResult result = new LicenseHeaderCheckerResult(build, licenseTemplates, sourceFiles);
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
