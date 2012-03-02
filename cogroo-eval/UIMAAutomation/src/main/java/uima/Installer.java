package uima;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.jar.JarFile;

import org.apache.uima.pear.tools.InstallationController;
import org.apache.uima.pear.tools.InstallationDescriptor;
import org.apache.uima.pear.tools.InstallationDescriptorHandler;
import org.apache.uima.pear.util.MessageRouter;
import org.apache.uima.pear.util.UIMAUtil;

public class Installer  {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  protected static class RunInstallation implements Runnable {
    private File pearFile;

    private File installationDir = null;

    /**
     * Constructor that sets a given input PEAR file and a given installation directory.
     * 
     * @param pearFile
     *          The given PEAR file.
     * @param installationDir
     *          The given installation directory.
     */
    public RunInstallation(File pearFile, File installationDir) {
      this.pearFile = pearFile;
      this.installationDir = installationDir;
    }

    /**
     * Runs the PEAR installation process. Notifies waiting threads upon completion.
     */
    public void run() {
      installPear(pearFile, installationDir);
      synchronized (this) {
        notifyAll();
      }
    }

  }
  
  static boolean errorFlag;
  static String message;
  
  static StringBuilder pearConsole = new StringBuilder();
  
  private static InstallationDescriptor insdObject;
  
  private static String mainComponentId;
  private static String mainComponentRootPath;
  private static final String SET_ENV_FILE = "metadata/setenv.txt";

  /**
   * Method that installs the given PEAR file to the given installation directory.
   * 
   * @param localPearFile
   *          The given PEAR file path.
   * @param installationDir
   *          The given installation directory.
   */
  private static void installPear(File localPearFile, File installationDir) {
    InstallationController.setLocalMode(true);
    InstallationDescriptorHandler installationDescriptorHandler = new InstallationDescriptorHandler();
    printInConsole(false, "");
    // check input parameters
    if (localPearFile != null && !localPearFile.exists()) {
      errorFlag = true;
      message = localPearFile.getAbsolutePath() + "file not found \n";
      printInConsole(errorFlag, message);
    } else {
      if (localPearFile != null) {
        pearConsole.append("PEAR file to install is => " + localPearFile.getAbsolutePath() + "\n");
      }
    }
    /* setting current working directory by default */
    if (installationDir == null) {
      installationDir = new File("./");
    }
    pearConsole.append("Installation directory is => " + installationDir.getAbsolutePath() + "\n");

    try {
      JarFile jarFile = new JarFile(localPearFile);
      installationDescriptorHandler.parseInstallationDescriptor(jarFile);
      insdObject = installationDescriptorHandler.getInstallationDescriptor();

      if (insdObject != null)
        mainComponentId = insdObject.getMainComponentId();

      else {
//        pearConsole.setForeground(new Color(0xFF0000));
        throw new FileNotFoundException("installation descriptor not found \n");
      }
      // this version does not support separate delegate components
      if (insdObject.getDelegateComponents().size() > 0) {
        throw new RuntimeException("separate delegate components not supported \n");
      }
    } catch (Exception err) {
      errorFlag = true;
      message = " terminated \n" + err.toString();
      printInConsole(errorFlag, message);
      System.exit(-1);
    }
    InstallationController installationController = new InstallationController(mainComponentId,
            localPearFile, installationDir);
    // adding installation controller message listener
    installationController.addMsgListener(new MessageRouter.StdChannelListener() {
      public void errMsgPosted(String errMsg) {
        printInConsole(true, errMsg);
      }

      public void outMsgPosted(String outMsg) {
        printInConsole(false, outMsg);
      }
    });
    insdObject = installationController.installComponent();
    if (insdObject == null) {
//      runButton.setEnabled(false);
      /* installation failed */
      errorFlag = true;
      message = " \nInstallation of " + mainComponentId + " failed => \n "
              + installationController.getInstallationMsg();
      printInConsole(errorFlag, message);

    } else {
      try {

        /* save modified installation descriptor file */
        installationController.saveInstallationDescriptorFile();
        mainComponentRootPath = insdObject.getMainComponentRoot();
        errorFlag = false;
        message = " \nInstallation of " + mainComponentId + " completed \n";
        printInConsole(errorFlag, message);
        message = "The " + mainComponentRootPath + "/" + SET_ENV_FILE
                + " \n    file contains required " + "environment variables for this component\n";
        printInConsole(errorFlag, message);
        /* 2nd step: verification of main component installation */
        if (installationController.verifyComponent()) {
          // enable 'run' button only for AE
          File xmlDescFile = new File(insdObject.getMainComponentDesc());
          try {
            String uimaCompCtg = UIMAUtil.identifyUimaComponentCategory(xmlDescFile);
          } catch (Exception e) {
            // Ignore exceptions!
          }
          errorFlag = false;
          message = "Verification of " + mainComponentId + " completed \n";
          printInConsole(errorFlag, message);
        } else {
          errorFlag = true;
          message = "Verification of " + mainComponentId + " failed => \n "
                  + installationController.getVerificationMsg();
          printInConsole(errorFlag, message);
        }
      } catch (Exception exc) {
        errorFlag = true;
        message = "Error in InstallationController.main(): " + exc.toString();
        printInConsole(errorFlag, message);
      } finally {
        installationController.terminate();
      }
    }
    if(errorFlag) {
      System.exit(-1);
    }
  }
  
  private static void printInConsole(boolean b, String string) {
    if(b) {
      System.err.println(string);
    } else {
      System.out.println(string);
    }
  }

  public static void main(String[] args) {
    
    if(args.length != 2) {
      System.out.println("Please pass pearFile and destFolder args");
      System.exit(-1);
    }
    
    File pear = new File(args[0]);
    File dest = new File(args[1]);
    RunInstallation installPear = new RunInstallation(pear, dest);
    Thread thread = new Thread(installPear);
    thread.start();
    synchronized (installPear) {
      try {
        installPear.wait(500000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
        System.exit(-1);
      }
    }
  }

}
