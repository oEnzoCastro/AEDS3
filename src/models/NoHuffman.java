package models;

public class NoHuffman {
    private int num;
    private char symbol;
    private NoHuffman esq;
    private NoHuffman dir;

    public NoHuffman(int num, char symbol) {
        this.setNum(num);
        this.setSymbol(symbol);
        this.setEsq(null);
        this.setDir(null);
    }


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public NoHuffman getEsq() {
        return esq;
    }

    public void setEsq(NoHuffman esq) {
        this.esq = esq;
    }

    public NoHuffman getDir() {
        return dir;
    }

    public void setDir(NoHuffman dir) {
        this.dir = dir;
    }

    public boolean isFolha (){
        return esq == null && dir == null;
    }
}
