package com.team12.coronawatch;

public class CustomList {
    private String name;
    private String isol;
    private String def;
    private String defInc;
    private String clear;
    private String death;

    CustomList(String name, long isol, long def, int defInc, long clear, long death) {
        this.name = name;
        this.isol = Long.valueOf(isol).toString();
        this.def = Long.valueOf(def).toString();
        this.defInc = Integer.valueOf(defInc).toString();
        this.clear = Long.valueOf(clear).toString();
        this.death = Long.valueOf(death).toString();
    }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setIsol(long isol) { this.isol = Long.valueOf(isol).toString(); }
    public String getIsol() { return this.isol; }
    public void setDef(long def) { this.def = Long.valueOf(def).toString(); }
    public String getDef() { return this.def; }
    public void setDefInc(int defInc) { this.defInc = Integer.valueOf(defInc).toString(); }
    public String getDefInc() { return this.defInc; }
    public void setClear(long clear) { this.clear = Long.valueOf(clear).toString(); }
    public String getClear() { return this.clear; }
    public void setDeath(long death) { this.death = Long.valueOf(death).toString(); }
    public String getDeath() { return this.death; }
}
