/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cheuk.licenseheaderchecker;

import cheuk.licenseheaderchecker.resource.DataGraph;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Result;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import java.io.IOException;
import java.util.Calendar;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author cheuk
 */
public class LicenseHeaderCheckerBuildAction extends Actionable implements Action, StaplerProxy {

    public static final String URL_NAME = "LicenseHeaderCheckerResult";

    public static LicenseHeaderCheckerBuildAction load(AbstractBuild<?, ?> owner, LicenseHeaderCheckerResult result) {
        return new LicenseHeaderCheckerBuildAction(owner, result);
    }
    
    static public LicenseHeaderCheckerBuildAction getPreviousResult(AbstractBuild<?, ?> start) {
        AbstractBuild<?, ?> b = start;
        while (true) {
            b = b.getPreviousNotFailedBuild();
            if (b == null){
                return null;
            }
            assert b.getResult() != Result.FAILURE;
            LicenseHeaderCheckerBuildAction r = b.getAction(LicenseHeaderCheckerBuildAction.class);
            if (r != null){
                return r;
            }
        }
    }
    
    private final AbstractBuild<?, ?> owner;
    private final LicenseHeaderCheckerResult result;
    
    LicenseHeaderCheckerBuildAction(AbstractBuild<?, ?> owner, LicenseHeaderCheckerResult result){
        this.owner = owner;
        this.result = result;
    }
    
    private DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> getDataSetBuilder(){
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb =
                new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
        
        for (LicenseHeaderCheckerBuildAction a = this; a != null; a = a.getPreviousResult()){
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);
            LicenseHeaderCheckerResult aResult = a.getResult();
            dsb.add(aResult.getNumberOfUnmatchedFiles(), "Percentage of files having license header", label);
        }
        
        return dsb;
    }
    
    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null){
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }
        Calendar timestamp = getOwner().getTimestamp();
        
        if (req.checkIfModified(timestamp, rsp)){
            return;
        }
        
        Graph g = new DataGraph(getOwner(), getDataSetBuilder().build(),
                "Percentage of files having license header", 500, 200);
        g.doPng(req, rsp);
    }
    
    
    public LicenseHeaderCheckerBuildAction getPreviousResult(){
        return getPreviousResult(owner);
    }
    
    public String getDisplayName() {
        return "License Header Checker Result";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public String getIconFileName() {
        return "clipboard.png";
    }

    public String getUrlName() {
        return URL_NAME;
    }
    
    public AbstractBuild<?, ?> getOwner(){
        return owner;
    }

    public Object getTarget() {
        return result;
    }

    public LicenseHeaderCheckerResult getResult() {
        return result;
    }
    
}
