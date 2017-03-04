package com.github.atramos.quant.etrade.cloud_loader;

import java.util.ArrayList;

class QueueStock
{
  private double last;

  public double getLast() { return this.last; }

  public void setLast(double last) { this.last = last; }

  private ArrayList<QueueChain> chains;

  public ArrayList<QueueChain> getChains() { return this.chains; }

  public void setChains(ArrayList<QueueChain> chains) { this.chains = chains; }

  private String symbol;

  public String getSymbol() { return this.symbol; }

  public void setSymbol(String symbol) { this.symbol = symbol; }
}