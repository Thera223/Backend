package com.example.cmd.DTO;

import lombok.Data;

@Data
public class AvisDTO {
    private Long produitId;
    private Long clientId;
    private int note;
    private String commentaire;


}
