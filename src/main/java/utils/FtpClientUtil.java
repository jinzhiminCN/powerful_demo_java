package utils;

import constants.UploadStatus;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class FtpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpClientUtil.class);

    private String host;
    private int port;
    private String username;
    private String password;

    private int bufferSize = 10 * 1024 * 1024;

    private int soTimeout = 15000;

    private FTPClient ftp;

    public FTPClient getFtp() {
        return ftp;
    }

    public void setFtp(FTPClient ftp) {
        this.ftp = ftp;
    }

    private UploadStatus uploadStatus;

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public static class Builder {
        private String host;
        private int port = 21;
        private String username;
        private String password;
        private int bufferSize = 1024 * 1024;
        private FTPClientConfig config;
        private int defaultTimeout = 15000;
        private int connectTimeout = 15000;
        private int dataTimeout = 15000;
        private int controlKeepAliveTimeout = 300;
        private int soTimeout = 15000;

        public Builder() {
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder config(FTPClientConfig config) {
            this.config = config;
            return this;
        }

        public Builder defaultTimeout(int defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder dataTimeout(int dataTimeout) {
            this.dataTimeout = dataTimeout;
            return this;
        }

        public Builder soTimeout(int soTimeout) {
            this.soTimeout = soTimeout;
            return this;
        }

        public Builder controlKeepAliveTimeout(int controlKeepAliveTimeout) {
            this.controlKeepAliveTimeout = controlKeepAliveTimeout;
            return this;
        }

        public FtpClientUtil build() throws IOException {
            FtpClientUtil instance = new FtpClientUtil(this.host, this.port, this.username, this.password,
                    this.bufferSize, this.config, this.defaultTimeout, this.dataTimeout, this.connectTimeout,
                    this.controlKeepAliveTimeout, this.soTimeout);
            return instance;
        }
    }

    private FtpClientUtil(String host, int port, String username, String password, int bufferSize,
                          FTPClientConfig config, int defaultTimeout, int dataTimeout, int connectTimeout,
                          int controlKeepAliveTimeout, int soTimeout) throws IOException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.bufferSize = bufferSize;
        this.soTimeout = soTimeout;
        this.ftp = new FTPClient();
        if (config != null) {
            this.ftp.configure(config);
        }
        ftp.setControlEncoding("UTF-8");
        ftp.enterLocalPassiveMode();
        ftp.setDefaultTimeout(defaultTimeout);
        ftp.setConnectTimeout(connectTimeout);
        ftp.setDataTimeout(dataTimeout);
        if (this.bufferSize > 0) {
            ftp.setBufferSize(this.bufferSize);
        }

        // keeping the control connection alive
        // 每大约5分钟发一次noop，防止大文件传输导致的控制连接中断
        ftp.setControlKeepAliveTimeout(controlKeepAliveTimeout);
    }

    public FtpClientUtil connect() throws SocketException, IOException {
        if (!this.ftp.isConnected()) {
            this.ftp.connect(this.host, this.port);
            int reply = this.ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.warn("ftp服务器返回码[{}], 连接失败...", reply);
                throw new IllegalStateException("连接ftp服务器失败,返回的状态码是" + reply);
            }
        }
        this.ftp.setSoTimeout(this.soTimeout);
        return this;
    }

    public FtpClientUtil login() throws IOException {
        boolean suc = this.ftp.login(this.username, this.password);
        if (!suc) {
            throw new IllegalStateException("登录ftp服务器失败");
        }
        return this;
    }

    /**
     * ftp上传文件功能
     *
     * @param file
     *            要上传的文件
     * @param relativePath
     *            要上传到ftp服务器的相对路径
     * @return
     * @throws IOException
     */
    public FtpClientUtil upload(File file, String relativePath) throws IOException {
        FileInputStream fInputStream = new FileInputStream(file);
        return this.upload(fInputStream, file.getName(), relativePath, file.length());
    }

    public FtpClientUtil upload(InputStream inputStream, String name, String relativePath, long localSize)
            throws IOException {
        ftp.setFileType(FTP.BINARY_FILE_TYPE);

        this.ftp.enterLocalPassiveMode();
        FTPFile[] listFiles = this.ftp.listFiles(name);
        // long localSize = inputStream.available();// ? 不知道好用否
        if (listFiles.length == 1) {
            long remoteSize = listFiles[0].getSize();

            if (remoteSize == localSize) {
                this.setUploadStatus(UploadStatus.File_Exits);
                return this;
            } else if (remoteSize > localSize) {
                this.setUploadStatus(UploadStatus.Remote_Bigger_Local);
                return this;
            }
            this.uploadFile(inputStream, name, remoteSize, localSize);
        } else {
            this.uploadFile(inputStream, name, 0, localSize);
        }
        logger.info("{}/{} upload success", relativePath, name);
        return this;
    }

    private void uploadFile(InputStream inputStream, String name, long remoteSize, long localSize) throws IOException {
        this.ftp.enterLocalPassiveMode();
        OutputStream output = null;
        long step = localSize / 100;
        long process = 0;
        long localReadBytes = 0L;
        try {
            if (remoteSize > 0) {
                output = this.ftp.appendFileStream(name);
                this.ftp.setRestartOffset(remoteSize);
                inputStream.skip(remoteSize);
                process = remoteSize / step;
                localReadBytes = remoteSize;
            } else {
                output = this.ftp.storeFileStream(name);
            }
            byte[] bytes = new byte[1024];
            int c;
            while ((c = inputStream.read(bytes)) != -1) {
                output.write(bytes, 0, c);
                localReadBytes += c;
                if (localReadBytes / step >= process + 10) {
                    process = localReadBytes / step;
                    logger.info("文件<" + name + ">上传ftp进度汇报, process = " + process);
                }
            }
            logger.info("文件" + name + "上传ftp进度汇报, process = " + 100);
            output.flush();
            inputStream.close();
            output.close();
            boolean result = this.ftp.completePendingCommand();
            if (remoteSize > 0) {
                this.setUploadStatus(
                        result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed);
            } else {
                this.setUploadStatus(
                        result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed);
            }
        } catch (Exception e) {
            this.setUploadStatus(
                    remoteSize > 0 ? UploadStatus.Upload_From_Break_Failed : UploadStatus.Upload_New_File_Failed);
        }

    }

    public OutputStream upload(String name, String relativePath) throws IOException {
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        changeWorkingDirectory(relativePath);
        ftp.enterLocalPassiveMode();
        return this.ftp.storeFileStream(name);
    }

    public void changeWorkingDirectory(String relativePath) throws IOException {
        if (relativePath == null) {
            throw new NullPointerException("relativePath can't be null");
        }
        String[] dirs = relativePath.split("/");
        for (String dir : dirs) {
            if (!this.ftp.changeWorkingDirectory(dir)) {
                if (this.ftp.makeDirectory(dir)) {
                    this.ftp.changeWorkingDirectory(dir);
                } else {
                    logger.warn("{}目录创建失败, 导致不能进入合适的目录进行上传", dir);
                }
            }
        }
    }

    /**
     * ftp上传目录下所有文件的功能
     *
     * @param file
     *            要上传的目录
     * @param relativePath
     *            要上传到ftp服务器的相对路径
     * @return
     * @throws IOException
     */
    public FtpClientUtil uploadDir(File file, String relativePath) throws IOException {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("file argument is not a directory!");
        }
        relativePath = relativePath + "/" + file.getName();
        File[] listFiles = file.listFiles();
        for (File f : listFiles) {
            this.uploadFree(f, relativePath);
        }
        return this;
    }

    /**
     * ftp上传文件, 调用方不用区分文件是否为目录，由该方法自己区分处理
     *
     * @param file
     *            要上传的文件
     * @param relativePath
     *            要上传到ftp服务器的相对路径
     * @return
     * @throws IOException
     */
    public FtpClientUtil uploadFree(File file, String relativePath) throws IOException {
        if (file.isDirectory()) {
            this.uploadDir(file, relativePath);
        } else {
            this.upload(file, relativePath);
        }
        return this;
    }

    /**
     * 本方法是上传的快捷方法，方法中自身包含了ftp 连接、登陆、上传、退出、断开各个步骤
     *
     * @param file
     *            要上传的文件
     * @param relativePath
     *            要上传到ftp服务器的相对路径
     */
    public boolean uploadOneStep(File file, String relativePath) {
        try {
            this.connect().login().uploadFree(file, relativePath);
            return true;
        } catch (IOException e) {
            String msg = String.format("ftp上传时发生异常, filename = [%s], relativePath = [%s]", file.getName(),
                    relativePath);
            logger.error(msg, e);
            return false;
        } finally {
            this.disconnectFinally();
        }
    }

    public boolean uploadOneStepForStream(InputStream inputStram, String name, String relativePath, long localSize) {
        try {
            this.connect().login().upload(inputStram, name, relativePath, localSize);
            return true;
        } catch (IOException e) {
            String msg = String.format("ftp上传时发生异常, filename = [%s], relativePath = [%s]", name, relativePath);
            logger.error(msg, e);
            return false;
        } finally {
            this.disconnectFinally();
        }
    }

    public interface OutputStreamForUpload {
        public void write(OutputStream outputStream) throws IOException;
    }

    public boolean uploadOneStepForStream(OutputStreamForUpload outputUpload, String name, String relativePath) {
        try {
            this.connect().login();
            OutputStream upload = this.upload(name, relativePath);
            outputUpload.write(upload);
            return true;
        } catch (IOException e) {
            String msg = String.format("ftp上传时发生异常, filename = [%s], relativePath = [%s]", name, relativePath);
            logger.error(msg, e);
            return false;
        } finally {
            this.disconnectFinally();
        }
    }

    public FtpClientUtil logout() throws IOException {
        this.ftp.logout();
        return this;
    }

    public void disconnect() {
        this.disconnectFinally();
    }

    private void disconnectFinally() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.disconnect();
            } catch (IOException ioe) {
                logger.warn("ftp断开服务器链接异常", ioe);
            }
        }
    }

    public String printWorkDir(){
        String pwd = "";
        try {
            pwd = this.ftp.printWorkingDirectory();
            logger.info("当前目录：" + pwd);
        } catch (IOException e) {
            e.printStackTrace();
            pwd = "";
        }
        return pwd;
    }

    public List listDir(String ftpPath){
        List fileNameList = new ArrayList<>();
        try {
            // 中文支持
            this.ftp.setControlEncoding("UTF-8");
            this.ftp.enterLocalPassiveMode();
            this.ftp.changeWorkingDirectory(ftpPath);

            FTPFile[] ftpFiles = this.ftp.listFiles();
            for(FTPFile ftpFile:ftpFiles){
                fileNameList.add(ftpFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNameList;
    }

    public void downloadFile(String ftpPath, String localPath,
                             String fileName) {
        try {
            // 中文支持
            this.ftp.setControlEncoding("UTF-8");
            this.ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            this.ftp.enterLocalPassiveMode();
            this.ftp.changeWorkingDirectory(ftpPath);

            File localFile = new File(localPath + File.separatorChar + fileName);
            OutputStream os = new FileOutputStream(localFile);
            this.ftp.retrieveFile(fileName, os);
            os.close();
        } catch (FileNotFoundException e) {
            logger.error("没有找到" + ftpPath + "文件");
            e.printStackTrace();
        } catch (SocketException e) {
            logger.error("连接FTP失败.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件读取错误。");
            e.printStackTrace();
        }
    }

    public void deleteFile(String fileName){
        String pwd = printWorkDir();
        try {
            this.ftp.setControlEncoding("UTF-8"); // 中文支持
            this.ftp.enterLocalPassiveMode();
            this.ftp.deleteFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("删除文件失败！文件;" + fileName + ", 当前目录:" + pwd);
        }
    }

    @Override
    public String toString() {
        return "FtpClientHelper [host=" + host + ", port=" + port + ", username="
                + username + ", password=" + password + "]";
    }

}