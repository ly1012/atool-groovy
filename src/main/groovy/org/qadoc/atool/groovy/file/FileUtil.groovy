package org.qadoc.atool.groovy.file

class FileUtil {


    def static FILE_SEPARATOR = System.getProperty("file.separator")

    /**
     * 强制删除文件。
     * 警告：1、本方法属于测试性质，不推荐使用。请优先通过关闭流（os.close()）来解决文件占用问题。2、使用本方法可能会导致进程等待较长一段时间。
     * @param filepath 文件路径
     * @return 如果删除成功返回true，否则返回false
     */
    boolean froceDelete(filepath){
        def file = new File(filepath)
        def count = 0
        while(file.exists() && count <100){
            System.gc()
            file.delete()
            count++
            sleep(20)
        }
        return !file.exists()
    }

    /**
     * 获取文件的文本内容，默认 UTF-8 编码
     * @param filepath 文件路径
     * @return 文件的文本内容
     */
    String getText(String filepath){
        getText(filepath,"utf-8")
    }

    /**
     * 获取文件的文本内容
     * @param filepath 文件路径
     * @param charset 文件编码格式
     * @return 文件的文本内容
     */
    String getText(String filepath,String charset){
        return new File(filepath).getText(charset)
    }

    /**
     * 保存文本内容到指定路径的文件，采用UTF-8编码
     * @param filepath 文件路径
     * @param content 文本内容
     */
    void save(String filepath,String content){
        new File(filepath).setText(content,"utf-8")
    }

    /**
     * 获取真实文件路径，即适配操作系统的文件路径
     * @param path 原始文件路径
     * @return 适配操作系统的文件路径
     */
    String getRealFilePath(String path) {
        return path.replace("/", FILE_SEPARATOR).replace("\\", FILE_SEPARATOR);
    }

}
