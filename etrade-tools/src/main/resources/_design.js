{
  "_id": "_design/main",
  "_rev": "88-25360739ce643e1fab20d44a7ed43ce3",
  "views": {
    "volume": {
      "map": "function (doc) {\n  emit((\"000000000000\" + doc.intraday.totalVolume).slice(-12), 1);\n}"
    },
    "strike": {
      "map": "function (doc) {\n  if(doc.product)\n    emit(doc.product.symbol, [[],[doc.intraday.lastTrade],[]])\n  else if(doc.array)\n    for(i=0; i < doc.array.length; ++i) {\n      emit(doc.array[i].call[0].rootSymbol, [doc.array[i].call,[],[]]);\n      emit(doc.array[i].put[0].rootSymbol, [[],[],doc.array[i].put]);\n    }\n}",
      "reduce": "function (keys, values, rereduce) {\n  var nvalues = [[],[],[]];\n  for(i=0; i<values.length; ++i) {\n    nvalues[0] = nvalues[0].concat(values[i][0]);\n    nvalues[1] = nvalues[1].concat(values[i][1]);\n    nvalues[2] = nvalues[2].concat(values[i][2]);\n  }\n  if(nvalues[1].length === 0) {\n    return nvalues;\n  }\n  for(i=0; i<nvalues[0].length; ++i) {\n    if(nvalues[0][i].strikePrice >= nvalues[1][0]) {\n      nvalues[0] = [ nvalues[0][i] ];\n      nvalues[2] = [ nvalues[2][i-1] ];\n      break;\n    }\n  }\n  return nvalues;\n}"
    }
  },
  "language": "javascript"
}