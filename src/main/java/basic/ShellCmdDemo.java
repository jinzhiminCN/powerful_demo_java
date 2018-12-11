package basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jinzhimin
 * @description: shell 命令执行
 */
public class ShellCmdDemo {

    private static final Logger logger = LoggerFactory.getLogger(ShellCmdDemo.class);

    public static String executeSimpleCmd(String cmd) {
        logger.info("cmd : " + cmd);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[8192];
            int readSize = 0;
            while ( (readSize = in.read(b)) != -1 ) {
                out.append(new String(b, 0, readSize));
            }
            logger.info("result:" + out.toString());
            in.close();
            process.destroy();

            return out.toString();
        } catch (IOException e) {
            logger.info("出现IO异常！", e);
        }
        return null;
    }

    public static List<String> executePipeCmd(String cmd){
        logger.info("cmd : " + cmd);
        Runtime run = Runtime.getRuntime();

        try {
            Process process = run.exec(new String[] {"/bin/sh", "-c", cmd});
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            List<String> list = new ArrayList<>();
            String result = null;
            logger.info("result:");
            while ((result = bs.readLine()) != null) {
                logger.info(result);
                list.add(result);
            }
            in.close();
            process.destroy();
            return list;
        } catch (IOException e) {
            logger.info("出现IO异常！", e);
        }
        return null;
    }

    public static void executeCmdFlow(){
        Runtime run = Runtime.getRuntime();
        File wd = new File("/bin");
        Process proc = null;
        try {
            proc = run.exec("/bin/bash", null, wd);
        } catch (IOException e) {
            logger.info("出现IO异常！", e);
        }
        if (proc != null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            out.println("cd /home/test");
            out.println("pwd");
            out.println("ls");
            // 这个命令必须执行，否则in流不结束。
            out.println("exit");
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    logger.info(line);
                }
                proc.waitFor();
                in.close();
                out.close();
                proc.destroy();
            } catch (Exception e) {
                logger.info("出现异常！", e);
            }
        }
    }

    public static List<String> executeCmdFlow(List<String> commands) {
        List<String> rspList = new ArrayList<String>();
        Runtime run = Runtime.getRuntime();
        try {
            Process proc = run.exec("/bin/bash", null, null);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            for (String line : commands) {
                out.println(line);
            }
            // 这个命令必须执行，否则in流不结束。
            out.println("exit");
            String rspLine = "";
            while ((rspLine = in.readLine()) != null) {
                logger.info(rspLine);
                rspList.add(rspLine);
            }
            proc.waitFor();
            in.close();
            out.close();
            proc.destroy();
        } catch (IOException e1) {
            logger.info("出现IO异常！", e1);
        } catch (InterruptedException e) {
            logger.info("出现异常！", e);
        }
        return rspList;
    }

    public static void main(String[] args) {
         String result = executeSimpleCmd("ls");
         logger.info(result);
    }
}
