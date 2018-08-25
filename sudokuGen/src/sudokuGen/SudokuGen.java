package sudokuGen;

import java.util.concurrent.ThreadLocalRandom;

public class SudokuGen {

	public static void main(String[] args) {

		int[][] board = tableGen("S1.txt");
		int[][][] domains = eliminateDomains(board);
	}
	// ************** Sudoku - Genarate new board ****************

	public static int[][] tableGen(String fileToRead) {
		int[][] board = fillFirstLevel();
		printBoard(board);
		System.out.println("\n\n\n***********************\n\n");

		//fillSecondLevel(board);

		solveSudoku(board);
		printBoard(board);

		return board;
	}

	public static int[][] fillFirstLevel() {
		int col = 0, row = 0;
		int[][] board = new int[9][9];

		for (int i = 1; i < 10; i++) {
			do {
				row = ThreadLocalRandom.current().nextInt(0, 3);
				col = ThreadLocalRandom.current().nextInt(0, 3);
			} while (board[row][col] != 0);

			board[row][col] = i;
			board[row + 6][col + 6] = (9-i);
		}

		for (int i = 1; i < 10; i++) {
			do {
				row = ThreadLocalRandom.current().nextInt(3, 6);
				col = ThreadLocalRandom.current().nextInt(3, 6);
			} while (board[row][col] != 0);

			board[row][col] = i;
		}

		if (ThreadLocalRandom.current().nextInt(1, 21) % 2 == 1) {
			swap(board, 6, 7, 7, 6);
			swap(board, 6, 8, 8, 6);
			swap(board, 7, 8, 8, 7);
		} else {
			swap(board, 8, 6, 6, 8);
			swap(board, 7, 8, 8, 7);
			swap(board, 7, 6, 6, 7);
		}

		return board;
	}

	public static void swap(int[][] board, int row1, int col1, int row2, int col2) {
		int temp = board[row1][col1];
		board[row1][col1] = board[row2][col2];
		board[row2][col2] = temp;
	}

