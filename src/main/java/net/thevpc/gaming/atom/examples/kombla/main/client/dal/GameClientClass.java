package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.GameClient;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;


/**
 *
 * @author selim
 */
public class GameClientClass implements GameClient,Remote {
    
    MainClientDAOListener listener;
    
    
    public GameClientImpl(MainClientDAOListener listener){
        this.listener=listener;
    }
    

    @Override
    public void modelChanged(DynamicGameModel dynamicGameModel) throws RemoteException {
        listener.onModelChanged(dynamicGameModel); 
    }            
 
    
}