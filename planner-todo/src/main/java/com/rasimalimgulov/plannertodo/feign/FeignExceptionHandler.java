package com.rasimalimgulov.plannertodo.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.CharStreams;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
@Log4j2
@Component
public class FeignExceptionHandler implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
       log.info("Выполняется Exception Handler");
        switch (response.status()){
            case 404:
                log.info("Handler перехватил 404 ошибку");
                return new UserNotFoundException(readMessage(response));
            case 503:

                log.info("Handler перехватил 503 ошибку");
                return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Сервис временно недоступен");

            default:
                return new RuntimeException("Unhandled error status: " + response.status());

        }
    }

    private String readMessage(Response response){
        String message = null;
        Reader reader=null;
        try{
            reader=response.body().asReader(Charset.defaultCharset());
            message= CharStreams.fromReader(reader).toString();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try { if (reader != null){
                    reader.close();
                }
            } catch (IOException e) {
                 e.printStackTrace();
                }
            }
        return message;
    }
}
