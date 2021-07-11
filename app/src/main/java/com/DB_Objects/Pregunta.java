package com.DB_Objects;

public class Pregunta {

    private String foto;
    private String optionA;
    private String optionB;
    private String optionC;
    private String ans;

    public Pregunta(){

    }

    public Pregunta(String foto, String optionA, String optionB, String optionC, String ans){
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.foto = foto;
        this.ans = ans;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }
    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
