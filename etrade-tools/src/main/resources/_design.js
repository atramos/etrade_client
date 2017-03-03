{
  "_id": "_design/main",
  "_rev": "194-79e2b4fbff907e66edfc07af9a255105",
  "views": {
    "volume": {
      "map": "function (doc) {\n  if(doc.product.type == \"EQ\")\n    emit((\"000000000000\" + doc.intraday.totalVolume).slice(-12), 1);\n}"
    },
    "strike": {
      "map": "function (doc) {\n  if(doc.product && doc.product.type == \"EQ\")\n    emit(doc.product.symbol, {\"last\": doc.intraday.lastTrade, \"symbol\": doc.product.symbol, \"chains\":[]})\n  else if(doc.array) {\n    var spec = doc.array[0].call[0];\n    var exp = spec.expireDate.year + \":\" + spec.expireDate.month + \":\" + spec.expireDate.day;\n    var callStrikes = doc.array.map(function(x) {return x.call[0].strikePrice;});\n    var putStrikes = doc.array.map(function(x) {return x.put[0].strikePrice;});\n    emit(spec.rootSymbol, {\"chains\": [{\"exp\": exp, \"calls\": callStrikes, \"puts\": putStrikes}]});\n  }\n}",
      "reduce": "function (keys, values, rereduce) {\n  var nvalues = { \"last\": \"\", \"symbol\":\"\", \"chains\": []};\n  for(i=0; i<values.length; ++i) {\n    nvalues.last = nvalues.last || values[i].last;\n    nvalues.symbol = nvalues.symbol || values[i].symbol;\n    nvalues.chains = nvalues.chains.concat(values[i].chains);\n  }\n  if(nvalues.last) {\n    nvalues.chains.forEach(function(chain) {\n      for(i=0; i < chain.calls.length; ++i) {\n        if(chain.calls[i] >= nvalues.last) {\n          chain.calls = [ chain.calls[i] ];\n          break;\n        }\n      }\n      for(i=chain.puts.length; i >= 0; --i) {\n        if(chain.puts[i] <= nvalues.last) {\n          chain.puts = [ chain.puts[i] ];\n          break;\n        }\n      }\n    });\n  }\n  return nvalues;\n}"
    },
    "spread_to_strike_ratio": {
      "map": "function (doc) {\n  if(doc.product.type == \"OPTN\" && doc.intraday.bid > 0)\n    var i = doc.intraday;\n    var r = (i.ask - i.bid) / doc.product.strikePrice;\n    emit((\"000\" + r.toFixed(6)).slice(-10), [i.bid, i.ask, doc.product.strikePrice]);\n}"
    },
    "removal_q": {
      "map": "function (doc) {\n  if(doc.errorMessage == \"Invalid Symbol\" || doc.product.type == \"OPTN\" || doc.product.type == \"EQ\")\n    emit(doc._rev);\n}"
    },
    "put_strike_yield": {
      "map": "function (doc) {\n  if(doc.product.type == \"OPTN\" && doc.intraday.bid > 0)\n    var i = doc.intraday;\n    var r = i.bid / doc.product.strikePrice;\n    emit((\"000\" + r.toFixed(6)).slice(-10), [i.bid, i.ask, doc.product.strikePrice]);\n}"
    },
    "put_under_yield": {
      "reduce": "function (keys, values, rereduce) {\n  var nval = {\"stock\":null,\"options\":[]};\n  for(i=0; i < values.length; ++i) {\n    nval.stock = nval.stock || values[i].stock;\n    nval.options = nval.options.concat(values[i].options);\n  }\n  if(nval.stock && nval.options) {\n    nval.options = nval.options.map(function(x) {\n      if(x) x.yield = x.bid / nval.stock.lastTrade;\n      return x;\n    });\n  }\n  return nval;\n}",
      "map": "function (doc) {\n  if(doc.product.type==\"EQ\") {\n    emit(doc.product.symbol, {stock: {symbol: doc.product.symbol, lastTrade: doc.intraday.lastTrade}});\n  }\n  else if(doc.product.type==\"OPTN\" && doc.product.optionType==\"PUT\") {\n    emit(doc.product.symbol, {options: [ {id: doc._id, bid: doc.intraday.bid} ] });\n  }\n}"
    },
    "dashboard": {
      "reduce": "function (keys, values, rereduce) {\n  var nval = {\"stock\":null,\"options\":[]};\n  for(i=0; i < values.length; ++i) {\n    nval.stock = nval.stock || values[i].stock;\n    nval.options = nval.options.concat(values[i].options);\n  }\n  return nval;\n}",
      "map": "function (doc) {\n  if(doc.product.type==\"EQ\") {\n    emit(doc.product.symbol, {\"stock\":doc});\n  }\n  else if(doc.product.type==\"OPTN\") {\n    emit(doc.product.symbol, {\"options\":[doc]});\n  }\n}"
    }
  },
  "language": "javascript"
}