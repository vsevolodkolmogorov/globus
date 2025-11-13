package ru.globus.globusproject.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.globus.globusproject.dto.xml.ValCursDto;

@FeignClient(
        name = "cbrClient",
        url = "https://www.cbr.ru",
        configuration = CbrFeignConfig.class
)
public interface CbrClient {
    @GetMapping(value = "/scripts/XML_daily.asp", produces = "application/xml")
    @ResponseBody
    ValCursDto getDailyRates();
}
