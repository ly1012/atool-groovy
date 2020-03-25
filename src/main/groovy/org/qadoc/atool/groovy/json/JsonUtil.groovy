package org.qadoc.atool.groovy.json

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import io.restassured.path.json.JsonPath
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * JSON工具类
 * author：liyun
 * time：2018-05-08
 */
class JsonUtil{

    /**
     * 比较两个json的指定key的值，支持嵌套key（用法：person.name）
     * @param json1 第一个JSON
     * @param json2 第二个JSON
     * @param compareKeys 需要比较的key，多个使用英文逗号","分隔
     * @return 如果两个json所有指定key的值都相等返回true，否则返回false
     */
    boolean compare(json1,json2,compareKeys){
        def json1_txt
        def json2_txt
        if(json1.class == java.lang.String){
            json1_txt = json1
        }else{
            json1_txt = JsonOutput.toJson(json1)
        }
        if(json2.class == java.lang.String){
            json2_txt = json2
        }else{
            json2_txt = JsonOutput.toJson(json2)
        }
        def json1_obj = parse(json1_txt)
        def json2_obj = parse(json2_txt)
        if(json1_obj == json2_obj){
            return true
        }
        def obj1 = JsonPath.from(json1_txt)
        def obj2 = JsonPath.from(json2_txt)
        def keys = compareKeys.split(",")
        def key
        for(def i = 0; i < keys.length; i++){
            key = keys[i]
            if(obj1.get(key) != obj2.get(key)){
                return false
            }
        }
        return  true
    }

    /**
     * JSON字符串检验
     * @param json JSON字符串
     * @return JSON字符串
     */
    String toJson(String json){
        parse(json)
        return json
    }

    /**
     * 对象转JSON字符串（中文不转义）
     * @param object 对象
     * @return 未转义的JSON字符串
     */
    String toJson(Object object){
        //普通对象转JSON文本：如果文本中有中文，中文会转义为Unicode字符
        //例如：groovy.json.JsonOutput.toJson("李云")
        //OutPut： "\u674e\u4e91"
        //def jsontext = JsonOutput.toJson(object)
        //处理JSON文本中的Unicode字符
        //return StringEscapeUtils.unescapeJava(jsontext)
        ///**
//         * ObjectMapper是JSON操作的核心，Jackson的所有JSON操作都是在ObjectMapper中实现。
//         * ObjectMapper有多个JSON序列化的方法，可以把JSON字符串保存File、OutputStream等不同的介质中。
//         * writeValue(File arg0, Object arg1)把arg1转成json序列，并保存到arg0文件中。
//         * writeValue(OutputStream arg0, Object arg1)把arg1转成json序列，并保存到arg0输出流中。
//         * writeValueAsBytes(Object arg0)把arg0转成json序列，并把结果输出成字节数组。
//         * writeValueAsString(Object arg0)把arg0转成json序列，并把结果输出成字符串。
//         */
        ObjectMapper mapper = new ObjectMapper();

        //User类转JSON
        //输出结果：{"name":"zhangsan","age":20,"birthday":844099200000,"email":"zhangsan@163.com"}
        String jsontt = mapper.writeValueAsString(object);
        return jsontt
    }
    
 

    /**
     * 普通对象转JSON对象
     * @param object 普通对象
     * @return JSON对象
     */
    Object parse(Object object){
        //普通对象转JSON文本
        def jsontext = toJson(object)
        //JSON文本转JSON对象
        return parse(jsontext)
    }

    /**
     * JSON字符串转JSON对象
     * @param jsontext JSON字符串
     * @return JSON对象
     */
    Object parse(String jsontext){
        return new JsonSlurper().parseText(jsontext)
    }

    /**
     * 移除JSON对象中的指定key
     * emails[0].id 将移除emails数组中第一个元素下的id节点
     * emails.id 将移除emails数组中所有元素下的id节点
     * @param json JSON对象
     * @param key 指定key,如：id or interests.id or emails[0].id or emails.id
     */
    void remove(json,key){
        if(key.trim().length() < 1){
            return
        }
        def keys = key.split("\\.")
        //获取层级数
        //get the number of layers in jsonpath
        def total = keys.size()
        def count = 1
        removeNode(json,keys,total,count)
    }

    /**
     * 更新JSON对象中指定key的Value
     * emails[0].id 将更新emails数组中第一个元素下的id节点的值
     * emails.id 将更新emails数组中所有元素下的id节点的值
     * @param json JSON对象
     * @param key 指定key,如：id or interests.id or emails[0].id or emails.id
     */
    void update(json,key,value){
        if(key.trim().length() < 1){
            return
        }
        def keys = key.split("\\.")
        //获取层级数
        //get the number of layers in jsonpath
        def total = keys.size()
        def count = 1
        updateNode(json,keys,total,count,value)
    }

    /**
     * 新增JSON对象中指定节点
     * emails[3].id 将新增emails数组中第四个元素下的id节点，数组长度不足四位，不足的位使用空对象填充
     * emails.id 将新增emails节点下的id节点
     * @param json JSON对象
     * @param key 指定key,如：id or interests.id or emails[0].id
     */
    void add(json,key,value){
        if(key.trim().length() < 1){
            return
        }
        def keys = key.split("\\.")
        //获取层级数
        //get the number of layers in jsonpath
        def total = keys.size()
        def count = 1
        addNode(json,keys,total,count,value)
    }

