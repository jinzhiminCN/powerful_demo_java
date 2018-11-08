package constant;

/**
 * @author jinzhimin
 * @description: FTP文件上传状态的枚举类
*/
public enum UploadFtpStatusEnum {
    /**
     * 断点上传成功。
     */
    Upload_From_Break_Success,
    /**
     * 断点上传失败。
     */
    Upload_From_Break_Failed,
    /**
     * 新文件上传成功。
     */
    Upload_New_File_Success,
    /**
     * 新文件上传失败。
     */
    Upload_New_File_Failed,
    /**
     * 文件已经存在。
     */
    File_Exits,
    /**
     * 远程文件比本地文件大。
     */
    Remote_Bigger_Local,
}
