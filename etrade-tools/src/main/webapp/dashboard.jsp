<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="WebDashboard" class="com.github.atramos.quant.etrade.cloudapp.WebDashboard"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Stock Option Dashboard</title>
<script src="static/table.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body>
<table class="table table-autostripe table-autosort table-autofilter table-stripeclass:alternate">
<thead>
  <tr>
    <th class="table-filterable">Stock</th>
    <th class="table-sortable table-sortable:numeric">Volume</th>
    <th class="table-sortable:numeric">Last</th>
    <th class="table-sortable:numeric">Change%</th>
    <th class="table-filterable">Expiry</th>
    <th>Strike</th>
    <th class="table-filterable">Type</th>
    <th>Opt.Bid</th>
    <th>Opt.Ask</th>
    <th class="table-sortable:numeric">Option Volume</th>
    <th class="table-sortable:numeric">(A-B)/P</th>
    <th class="table-sortable:numeric">Yield</th>
    <th class="table-sortable:numeric">Ann.Yield</th>
  </tr>
</thead>
<tbody>
  <c:forEach items="${data}" var="item">
    <tr>
      <td><a href="#" data-toggle="tooltip" title="${item.companyName}">${item.symbol}</a></td>
      <td><fmt:formatNumber pattern="#,##0" value="${item.totalVolume}"/></td>
      <td><fmt:formatNumber pattern="#,##0.00" value="${item.lastTrade}"/></td>
      <td>${item.chgClosePrcn}%</td>
      <td>${item.expiration}</td>
      <td>${item.strikePrice}</td>
      <td>${item.optionType}</td>
      <td><fmt:formatNumber pattern="#,##0.00" value="${item.optionBid}"/></td>
      <td><fmt:formatNumber pattern="#,##0.00" value="${item.optionAsk}"/></td>
      <td><fmt:formatNumber pattern="#,##0" value="${item.optionVolume}"/></td>
      <td><fmt:formatNumber pattern="#,##0.000" value="${item.spreadPct}"/>%</td>
      <td><fmt:formatNumber pattern="#,##0.0" value="${item.yield}"/>%</td>
      <td><fmt:formatNumber pattern="#,##0.0" value="${item.aYield}"/>%</td>
    </tr>
  </c:forEach>
</tbody>
</table>

</body>
</html>