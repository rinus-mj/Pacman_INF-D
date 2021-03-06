/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pacman_infd.Strategies;

import java.util.ArrayList;
import java.util.Collection;
import pacman_infd.Cell;
import pacman_infd.PathFinder;
import pacman_infd.Strategy;

/**
 *
 * @author Marinus
 */
public class FleeStrategy implements Strategy {

    private PathFinder pathFinder;
    private Cell previousCell;

    public FleeStrategy(){
        pathFinder = new PathFinder();
    }
    @Override
    public Cell giveNextCell(Cell currentCell) {
        
        ArrayList<Cell> possibleCell = new ArrayList<>();
        Cell towardsPacman = pathFinder.nextCellInPathToPacman(currentCell);
       
        for(Cell cell : (Collection<Cell>)currentCell.getNeighbors().values()){
            if(!cell.hasWall() && cell != towardsPacman && cell != previousCell){
                possibleCell.add(cell);
            }    
        }
        
        possibleCell.add(towardsPacman);
        
        previousCell = currentCell;
        return possibleCell.get(0);
        
    }
    
}
