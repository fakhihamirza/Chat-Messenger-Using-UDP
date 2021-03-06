import java.awt.event.*;
// import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;   //used to make a GUI with java 

// import jdk.internal.platform.Container;
 
//DatagramSocket  special socket used for connection less communication i.e usinf UDP
//datagramPacket (data , datalength , host ip , port num)  need to explicitly mention where the packet is req to go
//InetAddress.getLocalHost() since both are on same device hence local host ip used
//socket.send(packet)  means we are sending the datagram packet to the socket 
//socket.recv(packet) recieves the data into the packet
//packet.getdata extracts data from the packet
//for server datagram socket will take an arg of port num




public class ChatMessengerUDP extends JFrame
{    //Jframe = popup window
    public static final int HOST_MODE=0;
    public static final int CLIENT_MODE=1;
    public static ArrayList<client> ClientList;
    // NEEDED FOR THE GUI //
    JButton btn_send;
    JScrollPane jScrollPane1;
    // j.setBackground(Color.BLUE);
    JScrollPane jScrollPane;
    JTextArea jTextArea1;
    JTextArea jTextArea2;
    JLabel lbl_ipNroomName;
    JTextField txt_mymsg;
    int mode;
    String Name;
    String roomname;
    InetAddress hostip;
    ChatMessengerUDP my_chat_room;
    DatagramSocket socket;
    // ArrayList<client> ClientList;
    byte[] b;
   
    public ChatMessengerUDP(String Sender_name,int mod,String ip,String room)
    {
        try
        {
            // Assigning to variables
            Name = Sender_name;
            mode = mod;
            hostip = InetAddress.getByName(ip);  
            roomname = room;
            this.setTitle("Chat Room Using UDP");
            this.setLayout(null);
            this.setSize(600,600);
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  //Java default doesnt close on 'x' click
            // this.setVisible(true);

            lbl_ipNroomName = new JLabel("",SwingConstants.CENTER);
            txt_mymsg = new JTextField();
            btn_send = new JButton("Send");
            jScrollPane1 = new JScrollPane();
            jTextArea1 = new JTextArea("Your messages will appear here" , 8,8);
            Color myColor1 = new Color(210, 217,212);
            jTextArea1.setBackground(myColor1);
            jTextArea2 = new JTextArea(8,8);
            
            jTextArea2.setBackground(Color.lightGray);
            ClientList=new ArrayList<>();
            
            my_chat_room=this;

            // ROOM NAME OR IP ADDRESS
            add(lbl_ipNroomName);
            lbl_ipNroomName.setBounds(10,10,getWidth()-30,40);

            // MESSAGE AREA
            jScrollPane1.setViewportView(jTextArea1);
            add(jScrollPane1);
            jScrollPane1.setBounds(10,btn_send.getY()+40,lbl_ipNroomName.getWidth(),getHeight()-20-lbl_ipNroomName.getY()-110);

            jTextArea2.setBounds(10,jScrollPane1.getY()+jScrollPane1.getHeight(),getWidth()-30,30);
            add(jTextArea2);

            //INPUT FIELD AND SEND BUTTON
            add(txt_mymsg);
            txt_mymsg.setBounds(10,jTextArea2.getY()+jTextArea2.getHeight(),getWidth()-130,30);
            add(btn_send);
            btn_send.setBounds(txt_mymsg.getWidth()+20,txt_mymsg.getY(),80,30);
            btn_send.setEnabled(false);
            jTextArea1.setEditable(false);
            txt_mymsg.setEnabled(false);


            // EVENT TRIGGERED ON PRESSING SEND
            btn_send.addActionListener(new ActionListener() 
            {
            public void actionPerformed(ActionEvent e) 
            {
            String s = txt_mymsg.getText();    //Getting the text that you inputted
            if(s.equals("")==false)     //Checking if not empty string
                {
                if(mode==HOST_MODE)   
                    {broadcast(Name+": "+s);   //If mode is host i.e first connection to the room, call broadcast
                    
                }
                else
                    {sendToHost(Name+": "+s); 
                } // if mode is not client then send message to host to send to all clients
                txt_mymsg.setText("");      // Empty the text box
                }
                }
            }
            );
        

            if(mode==HOST_MODE)
                {
                socket=new DatagramSocket(80);   // new datagram socket is created
                lbl_ipNroomName.setText("My IP:"+InetAddress.getLocalHost().getHostAddress());    //Gets the ip and displays on top @server side
                }
            else
                {
                socket=new DatagramSocket();
                String Message_String="!!^^"+Name+"^^!!"; //Name = name of person sending
                DatagramPacket client_packet=new DatagramPacket(Message_String.getBytes(),Message_String.length(),hostip,65);
                socket.send(client_packet);  //send packet socket
                // FOR RECIEVING FROM HOST
                b=new byte[300];
                client_packet=new DatagramPacket(b,300);
                socket.setSoTimeout(6000);
                socket.receive(client_packet);
                Message_String=new String(client_packet.getData());

                if(Message_String.contains("!!^^"))
                    {
                    roomname=Message_String.substring(4,Message_String.indexOf("^^!!"));
                    lbl_ipNroomName.setText("ChatRoom: "+roomname);
                    btn_send.setEnabled(true);
                    txt_mymsg.setEnabled(true);
                    }
                else{  //If nothing sent within amount of time then
                    JOptionPane.showMessageDialog(my_chat_room,"No response from the server");System.exit(0);
                    }
                }
            Messenger.start();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,ex);}
    }


