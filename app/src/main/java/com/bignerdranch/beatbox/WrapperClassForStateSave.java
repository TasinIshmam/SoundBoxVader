package com.bignerdranch.beatbox;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tasin Ishmam on 6/29/2018.
 */
public class WrapperClassForStateSave implements Serializable {
    public ArrayList<Integer> chainedItens;
    public Integer selectionMode;

    public ArrayList<Integer> getChainedItens() {
        return chainedItens;
    }

    public Integer getSelectionMode() {
        return selectionMode;
    }

    public WrapperClassForStateSave(ArrayList<Integer> chainedItens, Integer selectionMode) {

        this.chainedItens = chainedItens;
        this.selectionMode = selectionMode;
    }
}