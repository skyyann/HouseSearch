<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>租房搜索</title>
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="http://cache.amap.com/lbs/static/main1119.css" />
    <link rel="stylesheet" href="http://cache.amap.com/lbs/static/jquery.range.css" />
    <script src="http://webapi.amap.com/maps?v=1.3&key=8a971a2f88a0ec7458d43b8bc03b6462&plugin=AMap.ArrivalRange,AMap.Scale,AMap.Geocoder,AMap.Transfer,AMap.Autocomplete,AMap.CitySearch,AMap.Walking"></script>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.js"></script>
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
</head>
<body>
<div class="container">
    <div id="container" clss="col-md-10">
    </div>
    <div class="col-md-4 col-md-offset-9">
        <div class="form-group">
            <label>当前城市：</label>
            <label id="city">北京市</label>
            <label id="citycode" style="display:none">010</label>
        </div>
        <div class="form-group">
            <label>最低价格</label>
            <input id="minprice" class="form-control col-md-1" />
        </div>
        <div class="form-group">
            <label>最高价格</label>
            <input id="maxprice" class="form-control col-md-1" />
        </div>
        <button class="btn btn-primary" onclick="serach()">搜索</button>
    </div>
</div>
</body>
<script>
    var map = new AMap.Map('container',{
        zoom: 10,
        center: [116.397428, 39.9092]
    });
    map.on('moveend', getCity);
    function getCity() {
        map.getCity(function(data) {
            if (data['citycode'] && typeof data['citycode'] === 'string') {
                document.getElementById('city').innerHTML = (data['city'] || data['province']);
                document.getElementById('citycode').innerHTML = data['city'];
            }
        });
    }
    function serach() {
        $.post(
            "HouseSearch",
            {
                cityCode:document.getElementById('citycode').innerHTML,
                minPrice:document.getElementById('minprice').val(),
                maxPrice:document.getElementById('maxprice').val()
            }
        );
    }
</script>
</html>
