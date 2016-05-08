/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt;

import java.io.*;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.RandomAccessFile;
//import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileSplitMerge {

//chunk size
    public static final long basePieceSize = (long)(1 * 1024 * 1024);
    
    public static long pieceSize = basePieceSize;
    
 //splitting the file    
    public static int SplitFile(String fName, String outDir) throws FileNotFoundException, IOException{
        
       
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fName));
       
        
            File file = new File(fName);
            long fSize = file.length();

            int sFile;
            PrintWriter write = null;
            try{
                System.out.println(outDir);
                write = new PrintWriter(outDir+"\\"+"piece.nasa", "UTF-8");

            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
   //writing the chunk information to piece.nasa
            for(sFile = 0; sFile < fSize / pieceSize; sFile++){
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outDir + "\\" + file.getName() + "." + sFile));
                write.print(sFile + "\t");

                for(int currByte = 0; currByte < pieceSize; currByte++){
                    bos.write(bis.read());
                }
                
                bos.close();
                
            }
    //creating chunks        
            if(fSize != pieceSize * (sFile -1)){
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outDir + "\\" + file.getName() + "." + sFile));
                write.print(sFile + "\t");
                
                int x;
                while((x = bis.read()) != -1){
                    bos.write(x);
                }
                bos.close();
                
            }
            
            bis.close();
            write.close();
            if(sFile==0){
                return 1;
            }
            return sFile + 1;      
    }
    
    //joining the file
    public static void JoinFile(String bfName) throws IOException{
        
        String dir = "C:\\wamp\\www\\BT\\Chunks\\" + removeExtension(bfName);
        int numPrts = getNumberOfParts(dir+"\\"+bfName);
        System.out.println("number of parts : " + numPrts);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("C:\\Project\\BT\\Downloaded_Files\\"+bfName));
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("C:\\Project\\BT\\Downloaded_Files\\"+bfName));
        //File f = new File("C:\\Projects\\BT\\Downloaded_Files\\"+bfName);
        
        RandomAccessFile raf = new RandomAccessFile("C:\\Project\\BT\\Downloaded_Files\\"+bfName, "rw");
        byte[] count;
    //joining all chunks together
        for(int prt = 0; prt < numPrts; prt++){
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dir + "\\" + bfName + "." + prt));
            
            RandomAccessFile fis = new RandomAccessFile(dir + "\\" + bfName + "." + prt, "r");
            File file = new File(dir + "\\" + bfName + "." + prt);
            System.out.println(dir + "\\" + bfName + "." + prt);
            count = new byte[2 * 1024 * 1024];
            
            
            
            int a;
            while((a = fis.read(count)) != -1){
                raf.write(count,0,a);
            }
            bis.close();
            fis.close();
            fis = null;
            
        }
        bos.close();
        
        
    }
   //Retrieving total number of chunks. 
    private static int getNumberOfParts(String bFName){
        File dir = new File(bFName).getAbsoluteFile().getParentFile();
        final String onlyFileName = new File(bFName).getName();
        String[] match = dir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dire, String name) {
                return name.startsWith(onlyFileName) && name.substring(onlyFileName.length()).matches("^\\.\\d+$");
            }
        });
        return match.length;
        
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
