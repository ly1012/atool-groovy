package org.qadoc.atool.groovy.expand

/**
 * 链接字符串解析工具类，有特定含义的字符串
 * 当前有两种类型链接字符串。
 * 1、纯文件链接  @autolink://fahai_person/fahai_person_native_001.json
 * 2、Excel文件链接，带Sheet页名称  @autolink://fahai_person/fahai_person_native.xlsx?sheetName=A001
 */
class LinkUtil {

    public static final String AUTOLINK_PREFIX = "@autolink://"

    /**
     * 获取链接中的文件路径
     * @param autolink 链接
     * @return 链接中的文件路径
     */
    String getPath(String autolink){
    	def link = getLink(autolink)
        return link.split("\\?")[0]
    }

    /**
     * 获取autolink中的文件夹路径
     */
    String getFolderPath(String autolink){
        def path  = getPath(autolink)
        def filename = getFileName(autolink)
        return path.replace("/"+filename,"")
    }

    /**
     * 获取文件名（不带扩展名）
     */
    String getFileNameNoExtension(String autolink){
        def fullname = getFileName(autolink)
        int dot = fullname.lastIndexOf('.')
        if ((dot > -1) && (dot < (fullname.length()))) { 
            return fullname.substring(0, dot); 
        }
        return fullname
    }

    /**
     * 获取文件名（包含扩展名）
     */
    String getFileName(String autolink){
        def path = getPath(autolink)
        return path.split("/")[-1]
    }

    /**
     * 获取链接当中的Sheet页名称
     * @param autolink 文件链接
     * @return 链接中的Sheet页名称
     */
    String getSheetName(String autolink){
    	def link = getLink(autolink)
    	def kvs = link.split("\\?")[1].split("&")
    	def sheetName
    	for(kv in kvs){
    		def kvarr = kv.split("=")
    		def key = kvarr[0]
    		if(key == "sheetName"){
    			sheetName = kvarr[1]
    			break
    		}
    	}
    	return sheetName
    }

    String getLink(String autolink){
    	if(!autolink.startsWith(AUTOLINK_PREFIX)){
        	throw new RuntimeException("autolink[${autolink}] is invalid,should be start with ${AUTOLINK_PREFIX},for example: @autolink://fahai_person/fahai_person_native.xlsx?sheetName=A001")
        }
        def link = autolink.replace(AUTOLINK_PREFIX,"")
        if(link.trim() == ""){
        	throw new RuntimeException("autolink[${autolink}] should not be empty!")
        }
        return link
    }

    boolean isAutoLink(String autolink){
        if(!autolink.startsWith(AUTOLINK_PREFIX)){
            return false
        }
        def link = autolink.replace(AUTOLINK_PREFIX,"")
        if(link.trim() == ""){
            throw new RuntimeException("autolink[${autolink}] should not be empty!")
        }
        return true
    }

}
