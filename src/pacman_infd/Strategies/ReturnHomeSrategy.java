/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pacman_infd.Strategies;

import pacman_infd.Cell;
import pacman_infd.PathFinder;
import pacman_infd.Strategy;

/**
 *
 * @author Marinus
 */
public class ReturnHomeSrategy implements Strategy{
    
        private PathFinder pathFinder;
        private Cell homeCell;
    
    public ReturnHomeSrategy(Cell homeCell){
        pathFinder = new PathFinder();
        this.homeCell = homeCell;
    }
    
    @Override
    public Cell giveNextCell(Cell currentCell) {

        return pathFinder.nextCellInPath(currentCell, homeCell);

    }
}
