<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
    <th>Stock</th>
    <th>Volume</th>
    <th>Last</th>
    <th>Change%</th>
    <th>Expiry</th>
    <th>Strike</th>
    <th>Type</th>
    <th>Opt.Bid</th>
    <th>Opt.Ask</th>
    <th>Option Volume</th>
    <th class="table-filterable table-sortable:numeric">(B-A)/L</th>
  </tr>
</thead>
<tbody>
  <c:forEach items="${data}" var="item">
    <c:forEach items="${item.options}" var="opt">
      <c:if test="${opt != null && opt.product != null}">
        <tr>
        <td><a href="#" data-toggle="tooltip" title="${item.stock.intraday.companyName}">${item.stock.product.symbol}</a></td>
        <td><fmt:formatNumber pattern="#,##0" value="${item.stock.intraday.totalVolume}"/></td>
        <td><fmt:formatNumber pattern="#,##0.00" value="${item.stock.intraday.lastTrade}"/></td>
        <td>${item.stock.intraday.chgClosePrcn}</td>
        <td>${opt.product.expirationYear}-${opt.product.expirationMonth}-${opt.product.expirationDay}</td>
        <td>${opt.product.strikePrice}</td>
        <td>${opt.product.optionType}</td>
        <td><fmt:formatNumber pattern="#,##0.00" value="${opt.intraday.bid}"/></td>
        <td><fmt:formatNumber pattern="#,##0.00" value="${opt.intraday.ask}"/></td>
        <td><fmt:formatNumber pattern="#,##0" value="${opt.intraday.totalVolume}"/></td>
        <td><fmt:formatNumber pattern="#,##0.000" value="${ (opt.intraday.ask - opt.intraday.bid) / item.stock.intraday.lastTrade * 100}"/>%</td>
        </tr>
      </c:if>
    </c:forEach>
  </c:forEach>
</tbody>
</table>

</body>
</html>