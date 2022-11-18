/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.gaming.atom.examples.kombla.main.server.dal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

/**
 *
 * @author aicha
 */
public class SocketMainServerDAO implements MainServerDAO {

    private Map<Integer, ClientSession> sessions = new HashMap<>();
    private MainServerDAOListener listener;
    private AppConfig properties;
    public SocketMainServerDAO() {
    }

    @Override
    public void start(MainServerDAOListener listener, AppConfig properties) {
        new Thread(
                () -> {
                    try {
                        ServerSocket ss = new ServerSocket(properties.getServerPort());
                        while (true) {
                            Socket s = ss.accept();

                            new Thread(
                                    () -> {
                                        try {
                                            processClient(s);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                            ).start();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
        ).start();
    }

    @Override
    public void sendModelChanged(DynamicGameModel dynamicGameModel) {
        for (Map.Entry<Integer, ClientSession> entry : sessions.entrySet()) {
            try {
                entry.getValue().out.writeObject(dynamicGameModel);
            } catch (IOException ex) {
                Logger.getLogger(SocketMainServerDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void processClient(Socket s) {
        try {
            ClientSession cs = new ClientSession();
            cs.socket=s;
            cs.out=new ObjectOutputStream(s.getOutputStream());
            cs.in=new ObjectInputStream(s.getInputStream());
            cs.in.readInt();
            String playerName=cs.in.readUTF();
            StartGameInfo g = listener.onReceivePlayerJoined(playerName);
            cs.out.writeInt(ProtocolConstants.OK);
            cs.out.writeObject(g);
            cs.id=g.getPlayerId();
            sessions.put(cs.id, cs);
            while(true){
                switch (cs.in.readInt()) {
                    case ProtocolConstants.UP:
                        listener.onReceiveMoveUp(cs.id);
                        break;
                    case ProtocolConstants.DOWN:
                        listener.onReceiveMoveDown(cs.id);
                        break;
                    case ProtocolConstants.LEFT:
                        listener.onReceiveMoveLeft(cs.id);
                        break;
                    case ProtocolConstants.RIGHT:
                        listener.onReceiveMoveRight(cs.id);
                        break;
                    case ProtocolConstants.FIRE:
                        listener.onReceiveReleaseBomb(cs.id);
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketMainServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class ClientSession {

        ObjectOutputStream out;
        ObjectInputStream in;
        Socket socket;
        int id;
    }

}
