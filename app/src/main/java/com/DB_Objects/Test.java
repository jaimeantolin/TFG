package com.DB_Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Test implements Serializable {

    private String nombreTest;
    private ArrayList<Elemento> elementosSelec;

    public Test(String NombreTest, ArrayList<Elemento> elementosSelec ){
        this.nombreTest = NombreTest;
        this.elementosSelec = elementosSelec;
    }

    public String getNombreTest() {
        return nombreTest;
    }

    public void setNombreTest(String nombreTest) {
        this.nombreTest = nombreTest;
    }

    public ArrayList<Elemento> getElementosSelec() {
        return elementosSelec;
    }

    public void setElementosSelec(ArrayList<Elemento> elementosSelec) {
        this.elementosSelec = elementosSelec;
    }
}