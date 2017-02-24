{
  "_id": "_design/main",
  "_rev": "117-8fe2b113e1244306df8ed034d3a3737d",
  "views": {
    "volume": {
      "map": "function (doc) {\n  if(doc.product.type == \"EQ\")\n    emit((\"000000000000\" + doc.intraday.totalVolume).slice(-12), 1);\n}"
    },
    "strike": {
      "map": "function (doc) {\n  if(doc.product && doc.product.type == \"EQ\")\n    emit(doc.product.symbol, [[],[doc.intraday.lastTrade],[]])\n  else if(doc.array)\n    for(i=0; i < doc.array.length; ++i) {\n      var call = doc.array[i].call[0];\n      emit(call.rootSymbol, [[{ \"strikePrice\": call.strikePrice, \n        \"symbol\": call.rootSymbol + \":\" + call.expireDate.year + \":\" + call.expireDate.month + \":\" + call.expireDate.day + \":CALL:\" + call.strikePrice}],[],[]]);\n      var put = doc.array[i].put[0];\n      emit(put.rootSymbol, [[],[],[{ \"strikePrice\": put.strikePrice, \n        \"symbol\": put.rootSymbol + \":\" + put.expireDate.year + \":\" + put.expireDate.month + \":\" + put.expireDate.day + \":PUT:\" + put.strikePrice}],[],[]]);\n    }\n}",
      "reduce": "function (keys, values, rereduce) {\n  var nvalues = [[],[],[]];\n  for(i=0; i<values.length; ++i) {\n    nvalues[0] = nvalues[0].concat(values[i][0]);\n    nvalues[1] = nvalues[1].concat(values[i][1]);\n    nvalues[2] = nvalues[2].concat(values[i][2]);\n  }\n  if(nvalues[1].length > 0) {\n    for(i=0; i<nvalues[0].length; ++i) {\n      if(nvalues[0][i].strikePrice >= nvalues[1][0]) {\n        nvalues[0] = [ nvalues[0][i] ];\n        break;\n      }\n    }\n    for(i=0; i<nvalues[2].length; ++i) {\n      if(nvalues[2][i].strikePrice >= nvalues[1][0]) {\n        nvalues[2] = [ nvalues[2][i-1] ];\n        break;\n      }\n    }\n  }\n  return nvalues;\n}"
    },
    "opt_spread_ratio": {
      "map": "function (doc) {\n  if(doc.product.type == \"OPTN\" && doc.intraday.bid > 0)\n    var i = doc.intraday;\n    var r = (i.ask - i.bid) / (i.ask + i.bid ) * 2;\n    emit((\"000\" + r.toFixed(6)).slice(-10), [i.bid, i.ask]);\n}"
    }
  },
  "language": "javascript"
}