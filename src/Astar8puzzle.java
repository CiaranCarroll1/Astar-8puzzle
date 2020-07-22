/*
	Group Members:
	Ciaran Carroll - 16181492
	Eoin Watkins - 16187539
	Conor Canton - 16164571
*/

import javax.swing.JOptionPane;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.*;
import java.util.regex.*;
import java.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Astar8puzzle {

    public static void main(String[] args)
	{
		boolean startIsValid = false;
		boolean endIsValid = false;
		
		String startStateInput = "";
		String endStateInput = "";
		
		while(!startIsValid)
		{
			startStateInput = JOptionPane.showInputDialog("Enter the Start State in the format '0 1 2 3 4 5 6 7 8' where 0 indicates the empty tile:");
		
			if (isValid(startStateInput) == true)
			{
				startIsValid = true;
			}
		}
		
		while(!endIsValid)
		{			
			endStateInput = JOptionPane.showInputDialog("Enter the End State in the format '0 1 2 3 4 5 6 7 8' where 0 indicates the empty tile:");
				
			if (isValid(endStateInput) == true)
			{
				endIsValid = true;
			}
		}
		
		ArrayList<Tile> start = new ArrayList<Tile>();
		ArrayList<Tile> end = new ArrayList<Tile>();		
		String[] splitStart = startStateInput.split(" ");
		String[] splitEnd = endStateInput.split(" ");
		int c = 0;
		
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				start.add(new Tile(Integer.parseInt(splitStart[c]), i, j));
				end.add(new Tile(Integer.parseInt(splitEnd[c]), i, j));
				c++;
			}
		}
		
		Board startBoard = new Board(start);
		Board endBoard = new Board(end);
		
		Search findFinish = new Search(startBoard, endBoard);
	}			
	
	
	public static boolean isValid(String input)
	{	
		boolean valid = false;
		
		String format = "^[0-8](\\s+)[0-8](\\s+)[0-8](\\s+)[0-8](\\s+)[0-8](\\s+)[0-8](\\s+)[0-8](\\s+)[0-8](\\s+)[0-8]$";
		if (!(input.matches(format)))
		{
			JOptionPane.showMessageDialog(null, "Error: Does not match pattern - Numbers 0-8 seperated by a space");
		}
		else{
			String[] splitInput = input.split(" ");
				
			for (int i = 0; i < splitInput.length; i++) 
			{ 
				for (int j = i + 1 ; j < splitInput.length; j++) 
				{ 
					if (splitInput[i].equals(splitInput[j])) 
					{ 
						JOptionPane.showMessageDialog(null, "Error: There are duplicates - Each number can only be entered once!");
					}
					else
					{
						valid = true;
					}				
				} 
			}
		}
		return valid;
	}
	
}

/*
	Search Class
*/
	
class Search {

	private Board board;
	private Board goalBoard;
	private int g = 0;

	ArrayList<Integer> openFValues = new ArrayList<Integer>();
	ArrayList<Tile> open = new ArrayList<Tile>();
	ArrayList<Board> closed = new ArrayList<Board>();

	private int lastMove = 0;

	public Search(Board board, Board goalBoard){
		this.board = board;
		this.goalBoard = goalBoard;
		
		System.out.println("START:");
		
		ArrayList<Tile> first = new ArrayList<Tile>();
		for(Tile aTile: board.getTiles())
			first.add(new Tile(aTile));
		closed.add(new Board(first));

		searchAlgo();
	}

	private void searchAlgo() {

		if (hFunction(board) == 0) {
			for(Board b: closed)
				b.print();
			System.out.println("FINISH: " + g + " steps");
		}
		else {
		
		stateManager();

		ArrayList<Tile> list = new ArrayList<Tile>();
		for (int value : openFValues) {

			if (value == Collections.min(openFValues))
				list.add(open.get(openFValues.indexOf(value)));
		}

		int minFValueIndex = openFValues.indexOf(Collections.min(openFValues));
		board.moveTile(board.findTile(0), open.get(minFValueIndex).getXPosition(),open.get(minFValueIndex).getYPosition());
		lastMove = open.get(minFValueIndex).getNumber();
		g += 1;
		
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for(Tile t: board.getTiles())
			tiles.add(new Tile(t));			
		closed.add(new Board(tiles));

		openFValues.clear();
		open.clear();

		searchAlgo();

		}
	}

