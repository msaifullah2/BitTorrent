/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerPeer extends Thread{
    
 //start of the server thread
	public void run(){
        ServerSocket s = null;
        PrintWriter write = null;
        //creating peerresonse.log
        try{
            write = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Project\\BT\\CSLogs\\"+"server.log", true)));
            
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
     //Creating a new socket   
        try{
            s = new ServerSocket(1991);
        }catch(IOException e){
            e.printStackTrace();
        }
        
        while(true){
            
            write.println(new java.util.Date() + " : Server Initialized");
            write.close();
            
            Socket c = null;
        //Accept incoming socket connection   
            try{
                c = s.accept();
            }catch(IOException e){
                e.printStackTrace();
            }
            
            try{
                write = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Project\\BT\\CSLogs\\" + "server.log", true)));
            }catch(IOException e){
                e.printStackTrace();
            }
            
            write.println(new java.util.Date() + " : Connection established with with : " + c.getInetAddress());
            write.close();
            
            BufferedReader buff = null;
            
            try{
                buff = new BufferedReader(new InputStreamReader(c.getInputStream()));
                
            }catch(IOException e){
                e.printStackTrace();
            }
            String str = null;
            try{
                str = buff.readLine();
            }catch(IOException e){
                e.printStackTrace();
            }
            
            
            PrintStream p = null;
            
            try{
                p = new PrintStream(c.getOutputStream());
            }catch(IOException e){
                e.printStackTrace();
            }
            
            try{
                write = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Project\\BT\\CSLogs\\" + "server.log", true)));
            }catch(IOException e){
                e.printStackTrace();
            }
            
            write.println("Received Message from : " + c.getInetAddress() + " : '" + str + "'");
            write.close();
            
            String[] strTemp = str.split("\t");
            String pi = null;
          //sending info about available pieces (of the file requested) to the requesting peer.
            if(strTemp[0]. equals("Requesting")){
                File file = new File("C:\\wamp\\www\\BT\\Chunks\\" + removeExtension(strTemp[1]));
          //If file being requested exists
                if(file.isDirectory()){
                    FileReader chunkInfo = null;
                    try{
                        chunkInfo = new FileReader("C:\\wamp\\www\\BT\\Chunks\\" + removeExtension(strTemp[1]) + "\\piece.nasa");
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    
                    try{
                        BufferedReader read = new BufferedReader(chunkInfo);
                        pi = read.readLine();
                    }catch(IOException e){
                        e.printStackTrace();;
                    }
                    
                   p.println("FileRequestResponse\t" + strTemp[1] + "\tYes\t" + pi);
                   
                   try{
                       chunkInfo.close();
                   }catch(IOException e){
                       e.printStackTrace();
                   }
            //If file being requested, does't exist           
                }else{
                    p.println("FileRequestResponse\t" + strTemp[1] + "\tNo");
                }
            }
                    
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

