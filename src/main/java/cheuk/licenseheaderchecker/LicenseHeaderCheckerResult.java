/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cheuk.licenseheaderchecker;

import cheuk.licenseheaderchecker.resource.LicenseHeaderCheckerReport;
import cheuk.licenseheaderchecker.resource.SourceFile;
import hudson.model.AbstractBuild;
import hudson.model.Item;
import hudson.util.ChartUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author cheuk
 */
public class LicenseHeaderCheckerResult implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private AbstractBuild<?, ?> owner;
    private List<LicenseHeaderCheckerReport> licenseHeaderCheckerReports;
    private HashMap<String, SourceFile> sourceFileHash;
            
    public LicenseHeaderCheckerResult(AbstractBuild <?, ?> owner, List<LicenseHeaderCheckerReport> licenseHeaderCheckerReports, HashMap<String, SourceFile> sourceFileHash){
        this.owner = owner;
        this.licenseHeaderCheckerReports = licenseHeaderCheckerReports;
        this.sourceFileHash = sourceFileHash;
    }
    
    public LicenseHeaderCheckerResult getPreviousresult(){
        LicenseHeaderCheckerBuildAction previousAction = getPreviousAction();
        LicenseHeaderCheckerResult previousResult = null;
        if (previousAction != null){
            previousResult = previousAction.getResult();
        }
        
        return previousResult;
    }

    private LicenseHeaderCheckerBuildAction getPreviousAction() {
        AbstractBuild<?, ?> previousBuild = owner.getPreviousBuild();
        if (previousBuild != null){
            return previousBuild.getAction(LicenseHeaderCheckerBuildAction.class);
        }
        return null;
    }
    
    public AbstractBuild<?, ?> getOwner(){
        return owner;
    }
    
    public List<LicenseHeaderCheckerReport> getLicenseHeaderCheckerReports(){
        return licenseHeaderCheckerReports;
    }
    
    public HashMap<String, SourceFile> getSourceFileHash(){
        return sourceFileHash;
    }
    
    @SuppressWarnings("unused")
    public Object getDynamic(final String link, final StaplerRequest request,
                            final StaplerResponse response) throws IOException{
        String linkModified = link.replaceAll("=", "/");

        if (linkModified.startsWith("source.")){
            if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)){
                response.sendRedirect2("nosourcepermission");
                return null;
            }
            if (sourceFileHash.containsKey(linkModified.replaceFirst("source.", ""))){
                return sourceFileHash.get(linkModified.replaceFirst("source.", ""));
            }
        }
        return null;
    }
    
    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null){
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }
        
        AbstractBuild<?, ?> build = getOwner();
        Calendar timestamp = build.getTimestamp();
        
        if (req.checkIfModified(timestamp, rsp)){
            return;
        }
        
        LicenseHeaderCheckerBuildAction buildAction = owner.getAction(LicenseHeaderCheckerBuildAction.class);
        if (buildAction != null){
            buildAction.doGraph(req, rsp);
        }
    }
}
