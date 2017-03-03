package com.github.atramos.quant.etrade.dao;

import java.util.ArrayList;

class QueueChain
{
  private String exp;

  public String getExp() { return this.exp; }

  public void setExp(String exp) { this.exp = exp; }

  private ArrayList<Double> calls;

  public ArrayList<Double> getCalls() { return this.calls; }

  public void setCalls(ArrayList<Double> calls) { this.calls = calls; }

  private ArrayList<Double> puts;

  public ArrayList<Double> getPuts() { return this.puts; }

  public void setPuts(ArrayList<Double> puts) { this.puts = puts; }
}