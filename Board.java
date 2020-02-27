public class Board {

    //  amount of moves made
    private int moveCount;

    //  current amount of pieces left
    private int whitePieceCount;
    private int blackPieceCount;

    //  current amount of kings left
    private int whiteKingCount;
    private int blackKingCount;

    //  0 = empty space; 1 = white piece; 2 = white king; -1 = black piece; -2 = black king
    private int[][] board;

    //  1 if white; -1 if black
    private int turn;

    public Board(){

        // no moves made
        this.moveCount = 0;

        //  white moves first
        this.turn = 1;

        //  set up empty board state
        board = new int[8][8];
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                board[i][j] = 0;
            }
        }

        //  put pieces in starting position
        for(int i=0;i<8;i++){
            this.board[i][1-(i%2)] = -1;
            this.board[i][5-((i%2)*3)] = (i%2)*-2+1;
            this.board[i][7-(i%2)] = 1;
        }

        //  set up piece counts
        this.whitePieceCount = 12;
        this.blackPieceCount = 12;
        this.whiteKingCount = 0;
        this.blackKingCount = 0;
    }

    //  copy constructor
    private Board(Board gameboard){

        this.moveCount = gameboard.moveCount;
        this.whitePieceCount = gameboard.whitePieceCount;
        this.blackPieceCount = gameboard.blackPieceCount;
        this.turn = gameboard.turn;
        this.board = new int[8][8];
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                this.board[i][j] = gameboard.board[i][j];
            }
        }
    }

    public int[][] getBoard(){
        return this.board;
    }

    /**
     * Prints the board state using unicode (not supported by many consoles)
     */
    public void printUnicode(){
        System.out.print("   ");
        for(int i=0;i<8;i++){
            System.out.print(Character.toString((char)(65296+i)));
        }
        System.out.println();
        for(int i=0;i<8;i++){
            System.out.printf("%2s ",8*i);
            for(int j=0;j<8;j++){
                switch (board[j][i]){

                    case 0:
                        System.out.print(Character.toString((char)65283));
                        break;
                    case -2:
                        System.out.print(Character.toString((char)9923));
                        break;
                    case -1:
                        System.out.print(Character.toString((char)9922));
                        break;
                    case 1:
                        System.out.print(Character.toString((char)9920));
                        break;
                    case 2:
                        System.out.print(Character.toString((char)9921));

                }
                //System.out.print(board[j][i]+" ");
            }
            System.out.println();
        }
    }

    /**
     * Prints the board state
     */
    public void print(){

        String[] pieces = {"k","m",".","M","K"};
        System.out.print("   ");
        for(int i=0;i<8;i++){
            System.out.print(i+" ");
        }
        for(int i=0;i<8;i++){
            System.out.printf("\n%2s ",(i*8));
            for(int j=0;j<8;j++){
                System.out.print(pieces[this.board[j][i]+2]+" ");
            }
            System.out.printf("%2s",(i*8));
        }
        System.out.print("\n   ");
        for(int i=0;i<8;i++){
            System.out.print(i+" ");
        }
        System.out.println();
    }

    /**
     * Applies move to board
     * @param move - move you want to do (example: 34-43)
     * @return A new board state with move applied (doesn't change this board)
     */
    public Board makeMove(String move){

        //  create copy of this board
        Board newBoard = new Board(this);

        //  for every hop in move
        String[] moveArray = move.split("-");
        for(int i=0;i<moveArray.length-1;i++){

            //  get stating square
            int start = Integer.parseInt(moveArray[i]);
            int startX = start%8;
            int startY = start/8;

            //  get ending square
            int end = Integer.parseInt(moveArray[i+1]);
            int endX = end%8;
            int endY = end/8;

            //  move piece
            newBoard.board[endX][endY] = newBoard.board[startX][startY];
            newBoard.board[startX][startY] = 0;

            //  if this is a capture move
            if(Math.abs(start-end)>13){

                //  find hopped piece coordinates
                int killX = ((start+end)/2)%8;
                int killY = ((start+end)/2)/8;

                //  decrement correct piece count
                int piece = newBoard.board[killX][killY];
                if(piece>0){
                    newBoard.whitePieceCount--;
                    if(piece==2){
                        newBoard.whiteKingCount--;
                    }
                }
                else {
                    newBoard.blackPieceCount--;
                    if(piece==-2){
                        newBoard.blackKingCount--;
                    }
                }

                //  kill piece that was hopped
                newBoard.board[killX][killY] = 0;
            }
        }

        //  change turn
        newBoard.turn *=-1;

        //  king any pieces needing kinging
        for(int i=0;i<8;i++){
            if(newBoard.board[i][0]==1){
                newBoard.board[i][0]=2;
            }
            if(newBoard.board[i][7]==-1){
                newBoard.board[i][7]=-2;
            }
        }

        //  add one to move count
        newBoard.moveCount++;

        //  return the new gameboard
        return newBoard;
    }

    public int getMoveCount(){
        return this.moveCount;
    }

    public int getWhitePieceCount() {
        return whitePieceCount;
    }

    public int getBlackPieceCount() {
        return blackPieceCount;
    }

    public int getTurn(){
        return turn;
    }

    /**
     * Lets user know if a player has no pieces left
     * @return 0 if game isn't over, 1 if white wins, -1 if black wins
     */
    public int isOver(){

        if(this.whitePieceCount==0){
            return -1;
        }
        else if(this.blackPieceCount==0){
            return 1;
        }
        else {
            return 0;
        }
    }

    public int getWhiteKingCount() {
        return this.whiteKingCount;
    }

    public int getBlackKingCount() {
        return this.blackKingCount;
    }
}