    //test regex for extract nodename and index from jsonpath
    //def str= "email[0]"
    //def matcher = str =~ /(\S+)\[([0-9]+)\]/
    //log.info matcher.matches()	//true
    //log.info matcher[0][0]	//email[0]
    //log.info matcher[0][1]	//email
    //log.info matcher[0][2]	//0

    // A private method service for method remove
    private void removeNode(json,keys,total,count){	//count begin 1
        //如果是最后一层，移除指定Key
        //if the node is the bottom of the jsonpath then remove this key.
        if(count == total){
            if(json instanceof Map){
                json.remove(keys[total-1])
            }else if(json instanceof List){
                json.each{ i ->
                    i.remove(keys[total-1])
                }
            }
            return
        }
        //如果不是最后一层，传递子节点
        //if the node is not the bottom of the jsonpath then deliver child node to next cycle
        def prechildkey = keys[count-1].trim()
        def matcher = prechildkey =~ /(\S+)\[([0-9]+|\*)\]/
        //如果本级key指定了下标
        //if current key assign a subscript
        if(matcher.matches()){
            def childkey = matcher[0][1].trim()
            def subscript = matcher[0][2]
            if(subscript instanceof Integer){
                def x = Integer.parseInt(subscript)
                def jsont = json.get(childkey).get(x)
                count++
                removeNode(jsont,keys,total,count)
            }else if(subscript instanceof String){
                if(subscript == '*'){
                    def list_a =  json.get(childkey)
                    count++
                list_a.each{ jsont ->
                        removeNode(jsont,keys,total,count)
                }
                }else{
                    throw new RuntimeException("jsonpath is invalid, ${subscript} should be * .".toString())
                }
            }else{
                throw new RuntimeException("I can't deal with this type")
            }
        
        //如果本级key没有指定下标
        //if current key not assign a subscript
        }else{
             def jsont = json.get(prechildkey)
             count++
             removeNode(jsont,keys,total,count)
        }
    }

    // A private method service for method update
    private void updateNode(json,keys,total,count,value){	//count begin 1
        //如果是最后一层，更新指定Key
        //if the node is the bottom of the jsonpath then remove this key.
        if(count == total){
            if(json instanceof Map){
                json.put(keys[total-1],value)
            }else if(json instanceof List){
                json.each{ i ->
                    i.put(keys[total-1],value)
                }
            }
            return
        }
        //如果不是最后一层，传递子节点
        //if the node is not the bottom of the jsonpath then deliver child node to next cycle
        def prechildkey = keys[count-1]
        def matcher = prechildkey =~ /(\S+)\[([0-9]+)\]/
        //如果本级key指定了下标
        //if current key assign a subscript
        if(matcher.matches()){
            def childkey = matcher[0][1]
            def x = Integer.parseInt(matcher[0][2])
            def jsont = json.get(childkey).get(x)
            count++
            updateNode(jsont,keys,total,count,value)
        //如果本级key没有指定下标
        //if current key not assign a subscript
        }else{
            def jsont = json.get(prechildkey)
            count++
            updateNode(jsont,keys,total,count,value)
        }
    }

    // A private method service for method add
    private void addNode(json,keys,total,count,value){    //count begin 1
        //如果是最后一层，添加指定Key
        //if the node is the bottom of the jsonpath then remove this key.
        if(count == total){
            if(json instanceof Map){
                json.put(keys[total-1],value)
            }else if(json instanceof List){
                json.each{ i ->
                    i.put(keys[total-1],value)
                }
            }
            return
        }
        //如果不是最后一层，传递子节点
        //if the node is not the bottom of the jsonpath then deliver child node to next cycle
        def prechildkey = keys[count-1]
        def matcher = prechildkey =~ /(\S+)\[([0-9]+)\]/
        //如果本级key指定了下标
        //if current key assign a subscript
        if(matcher.matches()){
            def childkey = matcher[0][1]
            def x = Integer.parseInt(matcher[0][2])
            def jsont_list = json.get(childkey)
            if(jsont_list == null){
                json.put(childkey,new ArrayList<>())
                jsont_list = json.get(childkey)
            }
            //自动填充空缺数组位
            //0-1-2-3，当前数组长度为2，截至[1]。当对[3]赋值时，自动用空对象填充[2]和[3]。
            def maxindex = jsont_list.size()-1
            if(maxindex < x){
                for(def k = 0; k < x - maxindex; k++){
                    jsont_list.add(new LazyMap())
                }
            }
            def jsont = jsont_list.get(x)
            count++
            addNode(jsont,keys,total,count,value)
        //如果本级key没有指定下标
        //if current key not assign a subscript
        }else{
            def jsont = json.get(prechildkey)
            if(jsont == null){
                json.put(prechildkey,new LazyMap())
                jsont = json.get(prechildkey)
            }
            count++
            addNode(jsont,keys,total,count,value)
        }
    }

}