	private void stateManager(){
		
		for(Tile tile: board.checkNeighbouringTiles(board.findTile(0))){
			if(lastMove != tile.getNumber()) {
				openFValues.add(state(tile));
				open.add(tile);
				state(tile);
			}

		}

	}


	private int state(Tile tile){

		Board stateBoard = new Board(board.getTiles());

		stateBoard.moveTile(stateBoard.findTile(0), tile.getXPosition(), tile.getYPosition());

		return g + hFunction(stateBoard);
	}

	public int hFunction(Board board) {

		int sum = 0;
		ArrayList<Tile> tiles = board.getTiles();
		int differenceX;
		int differenceY;

		for(Tile tile: tiles){

			if(tile.getNumber() != 0) {

				differenceX = goalBoard.findTile(tile.getNumber()).getXPosition() - tile.getXPosition();
				differenceY = goalBoard.findTile(tile.getNumber()).getYPosition() - tile.getYPosition();

				sum += Math.abs(differenceX) + Math.abs(differenceY);
			}

		}

		return sum;
	}
}

/*
	Board Class
*/
class Board {

	private ArrayList<Tile> board;

	public Board(ArrayList<Tile> tiles) {
		board = tiles;
	}
	

	public ArrayList<Tile> getTiles(){
		return board;
	}
	
	public void setTiles(ArrayList<Tile> tiles) {
		board = tiles;
	}

	public void moveTile(Tile tile, int x, int y) {
		int _x = tile.getXPosition();
		int _y = tile.getYPosition();

		Tile tileToBeMoved = board.get(board.indexOf(tile));
		Tile tileAtPosition = findTile(x, y);
  
		tileToBeMoved.setPosition(x, y);
		tileAtPosition.setPosition(_x, _y);
		
	}

	public ArrayList<Tile> checkNeighbouringTiles(Tile tile){

		ArrayList<Tile> neighbouringTiles = new ArrayList<>();

		int x = tile.getXPosition();
		int y = tile.getYPosition();

		for(Tile t: board) {
			if ((t.getXPosition() == x + 1) && (t.getYPosition() == y)) {
				neighbouringTiles.add(t);

			} else if((t.getXPosition() == x - 1) && (t.getYPosition() == y)){
				neighbouringTiles.add(t);

			} else if((t.getXPosition() == x) && (t.getYPosition() == y - 1)){
				neighbouringTiles.add(t);

			} else if((t.getXPosition() == x) && (t.getYPosition() == y + 1)){
				neighbouringTiles.add(t);
			}
		}

    return neighbouringTiles;
}

	public Tile findTile(int x, int y){

		for(Tile t: board) {
			if(t.getXPosition() == x && t.getYPosition() == y)
				return t;
		}

		return null;
	}


	public Tile findTile(int number){

		for(Tile t: board) {
			if(t.getNumber() == number)
				return t;
		}
		return null;
	}
	
	public void print()
	{
		int count = 0;
		System.out.println("=====");
		
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				System.out.print(board.get(board.indexOf(findTile(i, j))).getNumber() + " ");
				count++;
			}
			System.out.println();
		}
		System.out.println("=====");
	}
}

/*
	Tile Class
*/
class Tile {
	
	private int number;
	private int x;
	private int y;

	public Tile(int number, int x, int y){
		this.number = number;
		this.x = x;
		this.y = y;
	}
	
	public Tile(Tile that)
	{
		this(that.getNumber(), that.getXPosition(), that.getYPosition());
	}

	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getXPosition(){
		return x;
	}

	public int getYPosition(){
		return y;
	}

	public void setNumber(int number){
		this.number = number;
	}

	public int getNumber(){
		return number;
	}
}
