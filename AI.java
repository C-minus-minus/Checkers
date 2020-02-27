import java.util.ArrayList;

public class AI {

    //  depth the AI searches game tree
    private int depth;

    //  number to select which heuristic function AI will use
    private int evalFunc;

    //  used to generate legal moves in a given game position
    private MoveGenerator moveGenerator;

    /**
     * Creates an AI to play checkers
     * @param depth - How deep you want this to check in the game tree
     * @param evalFunc - which heuristic function you want to use
     */
    public AI(int depth,int evalFunc){
        this.depth = depth;
        this.evalFunc = evalFunc;
        this.moveGenerator = new MoveGenerator();
    }

    /**
     * Searches for the best move in the position and returns it
     * @param gameboard - state of the board
     * @return - randomly selects one move of the best moves in the position
     */
    public String getMove(Board gameboard){

        //  set up list of best possible moves
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        //  generate all legal moves in position
        ArrayList<String> moves = this.moveGenerator.moveGen(gameboard);

        //  if its whites turn set value to -infinity else set to +infinity
        int value = (Integer.MIN_VALUE+1)*gameboard.getTurn();

        //  for all moves
        for(int i=0;i<moves.size();i++){

            //  calculate score for move
            int moveScore = alphaBeta(gameboard.makeMove(moves.get(i)),this.depth,Integer.MIN_VALUE,Integer.MAX_VALUE);

            //  if move is just as good as best move make it a candidate
            if(value==moveScore){
                possibleMoves.add(i);
            }

            //  if black minimise moves
            if(gameboard.getTurn()==-1){
                if(value>moveScore){
                    possibleMoves.clear();
                    value = moveScore;
                    possibleMoves.add(i);
                }
            }

            //  if white maximise moves
            else {
                if(value<moveScore){
                    possibleMoves.clear();
                    value = moveScore;
                    possibleMoves.add(i);
                }
            }
        }

        //  select random move out of best move list
        return moves.get(possibleMoves.get((int)(Math.random()*possibleMoves.size())));
    }

    /**
     * Generates a score for a given checkers position using alpha beta pruning
     * @param gameboard - The state of the game
     * @param depth - How far you want to search in the game tree
     * @param alpha - pruning value set to -infinity
     * @param beta - pruning value set to +infinity
     * @return Score of position
     */
    private int alphaBeta(Board gameboard, int depth, int alpha, int beta){

        //  if game is over or depth is reached return heuristic value of board state
        if(depth==0||gameboard.isOver()!=0){
            return (this.evalFunc==0?this.eval(gameboard):this.eval1(gameboard));
        }

        //  get all children of board
        ArrayList<String> moves = this.moveGenerator.moveGen(gameboard);

        //  if no moves exist than return as losing position
        if(moves.size()==0){
            return gameboard.getTurn()*(Integer.MIN_VALUE+1);
        }

        //  if white maximise
        if(gameboard.getTurn()==1){
            int value = Integer.MIN_VALUE+1;
             for(int i=0;i<moves.size();i++){
                 value = Math.max(value,alphaBeta(gameboard.makeMove(moves.get(i)),depth-1,alpha,beta));
                 alpha = Math.max(alpha,value);
                 if(alpha>=beta){
                     break;
                 }
             }
             return value;
        }

        //  if black minimise
        else {
            int value = Integer.MAX_VALUE;
            for(int i=0;i<moves.size();i++){
                value = Math.min(value,alphaBeta(gameboard.makeMove(moves.get(i)),depth-1,alpha,beta));
                beta = Math.min(beta,value);
                if(alpha>=beta){
                    break;
                }
            }
            return value;
        }
    }

    /**
     * Gives the board a heuristic score based on
     * (amount of white pieces) - (amount of black pieces)
     * @param gameboard - state of board
     * @return - number representing heuristic score of position
     */
    private int eval(Board gameboard){
        if(gameboard.isOver()!=0){
            return Integer.MAX_VALUE*gameboard.isOver();
        }
        int whiteScore = gameboard.getWhitePieceCount();
        int blackScore = gameboard.getBlackPieceCount();
        return whiteScore-blackScore;
    }

    /**
     * Gives the board a heuristic score based on
     * (amount of men) + (amount of kings) + (amount of pieces on back rank)
     * it then subtracts blacks score from whites score and gets a total score
     * @param gameboard - state of board
     * @return - number representing heuristic score of position
     */
    private int eval1(Board gameboard){
        if(gameboard.isOver()!=0){
            return Integer.MAX_VALUE*gameboard.isOver();
        }

        int[][] board = gameboard.getBoard();
        int whiteBackCount = 0;
        int blackBackCount = 0;
        for(int i=0;i<8;i++){
            if(board[i][0]==-1){
                blackBackCount++;
            }
            if(board[i][7]==1){
                whiteBackCount++;
            }
        }

        int whiteScore = (whiteBackCount+gameboard.getWhiteKingCount()+gameboard.getWhitePieceCount());
        int blackScore = (blackBackCount+gameboard.getBlackKingCount()+gameboard.getBlackPieceCount());
        return whiteScore-blackScore;
    }
}
