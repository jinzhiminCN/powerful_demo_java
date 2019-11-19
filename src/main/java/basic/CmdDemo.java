package basic;

import java.util.Date;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description:
 */
public class CmdDemo {
  private static final Logger logger = LoggerFactory.getLogger(CmdDemo.class);

  public static void main(String[] args) {
      //
  }

  public void cmd2(String[] args) throws  Exception{
      args = new String[]{"-t", "rensanning", "-f", "c:/aa.txt", "-b", "-s10", "-Dkey1=value1", "-Dkey2=value2" };

      try {
          // create Options object
          Options options = new Options();
          options.addOption(new Option("t", "text", true, "use given information(String)"));
          options.addOption(new Option("b", false, "display current time(boolean)"));
          options.addOption(new Option("s", "size", true, "use given size(Integer)"));
          options.addOption(new Option("f", "file", true, "use given file(File)"));

          @SuppressWarnings({"static-access"})
          Option property = OptionBuilder.withArgName("property=value")
              .hasArgs(2)
              .withValueSeparator()
              .withDescription("use value for given property(property=value)")
              .create("D");
          property.setRequired(true);
          options.addOption(property);

          // print usage
          HelpFormatter formatter = new HelpFormatter();
          formatter.printHelp( "AntOptsCommonsCLI", options );
          System.out.println();

          // create the command line parser
          CommandLineParser parser = new PosixParser();
          CommandLine cmd = parser.parse(options, args);

          // check the options have been set correctly
          System.out.println(cmd.getOptionValue("t"));
          System.out.println(cmd.getOptionValue("f"));
          if (cmd.hasOption("b")) {
              System.out.println(new Date());
          }
          System.out.println(cmd.getOptionValue( "s" ));
          System.out.println(cmd.getOptionProperties("D").getProperty("key1") );
          System.out.println(cmd.getOptionProperties("D").getProperty("key2") );

      } catch (Exception ex) {
          System.out.println( "Unexpected exception:" + ex.getMessage() );
      }
  }

  public void cmd1(String[] args) throws Exception {
    // Create a Parser
    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("h", "help", false, "Print this usage information");
    options.addOption("v", "verbose", false, "Print out VERBOSE information");
    options.addOption("f", "file", true, "File to save program output to");
    // Parse the program arguments
    CommandLine commandLine = parser.parse(options, args);
    // Set the appropriate variables based on supplied options
    boolean verbose = false;
    String file = "";

    if (commandLine.hasOption('h')) {
      System.out.println("Help Message");
      System.exit(0);
    }
    if (commandLine.hasOption('v')) {
      verbose = true;
    }
    if (commandLine.hasOption('f')) {
      file = commandLine.getOptionValue('f');
    }
  }
}
