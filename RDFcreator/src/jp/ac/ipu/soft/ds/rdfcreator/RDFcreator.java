/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.ipu.soft.ds.rdfcreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.FileHandler;
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
    //static String startSubject = "http://dbpedia.org/resource/JapanConstitution/ChapterTen";
    static String startSubject = "http://dbpedia.org/resource/Jutland_Peninsula";
//    static String startSubject = "http://dbpedia.org/resource/Autism";
//    static int endCount = 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ConfigrationXML config = new ConfigrationXML(configFile);
        
        Date date = new Date();
        Format sdf = new SimpleDateFormat("yyyyMMdd");
        String now = sdf.format(sdf);
        try {
            FileHandler fh = new FileHandler(now + ".log", true);
            LOG.addHandler(fh);
            
        } catch (IOException ex) {
            Logger.getLogger(RDFcreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(RDFcreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        String rootPath = config.getProperty("path");
        String labelPath = config.getProperty("labelpath");
        BufferedReader br = null;
        ArrayList labelList, list;
        
        int endCounter = 0;
        
        
        try {
            br = new BufferedReader(new FileReader(new File(labelPath)));

            String label;
            
            //頭出し
            while((label = br.readLine()) != null){
                if(startSubject.equals(RedirectDetector.getSubjectString(label)))
                    break;
            }
                
            //label.ntから読み込み
            do {
                labelList = new ArrayList();
                File dir = new File(rootPath);
                File[] files = dir.listFiles();
                int fileCounter = 0;
                endCounter++;
                
                for (File file : files) {
                    //label値をそれぞれのファイルから取得
                    LOG.log(Level.INFO, "{0}/{1}Looking for from {2} to {3}", new Object[]{++fileCounter, files.length, file.getName(), RedirectDetector.getSubjectString(label)});
                    
                    list = CrawringRDF.getList(file, RedirectDetector.getSubjectString(label));
                    
                    
                    LOG.log(Level.INFO, "list is empty?: {0}", list.isEmpty());
                    for(Iterator ite = list.iterator(); ite.hasNext();) {
                        labelList.add(ite.next());
                    }
                    LOG.log(Level.INFO, "Finish Read {0}", file.getPath());
                }
                
                LOG.log(Level.INFO, "Complite; {0}", label);

                
                LOG.info("Writing: "+ "./resource/" + getLabelName(label) + ".nt");
                File writeFile = new File("./resource/" + getLabelName(label) + ".nt");
                writeFile.createNewFile();
                try (PrintWriter pw = new PrintWriter(new BufferedWriter(new PrintWriter(writeFile)))) {
                    for(Iterator ite = labelList.iterator(); ite.hasNext();){
                        pw.println(ite.next());
                    }
                }

                
                LOG.log(Level.INFO, "Finish!: {0}", label);
                if(endCounter > 800)
                    break;
            }  while ((label = br.readLine()) != null); 
             

             
        }   catch (FileNotFoundException ex) {
            Logger.getLogger(RDFcreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(RDFcreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private static final Logger LOG = Logger.getLogger(RDFcreator.class.getName());
    
    
    public static String getLabelName(String label){
        String subString = label.substring(label.indexOf("/resource/")+10, label.indexOf(">"));
        
        if(subString.indexOf("/") != -1)
            subString = subString.replaceAll("/", "_");
        
          return subString;
        
    }
    
    
    

}
   
