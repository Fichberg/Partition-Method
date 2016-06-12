import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.lang.Integer;

public class PartitionMethod
{
  private static final char NORMAL = 's';
  private static final char GUIDED = 'g';

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
    int states  = Integer.valueOf(fileLines.get(0));
    int inputs  = Integer.valueOf(fileLines.get(1));
    int outputs = Integer.valueOf(fileLines.get(2));
    fileLines = fileLines.subList(3, states + 3);

    int row = 0, column;
    int[][] nextStatesMatrix  = new int[states][inputs];
    int[][] outputsMatrix     = new int[states][outputs];

    //build matrixes with values
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

  private static void usage()
  {
    System.out.println("Usage:\n\n\t$ java PartitionMethod <input_file> [-g]");
    System.out.println("\nNote: <input_file> must be inside inputs directory.");
    System.out.println("      -g is an optional paramater for step-by-step execution.");
  }
}
