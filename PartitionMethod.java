import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.lang.Integer;

public class PartitionMethod
{
  private static final char NORMAL = 's';
  private static final char GUIDED = 'g';
  private static final int INCOMPATIBLE = 0;
  private static final int COMPATIBLE = 1;
  private static final int UNKNOWN = 2;

  public static void main(String[] args)
  {
    if(args.length == 1 || args.length == 2) {
      String fileName = System.getProperty("user.dir") + "/inputs/" + args[0];

      if(Files.exists(Paths.get(fileName))) {
        char mode = modeSelection(args);

        try {
          System.out.print("Input found.\nReading contents... ");
          List<String> fileLines = Files.readAllLines(Paths.get(fileName));
          System.out.println("Done!");
          partitionMethod(fileLines, mode);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else System.out.println("Input not found. Make sure the input file name is correct and run again.");
    }
    else usage();
  }

  private static void partitionMethod(List<String> fileLines, char mode)
  {
    Object[] obj;
    int states  = Integer.valueOf(fileLines.get(0));
    int inputs  = Integer.valueOf(fileLines.get(1));
    int outputs = Integer.valueOf(fileLines.get(2));
    fileLines = fileLines.subList(3, states + 3);

    int[][] nextStatesMatrix  = new int[states][inputs];
    int[][] outputsMatrix     = new int[states][outputs];

    obj = extractDataFromFile(fileLines, nextStatesMatrix, inputs, outputsMatrix, outputs);
    nextStatesMatrix = (int[][])obj[0]; outputsMatrix = (int[][])obj[1];


    int[][] implicationChart = new int[states][states];
    implicationChart = buildImplicationChart(states, implicationChart, mode);
    implicationChart = partitionateBasedOnOutput(states, implicationChart, outputsMatrix, outputs, mode);
    implicationChart = partitionateBasedOnNextStates(states, implicationChart, nextStatesMatrix, inputs, mode);
    answer(states, implicationChart);
  }

  private static Object[] extractDataFromFile(List<String> fileLines, int[][] nextStatesMatrix, int inputs, int[][] outputsMatrix, int outputs)
  {
    //build matrixes with values
    int row = 0, column;
    for(String line : fileLines) {
      int start = line.indexOf('('), end = line.indexOf(')');
      String str = line.substring(start + 1, end);
      String[] values = str.split(" ");

      for(column = 0; column < inputs; column++)
        nextStatesMatrix[row][column] = Integer.valueOf(values[column]);

      start = line.indexOf('(', end); end = line.indexOf(')', start);
      str = line.substring(start + 1, end);
      values = str.split(" ");

      for(column = 0; column < outputs; column++)
        outputsMatrix[row][column] = Integer.valueOf(values[column]);

      row++;
    }
    return new Object[]{nextStatesMatrix, outputsMatrix};
  }

  private static int[][] partitionateBasedOnNextStates(int states, int[][] implicationChart, int[][] nextStatesMatrix, int inputs, char mode)
  {
    boolean restart = true, changed;

    while(restart) {
      changed = false;
      for(int state2 = states - 1; state2 > 0; state2--) {
        for(int state1 = 0; state1 < state2; state1++)
          if(implicationChart[implicationChartRowIndex(state2, states)][state1] == UNKNOWN)
            for(int input = 0; input < inputs; input++) {
              if(implicationChart[implicationChartRowIndex(nextStatesMatrix[state2][input], states)][nextStatesMatrix[state1][input]] == INCOMPATIBLE) {
                implicationChart[implicationChartRowIndex(state2, states)][state1] = INCOMPATIBLE;
                changed = true;
                break;
              }
              if(implicationChart[implicationChartRowIndex(nextStatesMatrix[state1][input], states)][nextStatesMatrix[state2][input]] == INCOMPATIBLE) {
                implicationChart[implicationChartRowIndex(state2, states)][state1] = INCOMPATIBLE;
                changed = true;
                break;
              }
            }
        if(state2 == 1 && !changed)
          restart = false;
      }
    }

    for(int state1 = 0; state1 < states; state1++)
      for(int state2 = 0; state2 < states; state2++)
        if(implicationChart[state2][state1] == UNKNOWN)
          implicationChart[state2][state1] = COMPATIBLE;

    if(mode == GUIDED) guidedMode(states, implicationChart);

    return implicationChart;
  }

  private static int[][] partitionateBasedOnOutput(int states, int[][] implicationChart, int[][] outputsMatrix, int outputs, char mode)
  {
    for(int state2 = states - 1; state2 > 0; state2--)
      for(int state1 = 0; state1 < state2; state1++)
        for(int output = 0; output < outputs; output++)
          if(outputsMatrix[state1][output] != outputsMatrix[state2][output])
            implicationChart[implicationChartRowIndex(state2, states)][state1] = INCOMPATIBLE;

    if(mode == GUIDED) guidedMode(states, implicationChart);

    return implicationChart;
  }

  private static int[][] buildImplicationChart(int states, int[][] implicationChart, char mode)
  {
    for(int state1 = 0; state1 < states; state1++)
    for(int state2 = 0; state2 < states; state2++)
    implicationChart[state2][state1] = UNKNOWN;

    if(mode == GUIDED) guidedMode(states, implicationChart);

    return implicationChart;
  }

  private static void guidedMode(int states, int[][] implicationChart)
  {
    clear();
    System.out.println("\t S0 S1 ... S(N - 1)  ----->");
    System.out.println("| SN");
    System.out.println("| S(N-1)");
    System.out.println("| ...");
    System.out.println("| S1");
    System.out.println("V");
    for(int state2 = states - 1; state2 > 0; state2--) {
      System.out.print("\t ");
      for(int state1 = 0; state1 < state2; state1++)
        System.out.print(replace(implicationChart[implicationChartRowIndex(state2, states)][state1]) + " ");
      System.out.println();
    }
    System.out.println();
  }

  private static void answer(int states, int[][] implicationChart)
  {
    boolean[] writtenStates = new boolean[states];
    for(int i = 0; i < states; i++) writtenStates[i] = false;

    System.out.print("\nAnswer: ");
    for(int state1 = 0; state1 < states; state1++) {
      boolean first = true;
      for(int state2 = states - 1; state2 > state1; state2--)
        if(implicationChart[implicationChartRowIndex(state2, states)][state1] == COMPATIBLE) {
          if(first && !writtenStates[state1]) { System.out.print("( S" + state1 + " "); first = false; writtenStates[state1] = true; }
          if(!writtenStates[state2]) System.out.print("S" + state2 + " "); writtenStates[state2] = true;
        }
      if(!first) System.out.print(") ");
    }

    for(int i = 0; i < states; i++)
      if(!writtenStates[i]) System.out.print("( S" + i + " ) ");

    System.out.println();
  }

  private static char replace(int compatibility)
  {
    switch(compatibility) {
      case INCOMPATIBLE:
        return 'I';
      case COMPATIBLE:
        return 'C';
      default:
        return 'U';
    }
  }

  private static int implicationChartRowIndex(int state, int states)
  {
    return (states - 1) - state;
  }

  private static char modeSelection(String[] args)
  {
    if(args.length == 2 && args[1].equalsIgnoreCase("-g")) {
      System.out.println("Selected guided mode.");
      return GUIDED;
    }
    if(args.length == 2 && !args[1].equalsIgnoreCase("-g"))
      System.out.println("Unrecognized mode. Selected normal mode by default.");
    if(args.length == 1)
      System.out.println("Selected normal mode by default.");
    return NORMAL;
  }

  public static void clear()
  {
    System.out.print("\033[H\033[2J");
    System.out.flush();
   }

  private static void usage()
  {
    System.out.println("Usage:\n\n\t$ java PartitionMethod <input_file> [-g]");
    System.out.println("\nNote: <input_file> must be inside inputs directory.");
    System.out.println("      -g is an optional paramater for step-by-step execution.");
  }
}
