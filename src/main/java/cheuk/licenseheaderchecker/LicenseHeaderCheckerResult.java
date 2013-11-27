/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cheuk.licenseheaderchecker;

import cheuk.licenseheaderchecker.resource.LicenseHeaderCheckerFile;
import hudson.model.AbstractBuild;
import hudson.model.Item;
import hudson.util.ChartUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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
    private List<LicenseHeaderCheckerFile> licenseTemplates;
    private List<LicenseHeaderCheckerFile> sourceFiles;
    private double matchedRate;

    public LicenseHeaderCheckerResult(AbstractBuild owner, List<LicenseHeaderCheckerFile> licenseTemplates, List<LicenseHeaderCheckerFile> sourceFiles) {
        this.owner = owner;
        this.licenseTemplates = licenseTemplates;
        this.sourceFiles = sourceFiles;
        calcMatchedRate();
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
    
    public List<LicenseHeaderCheckerFile> getLicenseTemplates(){
        return licenseTemplates;
    }
    
    public List<LicenseHeaderCheckerFile> getSourceFiles(){
        return sourceFiles;
    }
    
    public double getMatchedRate() {
        return matchedRate;
    }
    
    private void calcMatchedRate(){
        int i = 0;
        Iterator<LicenseHeaderCheckerFile> it = sourceFiles.iterator();
        while (it.hasNext())
        {
            if (it.next().isMatched()){
                i++;
            }
        }
        if (sourceFiles.size() > 0)
        {
            matchedRate = 100 * i / sourceFiles.size();
        }
        else
        {
            matchedRate = 100;
        }
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
