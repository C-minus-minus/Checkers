import java.util.ArrayList;
import java.util.Scanner;

public class Checkers {

    public static void main(String[] args) {

        //  user input stuff
        String move;
        Scanner scanner = new Scanner(System.in);

        //  set up starting state of board
        Board board = new Board();

        //  set up AI to play against
        AI blackAI = new AI(10,0);

        //  create object to generate moves
        MoveGenerator moveGenerator = new MoveGenerator();

        //  display board
        board.print();

        //  main game loop
        while(true){

            //  announce who's turn it is to move
            System.out.println("\n"+(board.getTurn()==1?"White":"Black")+" To Play");

            //  player selects move
            if(board.getTurn()==1){

                //  generate legal moves
                ArrayList<String> legalMoves = moveGenerator.moveGen(board);

                //  input loop
                while(true){

                    //  display options
                    System.out.println("Move options are: "+legalMoves);

                    //  get move input from user
                    System.out.print("Please Enter Move: ");
                    move = scanner.nextLine();

                    //  check move legality
                    if(!legalMoves.contains(move)){
                        System.out.println("Sorry that move isn't legal, please try again");
                        continue;
                    }
                    break;
                }

                //  display chosen move
                System.out.println("Player chooses "+move);
            }

            //  AI selects move
            else {
                System.out.println("AI is thinking...");
                move = blackAI.getMove(board);
                System.out.println("AI chooses "+move);
            }

            //  make move
            board = board.makeMove(move);

            //  display board
            board.print();

            //  check for end conditions
            if(board.isOver()!=0){

                //  display victor
                System.out.println((board.isOver()==1?"White":"Black")+" Wins in "+board.getMoveCount()+" moves");
                break;
            }
            if(moveGenerator.moveGen(board).size()==0){
                //  display victor
                System.out.println((board.getTurn()==-1?"White":"Black")+" Wins in "+board.getMoveCount()+" moves");
            }
        }
    }

    /**
     * Has to AIs play checkers and prints the percentage of white wins
     * @param trials - the amount of games you want the AIs to play
     * @param white - The AI you want to play white
     * @param black - The AI you want to play black
     */
    private static void AIBattle(int trials, AI white,AI black){

        MoveGenerator moveGenerator = new MoveGenerator();
        double whiteWins = 0;
        for(int i=0;i<trials;i++){

            Board board = new Board();

            while(true){

                //  make move
                if(board.getTurn()==1){
                    board = board.makeMove(white.getMove(board));
                }
                else {
                    board = board.makeMove(black.getMove(board));
                }

                //  check for end conditions
                if(board.isOver()!=0){

                    if(board.isOver()==1){
                        whiteWins++;
                    }
                    break;
                }
                if(moveGenerator.moveGen(board).size()==0){
                    if(board.getTurn()==-1){
                        whiteWins++;
                    }
                    break;
                }
            }
        }
        System.out.printf("White won %.4s%% of the games",(whiteWins/(double)trials)*100);
    }
}


