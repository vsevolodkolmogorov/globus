package ru.globus.globusproject.client;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class SimpleJaxbDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        try (InputStream is = response.body().asInputStream()) {
            JAXBContext jaxbContext = JAXBContext.newInstance((Class<?>) type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
