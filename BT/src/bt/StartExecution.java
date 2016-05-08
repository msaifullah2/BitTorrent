/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class StartExecution {
    public static void main(String args[]){
        
    	//start server
        Thread server = new ServerPeer();
		server.start();
        
		//create required directories
        File dlDir = new File("C:\\Project\\BT\\Downloaded_Files\\");
        if(!dlDir.exists()){
            dlDir.mkdirs();
        }
        
        File torrDir = new File("C:\\Project\\BT\\Torrent_File\\");
        if(!torrDir.exists()){
            torrDir.mkdirs();
        }
        
        File logDir = new File("C:\\Project\\BT\\CSLogs\\");
        if(!logDir.exists()){
            logDir.mkdirs();
        }
        
        File wsDir = new File("C:\\wamp\\www\\BT\\Chunks\\");
        if(!wsDir.exists()){
            wsDir.mkdirs();
        }
        
        System.out.println("1 - Create a Torrent ( select number 1)");
        
        System.out.println("2 - Open a Torrent ( select number 2)");
        
        System.out.println("3 - Exit ( select number 3)");
        
        
        System.out.println("Enter your option");
        
        //waiting for user to make a choice
        Scanner s = new Scanner(System.in);
        
        int choice = s.nextInt();
        
        //create torrent
        switch(choice){
            case 1:
                int numPieces = 0 ;
                String IP = null;
                JFileChooser fChooser = new JFileChooser();
                
                int retVal = fChooser.showOpenDialog(null);
                File input = fChooser.getSelectedFile();
                
                if(retVal == JFileChooser.APPROVE_OPTION){
                    if(input.exists()){
                        File output = new File("C:\\wamp\\www\\BT\\Chunks\\" + removeExtension(input.getName()));
                        
                        if(!output.exists()){
                            output.mkdir();
                        }
                        
                        try{
                            numPieces = FileSplitMerge.SplitFile(input.getParent()+"\\"+input.getName(), "C:\\wamp\\www\\BT\\Chunks\\"+removeExtension(input.getName()+"\\"));
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        
                        PrintWriter write = null;
                        
                        try{
                            write = new PrintWriter("C:\\Project\\BT\\Torrent_File\\"+removeExtension(input.getName())+".torrent", "UTF-8");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        
                        write.println("Name\t" + input.getName());
                        
                        Enumeration<?> e1 = null;
                        try{
                            e1 = NetworkInterface.getNetworkInterfaces();
                        }catch(SocketException e){
                            e.printStackTrace();
                        }
                        
                        while(e1.hasMoreElements()){
                            NetworkInterface net = (NetworkInterface) e1.nextElement();
                            Enumeration<?> enm = net.getInetAddresses();
                            while(enm.hasMoreElements()){
                                InetAddress add = (InetAddress) enm.nextElement();
                                String tempIP = add.getHostAddress();
                                if(tempIP.substring(0, 3).equals("25.")){
                                    IP = tempIP;
                                }
                            }
                        }
                        
                        write.println("Tracker\t" + IP);
                        write.println("File Size\t" + input.length());
                        write.println("Pieces\t" + numPieces);
                        
                        write.close();
                        System.out.println("Torrent being created for file : "+input.getParent()+"\\"+input.getName());
                        
                    }
                }
            break;
            //opening a torrent    
            case 2:
                String fName = null, fSize = null, fPieces = null, fTracker = null;
                JFileChooser fChooser2 = new JFileChooser();
//                FileNameExtensionFilter filter = new FileNameExtensionFilter("Torrent File", ".torrent");
//                fChooser2.setFileFilter(filter);
                
                int retVal2 = fChooser2.showOpenDialog(fChooser2);
                File input2 = fChooser2.getSelectedFile();
                //fChooser2.setBackground(Color.RED);
                //fChooser2.setForeground(Color.black);
                
                
                if(retVal2 == JFileChooser.APPROVE_OPTION){
                    if(input2.exists()){
                        System.out.println("Opening Torrent : "+ input2.getParent()+"\\"+input2.getName());
                        System.out.println("Torrent Details:");
                        
                        FileReader file = null;
                        try{
                            file = new FileReader(input2.getParent()+"\\"+input2.getName());
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                        
                        try{
                            BufferedReader read = new BufferedReader(file);
                            
                            String str2 = null;
                            String strTemp2[];
                            str2 = read.readLine();
                            System.out.println(str2);
                            strTemp2 = str2.split("\t");
                            
                            if(strTemp2[0].equals("Name")){
                                fName = strTemp2[1];
                            }
                            
                            while((str2 = read.readLine()) != null){
                                strTemp2 = str2.split("\t");
                                if(strTemp2[0].equals("Name")){
                                    fName = strTemp2[1];
                                    System.out.println("\nName : " + fName);
                                } else if(strTemp2[0].equals("Tracker")){
                                    fTracker = strTemp2[1];
                                    System.out.println("\nTracker : " + fTracker);
                                } else if(strTemp2[0].equals("File Size")){
                                    fSize = strTemp2[1];
                                    System.out.println("\nFile Size : " + fSize);
                                } else if(strTemp2[0].equals("Pieces")){
                                    fPieces = strTemp2[1];
                                    System.out.println("\nPieces : " + fPieces);
                                }
                            }
                        
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        //start the client thread
                        Thread c = new ClientPeer(fName, fTracker, fSize, fPieces);
                        c.start();
                        
                        
            
                    }
                }
            break;
        case 3:
            System.exit(0);
        }
        
        
    }
    //retrieve filename without its extension
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