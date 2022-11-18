package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.GameClient;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.GameServer;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

/**
 *
 * @author selim
 */
public class RmiMainClientDAO implements MainClientDAO {
    
    AppConfig properties;
    Registry registry;
    GameServer stub;
    GameClient clientStub;

    @Override
    public void start(MainClientDAOListener listener, AppConfig properties) {
        new Thread(() -> {
            this.properties = properties;
            try {
                GameClientClass gcc = new GameClientClass(listener);
                registry = LocateRegistry.getRegistry(properties.getServerAddress(), properties.getServerPort());
                stub = (GameServer) registry.lookup("Client");
                clientStub = (GameClient) UnicastRemoteObject.exportObject( gcc, properties.getServerPort());
                registry.bind("Server", clientStub);  
            } catch (Exception ex) {
                Logger.getLogger(RmiMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        
    }

    @Override
    public StartGameInfo connect() {
        return stub.connect(properties.getPlayerName());
    }

    @Override
    public void sendMoveLeft() {
        try {
            stub.moveLeft();
        } catch (RemoteException ex) {
            Logger.getLogger(RmiMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveRight() {
        try {
            stub.moveRight();
        } catch (RemoteException ex) {
            Logger.getLogger(RmiMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveUp() {
        try {
            stub.moveUp();
        } catch (RemoteException ex) {
            Logger.getLogger(RmiMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMoveDown() {
        try {
            stub.moveDown();
        } catch (RemoteException ex) {
            Logger.getLogger(RmiMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public void sendFire() {
        try {
            stub.fire();
        } catch (RemoteException ex) {
            Logger.getLogger(RmiMainClientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    
}