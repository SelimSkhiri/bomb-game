/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.gaming.atom.examples.kombla.main.server.dal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
import net.thevpc.gaming.atom.model.DefaultPlayer;
import net.thevpc.gaming.atom.model.DefaultSprite;
import net.thevpc.gaming.atom.model.ModelPoint;
import net.thevpc.gaming.atom.model.Player;
import net.thevpc.gaming.atom.model.Sprite;

/**
 *
 * @author iheb
 */
public class SocketMainServerDAO2 implements MainServerDAO {

    private Map<Integer, ClientSession> playerToSocketMap = new HashMap<>();

    public class ClientSession {

        int playerId;
        int port;
        Socket socketClient;
        ObjectInputStream in;
        ObjectOutputStream out;

        public ClientSession(int playerId, int port, Socket socketClient, ObjectInputStream in, ObjectOutputStream out) {
            this.playerId = playerId;
            this.in = in;
            this.out = out;
            this.port = port;
            this.socketClient = socketClient;

        }

    }

    public void processClient(ClientSession client) {

    }

    public void writePlayer(Player p) throws IOException {
        int id = p.getId();
        String name = p.getName();
        DefaultPlayer instance = new DefaultPlayer(name);
        ObjectOutputStream out = this.playerToSocketMap.get(id).out;
        out.writeObject(instance);
    }

    public void writeSprite(Sprite s) throws IOException {
        String kind = s.getKind();
        int playerId = s.getPlayerId();
        DefaultSprite instance = new DefaultSprite(kind);
        ObjectOutputStream out = this.playerToSocketMap.get(playerId).out;
        out.writeObject(instance);
    }

    @Override
    public void start(MainServerDAOListener listener, AppConfig properties) {

        new Thread(() -> {
            try {
                ServerSocket socketServeur = new ServerSocket(properties.getServerPort());
                while (true) {
                    Socket socketClient = socketServeur.accept();
                    ObjectInputStream in = new ObjectInputStream(socketClient.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(socketClient.getOutputStream());
                    String name = (String) in.readUTF();
                    StartGameInfo info = listener.onReceivePlayerJoined(name);
                    ClientSession client = new ClientSession(info.getPlayerId(), properties.getServerPort(), socketClient, in, out);
                    this.playerToSocketMap.put(info.getPlayerId(), client);
                    out.writeObject(info);
                    out.writeInt(ProtocolConstants.OK);
                }
            } catch (IOException ex) {
                Logger.getLogger(SocketMainServerDAO2.class.getName()).log(Level.SEVERE, null, ex);
            }

        }).start();
    }

    @Override
    public void sendModelChanged(DynamicGameModel dynamicGameModel) {
        for (Map.Entry<Integer, ClientSession> entry : this.playerToSocketMap.entrySet()) {
            try {
                entry.getValue().out.writeObject(dynamicGameModel);
            } catch (IOException ex) {
                Logger.getLogger(SocketMainServerDAO2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
