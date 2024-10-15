package com.maltepuro.lagerlog.model;

import com.maltepuro.lagerlog.controller.RelatorioController;

public class Relatorio{
    // public static void main(String[] args) {
    //     RelatorioController relatorio= new RelatorioController();
    //     relatorio.geraRelatorio();
      
    // }

    private RelatorioController relatorioController;

    public Relatorio() {
        this.relatorioController = new RelatorioController();
    }

    public void gerarRelatorio() {
        relatorioController.geraRelatorio();
    }
    
}
