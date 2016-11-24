package com.example.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);
    static ObjectMapper mapper = new ObjectMapper();

    private CommonUtils() {

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> String object2XML(T obj) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Class<?> clazz = obj.getClass();
        Marshaller marshaller = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
        
        try {
            XmlRootElement rootElement = clazz.getAnnotation(XmlRootElement.class);
            if (rootElement == null
                    || rootElement.name().equals(XmlRootElement.class.getMethod("name").getDefaultValue().toString())) {
                marshaller.marshal(new JAXBElement(new QName("xml"), clazz, obj), os);
            } else {
                marshaller.marshal(obj, os);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return new String(os.toByteArray());
    }

    @SuppressWarnings("unchecked")
    public static <T> T xml2Object(String xmlStr, Class<T> clazz) {
        Unmarshaller unmarshaller = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            unmarshaller = jaxbContext.createUnmarshaller();
            Reader reader = new StringReader(xmlStr);
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> String object2Json(T clazz) {
        try {
            return mapper.writeValueAsString(clazz);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T jsonToObject(Class<T> clazz, String jsonString) {
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
