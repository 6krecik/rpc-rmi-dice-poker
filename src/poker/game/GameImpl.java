package poker.game;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import poker.game.GameState.GameParticipantState;

/**
 * Created by IntelliJ IDEA.
 * User: nickers
 * Date: 2010-09-02
 * Time: 01:11:18
 * To change this template use File | Settings | File Templates.
 */
public class GameImpl extends UnicastRemoteObject implements Game {
    private GameParticipant players[] = new GameParticipant[2];
    private GameState.GameParticipantState playersState[] = new GameState.GameParticipantState[2];
    private GameState gameState = new GameState();

    public GameImpl() throws RemoteException {
        for (int i=0; i<this.playersState.length; i++) {
            this.playersState[i] = this.gameState.new GameParticipantState();
        }
    }

    public boolean addPlayer(GameParticipant player) throws RemoteException{
        int i = 0;
        while (i<this.players.length) {
            if (this.players[i] == null) {
                this.players[i] = player;
                return true;
            }
            i++;
        }
        this.gameState.changed();
        return false;
    }

    public synchronized void waitForStateChange(GameState previous)  throws RemoteException{
        try {
            if (this.gameState.version==previous.version) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public GameState getGameState(GameParticipant player)  throws RemoteException{
        return this.gameState.clone();
    }

    public void leaveGame(GameParticipant player)  throws RemoteException{
        for (int i=0; i<this.players.length; i++) {
            if (this.players[i]==player) {
                this.players[i] = null;
                this.gameState.changed();
                return;
            }
        }
    }

    public void setPlayerDice(GameParticipant player, int dice[]) throws RemoteException {
        this.getParticipantState(player).dice = dice;
        if (!this.playGame()) {
            this.gameState.changed();
        }
    }

    public void acceptRound(GameParticipant player) throws RemoteException {
        this.getParticipantState(player).acceptedRound = true;
        if (!this.playGame()) {
            this.gameState.changed();
        }
    }

    private GameParticipantState getParticipantState(GameParticipant player) {
        for (int i=0; i<this.players.length; i++) {
            if (this.players[i]==player) {
                return this.playersState[i];
            }
        }
        return null;
    }

    /**
     * try to compute round results, true if done, false otherwise.
     * @return
     */
    private boolean playGame() {
        return false;
    }
}
