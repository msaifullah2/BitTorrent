/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


public class DownloadChunk extends Thread {
    String fURL = "";
    String destDir = "";
    String FName = "";
    //private static final Logger l = Logger.getLogger(DownloadChunk.class.getName());
    
    DownloadChunk(){
        
    }
   //chunk constructor 
    DownloadChunk(String fileURL, String dDir, String FName){
        destDir = dDir;
        fURL = fileURL;
        this.FName = FName;
    }
    //start of download chunk thread
    public void run() {
        dlChunk(fURL, destDir, FName);
    }
   //download a chunk 
    public synchronized void dlChunk(String fURL, String destDir, String FName){
        String dlFileName = fURL.substring(fURL.lastIndexOf("/") + 1);
        File dlsDir = new File(destDir);
        System.out.println(destDir);
        
        if(!dlsDir.exists()){
            dlsDir.mkdirs();
        }
        
        URL url = null;
        
        try{
            url = new URL(fURL);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        
        InputStream in = null;
        
        try{
            in = url.openStream();
         }catch(IOException e){
             e.printStackTrace();
         }
        DataOutputStream dos = null;
        FileOutputStream fo = null;
        BufferedOutputStream bos = null;
        
        
        try{
            fo = new FileOutputStream(destDir + dlFileName);
            bos = new BufferedOutputStream(fo);
            dos = new DataOutputStream(fo);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        byte[] buff = new byte[1048576];
        int bRead = 0;
        PrintWriter write2 = null;
        System.out.println("Downloading File : "+ dlFileName);
        String[] splitName = dlFileName.split("\\.");
        int splitNameSize = splitName.length;
        String chunkNumber = (splitName[splitNameSize - 1]);
        try{
            while((bRead = in.read(buff)) >= 0){
               
               dos.write(buff,0,bRead);
            }
      //writing the information about the chunk download      
            write2 = new PrintWriter(new BufferedWriter(new FileWriter("C:\\wamp\\www\\BT\\Chunks\\" + removeExtension(FName) + "\\piece.nasa", true)));
            write2.print(chunkNumber + "\t");
            System.out.println("Chunk " + dlFileName + " downloaded");
        }catch(IOException e){
            e.printStackTrace();
        }
        
        //System.out.println("Downloading has Finished Successfully.");
        try{
            fo.close();
            write2.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        try{
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    static String removeExtension (String s){
        if(s == null){
            return null;
        }
        
        int position = s.lastIndexOf(".");
        
        if(position == -1){
            return s;
        }
        
        return s.substring(0,position);
    }
}
