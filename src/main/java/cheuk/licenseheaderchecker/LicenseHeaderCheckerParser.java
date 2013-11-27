/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cheuk.licenseheaderchecker;

import cheuk.licenseheaderchecker.resource.Common;
import cheuk.licenseheaderchecker.resource.LicenseHeaderCheckerFile;
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

    public static List<LicenseHeaderCheckerFile> doParse(FilePath workspace, FilePath root, boolean isHeader) {
        List<FilePath> targetFile = Common.locateFiles(root);
        if (targetFile.isEmpty()){
            return null;
        }
        
        List<LicenseHeaderCheckerFile> licenseHeaderCheckerFiles = new ArrayList();
        Iterator<FilePath> it = targetFile.iterator();
        while (it.hasNext()){
            FilePath element = it.next();
            Boolean include = !Common.specificTypes;
            if (Common.specificTypes){
                String[] fileTypes = Common.fileTypes.split(" ");
                String elementType = element.getName().substring(element.getBaseName().length());
                for (int i = 0; i < fileTypes.length; i++)
                {
                    include = include || elementType.equals(fileTypes[i]);
                }
            }
            if (Common.ignoreHidden)
            {
                include = include && element.getName().charAt(0) != '.';
            }
            include = include || isHeader;
            if (include){
                LicenseHeaderCheckerFile currentFile = parseFile(element);
                currentFile.setFilename(element.getRemote().substring(workspace.getRemote().length()+1));
                licenseHeaderCheckerFiles.add(currentFile);
            }
        }
        return licenseHeaderCheckerFiles;
    }

    private static LicenseHeaderCheckerFile parseFile(FilePath filePath) {
        try {
            String fileName = filePath.getRemote();
            File file = new File (fileName);
            BufferedReader br;
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            LicenseHeaderCheckerFile currentFile = new LicenseHeaderCheckerFile();
            while (line != null) {
                currentFile.addRawLine(line);
                currentFile.addLine(line.trim());
                line = br.readLine();
            }
            br.close();
            return currentFile;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LicenseHeaderCheckerParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LicenseHeaderCheckerParser.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
    public static int checkAgainst(LicenseHeaderCheckerFile target, List<LicenseHeaderCheckerFile> template) {
        Iterator<LicenseHeaderCheckerFile> templateIt = template.iterator();
        while (templateIt.hasNext())
        {
            LicenseHeaderCheckerFile currentTemplate = templateIt.next();
            List<String> templateLines = currentTemplate.getLines();
            List<String> targetLines = target.getLines();
            
            int templateLength = templateLines.size();
            int targetLength = targetLines.size();
            if (templateLength == 0){
                continue;
            }
            for (int i = 0; i < targetLength; i++){
                if (targetLines.get(i).equals(templateLines.get(0))){
                    for (int j = 0; j < templateLength; j++)
                    {
                        if (targetLength < i + templateLength ){
                            break;
                        }
                        if (!targetLines.get(i+j).equals(templateLines.get(j))){
                            break;
                        }
                        if (j == templateLength - 1 && targetLines.get(i+j).equals(templateLines.get(j))){
                            return currentTemplate.getID();
                        }
                    }
                }
            }
        }       
        return -1;
    }
    
    
}
