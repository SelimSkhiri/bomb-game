/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.gaming.atom.examples.kombla.main.client.dal;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
import net.thevpc.gaming.atom.model.DefaultPlayer;
import net.thevpc.gaming.atom.model.DefaultSprite;


/**
 *
 * @author iheb
 */
public class SocketMainClientDAO implements MainClientDAO{
    
    public int serverPort;
    public String serverAddress ;
    public MainClientDAOListener listener;
    public Socket socket;
    public ObjectInputStream in;
    public ObjectOutputStream out;
    private AppConfig properties;
            
    @Override
    public void start(MainClientDAOListener listener, AppConfig properties) {
       this.serverPort = properties.getServerPort();
       this.serverAddress= properties.getServerAddress();
       this.listener = listener;
       this.properties = properties;
    }
    
    public void onLoopRecieveModelChanged(){
        new Thread(() ->{
            while(true){
            DynamicGameModel model= null;
                try {
                    model = (DynamicGameModel) in.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            listener.onModelChanged(model);
        }
    }).start();  
    }

    @Override
    public StartGameInfo connect() {
        StartGameInfo info = null ;
        try {
            
            socket = new Socket(serverAddress,serverPort);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeInt(ProtocolConstants.CONNECT);
            out.writeUTF(properties.getPlayerName());
            try {
                info = (StartGameInfo) in.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            onLoopRecieveModelChanged();           
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    
    public void readStartGameInfo(StartGameInfo info){
        
        int playerId = info.getPlayerId();
        int[][] maze = info.getMaze();
    }
    
    public DefaultPlayer readPlayer(){
        DefaultPlayer player = null;
        try {
            player = (DefaultPlayer) in.readObject();
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /*
        int Id = instance.getId();
        String name = instance.getName();
        Map<String, Object> properties = instance.getProperties();
        */
        
       return (DefaultPlayer) player.copy();
        
    }
    
    public DefaultSprite readSprite(){
        DefaultSprite sprite = null;
        try {
            sprite = (DefaultSprite) in.readObject();
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        int Id = instance.getId();
        String kind = instance.getKind();
        String name = instance.getName();
        ModelPoint location = instance.getLocation();
        double direction = instance.getDirection();
        int playerId = instance.getPlayerId();
        Map<String, Object> properties = instance.getProperties();
        */
         return (DefaultSprite) sprite.copy();
    }

    @Override
    public void sendMoveLeft() {  
        try {
            out.writeInt(ProtocolConstants.LEFT);
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveRight() {
  try {
            out.writeInt(ProtocolConstants.RIGHT);
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveUp() {
  try {
            out.writeInt(ProtocolConstants.UP);
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveDown() {
  try {
            out.writeInt(ProtocolConstants.DOWN);
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendFire() {
  try {
            out.writeInt(ProtocolConstants.FIRE);
        } catch (IOException ex) {
            Logger.getLogger(SocketMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
