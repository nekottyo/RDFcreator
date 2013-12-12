/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.ipu.soft.ds.rdfcreator;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.ac.ipu.soft.ds.ConfigrationXML;
import jp.ac.ipu.soft.ds.RedirectDetector;


/**
 *
 * @author taro
 */
public class RDFcreator {
    static String configFile ="./config.xml";
    static String startSubject = "http://dbpedia.org/resource/JapanConstitution/ChapterTen";
    
    static int endCount = 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ConfigrationXML config = new ConfigrationXML(configFile);
        
        String rootPath = config.getProperty("path");
        String labelPath = config.getProperty("labelpath");
        BufferedReader br = null;
        ArrayList list;
        
        
        try {
            br = new BufferedReader(new FileReader(new File(labelPath)));
            
            String label;
            
            //頭出し
            while((label = br.readLine()) != null){
                if(startSubject.equals(RedirectDetector.getSubjectString(br.readLine())))
                    break;
            }
                
            //label.ntから読み込み
             do {
                File dir = new File(rootPath);
                File[] files = dir.listFiles();
                
                for (File file : files) {
                    File childFiles[] = new File(file.getPath()).listFiles();
                    for(File childFile: childFiles){
                        list = CrawringRDF.getList(childFile, label);
                    }

                }
            }  while ((label = RedirectDetector.getSubjectString(br.readLine())) != null); 
             
        }   catch (FileNotFoundException ex) {
            Logger.getLogger(RDFcreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RDFcreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
}
    
        
    

}
   
