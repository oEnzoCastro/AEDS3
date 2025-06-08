package services;

import java.util.BitSet;

public class ArrayDeBits {
    private BitSet vetor;

    public ArrayDeBits() {
        vetor = new BitSet();
        vetor.set(0); // O bit na posição 0 atua como marcador para um comprimento de 0
    }

    public ArrayDeBits(int n) {
        vetor = new BitSet(n + 1);
        vetor.set(n); // O bit na posição 'n' atua como marcador para um comprimento de 'n'
    }

    public ArrayDeBits(byte[] v) {
        vetor = BitSet.valueOf(v);
    }

    public byte[] toByteArray() {
        return vetor.toByteArray();
    }

    public void set(int i) {
        if (i >= this.length()) {
            vetor.clear(this.length()); // reset vetor
            vetor.set(i + 1);
        }
        vetor.set(i);
    }

    public void clear(int i) {
        if (i >= this.length()) {
            vetor.clear(this.length()); // reset vetor
            vetor.set(i + 1);
        }
        vetor.clear(i);
    }

    public boolean get(int i) {
        return vetor.get(i);
    }

    public int length() {
        if (vetor.isEmpty()) {
            return 0;
        }
        return vetor.length() - 1;
    }

    public int size() {
        return vetor.size();
    }
}