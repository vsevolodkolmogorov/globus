package ru.globus.globusproject.dto.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class ValCursDto {
    @XmlAttribute(name = "Date")
    private String date;

    @XmlElement(name = "Valute")
    private List<ValuteDto> valutes;
}
