/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cheuk.licenseheaderchecker.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author oscarleung
 */
public class LicenseHeaderCheckerFile {

    static private int nextID = 0;
    
    private int id;
    private String filename;
    private List<String> lines;
    private List<String> rawLines;
    
    private int matchedTemplateID;
    
    public LicenseHeaderCheckerFile(){
        id = nextID;
        matchedTemplateID = -1;
        lines = new ArrayList();
        rawLines = new ArrayList();
        nextID++;
    }
    
    static public void initialize() {
        nextID = 0;
    }
    
    static public int getSize() {
        return nextID;
    }
    
    public int getID() {
        return id;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setMatchedTemplateID(int matchedTemplateID) {
        this.matchedTemplateID = matchedTemplateID;
    }
    
    public int getMatchedTemplateID() {
        return matchedTemplateID;
    }
    
    public boolean isMatched(){
        return (matchedTemplateID != -1);
    }
    
    public boolean isMatched(int templateID){
        return (matchedTemplateID == templateID);
    }
    
    public void addLine(String line) {
        lines.add(line);
    }
    
    public List<String> getLines() {
        return lines;
    }
    
    @Override
    public String toString() {
        Iterator<String> it = lines.iterator();
        String rtnString = "";
        while(it.hasNext()){
            rtnString = rtnString + it.next().trim() + "\n";
        }
        return rtnString;
    }
    
    public void addRawLine(String line) {
        rawLines.add(line);
    }
    
    public List<String> getRawLines() {
        return rawLines;
    }
    
    public String getRawLinesString() {
        Iterator<String> it = rawLines.iterator();
        String rtnString = "";
        while(it.hasNext()){
            rtnString = rtnString + it.next() + "\n";
        }
        return rtnString;
    }
    
}
