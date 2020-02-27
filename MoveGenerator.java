import java.rmi.MarshalledObject;
import java.util.ArrayList;
import java.util.Map;

public class MoveGenerator {

    /**
     * Generates a list of legal moves for given state
     * @param board - state of the board
     * @return array list of legal moves
     */
    public ArrayList<String> moveGen(Board board){

        //  set up list to hold moves
        ArrayList moveList = new ArrayList();

        //  generate all jump moves
        moveList.addAll(this.genJumpMoves(board));

        //  generate other moves if no jump moves exsist
        if(moveList.size()==0){
            moveList.addAll(genRegMoves(board));
        }

        //  return legal moves
        return moveList;
    }

    /**
     * Generates all legal jump moves
     * @param gameboard - state of board
     * @return list of legal jump moves
     */
    private ArrayList<String> genJumpMoves(Board gameboard){

        //  use this to hold all legal move
        ArrayList<String> moves = new ArrayList<>();

        //  get data from game board
        int[][] board = gameboard.getBoard();
        int turn = gameboard.getTurn();

        //  for game every square
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){

                //  if its this pieces turn
                if(board[i][j]*turn>0){

                    //  create root of jump
                    MoveTreeNode root = new MoveTreeNode(null,j*8+i);

                    //  generate all possibilities from jump
                    this.generateMoveTree(root,gameboard,Math.abs(board[i][j])==2);
                    moves.addAll(movesFromTree(root));
                }
            }
        }

        //  return list of legal jump moves
        return moves;
    }

    /**
     * Generates all legal single square moves
     * @param gameboard - state of board
     * @return - list of all legal single square moves
     */
    private ArrayList<String> genRegMoves(Board gameboard){

        //  set up list of legal moves
        ArrayList<String> moves = new ArrayList<>();

        //  get board state and game turn
        int[][] board = gameboard.getBoard();
        int turn = gameboard.getTurn();

        //  for every piece
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){

                //  if its this pieces turn
                if(board[i][j]*turn>0) {

                    //  if white piece or is a king
                    if(turn>0||(Math.abs(board[i][j])==2)){

                        //  up left
                        try{
                            if(board[i-1][j-1]==0){
                                moves.add(""+(j*8+i)+"-"+((j-1)*8+(i-1)));
                            }
                        }catch (Exception e){}

                        //  up right
                        try{
                            if(board[i+1][j-1]==0){
                                moves.add(""+(j*8+i)+"-"+((j-1)*8+(i+1)));
                            }
                        }catch (Exception e){}
                    }

                    //  if black piece or is a king
                    if(turn<0||(Math.abs(board[i][j])==2)){

                        //  down left
                        try{
                            if(board[i-1][j+1]==0){
                                moves.add(""+(j*8+i)+"-"+((j+1)*8+(i-1)));
                            }
                        }catch (Exception e){}

                        //  down right
                        try{
                            if(board[i+1][j+1]==0){
                                moves.add(""+(j*8+i)+"-"+((j+1)*8+(i+1)));
                            }
                        }catch (Exception e){}
                    }

                }
            }
        }

        //  return list of legal moves
        return moves;
    }

    /**
     * Generates a tree of jump moves given a root
     * @param root - starting jump of move tree
     * @param gameBoard - state of board
     * @param isKing - true if this piece is a king, false otherwise
     */
    private void generateMoveTree(MoveTreeNode root,Board gameBoard,boolean isKing){

        //  get board state and game turn
        int turn = gameBoard.getTurn();
        int[][] board = gameBoard.getBoard();

        //  get coordinates of starting position of move
        int x = root.getValue()%8;
        int y = root.getValue()/8;

        //  if white piece or is a king
        if(turn>0||isKing){

            //  up left
            try{
                if(board[x-1][y-1]*turn<0&&board[x-2][y-2]==0){
                    if(isHopMoveLegalInTree(root,y*8+x,(y-2)*8+(x-2))){
                        MoveTreeNode child = new MoveTreeNode(root,(y-2)*8+(x-2));
                        root.setChild(child,0);
                        generateMoveTree(child,gameBoard,isKing);
                    }
                }
            }catch (Exception e){}

            //  up right
            try{
                if(board[x+1][y-1]*turn<0&&board[x+2][y-2]==0){
                    if(isHopMoveLegalInTree(root,y*8+x,(y-2)*8+(x+2))){
                        MoveTreeNode child = new MoveTreeNode(root,(y-2)*8+(x+2));
                        root.setChild(child,1);
                        generateMoveTree(child,gameBoard,isKing);
                    }
                }
            }catch (Exception e){}
        }

        //  if black piece or is a king
        if(turn<0||isKing){

            //  down left
            try{
                if(board[x-1][y+1]*turn<0&&board[x-2][y+2]==0){
                    if(isHopMoveLegalInTree(root,y*8+x,(y+2)*8+(x-2))){
                        MoveTreeNode child = new MoveTreeNode(root,(y+2)*8+(x-2));
                        root.setChild(child,2);
                        generateMoveTree(child,gameBoard,isKing);
                    }
                }
            }catch (Exception e){}

            //  down right
            try{
                if(board[x+1][y+1]*turn<0&&board[x+2][y+2]==0){
                    if(isHopMoveLegalInTree(root,y*8+x,(y+2)*8+(x+2))){
                        MoveTreeNode child = new MoveTreeNode(root,(y+2)*8+(x+2));
                        root.setChild(child,3);
                        generateMoveTree(child,gameBoard,isKing);
                    }
                }
            }catch (Exception e){}
        }
    }

    /**
     * Makes sure a piece doesn't hop the same piece twice
     * @param current - current position in move tree
     * @param location - starting location of jump
     * @param destination - ending location of jump
     * @return - true if jump is legal false otherwise
     */
    private boolean isHopMoveLegalInTree(MoveTreeNode current, int location, int destination){

        //  get parent of node
        MoveTreeNode parent = current.getParent();

        //  if this isn't the root of the tree
        while(parent!=null){

            //  return false if this jump has been done before
            if( (parent.getValue()==location||parent.getValue()==destination) &&
                (current.getValue()==location||current.getValue()==destination)){
                return false;
            }

            //  go one level up in the tree
            current = parent;
            parent = parent.getParent();
        }

        //  return true because the move is legal
        return true;
    }

    /**
     * Generates all the legal moves generated in a tree
     * @param root - root of tree you want to get moves for
     * @return - List of legal moves in tree
     */
    private ArrayList<String> movesFromTree(MoveTreeNode root){

        //  set up list of moves
        ArrayList<String> moves = new ArrayList<>();

        //  initial assume this node is a leaf
        boolean hasChild = false;

        //  check all children of node
        for(int i=0;i<4;i++){

            //  if it has a child check if the child is a leaf
            if(root.getChild(i)!=null){
                hasChild = true;
                moves.addAll(movesFromTree(root.getChild(i)));
            }
        }

        //  if this node has no children
        if(!hasChild&&root.parent!=null){

            //  generate the move and add it to the list
            String path = "";
            while(root.parent != null){
                path = "-"+root.getValue()+path;
                root = root.parent;
            }
            path = root.getValue()+path;

            moves.add(path);
        }

        //  return list of legal moves
        return moves;
    }


    class MoveTreeNode{

        private int value;
        private MoveTreeNode parent;
        private MoveTreeNode[] children;

        public MoveTreeNode(MoveTreeNode parent, int value){

            this.parent = parent;
            this.value = value;
            this.children = new MoveTreeNode[4];
        }

        public int getValue() {
            return value;
        }

        public MoveTreeNode getParent() {
            return parent;
        }

        public void setChild(MoveTreeNode child,int index){
            this.children[index] = child;
        }

        public MoveTreeNode getChild(int index){
            return this.children[index];
        }
    }
}