	public static int[][] fillSecondLevel(int[][] board) {
		int col = 0, row = 0;
		int[][][] domains = eliminateDomains(board);

		for (int i = 0; i < 9; i++) {
			for (row = 6; row < 9; row++) {
				for (col = 2; col >= 0; col--) {

					if (board[row][col] == 0) {
						for (int j = 0; j < domains.length; j++) {
							if (domains[row][col][j] == 1) {
								board[row][col] = j + 1;

								for (int row11 = 6; row11 < 9; row11++) {
									for (int col11 = 0; col11 < 3; col11++) {
										if (domains[row11][col11][j] != 0)
											domains[row11][col11][j] = -1;
									}
								}
								boxFill(domains, board, 6, 0, 9, 3);

								break;
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < 9; i++) {
			for (col = 6; col < 9; col++) {
				for (row = 2; row >= 0; row--) {

					if (board[row][col] == 0) {
						for (int j = 0; j < domains.length; j++) {
							if (domains[row][col][j] == 1) {
								board[row][col] = j + 1;

								for (int row11 = 0; row11 < 3; row11++) {
									for (int col11 = 6; col11 < 9; col11++) {
										if (domains[row11][col11][j] != 0)
											domains[row11][col11][j] = -1;
									}
								}

								boxFill(domains, board, 0, 6, 3, 9);
								break;
							}
						}
					}
				}
			}
		}
		return board;
	}

	public static boolean boxFill(int[][][] domains, int[][] board, int row1, int col1, int row2, int col2) {
		int tmp = 0, counter = 0;
		boolean result = false;

		for (int i = row1; i < row2; i++) {
			for (int j = col1; j < col2; j++) {
				if (board[i][j] == 0) {// if not exist number, fill the number. working like eliminateDomains
					counter = 0;
					for (int k = 0; k < board.length; k++) {
						if (domains[i][j][k] == 1) {
							counter++;
							tmp = k;
						}
					}

					if (counter == 1) {
						result = true;
						board[i][j] = tmp + 1;

						int boxRowOffset = (i / 3) * 3;
						int boxColOffset = (j / 3) * 3;

						for (int row = 0; row < 3; row++) {
							for (int col = 0; col < 3; col++) {
								if (domains[boxRowOffset + row][boxColOffset + col][tmp] != 0)
									domains[boxRowOffset + row][boxColOffset + col][tmp] = -1;
							}
						}

						for (int k = 0; k < board.length; k++) {
							domains[i][j][k] = 0;
						}
					} // counter =1
				} // if the board not null
			} // for j
		} // for i

		return result;
	}

	// ************** Sudoku - int *******************************

	public static int[][][] init(int[][] board) {
		int tmp = 0;
		int[][][] domains = new int[9][9][9];

		for (int i = 0; i < domains.length; i++) {
			for (int j = 0; j < domains.length; j++) {
				if (board[i][j] == 0)// if not exists number
					tmp = 1;
				else// if exists number
					tmp = 0;

				for (int k = 0; k < domains.length; k++) {// filling the right value
					domains[i][j][k] = tmp;
				}
			}
		}

		return domains;
	}

	// ************** Sudoku - Part1 (iterative) *****************

	public static int[][][] eliminateDomains(int[][] board) {
		int tmp = 0;

		int[][][] domains = init(board);// init the matrix, with the board

		do {// while we can make iterative, do it
			for (int i = 0; i < domains.length; i++) {
				for (int j = 0; j < domains.length; j++) {
					if (board[i][j] != 0) {// if we have number, so we remove him from the options in the domain in
											// qube,row and col
						tmp = board[i][j] - 1;

						for (int row = 0; row < domains.length; row++) {// for the row
							if (domains[row][j][tmp] != 0)
								domains[row][j][tmp] = -1;
						}

						for (int col = 0; col < domains.length; col++) {// for col
							if (domains[i][col][tmp] != 0)
								domains[i][col][tmp] = -1;
						}

						int boxRowOffset = (i / 3) * 3;
						int boxColOffset = (j / 3) * 3;

						for (int row = 0; row < 3; row++) {// for the qube
							for (int col = 0; col < 3; col++) {
								if (domains[boxRowOffset + row][boxColOffset + col][tmp] != 0)
									domains[boxRowOffset + row][boxColOffset + col][tmp] = -1;
							}
						}
					} // if the board not null
				} // for j
			} // for i
		} while (fill(domains, board));

		return domains;
	}

	// ************** Sudoku - fill ******************************

	public static boolean fill(int[][][] domains, int[][] board) {// in this function we fill iterative all option that
																	// have only one way
		int tmp = 0, counter = 0;
		boolean result = false;

		for (int i = 0; i < domains.length; i++) {
			for (int j = 0; j < domains.length; j++) {
				if (board[i][j] == 0) {// if not exist number, fill the number. working like eliminateDomains
					counter = 0;
					for (int k = 0; k < board.length; k++) {
						if (domains[i][j][k] == 1) {
							counter++;
							tmp = k;
						}
					}

					if (counter == 1) {
						result = true;
						board[i][j] = tmp + 1;

						for (int row = 0; row < domains.length; row++) {
							if (domains[row][j][tmp] != 0)
								domains[row][j][tmp] = -1;
						}

						for (int col = 0; col < domains.length; col++) {
							if (domains[i][col][tmp] != 0)
								domains[i][col][tmp] = -1;
						}

						int boxRowOffset = (i / 3) * 3;
						int boxColOffset = (j / 3) * 3;

						for (int row = 0; row < 3; row++) {
							for (int col = 0; col < 3; col++) {
								if (domains[boxRowOffset + row][boxColOffset + col][tmp] != 0)
									domains[boxRowOffset + row][boxColOffset + col][tmp] = -1;
							}
						}

						for (int k = 0; k < board.length; k++) {
							domains[i][j][k] = 0;
						}
					} // counter =1
				} // if the board not null
			} // for j
		} // for i

		return result;
	}

	// ************** Sudoku - print *****************************

	public static void printBoard(int[][] board) {
		// in this function we print the board
		for (int i = 0; i < board.length; i++) {
			if (i % 3 == 0 && i != 0)
				System.out.println("---+---+---");

			for (int j = 0; j < board.length; j++) {
				if (j % 3 == 0 && j != 0)
					System.out.print("|");
				System.out.print(board[i][j]);
			}
			System.out.println();

		}
	}

	public static void printOptions(int[][][] domains, int[][] board) {
		// int this part we print the option for any number
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				System.out.print(i + "," + j + " = ");
				if (domains[i][j][0] == 0)
					System.out.print(board[i][j] + ",");
				else {
					for (int k = 0; k < board.length; k++) {
						if (domains[i][j][k] == 1)
							System.out.print(k + 1 + ",");

					}
				}
				System.out.println();
			}
		}
	}

	// ************** Sudoku - Part2 (recursive) *****************

	public static boolean solveSudoku(int[][] board) {
		int row = 0, col = 0;
		int[][][] domains = eliminateDomains(board);
		int[] index = new int[2];// index[0]=row,index[1]=col

		if (if_full(board, index, 0, 0)) {// check recursive if theboard full
			return true;// if full return true
		}

		row = index[0];// save the index in the first place that dont have a sulotion
		col = index[1];

		int[][] tmp_board = new int[9][9];

		return all_options(domains, board, tmp_board, row, col, 0);// check all option dor the place row,col
	}

	public static boolean all_options(int[][][] domains, int[][] board, int[][] tmp_board, int row, int col, int k) {

		if (k == 9)
			return false;

		if (domains[row][col][k] == 1) {
			cpy_arr(board, tmp_board, 0, 0);// save temp array of the board

			tmp_board[row][col] = k + 1;

			if (solveSudoku(tmp_board)) {// check if have a solution
				cpy_arr(tmp_board, board, 0, 0);// copy the full array
				return true;
			}
		}

		return all_options(domains, board, tmp_board, row, col, (k + 1));// running on the domin
	}

	public static boolean if_full(int[][] board, int[] index, int i, int j) {
		if (i == board.length)// return true if all places have something
			return true;

		if (board[i][j] == 0) {// if cell is not full exit and save index of this place
			index[0] = i;
			index[1] = j;
			return false;
		}

		j++;
		if (j == board.length) {
			j = 0;
			i++;
		}

		return if_full(board, index, i, j);
	}

	public static void cpy_arr(int[][] from_board, int[][] des_board, int i, int j) {// recursive copy function
		if (i == 9)
			return;

		des_board[i][j] = from_board[i][j];

		if (j == 8) {
			j = 0;
			i++;
		} else {
			j++;
		}

		cpy_arr(from_board, des_board, i, j);
	}

}
