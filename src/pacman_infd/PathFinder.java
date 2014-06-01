/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman_infd;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import pacman_infd.Elements.Pacman;

/**
 *
 * @author Marinus
 */
public class PathFinder {

    Cell rootCell;

    public PathFinder() {

    }
    
//    public void findPacman(){
//        List<Cell> path = findPathToPacman(rootCell);
//        
//        for(Cell cell : path){
//            Pacman p = new Pacman(cell, null, 0);
//        }
//          
//    }
    
    /**
     * The first cell in the path List is the one that the object moving towards pacman
     * needs to take, so this returns the first cell in the path.
     * @param rootCell
     * @return first cell in the path towards packman.
     */
    public Cell nextCellInPathToPacman(Cell rootCell)
    {
        List<Cell> path = findPathToPacman(rootCell);
        if(path != null && !path.isEmpty()){
            return path.get(0);
        }
        else{
            return rootCell;
        }           
    }
    
    /**
     * Constructs a path (List of cells in order) by 'walking' back along 
     * cellParents of each cell starting with the given cell.
     * @param cell start cell from which to walk back.
     * @return list of cells making up the path.
     */
    private List contructPath(Cell cell){
    
        LinkedList path = new LinkedList();
        while(cell.getPathParent() != null){
            path.addFirst(cell);
            cell = cell.getPathParent();
        }
        
        return path;
    }
    
    /**
     * Uses a Breath-First search algorithm to determine the shortest path from 
     * the start cell to the cell containing Pacman.
     * @param startCell
     * @return 
     */
    private List findPathToPacman(Cell startCell){

        LinkedList visitedCells = new LinkedList();
        
        Queue queue = new LinkedList();
        queue.offer(startCell);
        startCell.setPathParent(null);
        
        while(!queue.isEmpty()){
            Cell cell = (Cell)queue.poll();
            
            for(GameElement e : cell.getElements()){
                if(e instanceof Pacman){
                    //pacman found
                    return contructPath(cell);
                }
            }
            
            //if pacman not found
            visitedCells.add(cell);
            
            for (Cell cellChild : (Collection<Cell>)cell.getNeighbors().values()) {
                if(!cellChild.hasWall() && !visitedCells.contains(cellChild) && !queue.contains(cellChild)){
                    cellChild.setPathParent(cell);
                    queue.add(cellChild);
                }
                
            }
        }
        
        //no path found
        return null;
    }
    
}
