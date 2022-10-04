package com.macro.mall.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public final class XmlUtil {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

    /**
     * 　　* 将对象直接转换成String类型的 XML输出
     * 　　*
     * 　　* @param obj
     * 　　* @return
     */
    public static String convertToXml(Object obj) {
        // 创建输出流
        StringWriter sw = new StringWriter();
        try {
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw);
        } catch (JAXBException e) {
            throw new RuntimeException("object convert to xml error", e);
        }
        return sw.toString();
    }

    /**
     * 　　* 将对象根据路径转换成xml文件
     * 　　*
     * 　　* @param obj
     * 　　* @param path
     */
    public static String convertToXml(Object obj, String path) {
        FileWriter fw;
        try {
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // 将对象转换成输出流形式的xml
            // 创建输出流
            fw = new FileWriter(path);
            marshaller.marshal(obj, fw);
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("object convert to xml error", e);
        }
        return fw.toString();
    }

    /**
     * 　　* 将String类型的xml转换成对象
     * 　　*
     * 　　* @param clazz
     * 　　* @param xmlStr
     * 　　* @return
     */

    @SuppressWarnings("unchecked")
    public static <T> T convertXmlStrToObject(Class<T> clazz, String xmlStr) {
        T xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            xmlObject = (T) unmarshaller.unmarshal(sr);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return xmlObject;
    }
}
