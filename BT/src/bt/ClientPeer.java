/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;


public class ClientPeer extends Thread{

    
    String FName = null, FSize = null, FPieces = null, FTracker = null;
    ClientPeer(String fName, String fTracker, String fSize, String fPieces){
        FName = fName;
        FSize = fSize;
        FPieces = fPieces;
        FTracker = fTracker;
    }
   
    //static client IP's for current implementation-Add your Himachi(more information in 'Readme.txt') granted IP address here
    public final static String[] CLIENT_IP = {"25.7.65.9", "25.10.137.150","25.5.136.248"};
    
    //start of the client peer Thread
    public void run(){
        PrintWriter write = null;
        try{
    //Create Peerrequest.log    	
            write = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Project\\BT\\CSLogs\\"+"client.log", true)));
        }catch(IOException ex1){
            ex1.printStackTrace();
        }
     //Initial reply messages 
        String[] message_reply = {"NULL\tUnavailable","NULL\tUnavailable","NULL\tUnavailable","NULL\tUnavailable","NULL\tUnavailable"};
        String x = "";
        String cip = "";
       
     //search for the client IP in the network   
        for(int j = 0; j < CLIENT_IP.length; j++){
            Enumeration<?> cs1 = null;
            
            try{
                cs1 = NetworkInterface.getNetworkInterfaces();
            }catch(SocketException ex2){
                ex2.printStackTrace();
            }
            
            while(cs1.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface) cs1.nextElement();
                
                Enumeration<?> en = ni.getInetAddresses();
                while(en.hasMoreElements()){
                    InetAddress ad1 = (InetAddress) en.nextElement();
                    
                    String ip = ad1.getHostAddress();
                    //System.out.println(ip + "first ip");
       //Make sure, IP is of the client(Himachi specified)             
                    if(ip.substring(0, 3).equals("25.")){
                        cip = ip;
                        //System.out.println(cip + "first ip");
                    }
                    
                }
            }
       //Once the client has been found
            if(!CLIENT_IP[j].equals(cip)){
                try{
                    //System.out.println("hell2");
      //ping client to check if he is online
                    Process p = java.lang.Runtime.getRuntime().exec("ping -n 1 " + CLIENT_IP[j]);
                    int tempReturn = 1;
                    try{
                        tempReturn = p.waitFor();
                    }catch(InterruptedException ex3){
                        ex3.printStackTrace();
                    }
                    
                    boolean can_reach = (tempReturn == 0);
                    if(can_reach){
                        Socket cli = null;
                        
                        try{
                            cli = new Socket(CLIENT_IP[j], 1991);
                        }catch(Exception ex5){
                            ex5.printStackTrace();
                        }
                        
                        write.println(new java.util.Date() + ": File Request for (" + FName + ") being sent to " + CLIENT_IP[j]);
                        write.close();
                        @SuppressWarnings("unused")
                        
                        BufferedReader buff1 = new BufferedReader(new InputStreamReader(System.in));
                        PrintStream prints = null;
                        
                        
                        try{
                            prints = new PrintStream(cli.getOutputStream());
                            
                        }catch(IOException ex6){
                            ex6.printStackTrace();
                        }
                        
                        BufferedReader buff2 = null;
                        try{
                            buff2 = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                        }catch(IOException ex7){
                            ex7.printStackTrace();
                        }
                        
                        try{
                            write = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Project\\BT\\CSLogs\\"+"client.log", true)));
                        }catch(IOException ex8){
                            ex8.printStackTrace();
                        }
                        
                        write.println(new java.util.Date() + ": Requesting information about the File from :" + CLIENT_IP[j]);
                        write.close();
                        
                        String str1 = "Requesting\t" + FName;
                        prints.println(str1);
                        
                        String str2 = null;
                        
                        try{
                            str2 = buff2.readLine();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        
                        try{
                            write = new PrintWriter(new BufferedWriter((new FileWriter("C:\\Project\\BT\\CSLogs\\"+"client.log", true))));
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        
                        write.println(new java.util.Date() + ": Response from " + CLIENT_IP[j] + ": " + str2);
                        
                        
                        try{
                            cli.close();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                //if client is online        
                        message_reply[j] = CLIENT_IP[j] + "\t" + str2;
                        write.close();
                        
                    }
               //if client is offline
                    else{
                        message_reply[j] = CLIENT_IP[j] + "\tUnavailable";
                    }
                }catch(Exception ex4){
                        ex4.printStackTrace();
               }
                
                System.out.println(message_reply[j]);
            }
        }
        //Download the actual file 
        try{
            x = downloadFile(message_reply, FName, FPieces) + "\t";
        }catch(Exception e){
            java.util.logging.Logger.getLogger(ClientPeer.class.getName()).log(Level.SEVERE, null, e);
        }
        //System.out.println("x is :" + x);
        //PrintWriter write2 = null;
        
//        try {
//            write2 = new PrintWriter(new BufferedWriter(new FileWriter("C:\\wamp\\www\\BT\\Chunks\\" + removeExtension(FName) + "\\piece.nasa", true)));
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(ClientPeer.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //write2.println(x);
        //write2.close();
        System.out.println("File " + FName + " has been Downloaded");
    }
    //Download the file
    public String downloadFile(String[] res, String fileName, String pcs) throws IOException {
    	
    //create a Hashmap- for mapping the chunks to client IP's (acts as tracker)
        Map<String, String> dl = new HashMap<String, String>();
        
        for(int j = 0; j < 5; j++){
            String[] t = res[j].split("\t");
            if(!t[1].equals("Unavailable")){
                System.out.println("breakpoint");
                if(t[3].equals("Yes")){
                    for(int i = 4; i < t.length; i++){
                        if(dl.containsKey(t[i])){
                            dl.put(t[i], dl.get(t[i]) + "\t" + t[0]);
                        }else{
                            dl.put(t[i],t[0]);
                        }
                        
                    }
                }
            }
        }
        
        PrintWriter write = null;
        
        try{
            write = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Project\\BT\\CSLogs\\"+"client.log", true)));
           }catch(IOException e){
               e.printStackTrace();
           }
        
        write.println("File being Downloaded: " + fileName + ". It has '" + pcs + "' pieces from the following seeds"  );
        String bURL = "/BT/Chunks/" + removeExtension(fileName)+"/";
        String URL = "";
        
        Iterator<Entry<String, String>> ite = dl.entrySet().iterator();
        
        String c = "";
       //Source address of peers from where chunk has to be downloaded 
       
        List<String> t = new ArrayList<String>();
        
        while(ite.hasNext()){
            Map.Entry prs = (Map.Entry)ite.next();
            String[] str1 = ((String) prs.getValue()).split("\t");
            Random rand = new Random();
            
            int r = rand.nextInt(str1.length);
            URL = "http://"+str1[r] + bURL + fileName + "." + prs.getKey();
            write.println("\t" + URL);
            t.add(URL);
            c += prs.getKey() + "\t";
            System.out.println(prs);
        }
        System.out.println("C:" + c +"\n");
        
        int k = 0;
      //choke implementation- only allow 5 peers to be connected at a time- download only 5 chunks at a time
        
        Thread[] dload = new Thread[5];
        
        dload[0] = null;
        dload[1] = null;
        dload[2] = null;
        dload[3] = null;
        dload[4] = null;
        
        while(k < t.size()){
            for(int m = 0; m < 5; m++){
                if(k < t.size()){
                    if(dload[m] == null || !dload[m].isAlive()){
                        System.out.println("C:\\wamp\\www\\BT\\Chunks\\" + removeExtension(FName) + "\\");
                        dload[m] = new DownloadChunk(t.get(k),"C:\\wamp\\www\\BT\\Chunks\\"+removeExtension(FName)+"\\",FName);
                        dload[m].start();
                        k++;
                    }
                }else{
                    break;
                }
            }
        }
        
        write.close();
        FileSplitMerge.JoinFile(FName);
        
        return c;
        
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
