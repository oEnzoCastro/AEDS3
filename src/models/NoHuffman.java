package models;

import java.io.Serializable;

public class NoHuffman implements Serializable{
    private static final long serialVersionUID = 1L;

    private int num;
    private byte symbol;
    private NoHuffman esq;
    private NoHuffman dir;

    public NoHuffman(int num, byte symbol) {
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

    public byte getSymbol() {
        return symbol;
    }

    public void setSymbol(byte symbol) {
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
