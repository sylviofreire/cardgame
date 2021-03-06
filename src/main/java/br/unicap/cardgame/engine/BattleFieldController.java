package br.unicap.cardgame.engine;

import br.unicap.cardgame.model.BattleFieldStatus;
import br.unicap.cardgame.model.Player;
import br.unicap.cardgame.util.Constants;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class BattleFieldController {
    
    @EJB
    private BattleField battleField;

    public void addPlayer(Player player) {    
        battleField.addAudiencePlayer(player);        
        if(hasPlayersToPlay() && isBattleFieldEmpty())                
            battleField.nextFight();
    }
    
    public void removePlayer(Player player) {
        if(player.equals(battleField.getCurrentPlayer())) {
            battleField.setCurrentPlayer(null);
            battleField.setWinner(battleField.getOpponentPlayer());
        } else if(player.equals(battleField.getOpponentPlayer())) {
            battleField.setCurrentPlayer(null);
            battleField.setWinner(battleField.getCurrentPlayer());
        } else if(battleField.getAudience().contains(player)) {
            battleField.removeAudiencePlayer(player);
        }        
    }
    
    public List<Player> connectedPlayers() {
        return new ArrayList<>(battleField.getAudience());        
    }
       
    public void move(Player player, int position) {                  
        if(!isEverybodyAlive()) return;
        if(canMove(player) && canPutCardInGame()) {
            battleField.move(position);
        }
    }
     
    public void play(Player player, int answer) {                          
        if(canMove(player)) {
            battleField.play(answer);
            if(!isEverybodyAlive() && hasPlayersToPlay()) 
                battleField.nextFight();
        }
    } 
    
    public BattleFieldStatus gameStatus() {
        BattleFieldStatus status = null;
        if(battleField.getCurrentPlayer() != null && battleField.getOpponentPlayer() != null) {
            status = new BattleFieldStatus(battleField.getCurrentPlayer(), battleField.getOpponentPlayer());
        } else if(battleField.getWinner() != null) {
            status = new BattleFieldStatus(battleField.getWinner());
        }
        return status;
    }    

    //helper methods
    private boolean canMove(Player player) {
        return player.equals(battleField.getCurrentPlayer());
    }
    
    private boolean isEverybodyAlive() {
        return battleField.getCurrentPlayer().getCharacter().isAlive() && 
               battleField.getOpponentPlayer().getCharacter().isAlive();
    }
    
    private boolean canPutCardInGame() {
        return battleField.getCurrentPlayer().getCardsInGame().size() < Constants.MAX_CARDS_IN_GAME;
    }
    
    private boolean isBattleFieldEmpty() {
        return battleField.getCurrentPlayer() == null && battleField.getOpponentPlayer() == null;
    }
    
    private boolean hasPlayersToPlay() {
        return battleField.getAudience().size() > 1;
    }
}