public static void main(String args[]) 
{
        try 
        {
            UIManager UI =new UIManager();
            UI.put("OptionPane.background",Color.LIGHT_GRAY);
            UI.put("Panel.background",Color.LIGHT_GRAY);

            String host="",room="";

            String name=JOptionPane.showInputDialog(null,"Enter Your Name","Hello, whats your name?",JOptionPane.INFORMATION_MESSAGE);
            if(name==null||name.equals(""))
                {
                    JOptionPane.showMessageDialog(null, "Name cannot be blank");
                return;
                }
            int mode=JOptionPane.showConfirmDialog(null,"Welcome to Chat room using UDP\nDo you want to create a new room?","Welcome!!",JOptionPane.YES_NO_OPTION);
            if(mode==1)
                {
                host=JOptionPane.showInputDialog("Enter the host ip address");
                if(host==null||host.equals(""))
                    {JOptionPane.showMessageDialog(null, "IP of host is mandatory");
                    return;
                }
                }
            else
                room=JOptionPane.showInputDialog("Welcome, You are now a host.\n Name your chat room");
                ChatMessengerUDP obj= new ChatMessengerUDP(name,mode,host,room);
            obj.setVisible(true);   //Jframe and chatmessenger obj made
        } catch (Exception ex) {JOptionPane.showMessageDialog(null,ex);}
    }
// private void getComponents(Container c){

//     Component[] m = c.getComponents();

//     for(int i = 0; i < m.length; i++){

//         if(m[i].getClass().getName() == "javax.swing.JPanel")
//             m[i].setBackground(Color.white);

//         if(c.getClass().isInstance(m[i]))
//             getComponents((Container)m[i]);
//     }
// }
public void broadcast(String str)
{
    try 
    {
        DatagramPacket pack=new DatagramPacket(str.getBytes(),str.length());
        for(int i=0;i<ClientList.size();i++)
            {
                pack.setAddress(InetAddress.getByName(ClientList.get(i).ip));
                pack.setPort(ClientList.get(i).port);
                socket.send(pack);
            }
        jTextArea1.setText(jTextArea1.getText()+"\n"+str);
       
        
    } catch (Exception ex) {JOptionPane.showMessageDialog(my_chat_room,ex);}
}
// public void broadcast_ClientList()
// {
//     try 
//     {
//         String [] c = {}; int len , byte by;
//         for(int i=0;i<ClientList.size();i++)
//         { 
//             c[i] = ClientList.get(i).name;
//             len = len + c[i].length();
//             by = by + c[i].getBytes();
//         }

//         DatagramPacket pack=new DatagramPacket(ClientList.getBytes(),client.length());
//         for(int i=0;i<ClientList.size();i++)
//             {
//                 pack.setAddress(InetAddress.getByName(ClientList.get(i).ip));
//                 pack.setPort(ClientList.get(i).port);
//                 socket.send(pack);
//             }
//     } catch (Exception ex) {JOptionPane.showMessageDialog(my_chat_room,ex);}
// }


public void sendToHost(String str)
{
    DatagramPacket pack=new DatagramPacket(str.getBytes(),str.length(),hostip,65);
    try 
    {
        socket.send(pack);
    } catch (Exception ex)
    {
    JOptionPane.showMessageDialog(my_chat_room,"Sending to server failed");}
}

Thread Messenger=new Thread()
{
    public void run()
    {
        try 
        {
            while(true)
                {
                    b=new byte[300];
                    DatagramPacket pkt=new DatagramPacket(b,300);
                    socket.setSoTimeout(0);   // no timeout means waiting forever
                    socket.receive(pkt);
                    String s=new String(pkt.getData());
                    if(mode==HOST_MODE)
                    {
                        if(s.contains("!!^^"))    // This is sent from above with the host name ans message
                        {
                            //Creating a new client 
                            client temp=new client();
                            temp.name=s.substring(4,s.indexOf("^^!!"));
                            
                            //Getting the host ipp and port //

                            temp.ip=pkt.getAddress().getHostAddress();   
                            temp.port=pkt.getPort();
                            broadcast(s.substring(4,s.indexOf("^^!!"))+" joined.");
                            ClientList.add(temp);
                            String a;
                            jTextArea2.setText("Clients Connected: \n");
                            for(int i=0;i<ClientList.size();i++)
                            {
                                a = ClientList.get(i).name;
                                jTextArea2.append(a + " ");
                            }
                            s="!!^^"+roomname+"^^!!";
                            pkt=new DatagramPacket(s.getBytes(),s.length(),InetAddress.getByName(temp.ip),temp.port);
                            socket.send(pkt);
                            btn_send.setEnabled(true);
                            txt_mymsg.setEnabled(true);
                        }
                        else
                        {
                            broadcast(s);
                        }
                    }
                    else
                        {
                        jTextArea1.setText(jTextArea1.getText()+"\n"+s);
                        jTextArea2.setText("\n");
                        
                        }
                }
            } catch (IOException ex) {JOptionPane.showMessageDialog(my_chat_room,ex);System.exit(0);}
        }
    };
}

class client
{
public String ip;
public int port;
public String name;
}