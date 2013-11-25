/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cheuk.licenseheaderchecker;

import cheuk.licenseheaderchecker.resource.Common;
import cheuk.licenseheaderchecker.resource.LicenseHeaderCheckerReport;
import hudson.FilePath;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author cheuk
 */
public class LicenseHeaderCheckerParser {
    static public List<LicenseHeaderCheckerReport> doParse(FilePath root){
        List<FilePath> targetFile = Common.locateFiles(root);
        if (targetFile.isEmpty()){
            return null;
        }
        
        List<LicenseHeaderCheckerReport> licenseHeaderCheckerReports = new ArrayList();
        Iterator<FilePath> it = targetFile.iterator();
        while (it.hasNext()){
            FilePath element = it.next();
            licenseHeaderCheckerReports.addAll(parseSmell(element));
        }
        return licenseHeaderCheckerReports;
    }

    static private List<LicenseHeaderCheckerReport> parseSmell(FilePath filePath) {
        try {
            String fileName = filePath.getRemote();
            File file = new File (fileName);
            BufferedReader br;
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            List <LicenseHeaderCheckerReport> licenseHeaderCheckerReports = new ArrayList();

            while (line != null) {
                if (line.trim().matches("- !ruby/object:Reek::SmellWarning")) {
                    LicenseHeaderCheckerReport currentReport = new LicenseHeaderCheckerReport();
                    line = br.readLine();
                    while (line != null && !line.matches("- !ruby/object:Reek::SmellWarning")) {
                        setCurrentReportByLine(currentReport, line);
                        line = br.readLine();
                    }
                    licenseHeaderCheckerReports.add(currentReport);
                }
                else {
                    line = br.readLine();
                }
            }
            br.close();
            return licenseHeaderCheckerReports;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LicenseHeaderCheckerParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LicenseHeaderCheckerParser.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
    
    private static void setCurrentReportByLine(LicenseHeaderCheckerReport report, String line) {
        if (line.trim().matches("source:(.*)")){
            report.setSource(line.replaceFirst("source:", "").trim());
        }
        if (line.trim().matches("- ([0-9]+)")){
            report.addLine(Integer.parseInt(line.replaceFirst("-", "").trim()));
        }
        if (line.trim().matches("context:(.*)")){
            report.setContext(line.replaceFirst("context:", "").trim());
        }
        if (line.trim().matches("class:(.*)")){
            report.setSmellClass(line.replaceFirst("class:", "").trim());
        }
        if (line.trim().matches("subclass:(.*)")){
            report.setSubClass(line.replaceFirst("subclass:", "").trim());
        }
        if (line.trim().matches("message:(.*)")){
            report.setMessage(line.replaceFirst("message:", "").trim());
        }
        if (line.trim().matches("occurrences:(.*)")){
            report.setOccurrences(Integer.parseInt(line.replaceFirst("occurrences:", "").trim()));
        }
        if (line.trim().matches("depth:(.*)")){
            report.setDepth(Integer.parseInt(line.replaceFirst("depth:", "").trim()));
        }
        if (line.trim().matches("call:(.*)")){
            report.setCall(line.replaceFirst("call:", "").trim());
        }
        if (line.trim().matches("method_name:(.*)")){
            report.setMethodName(line.replaceFirst("method_name:", "").trim());
        }
        if (line.trim().matches("variable_name:(.*)")){
            report.setVariableName(line.replaceFirst("variable_name:", "").trim());
        }
        if (line.trim().matches("parameter:(.*)")){
            report.setParameter(line.replaceFirst("parameter:", "").trim());
        }
        if (line.trim().matches("is_active:( *)true( *)")){
            report.setIsActive(true);
        }
        if (line.trim().matches("is_active:( *)false( *)")){
            report.setIsActive(false);
        }
        
        
    }
    
    
}
