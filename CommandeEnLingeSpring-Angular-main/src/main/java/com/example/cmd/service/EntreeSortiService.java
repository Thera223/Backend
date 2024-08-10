package com.example.cmd.service;

import com.example.cmd.model.EntreeSorti;

import java.util.List;

public interface EntreeSortiService {
    EntreeSorti creer(EntreeSorti entreeSorti);
    List<EntreeSorti> voir_EntreeSorti();
}