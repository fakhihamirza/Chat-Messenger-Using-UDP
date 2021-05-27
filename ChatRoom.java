import java.awt.event.*;
import java.awt.BorderLayout;
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




public class ChatRoom extends JFrame{    //Jframe = popup window
    public static final int HOST_MODE=0;
    public static final int CLIENT_MODE=1;
    
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
    ChatRoom my_chat_room;
    DatagramSocket socket;
    ArrayList<client> ClientList;
    byte[] b;
   
public ChatRoom(String Sender_name,int mod,String ip,String room)
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
        // jTextArea2 = new JTextArea("Display clients ", 8,8);
        ClientList=new ArrayList<>();
        
        my_chat_room=this;

        // ROOM NAME OR IP ADDRESS
        add(lbl_ipNroomName);
        lbl_ipNroomName.setBounds(10,10,getWidth()-30,40);

        // MESSAGE AREA
        jScrollPane1.setViewportView(jTextArea1);
        add(jScrollPane1);
        jScrollPane1.setBounds(10,btn_send.getY()+40,lbl_ipNroomName.getWidth(),getHeight()-20-jScrollPane1.getY()-110);

        //INPUT FIELD AND SEND BUTTON
        add(txt_mymsg);
        txt_mymsg.setBounds(10,jScrollPane1.getY()+jScrollPane1.getHeight(),getWidth()-130,30);
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
                broadcast(Name+": "+s);   //If mode is host i.e first connection to the room, call broadcast
            else
                sendToHost(Name+": "+s);  // if mode is not client then send message to host to send to all clients
            txt_mymsg.setText("");      // Empty the text box
            }
            }
        }
        );
       

        if(mode==HOST_MODE)
            {
            socket=new DatagramSocket(37988);   // new datagram socket is created
            lbl_ipNroomName.setText("My IP:"+InetAddress.getLocalHost().getHostAddress());    //Gets the ip and displays on top @server side
            }
        else
            {
            socket=new DatagramSocket();
            String Message_String="!!^^"+Name+"^^!!"; //Name = name of person sending
            DatagramPacket pk=new DatagramPacket(Message_String.getBytes(),Message_String.length(),hostip,37988);
            socket.send(pk);  //send packet socket
            b=new byte[300];
            pk=new DatagramPacket(b,300);
            socket.setSoTimeout(6000);
            socket.receive(pk);
            Message_String=new String(pk.getData());
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

// public void Set_GUI_For_Room(){


// }
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
        ChatRoom obj= new ChatRoom(name,mode,host,room);
        obj.setVisible(true);
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
try {
DatagramPacket pack=new DatagramPacket(str.getBytes(),str.length());
for(int i=0;i<ClientList.size();i++)
    {
    pack.setAddress(InetAddress.getByName(ClientList.get(i).ip));
    pack.setPort(ClientList.get(i).port);
    socket.send(pack);
    }
jTextArea1.setText(jTextArea1.getText()+"\n"+str);
} catch (Exception ex) {JOptionPane.showMessageDialog(pt,ex);}
}

public void sendToHost(String str)
{
DatagramPacket pack=new DatagramPacket(str.getBytes(),str.length(),hostip,37988);
try {socket.send(pack);} catch (Exception ex)
{JOptionPane.showMessageDialog(my_chat_room,"Sending to server failed");}
}

Thread Messenger=new Thread()
{
public void run()
{
try {
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
            
            //Getting the host ipp and port //

            temp.ip=pkt.getAddress().getHostAddress();   
            temp.port=pkt.getPort();
            broadcast(s.substring(4,s.indexOf("^^!!"))+" joined.");
            ClientList.add(temp);
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
        }
    }
}catch (IOException ex) {JOptionPane.showMessageDialog(my_chat_room,ex);System.exit(0);}
}
};
}

class client
{
public String ip;
public int port;
public String name;
}