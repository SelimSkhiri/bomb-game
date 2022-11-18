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
 * @author selim
 */
public class SocketMainClientDAO implements MainClientDAO {

    private MainClientDAOListener listener;
    private AppConfig properties;
    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    @Override
    public void start(MainClientDAOListener listener, AppConfig properties) {
        this.listener = listener;
        this.properties = properties;
    }

    public void onLoopReceivedModelChanged() {
        new Thread(() -> {
            try {
                while (true) {
                    DynamicGameModel dgm = (DynamicGameModel) in.readObject();
                    listener.onModelChanged(dgm);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }).start();

    }

    @Override
    public StartGameInfo connect() {
        try {
            s = new Socket(properties.getServerAddress(), properties.getServerPort());
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());
            out.writeInt(ProtocolConstants.CONNECT);
            out.writeUTF(properties.getPlayerName());
            in.readInt();
            StartGameInfo sgi = (StartGameInfo) in.readObject();
            onLoopReceivedModelChanged();
            return sgi;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void sendMoveLeft() {
        try {
            out.writeInt(ProtocolConstants.LEFT);
        } catch (IOException ex) {
            Logger.getLogger(TCPMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveRight() {
        try {
            out.writeInt(ProtocolConstants.RIGHT);
        } catch (IOException ex) {
            Logger.getLogger(TCPMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveUp() {
        try {
            out.writeInt(ProtocolConstants.UP);
        } catch (IOException ex) {
            Logger.getLogger(TCPMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveDown() {
        try {
            out.writeInt(ProtocolConstants.DOWN);
        } catch (IOException ex) {
            Logger.getLogger(TCPMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendFire() {
        try {
            out.writeInt(ProtocolConstants.FIRE);
        } catch (IOException ex) {
            Logger.getLogger(TCPMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
