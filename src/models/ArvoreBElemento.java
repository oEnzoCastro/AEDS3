package models;

public class ArvoreBElemento {

    long esq;
    int id;
    long posicao;
    long dir;

    public long getEsq() {
        return esq;
    }

    public void setEsq(long esq) {
        this.esq = esq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPosicao() {
        return posicao;
    }

    public void setPosicao(long posicao) {
        this.posicao = posicao;
    }

    public long getDir() {
        return dir;
    }

    public void setDir(long dir) {
        this.dir = dir;
    }

    public ArvoreBElemento() {
        this.esq = -1;
        this.id = 0;
        this.posicao = -1;
        this.dir = -1;
    }

}
